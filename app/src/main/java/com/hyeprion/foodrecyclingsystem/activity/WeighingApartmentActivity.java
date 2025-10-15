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

    @Override
    protected void initView() {
        MyApplication.soundPlayUtils.loadMedia(Constant.music_4_weighing);
        baseHandler.postDelayed(() -> {
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
            MyApplication.adminParameterBean.setInletLimitedTotalAccumulation(DecimalFormatUtil.DecimalFormatThree(
                    (Float.parseFloat(MyApplication.adminParameterBean.getInletLimitedTotalAccumulation()) + nowWeight)) + "");
            SharedPreFerUtil.saveObj(Constant.ADMIN_PARAMETER_FILENAME, Constant.ADMIN_PARAMETER_KEY,
                    JSON.toJSONString(MyApplication.adminParameterBean));

            GreenDaoUtil.getInstance().insertTimesInlet(nowWeight, MyApplication.loginType);

            // 无论什么模式都发送请求到服务器
            sendWeight();
        }, Integer.parseInt(MyApplication.adminParameterBean.getFeedingAfterWeightTime()) * Constant.second);
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
                        try {
                            PutIntoGarbageResponse putIntoGarbageResponse = JSON.parseObject(res, PutIntoGarbageResponse.class);
                            Intent intent = new Intent(mActivity, WeighingCumulantApartmentActivity.class);
                            intent.putExtra("now_weight", weightStr);
                            intent.putExtra("total_weight", putIntoGarbageResponse.getWeight() + "");
                            intent.putExtra("request_info", requestInfo);
                            startActivity(intent);
                            mActivity.finish();
                        } catch (JSONException e) {
                            LogUtils.e("JSON解析失败", e);
                            ToastUtils.showShort(R.string.please_retry);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Intent intent = new Intent(mActivity, WeighingCumulantApartmentActivity.class);
                        intent.putExtra("now_weight", weightStr);
                        intent.putExtra("request_info", requestInfo);
                        startActivity(intent);
                        mActivity.finish();
                    }
                });
    }
}