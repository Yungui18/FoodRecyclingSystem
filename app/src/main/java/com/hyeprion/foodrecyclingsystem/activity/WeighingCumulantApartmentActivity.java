package com.hyeprion.foodrecyclingsystem.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
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

        // 优化nowWeight计算
        String rawNowWeight = getIntent().getStringExtra("now_weight");
        // 处理空值和格式化
        if (TextUtils.isEmpty(rawNowWeight)) {
            nowWeight = "0";
        } else {
            float nowWeightValue = Float.parseFloat(rawNowWeight);
            nowWeightValue = Math.max(nowWeightValue, 0); // 确保非负
            // 一次性完成格式化
            nowWeight = MyApplication.adminParameterBean.isWeightDecimalShow()
                    ? String.valueOf(nowWeightValue)
                    : String.valueOf(DecimalFormatUtil.DecimalFormatInt(nowWeightValue));
        }

        String requestInfo = getIntent().getStringExtra("request_info");

        // 优化totalWeight计算
        totalWeight = getIntent().getStringExtra("total_weight");
        if (TextUtils.isEmpty(totalWeight)) {
            totalWeight = GreenDaoUtil.getInstance().getMonthInletWeight();
            viewBinding.groupWeightingCumulant.setVisibility(View.GONE);
        }
        // 一次性完成格式化
        float totalWeightValue = Float.parseFloat(totalWeight);
        totalWeight = MyApplication.adminParameterBean.isWeightDecimalShow()
                ? String.valueOf(DecimalFormatUtil.DecimalFormatThree(totalWeightValue))
                : String.valueOf(DecimalFormatUtil.DecimalFormatInt(totalWeightValue));

        viewBinding.tvWeighingThisTime.setText(nowWeight + "");
        LogUtils.e("total_weight" + getIntent().getStringExtra("total_weight"));
        viewBinding.tvWeightingCumulant.setText(totalWeight + "");
        viewBinding.tvCountdown.setText(String.format(getString(R.string.countdown), "9"));
        countDown30S();
    }

    @Override
    protected void initListener() {
    }

    @Override
    public void onClick(View view) {
    }

    private void countDown30S() {
        long millisInFuture = 10 * Constant.second;
        myCountDownTimer = new MyCountDownTimer(millisInFuture, Constant.second) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / Constant.second - 1;
                viewBinding.tvCountdown.setText(String.format(getString(R.string.countdown), seconds + ""));
            }

            @Override
            public void onFinish() {
                viewBinding.tvCountdown.setText(String.format(getString(R.string.countdown), "0"));
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