package com.hyeprion.foodrecyclingsystem.activity;

import android.content.Intent;
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
            // 关门时重量 - 开门前重量 = 本次投入量

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
            // 每次投入量的累积
            MyApplication.adminParameterBean.setInletLimitedTotalAccumulation(DecimalFormatUtil.DecimalFormatThree(
                    (Float.parseFloat(MyApplication.adminParameterBean.getInletLimitedTotalAccumulation()) + nowWeight)) + "");
            SharedPreFerUtil.saveObj(Constant.ADMIN_PARAMETER_FILENAME, Constant.ADMIN_PARAMETER_KEY,
                    JSON.toJSONString(MyApplication.adminParameterBean));

            GreenDaoUtil.getInstance().insertTimesInlet(nowWeight, MyApplication.loginType);

            if (MyApplication.adminParameterBean.getLoginMode().equals(Constant.NONE)) {
                startMotor();

                Intent intent = new Intent(mActivity, WeighingCumulantApartmentActivity.class);
                intent.putExtra("now_weight", nowWeight + "");
                startActivity(intent);
                mActivity.finish();
                return;
            }
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
        PortControlUtil.getInstance().sendCommands(PortControlUtil.getInstance().getHeater1AutomaticCommands()); //加热1自动
        PortControlUtil.getInstance().sendCommands(PortControlUtil.getInstance().getHeater2AutomaticCommands()); //加热2自动


        // 打开搅拌电机命令
        PortControlUtil.getInstance().sendCommands(PortConstants.STIR_FORWARD);
        PortControlUtil.getInstance().sendCommands(PortConstants.FAN1_AUTOMATIC); //风扇启动
        PortControlUtil.getInstance().sendCommands(PortControlUtil.getInstance().getHeater1AutomaticCommands()); //加热1自动
        PortControlUtil.getInstance().sendCommands(PortControlUtil.getInstance().getHeater2AutomaticCommands()); //加热2自动
        PortControlUtil.getInstance().sendCommands(PortControlUtil.getInstance().getDehumidificationAutomaticCommands()); //烘干机自动
        // 绿灯常亮

    }

    private void sendWeight() {
        HttpParams httpParams = new HttpParams();
        httpParams.put(Constant.HTTP_PARAMS_TYPE, Constant.HTTP_PARAMS_TYPE_PUT_INTO_RUBBISH);
        httpParams.put(Constant.HTTP_PARAMS_SYSTEM_NO, MyApplication.deviceId);// 设备编码
        httpParams.put(Constant.HTTP_PARAMS_WEIGHT, inletWeight); // 本次重量
        if (MyApplication.loginType.equals(getString(R.string.login_type_pw))) {
            httpParams.put(Constant.HTTP_PARAMS_LOGIN_ID, MyApplication.loginId);
            httpParams.put(Constant.HTTP_PARAMS_LOGIN_PW, MyApplication.loginPW);
            httpParams.put(Constant.HTTP_PARAMS_LOGIN_TYPE, Constant.HTTP_LOGIN_TYPE_ID);
        } else if (MyApplication.loginType.equals(getString(R.string.login_type_RFID))) {
            httpParams.put(Constant.HTTP_PARAMS_CARD_NO, MyApplication.cardNo);
            httpParams.put(Constant.HTTP_PARAMS_LOGIN_TYPE, Constant.HTTP_LOGIN_TYPE_RFID);
        }

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
                            //正常：”weight”:累积量,”systemNo”:”设备编码”,”loginId”:”几幢几号”
                            Intent intent = new Intent(mActivity, WeighingCumulantApartmentActivity.class);
                            intent.putExtra("now_weight", inletWeight + "");
                            intent.putExtra("total_weight", putIntoGarbageResponse.getWeight() + "");
                            startActivity(intent);
                            mActivity.finish();
                        } catch (JSONException jsonException) {
                            ToastUtils.showShort(R.string.please_retry);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        sendWeight();
                       /* netRetryTimes++;
                        if (netRetryTimes < 3) {
                            sendWeight();
                        } else {
                            netRetryTimes = 0;
                            runOnUiThread(() -> DialogManage.getDialog2SDismiss(WeighingApartmentActivity.this, getString(R.string.please_check_net_and_retry)).show());
                            new Handler(WeighingApartmentActivity.this.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    WeighingApartmentActivity.this.finish();
                                }
                            },3*Constant.second);
                        }*/
                    }
                });
    }
}
