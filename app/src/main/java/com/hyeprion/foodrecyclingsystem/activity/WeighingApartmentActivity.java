package com.hyeprion.foodrecyclingsystem.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hyeprion.foodrecyclingsystem.R;
import com.hyeprion.foodrecyclingsystem.base.BaseActivity;
import com.hyeprion.foodrecyclingsystem.base.MyApplication;
import com.hyeprion.foodrecyclingsystem.bean.PutIntoGarbageResponse;
import com.hyeprion.foodrecyclingsystem.databinding.ActivityWeighingApartment2Binding;
import com.hyeprion.foodrecyclingsystem.util.Constant;
import com.hyeprion.foodrecyclingsystem.util.DecimalFormatUtil;
import com.hyeprion.foodrecyclingsystem.util.GreenDaoUtil;
import com.hyeprion.foodrecyclingsystem.util.SaveToSdUtil;
import com.hyeprion.foodrecyclingsystem.util.SharedPreFerUtil;
import com.hyeprion.foodrecyclingsystem.util.port.PortConstants;
import com.hyeprion.foodrecyclingsystem.util.port.PortControlUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

/**
 * Apartment-Must/Shop-None
 * 内部连接称重装置的重量值,称重过程2s
 */
public class WeighingApartmentActivity extends BaseActivity<ActivityWeighingApartment2Binding> {
    //投入垃圾之前的重量
    private float inletWeight;
    // 防护标记
    private boolean isDelayTaskSubmitted = false; // 延迟任务是否已提交
    private boolean isWeightRequestSending = false; // 重量上传请求是否正在执行
    private boolean isJumpCompleted = false; // 是否已跳转至完成页面

    @Override
    protected void initView() {
        MyApplication.soundPlayUtils.loadMedia(Constant.music_4_weighing);

        // 防止延迟任务重复提交
        if (isDelayTaskSubmitted) {
            LogUtils.e("WeighingApartmentActivity: 延迟任务已提交，跳过本次");
            return;
        }
        isDelayTaskSubmitted = true;

        baseHandler.postDelayed(() -> {
            // 获取PortStatus前判空，避免空指针导致逻辑异常
            PortControlUtil portControl = PortControlUtil.getInstance();
            if (portControl == null || portControl.getPortStatus() == null) {
                LogUtils.e("WeighingApartmentActivity: PortControl/PortStatus 未初始化");
                // 跳转至完成页面（异常场景兜底）
                safeJumpToCompletedPage("0", "", "");
                return;
            }

            SaveToSdUtil.savePortDataToSD("WeighingApartmentActivity feeding after weight:" +
                    PortControlUtil.getInstance().getPortStatus().getChooseUseWeighing(), 2);
            float nowWeight = (PortControlUtil.getInstance().getPortStatus().getChooseUseWeighing() -
                    getIntent().getFloatExtra("inletBeforeWeight", 0));
            nowWeight= nowWeight <= 0? 0:nowWeight;
            nowWeight = DecimalFormatUtil.DecimalFormatTwo(nowWeight);
            inletWeight = nowWeight;
            SaveToSdUtil.savePortDataToSD("WeighingApartmentActivity feeding weight:" + nowWeight, 2);
            LogUtils.e("WeighingApartmentActivity feeding after weight:" +
                    PortControlUtil.getInstance().getPortStatus().getChooseUseWeighing() + "\n"
                    + "WeighingApartmentActivity feeding weight:" + nowWeight);

            try {
                MyApplication.adminParameterBean.setInletLimitedTotalAccumulation(DecimalFormatUtil.DecimalFormatThree(
                        (Float.parseFloat(MyApplication.adminParameterBean.getInletLimitedTotalAccumulation()) + nowWeight)) + "");
            } catch (NumberFormatException e) {
                LogUtils.e("累计重量计算失败: " + e.getMessage());
                MyApplication.adminParameterBean.setInletLimitedTotalAccumulation(nowWeight + "");
            }
            SharedPreFerUtil.saveObj(Constant.ADMIN_PARAMETER_FILENAME, Constant.ADMIN_PARAMETER_KEY,
                    JSON.toJSONString(MyApplication.adminParameterBean));

            GreenDaoUtil.getInstance().insertTimesInlet(nowWeight, MyApplication.loginType);

            // 无论什么模式都发送请求到服务器
            sendWeight();
        }, Integer.parseInt(MyApplication.adminParameterBean.getFeedingAfterWeightTime()) * Constant.second);
    }

    /**
     * 安全跳转至称重完成页面（防止重复跳转）
     */
    private void safeJumpToCompletedPage(String nowWeight, String totalWeight, String requestInfo) {
        if (isJumpCompleted) {
            LogUtils.e("WeighingApartmentActivity: 已跳转至完成页面，跳过本次");
            return;
        }
        isJumpCompleted = true;

        Intent intent = new Intent(mActivity, WeighingCumulantApartmentActivity.class);
        intent.putExtra("now_weight", nowWeight);
        intent.putExtra("total_weight", totalWeight);
        intent.putExtra("request_info", requestInfo);
        startActivity(intent);
        mActivity.finish();
    }


    @Override
    protected void initListener() {

    }

    @Override
    public void onClick(View view) {

    }

    private void startMotor() {
        if (PortControlUtil.getInstance().getPortStatus().getObserveDoorStatus() == 0) {
            return;
        }
        PortControlUtil.getInstance().sendCommands(PortControlUtil.getInstance().getHeater1AutomaticCommands());
        PortControlUtil.getInstance().sendCommands(PortControlUtil.getInstance().getHeater2AutomaticCommands());

        PortControlUtil.getInstance().sendCommands(PortConstants.STIR_FORWARD);
        PortControlUtil.getInstance().sendCommands(PortConstants.FAN1_AUTOMATIC);
        PortControlUtil.getInstance().sendCommands(PortControlUtil.getInstance().getHeater1AutomaticCommands());
        PortControlUtil.getInstance().sendCommands(PortControlUtil.getInstance().getHeater2AutomaticCommands());
        PortControlUtil.getInstance().sendCommands(PortControlUtil.getInstance().getDehumidificationAutomaticCommands());
    }

    private void sendWeight() {
        // 防止请求重复发送
        if (isWeightRequestSending) {
            LogUtils.e("WeighingApartmentActivity: 重量上传请求正在执行，跳过本次");
            return;
        }
        isWeightRequestSending = true;

        String weightStr = String.format("%.2f", inletWeight);

        HttpParams httpParams = new HttpParams();
        httpParams.put(Constant.HTTP_PARAMS_TYPE, Constant.HTTP_PARAMS_TYPE_PUT_INTO_RUBBISH);
        httpParams.put(Constant.HTTP_PARAMS_SYSTEM_NO, MyApplication.deviceId);
        httpParams.put(Constant.HTTP_PARAMS_WEIGHT, inletWeight);

        // 为NONE模式添加特殊标识
        if (MyApplication.adminParameterBean.getLoginMode().equals(Constant.NONE)) {
            httpParams.put(Constant.HTTP_PARAMS_LOGIN_TYPE, "NONE");
        } else if (MyApplication.loginType.equals(getString(R.string.login_type_pw))) {
            httpParams.put(Constant.HTTP_PARAMS_LOGIN_ID, MyApplication.loginId);
            httpParams.put(Constant.HTTP_PARAMS_LOGIN_PW, MyApplication.loginPW);
            httpParams.put(Constant.HTTP_PARAMS_LOGIN_TYPE, Constant.HTTP_LOGIN_TYPE_ID);
        } else if (MyApplication.loginType.equals(getString(R.string.login_type_RFID))) {
            httpParams.put(Constant.HTTP_PARAMS_CARD_NO, MyApplication.cardNo);
            httpParams.put(Constant.HTTP_PARAMS_LOGIN_TYPE, Constant.HTTP_LOGIN_TYPE_RFID);
        }

        String requestInfo = httpParams.toString();

        OkGo.<String>post(Constant.IP)
                .params(httpParams)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        netRetryTimes = 0;
                        startMotor();

                        String res = response.body();
                        LogUtils.e(res);
                        String totalWeight = "";
                        try {
                            if (!TextUtils.isEmpty(res)) {
                                PutIntoGarbageResponse putIntoGarbageResponse = JSON.parseObject(res, PutIntoGarbageResponse.class);
                                totalWeight = putIntoGarbageResponse.getWeight() + "";
                            }
                        } catch (JSONException e) {
                            LogUtils.e("JSON解析失败", e);
                            ToastUtils.showShort(R.string.please_retry);
                        } finally {
                            // 安全跳转（确保只执行一次）
                            safeJumpToCompletedPage(weightStr, totalWeight, requestInfo);
                            isWeightRequestSending = false; // 重置请求状态
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        // 安全跳转（确保只执行一次）
                        safeJumpToCompletedPage(String.format("%.2f", inletWeight), "", httpParams.toString());
                        isWeightRequestSending = false; // 重置请求状态
                    }
                });
    }

    // 页面销毁时清理资源
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 移除未执行的延迟任务，避免页面销毁后触发逻辑
        if (baseHandler != null) {
            baseHandler.removeCallbacksAndMessages(null);
        }
        // 重置防护标记
        isDelayTaskSubmitted = false;
        isWeightRequestSending = false;
        isJumpCompleted = false;
    }
}