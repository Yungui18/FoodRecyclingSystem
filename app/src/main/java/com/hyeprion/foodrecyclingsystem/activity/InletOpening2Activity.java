package com.hyeprion.foodrecyclingsystem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hyeprion.foodrecyclingsystem.R;
import com.hyeprion.foodrecyclingsystem.base.BaseActivity;
import com.hyeprion.foodrecyclingsystem.base.MyApplication;
import com.hyeprion.foodrecyclingsystem.bean.WeightBeforeOpenEvent;
import com.hyeprion.foodrecyclingsystem.util.Constant;
import com.hyeprion.foodrecyclingsystem.util.HTTPServerUtil;
import com.hyeprion.foodrecyclingsystem.util.SaveToSdUtil;
import com.hyeprion.foodrecyclingsystem.util.countdowntimer.MyCountDownTimer;
import com.hyeprion.foodrecyclingsystem.util.port.PortConstants;
import com.hyeprion.foodrecyclingsystem.util.port.PortControlUtil;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.Nullable;

/**
 * apartment- must / shop-option
 * 状态页面 放置口开启中
 * from {@link InletControlActivity}
 */
public class InletOpening2Activity extends BaseActivity<com.hyeprion.foodrecyclingsystem.databinding.ActivityInletOpening2Binding> implements View.OnClickListener {
    private boolean first = true;
    private MyCountDownTimer myCountDownTimer;
    private float beforeOpenDoorWeight = 0;
    private boolean flag = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding.clPrepare.setVisibility(View.VISIBLE);
        viewBinding.clOpening.setVisibility(View.GONE);
        viewBinding.clOpeningFw2.setVisibility(View.GONE);
        MyApplication.soundPlayUtils.loadMedia(Constant.music_1_preparing);
        // 停止搅拌电机命令
        PortControlUtil.getInstance().sendCommands(PortConstants.STIR_STOP);
        PortControlUtil.getInstance().sendCommands(PortConstants.FAN1_STOP);
        PortControlUtil.getInstance().sendCommands(PortConstants.DEHUMIDIFICATION_STOP);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MyApplication.soundPlayUtils.loadMedia(Constant.music_2_press_open);
                myHandler.post(myRunnable);
                viewBinding.tvCountdown.setVisibility(View.VISIBLE);
                viewBinding.clPrepare.setVisibility(View.GONE);
                countDown30S();

                beforeOpenDoorWeight = PortControlUtil.getInstance().getPortStatus().getChooseUseWeighing();
                LogUtils.e("InletOpeningActivity feeding before weight:" + beforeOpenDoorWeight);
                SaveToSdUtil.savePortDataToSD("InletOpeningActivity feeding before weight:" + beforeOpenDoorWeight, 2);
                EventBus.getDefault().post(new WeightBeforeOpenEvent(beforeOpenDoorWeight));

//                viewBinding.tvInletOpening.setText(R.string.please_click_open_button);
                if (MyApplication.getFirmwareVersionNew()) { // 新pcb板
                    viewBinding.clOpeningFw2.setVisibility(View.VISIBLE);
                    viewBinding.ivOpeningUpDown.setImageResource(R.mipmap.updownarrow);
                    RequestOptions options = new RequestOptions()
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.DATA);
                    Glide.with(mActivity)
                            .asGif()
                            .load(R.mipmap.updownarrow)
                            .apply(options)
                            .into(viewBinding.ivOpeningUpDown);
                } else {

                    viewBinding.clOpening.setVisibility(View.VISIBLE);

                    // 投料口开关时,黄灯闪烁
                    PortControlUtil.getInstance().sendCommands(PortConstants.LED1_TOGGLE_YELLOW);
                    PortControlUtil.getInstance().sendCommands(PortConstants.INLET_UNLOCK);
                }


            }
        }, Integer.parseInt(MyApplication.adminParameterBean.getFeedingBeforeWeightTime()) * Constant.second);


        myHandler = new Handler();
        //每隔1s获取投入口的状态
        myRunnable = new Runnable() {
            @Override
            public void run() {
//                LogUtils.e("getInletStatus："+PortControlUtil.getInstance().getPortStatus().getInletStatus());
                //开门完成，返回门控界面
                if (PortControlUtil.getInstance().getPortStatus().getInletStatus() == 2/*||
                        PortControlUtil.getInstance().getPortStatus().getInletStatus() == 7*/) {
                    HTTPServerUtil.sendHeartBeat2();
                    // 绿灯常亮
                    flag = false;
                    PortControlUtil.getInstance().sendCommands(PortConstants.LED1_ON_GREEN);
                    Intent intent = new Intent(InletOpening2Activity.this, InletControlActivity.class);
                    intent.putExtra("beforeOpenDoorWeight", beforeOpenDoorWeight);
                    startActivity(intent);
                    InletOpening2Activity.this.finish();
                    return;
                }
                //要做的事情，这里再次调用此Runnable对象，以实现每秒实现一次的定时器操作
                myHandler.postDelayed(this, Constant.second);

            }
        };
        //3，使用PostDelayed方法，调用此Runnable对象


    }

    @Override
    protected void initView() {

    }

    protected void initListener() {
        viewBinding.btnOpeningInlet.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_opening_inlet:
                // 投料口开关时,黄灯闪烁
                PortControlUtil.getInstance().sendCommands(PortConstants.LED1_TOGGLE_YELLOW);
                PortControlUtil.getInstance().sendCommands(PortConstants.LOCK1_CONTROL_UNLOCK);
                PortControlUtil.getInstance().sendCommands(PortConstants.INLET_OPEN);
                 new Handler().postDelayed(new Runnable() {
                     @Override
                     public void run() {
                         PortControlUtil.getInstance().sendCommands(PortConstants.LOCK1_CONTROL_LOCK);
                     }
                 },Constant.second);
                break;
        }
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
                if (!flag) {
                    return;
                }
                PortControlUtil.getInstance().sendCommands(PortConstants.LED1_ON_GREEN);
                PortControlUtil.getInstance().sendCommands(PortConstants.INLET_LOCK);
                InletOpening2Activity.this.finish();
            }
        };
        myCountDownTimer.start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myHandler != null) {
            myHandler.removeCallbacks(myRunnable);
            myHandler.removeCallbacksAndMessages(null);
        }
        if (myCountDownTimer != null) {
            myCountDownTimer.cancel();
            myCountDownTimer = null;
        }
    }
}
