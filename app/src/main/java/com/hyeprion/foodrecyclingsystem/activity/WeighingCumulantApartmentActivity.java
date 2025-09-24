package com.hyeprion.foodrecyclingsystem.activity;

import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.hyeprion.foodrecyclingsystem.R;
import com.hyeprion.foodrecyclingsystem.base.BaseActivity;
import com.hyeprion.foodrecyclingsystem.base.MyApplication;
import com.hyeprion.foodrecyclingsystem.databinding.ActivityWeighingCumulantApartment2Binding;
import com.hyeprion.foodrecyclingsystem.util.Constant;
import com.hyeprion.foodrecyclingsystem.util.DecimalFormatUtil;
import com.hyeprion.foodrecyclingsystem.util.GreenDaoUtil;
import com.hyeprion.foodrecyclingsystem.util.countdowntimer.MyCountDownTimer;

import androidx.annotation.Nullable;

/**
 * Apartment-Must/Shop-None
 * 称重完成显示页面
 * 重量详细，显示当前投入量和月投入总量
 * from {@link WeighingApartmentActivity}
 */
public class WeighingCumulantApartmentActivity extends BaseActivity<ActivityWeighingCumulantApartment2Binding> {
    private String nowWeight;
    private String totalWeight;
    private MyCountDownTimer myCountDownTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.soundPlayUtils.loadMedia(Constant.music_5_weigh_finish);
    }

    @Override
    protected void initView() {
        if (MyApplication.adminParameterBean.getWeighingUnit() == Constant.WEIGHING_UNIT_POUND){
            viewBinding.iv1.setImageResource(R.mipmap.weight_lbs);
            viewBinding.iv2.setImageResource(R.mipmap.weight_lbs);
        }


        nowWeight = getIntent().getStringExtra("now_weight") + "";
        nowWeight = MyApplication.adminParameterBean.isWeightDecimalShow()?nowWeight:
                String.valueOf(DecimalFormatUtil.DecimalFormatInt(Float.parseFloat(nowWeight)));
        if (Float.parseFloat(nowWeight) <= 0) {
            nowWeight = "0";
        }
        totalWeight = getIntent().getStringExtra("total_weight");


        if (totalWeight == null || totalWeight.equals("")) {
            // TODO: 2023/2/28 从数据库查询本月总投入重量
//            totalWeight = "252";
            totalWeight = GreenDaoUtil.getInstance().getMonthInletWeight();
            viewBinding.groupWeightingCumulant.setVisibility(View.GONE);
        }
        totalWeight = String.valueOf(MyApplication.adminParameterBean.isWeightDecimalShow()?
                DecimalFormatUtil.DecimalFormatThree(Float.parseFloat(totalWeight)):
                DecimalFormatUtil.DecimalFormatInt(Float.parseFloat(totalWeight)));
        viewBinding.tvWeighingThisTime.setText(/*String.format(getString(R.string.this_time_plate_weighing),*/ nowWeight + "");
        LogUtils.e("total_weight" + getIntent().getStringExtra("total_weight"));
        viewBinding.tvWeightingCumulant.setText(/*String.format(
                getString(R.string.monthly_cumulative_placement),*/totalWeight + "");
        viewBinding.tvCountdown.setText(String.format(getString(R.string.countdown), "10"));
        countDown30S();
    }

    @Override
    protected void initListener() {
//        viewBinding.btnConfirm.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View view) {
//        if (view.getId() == R.id.btn_confirm) {
//            this.finish();
//        }
    }


    /**
     * 30S倒计时，结束关闭页面跳转 主界面{@link LoginActivity}
     */
    private void countDown30S() {
        // 倒计时总时长
        long millisInFuture = 10 * Constant.second;
        myCountDownTimer = new MyCountDownTimer(millisInFuture, Constant.second) {
            @Override
            public void onTick(long millisUntilFinished) {
                viewBinding.tvCountdown.setText(String.format(getString(R.string.countdown), (millisUntilFinished / Constant.second) + ""));
            }

            @Override
            public void onFinish() {
                viewBinding.tvCountdown.setText(String.format(getString(R.string.countdown), "1"));
                WeighingCumulantApartmentActivity.this.finish();
            }
        };
        myCountDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myCountDownTimer != null) {
            myCountDownTimer.cancel();
            myCountDownTimer = null;
        }
    }
}
