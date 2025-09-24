package com.hyeprion.foodrecyclingsystem.activity;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.LanguageUtils;
import com.hyeprion.foodrecyclingsystem.R;
import com.hyeprion.foodrecyclingsystem.base.BaseActivity;
import com.hyeprion.foodrecyclingsystem.base.MyApplication;
import com.hyeprion.foodrecyclingsystem.bean.AdminParameterBean;
import com.hyeprion.foodrecyclingsystem.databinding.ActivityAdminParameter2Binding;
import com.hyeprion.foodrecyclingsystem.dialog.DialogChooseTemperature;
import com.hyeprion.foodrecyclingsystem.util.Constant;
import com.hyeprion.foodrecyclingsystem.util.SharedPreFerUtil;
import com.hyeprion.foodrecyclingsystem.util.SwitchLanguageUtil;
import com.hyeprion.foodrecyclingsystem.util.port.PortControlUtil;

import java.util.List;

/**
 * 管理员页面-参数页面
 * from {@link AdminActivity}
 */
public class AdminParameterActivity extends BaseActivity<ActivityAdminParameter2Binding> implements
        RadioGroup.OnCheckedChangeListener, DialogChooseTemperature.CallBackListener, CompoundButton.OnCheckedChangeListener {
    /**
     * 当前语言
     * <p>
     * <li>中文{@link com.hyeprion.foodrecyclingsystem.util.Constant#CHINESE}
     * <li>日文{@link com.hyeprion.foodrecyclingsystem.util.Constant#KOREAN}
     * <li>韩语{@link com.hyeprion.foodrecyclingsystem.util.Constant#JAPANESE}
     * <li>英语{@link com.hyeprion.foodrecyclingsystem.util.Constant#ENGLISH}
     * </p>
     */
    private String language;
    /**
     * 当前app模式
     * <p>
     * <li>apartment {@link com.hyeprion.foodrecyclingsystem.util.Constant#APARTMENT}
     * <li>shop {@link com.hyeprion.foodrecyclingsystem.util.Constant#SHOP}
     * </p>
     */
//    private String appMode;
    /**
     * 称重模式
     * <p>
     * <li>外部{@link com.hyeprion.foodrecyclingsystem.util.Constant#HAVE}
     * <li>无{@link com.hyeprion.foodrecyclingsystem.util.Constant#NONE}
     * </p>
     */
    private String weighingMode;
    /**
     * 设备报警总重量（通过心跳中净重总值进行判断，用于故障信息里面的红灯） KG
     */
    private String inletLimitedAlarmTotal;
    /**
     * 总限定投入量（根据每次差值进行累计，用于提醒排料，超过此值黄灯2亮，后面加一个重置按钮，点击后重新累计差值）
     */
    private String inletLimitedTotal;
    /**
     * 日（24H）限定投入量（根据每次差值进行累计，累计24小时总重量不得超过此值，用处1：超过此值黄灯2亮；用处2：:计算主界面24H剩余可投入量）
     */
    private String inletLimited24H;
    /**
     * 称重报警 时间 单位分钟
     */
    private String weighAlarmTime;
    /**
     * 称重报警重量 单位KG
     */
    private String weighAlarmWeight;

    /**
     * 是否需要登录模块
     * <p>
     * <li>需要，有{@link com.hyeprion.foodrecyclingsystem.util.Constant#HAVE}
     * <li>不需要，无{@link com.hyeprion.foodrecyclingsystem.util.Constant#NONE}
     * </p>
     */
    private String loginMode;
    /**
     * 投入口模式
     * <p>
     * <li>自动{@link com.hyeprion.foodrecyclingsystem.util.Constant#AUTOMATION}
     * <li>手动{@link com.hyeprion.foodrecyclingsystem.util.Constant#MANUAL}
     * </p>
     */
    private String inletMode;
    /**
     * 加热板设置，目标温度和浮动温度，加热温度=目标-浮动 停止加热温度=目标+浮动  报警温度
     */
    private AdminParameterBean.HeartingTemperature heartingTemperature;
    /**
     * 除湿设置，目标湿度和浮动湿度  除湿湿度= 目标+浮动  停止湿度=目标-浮动
     */
    private AdminParameterBean.HumiditySetting humiditySetting;
    /**
     * 投入口超时时间 单位S
     */
    private String inletTimeoutTime;
    /**
     * 投入口低速电压
     */
    private String inletLowSpeedVoltage;
    /**
     * 风扇湿度设置，包含风扇档位及对应的湿度、电压 分为1,2,3挡
     */
    private List<AdminParameterBean.FanHumiditySettingBean> fanHumiditySettingBeanList;

    /**
     * 搅拌运行时间 normal
     */
    private String stirRunTimeNormal;
    /**
     * 搅拌间隔时间 normal
     */
    private String stirIntervalTimeNormal;
    /**
     * 搅拌运行时间 节电模式 power saving
     */
    private String stirRunTimePowerSaving;
    /**
     * 搅拌间隔时间 节电模式 power saving
     */
    private String stirIntervalPowerSaving;

    /**
     * 加热温度设置（节电模式 power saving）目标温度和浮动温度，加热温度=目标-浮动 停止加热温度=目标+浮动
     */
    private AdminParameterBean.HeartingTemperaturePowerSaving heartingTemperaturePowerSaving;

    private int type = -1; // 选择器类型

    private String hexHeater; // 自动加热命令未添加CRC
    private String hexDehumidification; // 自动除湿命令未添加CRC
    private String weighingUnit = "";

    @Override
    protected void initView() {
        language = MyApplication.adminParameterBean.getLanguage();
//        appMode = MyApplication.adminParameterBean.getAppMode();
        weighingMode = MyApplication.adminParameterBean.getWeighingMode();
        inletLimitedAlarmTotal = MyApplication.adminParameterBean.getInletLimitedAlarmTotalMax();
        inletLimitedTotal = MyApplication.adminParameterBean.getInletLimitedTotal();
        inletLimited24H = MyApplication.adminParameterBean.getInletLimited24H();
        weighAlarmTime = MyApplication.adminParameterBean.getWeighAlarmTime();
        weighAlarmWeight = MyApplication.adminParameterBean.getWeighAlarmWeight();
        loginMode = MyApplication.adminParameterBean.getLoginMode();
        inletMode = MyApplication.adminParameterBean.getInletMode();
        heartingTemperature = MyApplication.adminParameterBean.getHeartingTemperature();
        humiditySetting = MyApplication.adminParameterBean.getHumiditySetting();
        inletTimeoutTime = MyApplication.adminParameterBean.getInletTimeoutTime();
        inletLowSpeedVoltage = MyApplication.adminParameterBean.getInletLowSpeedVoltage();
        fanHumiditySettingBeanList = MyApplication.adminParameterBean.getFanHumiditySettingBeanList();
        stirRunTimeNormal = MyApplication.adminParameterBean.getStirRunTimeNormal();
        stirIntervalTimeNormal = MyApplication.adminParameterBean.getStirIntervalTimeNormal();
        stirRunTimePowerSaving = MyApplication.adminParameterBean.getStirRunTimePowerSaving();
        stirIntervalPowerSaving = MyApplication.adminParameterBean.getStirIntervalPowerSaving();
        heartingTemperaturePowerSaving = MyApplication.adminParameterBean.getHeartingTemperaturePowerSaving();

        if (MyApplication.adminParameterBean.getWeighingUnit() == Constant.WEIGHING_UNIT_KG){
            viewBinding.rb24Kg.setChecked(true);
            weighingUnit = getString(R.string.kg_2);
        }else if (MyApplication.adminParameterBean.getWeighingUnit() == Constant.WEIGHING_UNIT_POUND){
            weighingUnit = getString(R.string.LB);
            viewBinding.rb24Lb.setChecked(true);
        }

        // 语言设置
        if (language.equals(Constant.CHINESE)) {
            viewBinding.rb1Chinese.setChecked(true);
        } else if (language.equals(Constant.KOREAN)) {
            viewBinding.rb1Korean.setChecked(true);
        } else if (language.equals(Constant.JAPANESE)) {
            viewBinding.rb1Japanese.setChecked(true);
        } else if (language.equals(Constant.ENGLISH)) {
            viewBinding.rb1English.setChecked(true);
        }

        // 称重模式
        if (weighingMode.equals(Constant.HAVE)) {
            viewBinding.rb3Have.setChecked(true);
        } else if (weighingMode.equals(Constant.NONE)) {
            viewBinding.rb3None.setChecked(true);
        }

        setWeighingUnit();

        // 登录设置
        if (loginMode.equals(Constant.HAVE)) {
            viewBinding.rb4Have.setChecked(true);
        } else if (loginMode.equals(Constant.NONE)) {
            viewBinding.rb4None.setChecked(true);
        } else if (loginMode.equals(Constant.BINDING)) {
            viewBinding.rb4Binding.setChecked(true);
        }

        // 投入口设置
        if (inletMode.equals(Constant.AUTOMATION)) {
            viewBinding.rb5Automation.setChecked(true);
            viewBinding.groupInletQuicklyCloseTime.setVisibility(View.VISIBLE);
            viewBinding.groupInletSetting.setVisibility(View.VISIBLE);
//            viewBinding.rb4None.setVisibility(View.GONE);
        } else if (inletMode.equals(Constant.MANUAL)) {
            viewBinding.rb5Manual.setChecked(true);
            viewBinding.groupInletQuicklyCloseTime.setVisibility(View.GONE);
            viewBinding.groupInletSetting.setVisibility(View.GONE);
            viewBinding.rb4Have.setVisibility(View.GONE);
        }

        if (loginMode.equals(Constant.HAVE) && inletMode.equals(Constant.MANUAL)) {
            viewBinding.btn4Unbind.setVisibility(View.VISIBLE);
        }

        // 加热板温度设置 1 前
        viewBinding.tv6TargetTemperatureNum.setText(heartingTemperature.getTargetTem() + "℃");
        viewBinding.tv6FloatTemperatureNum.setText(heartingTemperature.getFloatTem() + "℃");

        // 加热板设置 2 后
        viewBinding.tv25TargetTemperatureNum2.setText(heartingTemperature.getTargetTem2() + "℃");
        viewBinding.tv25FloatTemperatureNum2.setText(heartingTemperature.getFloatTem2() + "℃");

        // 加热报警设置
        viewBinding.tv19MaxAlarmTemperatureNum.setText(heartingTemperature.getMaxTemp() + "℃");
        viewBinding.tv19MinAlarmTemperatureNum.setText(heartingTemperature.getMinTemp() + "℃");
        viewBinding.tv19MinAlarmTempIgnoreTimeNum.setText(heartingTemperature.getIgnoreTime() + "min");

        // 除湿 烘干设置
        viewBinding.tv8TargetHumidityNum.setText(humiditySetting.getTargetHumidity() + "%");
        viewBinding.tv8FloatHumidityNum.setText(humiditySetting.getFloatHumidity() + "%");

        // 投入口开门关门超时时间、慢速电压、开门电压、关门电压
        viewBinding.tv10InletTimeoutNum.setText(inletTimeoutTime + "s");
        viewBinding.tv10InletVoltageNum.setText(inletLowSpeedVoltage + "V");
        viewBinding.tv10InletOpenVoltageNum.setText(MyApplication.adminParameterBean.getInletOpenVoltage() + "V");
        viewBinding.tv10InletCloseVoltageNum.setText(MyApplication.adminParameterBean.getInletCloseVoltage() + "V");


//        viewBinding.tv11FanGearNum.setText(fanHumiditySettingBeanList.get(0).getFanHumidityGear() + "");
        viewBinding.tv11FanGearNum.setText("3");
        viewBinding.tv11FanHumidityNum.setText(fanHumiditySettingBeanList.get(2).getFanHumidityNum() + "%");
        viewBinding.tv11FanVoltageNum.setText(fanHumiditySettingBeanList.get(2).getFanHumidityVoltage() + "V");

        // 搅拌电机正转 运行和间隔时间  normal
        viewBinding.tv14RunTimeNormalNum.setText(stirRunTimeNormal + "min");
        viewBinding.tv14IntervalTimeNormalNum.setText(stirIntervalTimeNormal + "min");

        viewBinding.tv15EnterPowerSavingTimeNum.setText(MyApplication.adminParameterBean.getEnterPowerSavingTime() + "h");

        viewBinding.tv16RunTimePowerSavingNum.setText(stirRunTimePowerSaving + "min");
        viewBinding.tv16IntervalTimePowerSavingNum.setText(stirIntervalPowerSaving + "min");

        // 加热板温度省点模式设置 1 前
        viewBinding.tv17TargetTemperaturePowerSavingNum.setText(heartingTemperaturePowerSaving.getTargetTem() + "℃");
        viewBinding.tv17FloatTemperaturePowerSavingNum.setText(heartingTemperaturePowerSaving.getFloatTem() + "℃");

        // 加热板温度省点模式设置 2 后
        viewBinding.tv26TargetTemperaturePowerSavingNum2.setText(heartingTemperaturePowerSaving.getTargetTem2() + "℃");
        viewBinding.tv26FloatTemperaturePowerSavingNum2.setText(heartingTemperaturePowerSaving.getFloatTem2() + "℃");

        viewBinding.tv18FeedingBeforeWeightTimeNum.setText(MyApplication.adminParameterBean.getFeedingBeforeWeightTime() + "s");
        viewBinding.tv18FeedingAfterWeightTimeNum.setText(MyApplication.adminParameterBean.getFeedingAfterWeightTime() + "s");

        viewBinding.tv20ScreensaverTimeNum.setText(MyApplication.adminParameterBean.getScreensaverTime() + "min");

        viewBinding.tv21FanRunTimePowerSavingNum.setText(MyApplication.adminParameterBean.getFanRunTimePowerSaving() + "min");
        viewBinding.tv21FanIntervalTimePowerSavingNum.setText(MyApplication.adminParameterBean.getFanIntervalPowerSaving() + "min");

        viewBinding.sc22ObservePortAlarm.setChecked(MyApplication.adminParameterBean.isObservePortAlarm());

//        if (MyApplication.getFirmwareVersionNew()) { // 新pcb板
//        viewBinding.groupInletQuicklyCloseTime.setVisibility(View.VISIBLE);
        viewBinding.tv10InletQuicklyCloseTimeNum.setText(MyApplication.adminParameterBean.getInletQuicklyCloseTime() + "ms");

        viewBinding.groupWeighingSourceSetting.setVisibility(View.VISIBLE);
        // 称重源设置
        if (MyApplication.adminParameterBean.getWeighingSourceSetting() == Constant.WEIGHING_SOURCE_485) {
            viewBinding.rb23485.setChecked(true);
        } else if (MyApplication.adminParameterBean.getWeighingSourceSetting() == Constant.WEIGHING_SOURCE_LOADCELL1) {
            viewBinding.rb23Loadcell1.setChecked(true);
        } else if (MyApplication.adminParameterBean.getWeighingSourceSetting() == Constant.WEIGHING_SOURCE_LOADCELL2) {
            viewBinding.rb23Loadcell2.setChecked(true);
        }
//        }

        // 设置湿度报警时间
        viewBinding.tv27HumidityAlarmTimeNum.setText(
                MyApplication.adminParameterBean.getHumidityAlarmTime() + "min");
        // 设置湿度报警对应的湿度
        viewBinding.tv27HumidityAlarmHumidityNum.setText(
                MyApplication.adminParameterBean.getHumidityAlarmHumidity() + "%");
        // 设置湿度报警开关
        viewBinding.sc27HumidityAlarmSwitch.setChecked(MyApplication.adminParameterBean.isHumidityAlarmSwitch());

        //  搅拌电机反转 运行、间隔时间 normal
        viewBinding.tv28ReverseRunTimeNormalNum.setText(
                MyApplication.adminParameterBean.getStirReverseRunTimeNormal() + "min");
        viewBinding.tv28ReverseIntervalTimeNormalNum.setText(
                MyApplication.adminParameterBean.getStirReverseIntervalTimeNormal() + "min");
        //  搅拌电机反转 运行、间隔时间 PowerSaving
        viewBinding.tv29ReverseRunTimePowerSavingNum.setText(
                MyApplication.adminParameterBean.getStirReverseRunTimePowerSaving() + "min");
        viewBinding.tv29ReverseIntervalTimePowerSavingNum.setText(
                MyApplication.adminParameterBean.getStirReverseIntervalPowerSaving() + "min");
        // 杀菌模式 温度、时间
        viewBinding.tv30SterilizationModeTempNum.setText(MyApplication.adminParameterBean.getSterilizationModeTemp()+"℃");
        viewBinding.tv30SterilizationModeTimeNum.setText(MyApplication.adminParameterBean.getSterilizationModeTime()+"min");

        // 重量小数显示
        if (MyApplication.adminParameterBean.isWeightDecimalShow()){
            viewBinding.rb31Yes.setChecked(true);
        }else {
            viewBinding.rb31No.setChecked(true);
        }
    }

    /**
     * 设置和重量相关的控件
     */
    private void setWeighingUnit(){
        // 设备报警总重量最大值
        viewBinding.tv7AlarmMaxWeightNum.setText(inletLimitedAlarmTotal + weighingUnit);
        // 设备报警总重量最小值
        viewBinding.tv7AlarmMinWeightNum.setText(MyApplication.adminParameterBean.getInletLimitedAlarmTotalMin() + weighingUnit);
        // 总限定投入量
        viewBinding.tv13TotalInletLimitedNum.setText(inletLimitedTotal + weighingUnit);
        // 日（24h）限定投入量
        viewBinding.tv1224HInletLimitedNum.setText(inletLimited24H + weighingUnit);

        // 称重报警时间 重量
        viewBinding.tv9WeighAlarmTimeNum.setText(weighAlarmTime + "min");
        viewBinding.tv9WeighAlarmWeightNum.setText(weighAlarmWeight + weighingUnit);
    }

    @Override
    protected void initListener() {
        viewBinding.btnBack.setOnClickListener(this);

        viewBinding.rg1Language.setOnCheckedChangeListener(this);
//        viewBinding.rg2AppMode.setOnCheckedChangeListener(this);
        viewBinding.rg3WeighingMode.setOnCheckedChangeListener(this);
        viewBinding.rg4LoginSetting.setOnCheckedChangeListener(this);
        viewBinding.btn4Unbind.setOnClickListener(this);
        viewBinding.rg5InletMode.setOnCheckedChangeListener(this);
        viewBinding.sc22ObservePortAlarm.setOnCheckedChangeListener(this);
        viewBinding.rg23WeighingSourceSetting.setOnCheckedChangeListener(this);
        viewBinding.rg24WeighingUnit.setOnCheckedChangeListener(this);

        viewBinding.cl6TargetTemperature.setOnClickListener(this);
        viewBinding.cl6FloatTemperature.setOnClickListener(this);

        viewBinding.cl7AlarmMaxWeight.setOnClickListener(this);
        viewBinding.cl7AlarmMinWeight.setOnClickListener(this);
        viewBinding.cl8TargetHumidity.setOnClickListener(this);
        viewBinding.cl8FloatHumidity.setOnClickListener(this);
        viewBinding.cl9WeighAlarmTime.setOnClickListener(this);
        viewBinding.cl9WeighAlarmWeight.setOnClickListener(this);
        viewBinding.cl10InletTimeout.setOnClickListener(this);
        viewBinding.cl10InletVoltage.setOnClickListener(this);
        viewBinding.cl10InletOpenVoltage.setOnClickListener(this);
        viewBinding.cl10InletCloseVoltage.setOnClickListener(this);
        viewBinding.cl10InletQuicklyCloseTime.setOnClickListener(this);
//        viewBinding.cl11FanGear.setOnClickListener(this);
        viewBinding.cl11FanHumidity.setOnClickListener(this);
        viewBinding.cl11FanVoltage.setOnClickListener(this);
        viewBinding.cl1224HInletLimited.setOnClickListener(this);
        viewBinding.btn12Reset24HInletLimited.setOnClickListener(this);
        viewBinding.cl13TotalInletLimited.setOnClickListener(this);
        viewBinding.btn13ResetTotalInletLimited.setOnClickListener(this);
        viewBinding.cl14RunTimeNormal.setOnClickListener(this);
        viewBinding.cl14IntervalTimeNormal.setOnClickListener(this);
        viewBinding.cl15EnterPowerSavingTime.setOnClickListener(this);
        viewBinding.cl16RunTimePowerSaving.setOnClickListener(this);
        viewBinding.cl16IntervalTimePowerSaving.setOnClickListener(this);
        viewBinding.cl17TargetTemperaturePowerSaving.setOnClickListener(this);
        viewBinding.cl17FloatTemperaturePowerSaving.setOnClickListener(this);
        viewBinding.cl18FeedingBeforeWeightTime.setOnClickListener(this);
        viewBinding.cl18FeedingAfterWeightTime.setOnClickListener(this);
        viewBinding.cl19MaxAlarmTemperature.setOnClickListener(this);
        viewBinding.cl19MinAlarmTemperature.setOnClickListener(this);
        viewBinding.cl19MinAlarmTempIgnoreTime.setOnClickListener(this);
        viewBinding.cl20ScreensaverTime.setOnClickListener(this);
        viewBinding.cl21FanRunTimePowerSaving.setOnClickListener(this);
        viewBinding.cl21FanIntervalTimePowerSaving.setOnClickListener(this);

        viewBinding.cl25TargetTemperature2.setOnClickListener(this);
        viewBinding.cl25FloatTemperature2.setOnClickListener(this);

        viewBinding.cl26TargetTemperaturePowerSaving2.setOnClickListener(this);
        viewBinding.cl26FloatTemperaturePowerSaving2.setOnClickListener(this);

        // 湿度报警 时间和湿度 设置
        viewBinding.cl27HumidityAlarmTime.setOnClickListener(this);
        viewBinding.cl27HumidityAlarmHumidity.setOnClickListener(this);
        viewBinding.sc27HumidityAlarmSwitch.setOnCheckedChangeListener(this);

        // 反转 搅拌运行、间隔时间 normal
        viewBinding.cl28ReverseRunTimeNormal.setOnClickListener(this);
        viewBinding.cl28ReverseIntervalTimeNormal.setOnClickListener(this);

        // 反转 搅拌运行、间隔时间 PowerSaving
        viewBinding.cl29ReverseRunTimePowerSaving.setOnClickListener(this);
        viewBinding.cl29ReverseIntervalTimePowerSaving.setOnClickListener(this);

        // 杀菌模式 温度、时间
        viewBinding.cl30SterilizationModeTemp.setOnClickListener(this);
        viewBinding.cl30SterilizationModeTime.setOnClickListener(this);

        viewBinding.rg31WeightDecimalShow.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
//                MyApplication.adminParameterBean.setAppMode(appMode);
                MyApplication.adminParameterBean.setWeighingMode(weighingMode);
                MyApplication.adminParameterBean.setLoginMode(loginMode);
                MyApplication.adminParameterBean.setInletMode(inletMode);
//                MyApplication.adminParameterBean.setInletLimitedAlarmTotal(inletLimitedAlarmTotal);
//                MyApplication.adminParameterBean.setWeighAlarmTime(weighAlarmTime);
//                MyApplication.adminParameterBean.setWeighAlarmWeight(weighAlarmWeight);



                if (loginMode.equals(Constant.NONE)) {// 登录模块取消

                }
                // 如果切换了语言，则更换应用语言
                if (!language.equals(MyApplication.adminParameterBean.getLanguage())) {
                    MyApplication.adminParameterBean.setLanguage(language);
                    LanguageUtils.applyLanguage(SwitchLanguageUtil.getLocale(language,this), true);
                }
                SharedPreFerUtil.saveObj(Constant.ADMIN_PARAMETER_FILENAME, Constant.ADMIN_PARAMETER_KEY,
                        JSON.toJSONString(MyApplication.adminParameterBean));
                this.finish();
                break;
            case R.id.btn4_unbind:
                MyApplication.adminParameterBean.setLoginId("");
                MyApplication.adminParameterBean.setLoginPW("");
                SharedPreFerUtil.saveObj(Constant.ADMIN_PARAMETER_FILENAME, Constant.ADMIN_PARAMETER_KEY,
                        JSON.toJSONString(MyApplication.adminParameterBean));
                break;
            case R.id.cl6_target_temperature: // 目标温度设置 1 前
                type = 1;
                showDialog(viewBinding.tv6TargetTemperatureNum.getText().toString().split("℃")[0]);
                break;
            case R.id.cl6_float_temperature: // 浮动温度设置 1 前
                type = 2;
                showDialog(viewBinding.tv6FloatTemperatureNum.getText().toString().split("℃")[0]);
                break;

            case R.id.cl8_target_humidity: // 除湿目标湿度
                type = 5;
                showDialog(viewBinding.tv8TargetHumidityNum.getText().toString().split("%")[0]);
                break;
            case R.id.cl8_float_humidity: // 除湿浮动湿度
                type = 6;
                showDialog(viewBinding.tv8FloatHumidityNum.getText().toString().split("%")[0]);
                break;
            case R.id.cl9_weigh_alarm_time: // 称重报警时间
                type = 7;
                showDialog(viewBinding.tv9WeighAlarmTimeNum.getText().toString().split("min")[0]);
                break;
            case R.id.cl9_weigh_alarm_weight: // 称重报警重量
                type = 8;
//                showDialog(viewBinding.tv9WeighAlarmWeightNum.getText().toString().split("KG")[0]);
                showDialog(viewBinding.tv9WeighAlarmWeightNum.getText().toString().split(weighingUnit)[0]);
                break;
            case R.id.cl10_inlet_timeout: // 投入口超时时间
                type = 9;
                showDialog(viewBinding.tv10InletTimeoutNum.getText().toString().split("s")[0]);
                break;
            case R.id.cl10_inlet_voltage: // 投入口慢速电压
                type = 10;
                showDialog(viewBinding.tv10InletVoltageNum.getText().toString().split("V")[0]);
                break;
            case R.id.cl11_fan_gear: // 风扇档位选择
                type = 11;
                showDialog(viewBinding.tv11FanGearNum.getText().toString());
                break;
            case R.id.cl11_fan_humidity: // 风扇湿度
                type = 12;
                showDialog(viewBinding.tv11FanHumidityNum.getText().toString().split("%")[0]);
                break;
            case R.id.cl11_fan_voltage: // 风扇电压
                type = 13;
                showDialog(viewBinding.tv11FanVoltageNum.getText().toString().split("V")[0]);
                break;
            case R.id.cl12_24H_inlet_limited: // 日限定投入量
                type = 14;
//                showDialog(viewBinding.tv1224HInletLimitedNum.getText().toString().split("KG")[0]);
                showDialog(viewBinding.tv1224HInletLimitedNum.getText().toString().split(weighingUnit)[0]);
                break;
            case R.id.cl13_total_inlet_limited: // 总限定投入量
                type = 15;
//                showDialog(viewBinding.tv13TotalInletLimitedNum.getText().toString().split("KG")[0]);
                showDialog(viewBinding.tv13TotalInletLimitedNum.getText().toString().split(weighingUnit)[0]);
                break;
            case R.id.btn13_reset_total_inlet_limited: // 总限定投入量重置按钮
                MyApplication.adminParameterBean.setInletLimitedTotalAccumulation("0");
                SharedPreFerUtil.saveObj(Constant.ADMIN_PARAMETER_FILENAME, Constant.ADMIN_PARAMETER_KEY,
                        JSON.toJSONString(MyApplication.adminParameterBean));
                break;
            case R.id.btn12_reset_24H_inlet_limited: // 24H限定投入量重置按钮,因为是时时累积的，所以重置即删除数据库数据
                MyApplication.getInstance().getDaoSession().getTimesInletDao().deleteAll(); // 删除所有重量数据
                break;
            case R.id.cl14_run_time_normal: // 搅拌正转运行时间 normal
                type = 16;
                showDialog(viewBinding.tv14RunTimeNormalNum.getText().toString().split("min")[0]);
                break;
            case R.id.cl14_interval_time_normal: //  搅拌正转间隔时间 normal
                type = 17;
                showDialog(viewBinding.tv14IntervalTimeNormalNum.getText().toString().split("min")[0]);
                break;
            case R.id.cl16_run_time_power_saving: // 搅拌正转运行时间 节电模式 power saving
                type = 18;
                showDialog(viewBinding.tv16RunTimePowerSavingNum.getText().toString().split("min")[0]);
                break;
            case R.id.cl16_interval_time_power_saving: // 搅拌正转间隔时间 节电模式 power saving
                type = 19;
                showDialog(viewBinding.tv16IntervalTimePowerSavingNum.getText().toString().split("min")[0]);
                break;
            case R.id.cl17_target_temperature_power_saving: //  加热温度设置（节电模式 power saving）目标温度
                type = 20;
                showDialog(viewBinding.tv17TargetTemperaturePowerSavingNum.getText().toString().split("℃")[0]);
                break;
            case R.id.cl17_float_temperature_power_saving: //  加热温度设置（节电模式 power saving）浮动温度
                type = 21;
                showDialog(viewBinding.tv17FloatTemperaturePowerSavingNum.getText().toString().split("℃")[0]);
                break;
            case R.id.cl15_enter_power_saving_time: //  进入节电模式的时间
                type = 22;
                showDialog(viewBinding.tv15EnterPowerSavingTimeNum.getText().toString().split("h")[0]);
                break;
            case R.id.cl18_feeding_before_weight_time: //  投料前称重时间
                type = 23;
                showDialog(viewBinding.tv18FeedingBeforeWeightTimeNum.getText().toString().split("s")[0]);
                break;
            case R.id.cl18_feeding_after_weight_time: //  投料后称重时间
                type = 24;
                showDialog(viewBinding.tv18FeedingAfterWeightTimeNum.getText().toString().split("s")[0]);
                break;
            case R.id.cl19_max_alarm_temperature: // 加热报警最大温度设置
                type = 3;
                showDialog(viewBinding.tv19MaxAlarmTemperatureNum.getText().toString().split("℃")[0]);
                break;
            case R.id.cl19_min_alarm_temperature: // 加热报警最小温度设置
                type = 25;
                showDialog(viewBinding.tv19MinAlarmTemperatureNum.getText().toString().split("℃")[0]);
                break;
            case R.id.cl19_min_alarm_temp_ignore_time: // 加热报警最小温度忽略时间设置
                type = 26;
                showDialog(viewBinding.tv19MinAlarmTempIgnoreTimeNum.getText().toString().split("min")[0]);
                break;
            case R.id.cl7_alarm_max_weight: //报警最大重量
                type = 4;
//                showDialog(viewBinding.tv7AlarmMaxWeightNum.getText().toString().split("KG")[0]);
                showDialog(viewBinding.tv7AlarmMaxWeightNum.getText().toString().split(weighingUnit)[0]);
                break;
            case R.id.cl7_alarm_min_weight: //报警最小重量
                type = 27;
//                showDialog(viewBinding.tv7AlarmMinWeightNum.getText().toString().split("KG")[0]);
                showDialog(viewBinding.tv7AlarmMinWeightNum.getText().toString().split(weighingUnit)[0]);
                break;
            case R.id.cl20_screensaver_time: //进入屏保时间设定
                type = 28;
                showDialog(viewBinding.tv20ScreensaverTimeNum.getText().toString().split("min")[0]);
                break;
            case R.id.cl21_fan_run_time_power_saving: //节电模式 风扇运行时间
                type = 29;
                showDialog(viewBinding.tv21FanRunTimePowerSavingNum.getText().toString().split("min")[0]);
                break;
            case R.id.cl21_fan_interval_time_power_saving: //节电模式 风扇停止时间
                type = 30;
                showDialog(viewBinding.tv21FanIntervalTimePowerSavingNum.getText().toString().split("min")[0]);
                break;
            case R.id.cl10_inlet_open_voltage: // 投入口开门电压
                type = 31;
                showDialog(viewBinding.tv10InletOpenVoltageNum.getText().toString().split("V")[0]);
                break;
            case R.id.cl10_inlet_close_voltage: // 投入口关门电压
                type = 32;
                showDialog(viewBinding.tv10InletCloseVoltageNum.getText().toString().split("V")[0]);
                break;
            case R.id.cl10_inlet_quickly_close_time: // 投入口快速关门时间
                type = 33;
                showDialog(viewBinding.tv10InletQuicklyCloseTimeNum.getText().toString().split("ms")[0]);
                break;
            case R.id.cl25_target_temperature2: // 目标温度设置 2 后
                type = 35;
                showDialog(viewBinding.tv25TargetTemperatureNum2.getText().toString().split("℃")[0]);
                break;
            case R.id.cl25_float_temperature2: // 浮动温度设置 2 后
                type = 36;
                showDialog(viewBinding.tv25FloatTemperatureNum2.getText().toString().split("℃")[0]);
                break;
            case R.id.cl26_target_temperature_power_saving2: // 节电模式目标温度设置 2 后
                type = 37;
                showDialog(viewBinding.tv26TargetTemperaturePowerSavingNum2.getText().toString().split("℃")[0]);
                break;
            case R.id.cl26_float_temperature_power_saving2: // 节电模式浮动温度设置 2 后
                type = 38;
                showDialog(viewBinding.tv26FloatTemperaturePowerSavingNum2.getText().toString().split("℃")[0]);
                break;
            case R.id.cl27_humidity_alarm_time: // 湿度报警时间 单位 min
                type = 39;
                showDialog(viewBinding.tv27HumidityAlarmTimeNum.getText().toString().split("min")[0]);
                break;
            case R.id.cl27_humidity_alarm_humidity: // 湿度报警对应的湿度 单位 %
                type = 40;
                showDialog(viewBinding.tv27HumidityAlarmHumidityNum.getText().toString().split("%")[0]);
                break;
            case R.id.cl28_reverse_run_time_normal: // 搅拌反转 运行时间 normal
                type = 41;
                showDialog(viewBinding.tv28ReverseRunTimeNormalNum.getText().toString().split("min")[0]);
                break;
            case R.id.cl28_reverse_interval_time_normal: //  搅拌反转 间隔时间 normal
                type = 42;
                showDialog(viewBinding.tv28ReverseIntervalTimeNormalNum.getText().toString().split("min")[0]);
                break;
            case R.id.cl29_reverse_run_time_power_saving: // 搅拌反转 运行时间 PowerSaving
                type = 43;
                showDialog(viewBinding.tv29ReverseRunTimePowerSavingNum.getText().toString().split("min")[0]);
                break;
            case R.id.cl29_reverse_interval_time_power_saving: //  搅拌反转 间隔时间 PowerSaving
                type = 44;
                showDialog(viewBinding.tv29ReverseIntervalTimePowerSavingNum.getText().toString().split("min")[0]);
                break;
            case R.id.cl30_sterilization_mode_temp: // 杀菌模式 温度
                type = 45;
                showDialog(viewBinding.tv30SterilizationModeTempNum.getText().toString().split("℃")[0]);
                break;
            case R.id.cl30_sterilization_mode_time:// 杀菌模式 时间
                type = 46;
                showDialog(viewBinding.tv30SterilizationModeTimeNum.getText().toString().split("min")[0]);
                break;
        }
    }

    /**
     * 显示选择器
     *
     * @param nowNum 当前数值
     * @param type   类型
     */
    private void showDialog(String nowNum, int type) {
        DialogChooseTemperature dialogChooseTemperature = new DialogChooseTemperature(mActivity, nowNum, type);
        dialogChooseTemperature.show(getSupportFragmentManager(), "choose_temperature");
    }

    /**
     * 显示温度选择器
     *
     * @param temperature
     * @param anotherTemp
     * @param type
     */
    private void showDialog(String temperature, String anotherTemp, int type) {
        DialogChooseTemperature dialogChooseTemperature = new DialogChooseTemperature(mActivity, temperature, anotherTemp, type);
        dialogChooseTemperature.show(getSupportFragmentManager(), "choose_temperature");
    }

    /**
     * 显示限重选择器
     *
     * @param nowNum
     */
    private void showDialog(String nowNum) {
        DialogChooseTemperature dialogChooseTemperature = new DialogChooseTemperature(mActivity, nowNum, type);
        dialogChooseTemperature.show(getSupportFragmentManager(), "choose_temperature");
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (radioGroup.getId()) {
            case R.id.rg1_language:
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.rb1_chinese:
                        language = Constant.CHINESE;
                        break;
                    case R.id.rb1_korean:
                        language = Constant.KOREAN;
                        break;
                    case R.id.rb1_japanese:
                        language = Constant.JAPANESE;
                        break;
                    case R.id.rb1_english:
                        language = Constant.ENGLISH;
                        break;
                }
                break;
          /*  case R.id.rg2_app_mode:
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.rb2_apartment:
                        viewBinding.rb2Apartment.setChecked(true);
                        appMode = Constant.APARTMENT;

                        viewBinding.rb3Inside.setVisibility(View.VISIBLE);
                        viewBinding.rb3Inside.setChecked(true);
                        weighingMode = Constant.INSIDE;
                        viewBinding.rb3Outside.setVisibility(View.GONE);
                        viewBinding.rb3None.setVisibility(View.GONE);

                        viewBinding.rb4Have.setVisibility(View.VISIBLE);
                        viewBinding.rb4Have.setChecked(true);
                        loginMode = Constant.HAVE;
                        viewBinding.rb4None.setVisibility(View.GONE);

                        viewBinding.rb5Automation.setVisibility(View.VISIBLE);
                        viewBinding.rb5Automation.setChecked(true);
                        inletMode = Constant.AUTOMATION;
                        viewBinding.rb5Manual.setVisibility(View.GONE);

                        viewBinding.group7DailyInletLimited.setVisibility(View.GONE);
                        viewBinding.tv3WeighingMode.setText(R.string.weighing_mode);
                        break;
                    case R.id.rb2_shop:
                        viewBinding.tv3WeighingMode.setText(R.string.outside_weighing);
                        viewBinding.rb2Shop.setChecked(true);
                        appMode = Constant.SHOP;

                        viewBinding.rb3Outside.setVisibility(View.VISIBLE);
                        viewBinding.rb3Outside.setChecked(true);
                        weighingMode = Constant.OUTSIDE;
                        viewBinding.rb3None.setVisibility(View.VISIBLE);
                        viewBinding.rb3Inside.setVisibility(View.GONE);

                        viewBinding.rb4Have.setChecked(true);
                        loginMode = Constant.HAVE;
                        viewBinding.rb4None.setVisibility(View.VISIBLE);

                        viewBinding.rb5Automation.setChecked(true);
                        inletMode = Constant.AUTOMATION;
                        viewBinding.rb5Manual.setVisibility(View.VISIBLE);

                        viewBinding.group7DailyInletLimited.setVisibility(View.VISIBLE);
                        break;
                }
                break;*/
            case R.id.rg3_weighing_mode:
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.rb3_have:
                        weighingMode = Constant.HAVE;
                        break;
                    case R.id.rb3_none:
                        weighingMode = Constant.NONE;
                        break;
                }
                break;
            case R.id.rg4_login_setting:
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.rb4_have:
                        loginMode = Constant.HAVE;
                        break;
                    case R.id.rb4_none:
                        loginMode = Constant.NONE;
                        break;
                    case R.id.rb4_binding:
                        loginMode = Constant.BINDING;
                        break;
                }
                break;
            case R.id.rg5_inlet_mode:
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.rb5_automation:
                        inletMode = Constant.AUTOMATION;
                        viewBinding.rb4Have.setVisibility(View.VISIBLE);
                        viewBinding.groupInletSetting.setVisibility(View.VISIBLE);
                        viewBinding.groupInletQuicklyCloseTime.setVisibility(View.VISIBLE);
                        if (viewBinding.rg4LoginSetting.getCheckedRadioButtonId() != R.id.rb4_binding) {
                            viewBinding.rb4Have.setChecked(true);
                        }
//                        viewBinding.rb4None.setVisibility(View.GONE);
                        break;
                    case R.id.rb5_manual:
                        inletMode = Constant.MANUAL;
                        viewBinding.rb4Have.setVisibility(View.GONE);
                        viewBinding.rb4None.setVisibility(View.VISIBLE);
                        viewBinding.groupInletSetting.setVisibility(View.GONE);
                        viewBinding.groupInletQuicklyCloseTime.setVisibility(View.GONE);
                        if (viewBinding.rg4LoginSetting.getCheckedRadioButtonId() != R.id.rb4_binding) {
                            viewBinding.rb4None.setChecked(true);
                        }
                        break;
                }
                break;
            case R.id.rg23_weighing_source_setting:
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.rb23_485:
                        MyApplication.adminParameterBean.setWeighingSourceSetting(Constant.WEIGHING_SOURCE_485);
                        break;
                    case R.id.rb23_loadcell1:
                        MyApplication.adminParameterBean.setWeighingSourceSetting(Constant.WEIGHING_SOURCE_LOADCELL1);
                        break;
                    case R.id.rb23_loadcell2:
                        MyApplication.adminParameterBean.setWeighingSourceSetting(Constant.WEIGHING_SOURCE_LOADCELL2);
                        break;
                }
                SharedPreFerUtil.saveObj(Constant.ADMIN_PARAMETER_FILENAME, Constant.ADMIN_PARAMETER_KEY,
                        JSON.toJSONString(MyApplication.adminParameterBean));
                break;

            case R.id.rg24_weighing_unit:
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.rb24_kg:
                        MyApplication.adminParameterBean.setWeighingUnit(Constant.WEIGHING_UNIT_KG);
                        weighingUnit = getString(R.string.kg_2);
                        break;
                    case R.id.rb24_lb:
                        MyApplication.adminParameterBean.setWeighingUnit(Constant.WEIGHING_UNIT_POUND);
                        weighingUnit = getString(R.string.LB);
                        break;
                }

                MyApplication.weighingUnit = weighingUnit;
                setWeighingUnit();
                SharedPreFerUtil.saveObj(Constant.ADMIN_PARAMETER_FILENAME, Constant.ADMIN_PARAMETER_KEY,
                        JSON.toJSONString(MyApplication.adminParameterBean));
                break;
            case R.id.rg31_weight_decimal_show:
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.rb31_yes:
                        MyApplication.adminParameterBean.setWeightDecimalShow(true);
                        break;
                    case R.id.rb31_no:
                        MyApplication.adminParameterBean.setWeightDecimalShow(false);
                        break;
                }
                SharedPreFerUtil.saveObj(Constant.ADMIN_PARAMETER_FILENAME, Constant.ADMIN_PARAMETER_KEY,
                        JSON.toJSONString(MyApplication.adminParameterBean));
                break;

        }
        if (loginMode.equals(Constant.BINDING) /*&& inletMode.equals(Constant.MANUAL)*/) {
            viewBinding.btn4Unbind.setVisibility(View.VISIBLE);
        } else {
            viewBinding.btn4Unbind.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()){
            case R.id.sc22_observe_port_alarm: // 安全门报警开关
                MyApplication.adminParameterBean.setObservePortAlarm(b);
                break;
            case R.id.sc27_humidity_alarm_switch: // 湿度报警开关
                MyApplication.adminParameterBean.setHumidityAlarmSwitch(b);
                break;
        }

        SharedPreFerUtil.saveObj(Constant.ADMIN_PARAMETER_FILENAME, Constant.ADMIN_PARAMETER_KEY,
                JSON.toJSONString(MyApplication.adminParameterBean));
    }

    @Override
    public void returnFragmentData(String message) {
        if (type == 1) {
            // 目标温度设置
            viewBinding.tv6TargetTemperatureNum.setText(message + "℃");
            MyApplication.adminParameterBean.getHeartingTemperature().setTargetTem(message);
            int floatTemNum = Integer.parseInt(viewBinding.tv6FloatTemperatureNum.getText().toString().split("℃")[0]);
            hexHeater = PortControlUtil.getHeater1AutomaticCommands(Integer.parseInt(message), floatTemNum);
            PortControlUtil.getInstance().sendCommands(hexHeater);

//            hexHeater = PortControlUtil.getHeater2AutomaticCommands(Integer.parseInt(message), floatTemNum);
//            PortControlUtil.getInstance().sendCommands(hexHeater);
        } else if (type == 2) {
            // 浮动温度设置
            viewBinding.tv6FloatTemperatureNum.setText(message + "℃");
            MyApplication.adminParameterBean.getHeartingTemperature().setFloatTem(message);
            int targetTemNum = Integer.parseInt(viewBinding.tv6TargetTemperatureNum.getText().toString().split("℃")[0]);
            hexHeater = PortControlUtil.getHeater1AutomaticCommands(targetTemNum, Integer.parseInt(message));
            PortControlUtil.getInstance().sendCommands(hexHeater);

//            baseHandler.postDelayed(() -> {
//            hexHeater = PortControlUtil.getHeater2AutomaticCommands(targetTemNum, Integer.parseInt(message));
//            PortControlUtil.getInstance().sendCommands(hexHeater);
//            }, 300);
        } else if (type == 3) {
            // 加热报警最大温度设置
            viewBinding.tv19MaxAlarmTemperatureNum.setText(message + "℃");
            MyApplication.adminParameterBean.getHeartingTemperature().setMaxTemp(message);
        } else if (type == 4) {
            // 设置报警最大重量
            viewBinding.tv7AlarmMaxWeightNum.setText(message + weighingUnit);
//            inletLimitedAlarmTotal
            MyApplication.adminParameterBean.setInletLimitedAlarmTotalMax(message);
        } else if (type == 5) {
            // 除湿目标湿度
            viewBinding.tv8TargetHumidityNum.setText(message + "%");
            MyApplication.adminParameterBean.getHumiditySetting().setTargetHumidity(message);
            int floatHumidityNum = Integer.parseInt(viewBinding.tv8FloatHumidityNum.getText().toString().split("%")[0]);
            hexDehumidification = PortControlUtil.getDehumidificationAutomaticCommands(Integer.parseInt(message), floatHumidityNum);
            PortControlUtil.getInstance().sendCommands(hexDehumidification);
        } else if (type == 6) {
            // 除湿浮动湿度
            viewBinding.tv8FloatHumidityNum.setText(message + "%");
            MyApplication.adminParameterBean.getHumiditySetting().setFloatHumidity(message);
            int targetHumidityNum = Integer.parseInt(viewBinding.tv8TargetHumidityNum.getText().toString().split("%")[0]);
            hexDehumidification = PortControlUtil.getDehumidificationAutomaticCommands(targetHumidityNum, Integer.parseInt(message));
            PortControlUtil.getInstance().sendCommands(hexDehumidification);
        } else if (type == 7) {
            //称重报警时间
            viewBinding.tv9WeighAlarmTimeNum.setText(message + "min");
            MyApplication.adminParameterBean.setWeighAlarmTime(message);
        } else if (type == 8) {
            // 称重报警重量
//            viewBinding.tv9WeighAlarmWeightNum.setText(message + "KG");
            viewBinding.tv9WeighAlarmWeightNum.setText(message + weighingUnit);
            MyApplication.adminParameterBean.setWeighAlarmWeight(message);
        } else if (type == 9) {
            // 投入口超时时间
            viewBinding.tv10InletTimeoutNum.setText(message + "s");
            MyApplication.adminParameterBean.setInletTimeoutTime(message);
            int inletVoltage = Integer.parseInt(viewBinding.tv10InletVoltageNum.getText().toString().split("V")[0]);
            String inletTimeoutVoltage = PortControlUtil.inletTimeoutVoltageHexData(Integer.parseInt(message), inletVoltage);
            PortControlUtil.getInstance().sendCommands(inletTimeoutVoltage);
        } else if (type == 10) {
            // 投入口慢速电压
            viewBinding.tv10InletVoltageNum.setText(message + "V");
            MyApplication.adminParameterBean.setInletLowSpeedVoltage(message);
            int inletTimeout = Integer.parseInt(viewBinding.tv10InletTimeoutNum.getText().toString().split("s")[0]);
            String inletTimeoutVoltage = PortControlUtil.inletTimeoutVoltageHexData(inletTimeout, Integer.parseInt(message));
            PortControlUtil.getInstance().sendCommands(inletTimeoutVoltage);
        } else if (type == 11) {
            // 风扇档位选择
            if (message.equals(viewBinding.tv11FanGearNum.getText().toString())) {
                return;
            }
            viewBinding.tv11FanGearNum.setText(message);
            AdminParameterBean.FanHumiditySettingBean fanHumiditySettingBean =
                    MyApplication.adminParameterBean.getFanHumiditySettingBeanList().get(Integer.parseInt(message) - 1);
            viewBinding.tv11FanHumidityNum.setText(fanHumiditySettingBean.getFanHumidityNum() + "%");
            viewBinding.tv11FanVoltageNum.setText(fanHumiditySettingBean.getFanHumidityVoltage() + "V");
        } else if (type == 12) {
            // 风扇湿度
            viewBinding.tv11FanHumidityNum.setText(message + "%");
//            int gear = Integer.parseInt(viewBinding.tv11FanGearNum.getText().toString());
            MyApplication.adminParameterBean.getFanHumiditySettingBeanList().get(0).setFanHumidityNum(Integer.parseInt(message));
            MyApplication.adminParameterBean.getFanHumiditySettingBeanList().get(1).setFanHumidityNum(Integer.parseInt(message));
            MyApplication.adminParameterBean.getFanHumiditySettingBeanList().get(2).setFanHumidityNum(Integer.parseInt(message));
            int fanVoltageNum = Integer.parseInt(viewBinding.tv11FanVoltageNum.getText().toString().split("V")[0]);
            String fanGearHumidityVoltage1 = PortControlUtil.getFan1GearHumidityVoltageCommands(1, fanVoltageNum, Integer.parseInt(message));
            PortControlUtil.getInstance().sendCommands(fanGearHumidityVoltage1);
            String fanGearHumidityVoltage2 = PortControlUtil.getFan1GearHumidityVoltageCommands(2, fanVoltageNum, Integer.parseInt(message));
            PortControlUtil.getInstance().sendCommands(fanGearHumidityVoltage2);
            String fanGearHumidityVoltage3 = PortControlUtil.getFan1GearHumidityVoltageCommands(3, fanVoltageNum, Integer.parseInt(message));
            PortControlUtil.getInstance().sendCommands(fanGearHumidityVoltage3);
        } else if (type == 13) {
            // 风扇电压
            viewBinding.tv11FanVoltageNum.setText(message + "V");
            int gear = Integer.parseInt(viewBinding.tv11FanGearNum.getText().toString());
            MyApplication.adminParameterBean.getFanHumiditySettingBeanList().get(gear - 1).setFanHumidityVoltage(Integer.parseInt(message));
            int fanHumidityNum = Integer.parseInt(viewBinding.tv11FanHumidityNum.getText().toString().split("%")[0]);
            String fanGearHumidityVoltage = PortControlUtil.getFan1GearHumidityVoltageCommands(gear, Integer.parseInt(message), fanHumidityNum);
            PortControlUtil.getInstance().sendCommands(fanGearHumidityVoltage);
        } else if (type == 14) {
            // 日限定投入量
//            viewBinding.tv1224HInletLimitedNum.setText(message + "KG");
            viewBinding.tv1224HInletLimitedNum.setText(message + weighingUnit);
            MyApplication.adminParameterBean.setInletLimited24H(message);
        } else if (type == 15) {
            //总限定投入量
//            viewBinding.tv13TotalInletLimitedNum.setText(message + "KG");
            viewBinding.tv13TotalInletLimitedNum.setText(message + weighingUnit);
            MyApplication.adminParameterBean.setInletLimitedTotal(message);
        } else if (type == 16) {
            //搅拌运行时间 normal
            viewBinding.tv14RunTimeNormalNum.setText(message + "min");
            MyApplication.adminParameterBean.setStirRunTimeNormal(message);
        } else if (type == 17) {
            //搅拌间隔时间 normal
            viewBinding.tv14IntervalTimeNormalNum.setText(message + "min");
            MyApplication.adminParameterBean.setStirIntervalTimeNormal(message);
        } else if (type == 18) {
            //搅拌运行时间 节电模式 power saving
            viewBinding.tv16RunTimePowerSavingNum.setText(message + "min");
            MyApplication.adminParameterBean.setStirRunTimePowerSaving(message);
        } else if (type == 19) {
            //搅拌间隔时间 节电模式 power saving
            viewBinding.tv16IntervalTimePowerSavingNum.setText(message + "min");
            MyApplication.adminParameterBean.setStirIntervalPowerSaving(message);
        } else if (type == 20) {
            // 加热温度设置1 前（节电模式 power saving）目标温度
            viewBinding.tv17TargetTemperaturePowerSavingNum.setText(message + "℃");
            MyApplication.adminParameterBean.getHeartingTemperaturePowerSaving().setTargetTem(message);
        } else if (type == 21) {
            // 加热温度设置1 前（节电模式 power saving）浮动温度
            viewBinding.tv17FloatTemperaturePowerSavingNum.setText(message + "℃");
            MyApplication.adminParameterBean.getHeartingTemperaturePowerSaving().setFloatTem(message);
        } else if (type == 22) {
            // 进入节电模式的时间
            viewBinding.tv15EnterPowerSavingTimeNum.setText(message + "h");
            MyApplication.adminParameterBean.setEnterPowerSavingTime(message);
        } else if (type == 23) {
            // 投料前称重时间
            viewBinding.tv18FeedingBeforeWeightTimeNum.setText(message + "s");
            MyApplication.adminParameterBean.setFeedingBeforeWeightTime(message);
        } else if (type == 24) {
            // 投料后称重时间
            viewBinding.tv18FeedingAfterWeightTimeNum.setText(message + "s");
            MyApplication.adminParameterBean.setFeedingAfterWeightTime(message);
        } else if (type == 25) {
            // 加热报警最小温度设置
            viewBinding.tv19MinAlarmTemperatureNum.setText(message + "℃");
            MyApplication.adminParameterBean.getHeartingTemperature().setMinTemp(message);
        } else if (type == 26) {
            // 加热报警最小温度忽略时间设置
            viewBinding.tv19MinAlarmTempIgnoreTimeNum.setText(message + "min");
            MyApplication.adminParameterBean.getHeartingTemperature().setIgnoreTime(message);
        } else if (type == 27) {
            // 设置报警最小重量
//            viewBinding.tv7AlarmMinWeightNum.setText(message + "KG");
            viewBinding.tv7AlarmMinWeightNum.setText(message + weighingUnit);
            MyApplication.adminParameterBean.setInletLimitedAlarmTotalMin(message);
        } else if (type == 28) {
            // 进入屏保时间
            viewBinding.tv20ScreensaverTimeNum.setText(message + "min");
            MyApplication.adminParameterBean.setScreensaverTime(message);
        } else if (type == 29) {
            // 节电模式 风扇运行时间
            viewBinding.tv21FanRunTimePowerSavingNum.setText(message + "min");
            MyApplication.adminParameterBean.setFanRunTimePowerSaving(message);
        } else if (type == 30) {
            // 节电模式 风扇停止时间
            viewBinding.tv21FanIntervalTimePowerSavingNum.setText(message + "min");
            MyApplication.adminParameterBean.setFanIntervalPowerSaving(message);
        } else if (type == 31) {
            // 投入口开门电压
            viewBinding.tv10InletOpenVoltageNum.setText(message + "V");
            MyApplication.adminParameterBean.setInletOpenVoltage(message);
            int closeVoltage = Integer.parseInt(viewBinding.tv10InletCloseVoltageNum.getText().toString().split("V")[0]);
            String inletOpenCloseVoltage = PortControlUtil.inletOpenCloseVoltageHexData(Integer.parseInt(message), closeVoltage);
            PortControlUtil.getInstance().sendCommands(inletOpenCloseVoltage);
        } else if (type == 32) {
            // 投入口关门电压
            viewBinding.tv10InletCloseVoltageNum.setText(message + "V");
            MyApplication.adminParameterBean.setInletCloseVoltage(message);
            int openVoltage = Integer.parseInt(viewBinding.tv10InletOpenVoltageNum.getText().toString().split("V")[0]);
            String inletOpenCloseVoltage = PortControlUtil.inletOpenCloseVoltageHexData(openVoltage, Integer.parseInt(message));
            PortControlUtil.getInstance().sendCommands(inletOpenCloseVoltage);
        } else if (type == 33) {
            // 投入口高速关门时间
            viewBinding.tv10InletQuicklyCloseTimeNum.setText(message + "ms");
            MyApplication.adminParameterBean.setInletQuicklyCloseTime(message);
            String inletQuicklyCloseTime = PortControlUtil.inletQuicklyCloseTimeHexData(message);
            PortControlUtil.getInstance().sendCommands(inletQuicklyCloseTime);
        }else if (type == 35) {
            // 目标温度设置
            viewBinding.tv25TargetTemperatureNum2.setText(message + "℃");
            MyApplication.adminParameterBean.getHeartingTemperature().setTargetTem2(message);
            int floatTemNum = Integer.parseInt(viewBinding.tv25FloatTemperatureNum2.getText().toString().split("℃")[0]);

            hexHeater = PortControlUtil.getHeater2AutomaticCommands(Integer.parseInt(message), floatTemNum);
            PortControlUtil.getInstance().sendCommands(hexHeater);
        } else if (type == 36) {
            // 浮动温度设置
            viewBinding.tv25FloatTemperatureNum2.setText(message + "℃");
            MyApplication.adminParameterBean.getHeartingTemperature().setFloatTem2(message);
            int targetTemNum = Integer.parseInt(viewBinding.tv25TargetTemperatureNum2.getText().toString().split("℃")[0]);

            hexHeater = PortControlUtil.getHeater2AutomaticCommands(targetTemNum, Integer.parseInt(message));
            PortControlUtil.getInstance().sendCommands(hexHeater);
        }else if (type == 37) {
            // 加热温度设置2后（节电模式 power saving）目标温度
            viewBinding.tv26TargetTemperaturePowerSavingNum2.setText(message + "℃");
            MyApplication.adminParameterBean.getHeartingTemperaturePowerSaving().setTargetTem2(message);
        } else if (type == 38) {
            // 加热温度设置2后（节电模式 power saving）浮动温度
            viewBinding.tv26FloatTemperaturePowerSavingNum2.setText(message + "℃");
            MyApplication.adminParameterBean.getHeartingTemperaturePowerSaving().setFloatTem2(message);
        }else if (type == 39) {
            // 湿度报警时间设置
            viewBinding.tv27HumidityAlarmTimeNum.setText(message + "min");
            MyApplication.adminParameterBean.setHumidityAlarmTime(message);
        }else if (type == 40) {
            // 湿度报警对应湿度设置
            viewBinding.tv27HumidityAlarmHumidityNum.setText(message + "%");
            MyApplication.adminParameterBean.setHumidityAlarmHumidity(Integer.parseInt(message));
        } else if (type == 41) {
            //搅拌反转运行时间 normal
            viewBinding.tv28ReverseRunTimeNormalNum.setText(message + "min");
            MyApplication.adminParameterBean.setStirReverseRunTimeNormal(message);
        } else if (type == 42) {
            //搅拌反转间隔时间 normal
            viewBinding.tv28ReverseIntervalTimeNormalNum.setText(message + "min");
            MyApplication.adminParameterBean.setStirReverseIntervalTimeNormal(message);
        }else if (type == 43) {
            //搅拌反转运行时间 PowerSaving
            viewBinding.tv29ReverseRunTimePowerSavingNum.setText(message + "min");
            MyApplication.adminParameterBean.setStirReverseRunTimePowerSaving(message);
        } else if (type == 44) {
            //搅拌反转间隔时间 PowerSaving
            viewBinding.tv29ReverseIntervalTimePowerSavingNum.setText(message + "min");
            MyApplication.adminParameterBean.setStirReverseIntervalPowerSaving(message);
        }else if (type == 45) {
            //杀菌模式 温度
            viewBinding.tv30SterilizationModeTempNum.setText(message + "℃");
            MyApplication.adminParameterBean.setSterilizationModeTemp(message);
        } else if (type == 46) {
            //杀菌模式 时间
            viewBinding.tv30SterilizationModeTimeNum.setText(message + "min");
            MyApplication.adminParameterBean.setSterilizationModeTime(message);
        }
        SharedPreFerUtil.saveObj(Constant.ADMIN_PARAMETER_FILENAME, Constant.ADMIN_PARAMETER_KEY,
                JSON.toJSONString(MyApplication.adminParameterBean));
    }


}
