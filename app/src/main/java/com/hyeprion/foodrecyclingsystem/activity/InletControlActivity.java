package com.hyeprion.foodrecyclingsystem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hyeprion.foodrecyclingsystem.R;
import com.hyeprion.foodrecyclingsystem.base.BaseActivity;
import com.hyeprion.foodrecyclingsystem.base.MyApplication;
import com.hyeprion.foodrecyclingsystem.bean.WeightBeforeOpenEvent;
import com.hyeprion.foodrecyclingsystem.databinding.ActivityInletControl2Binding;
import com.hyeprion.foodrecyclingsystem.util.Constant;
import com.hyeprion.foodrecyclingsystem.util.countdowntimer.MyCountDownTimer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.Nullable;

/**
 * apartment must / shop-option
 * 投入口控制界面
 */
public class InletControlActivity extends BaseActivity<ActivityInletControl2Binding> {
    /**
     * 开门放垃圾之前的净重，在获取投入垃圾后的净重，用投入后减去投入前为此次投入重量
     */
    private float inletBeforeWeight;
    private boolean warningFlag = false;
    private MyCountDownTimer myCountDownTimer;
    private boolean flag = true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        inletBeforeWeight = getIntent().getFloatExtra("beforeOpenDoorWeight",0);
        countDown30S();
        MyApplication.soundPlayUtils.loadMedia(Constant.music_3_inlet_press_close);
    }

    @Override
    protected void initView() {
//        viewBinding.btnCloseInlet.setTextColor(R.drawable.bg_text_click_blue_white);
        RequestOptions options = new RequestOptions()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.DATA);
        Glide.with(mActivity)
                .asGif()
                .load(R.mipmap.updownarrow)
                            .apply(options)
                .into(viewBinding.ivInletControl);
    }


    @Override
    protected void initListener() {
        viewBinding.btnBack.setOnClickListener(this);
//        viewBinding.btnOpenInlet.setOnClickListener(this);
        viewBinding.btnCloseInlet.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back:
                this.finish();
                break;
//            case R.id.btn_open_inlet:
//                inletBeforeWeight = PortControlUtil.getInstance().getPortStatus().getNetWeight();
//                Intent intent = new Intent(this,InletOpeningActivity.class);
//                startActivity(intent);
//                break;
            case R.id.btn_close_inlet:
                // 门未打开
//                if (PortControlUtil.getInstance().getPortStatus().getInletStatus() != 2){
//                    ToastUtils.showShort(R.string.pelese_first_open);
//                    return;
//                }
                flag = false;
                Intent intent2 = new Intent(this,InletClosingActivity.class);
                intent2.putExtra("inletBeforeWeight",inletBeforeWeight);
                startActivity(intent2);
                this.finish();
                break;
        }
    }
    /**
     * @param
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getWeight(WeightBeforeOpenEvent weightBeforeOpenEvent) {
        inletBeforeWeight = weightBeforeOpenEvent.getWeight();
    }

    /**
     * 30S倒计时，结束关闭页面跳转 主界面{@link LoginActivity}
     */
    private void countDown30S() {
        // 倒计时总时长
        long millisInFuture = 30 * Constant.second;
        myCountDownTimer = new MyCountDownTimer(millisInFuture, Constant.second) {
            @Override
            public void onTick(long millisUntilFinished) {
                viewBinding.tvCountdown.setText(String.format(getString(R.string.countdown), (millisUntilFinished / Constant.second) + ""));
            }

            @Override
            public void onFinish() {
                if (!flag){
                    return;
                }
                Intent intent2 = new Intent(mContext,InletClosingActivity.class);
                intent2.putExtra("inletBeforeWeight",inletBeforeWeight);
                startActivity(intent2);
                InletControlActivity.this.finish();
            }
        };
        myCountDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (myHandler != null){
            myHandler.removeCallbacks(myRunnable);
            myHandler.removeCallbacksAndMessages(null);
        }
        if (myCountDownTimer != null) {
            myCountDownTimer.cancel();
            myCountDownTimer = null;
        }
    }
}
