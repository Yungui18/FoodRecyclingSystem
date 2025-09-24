package com.hyeprion.foodrecyclingsystem.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 管理员参数设置当前设置
 */
public class AdminParameterBean implements Serializable {
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
     * <li>apartment{@link com.hyeprion.foodrecyclingsystem.util.Constant#APARTMENT}
     * <li>shop{@link com.hyeprion.foodrecyclingsystem.util.Constant#SHOP}
     * </p>
     */
    private String appMode;
    /**
     * 称重模式
     * <p>
     * <li>有{@link com.hyeprion.foodrecyclingsystem.util.Constant#HAVE}
     * <li>没有{@link com.hyeprion.foodrecyclingsystem.util.Constant#NONE}
     * </p>
     */
    private String weighingMode;

    /**
     * 设备报警总重量（通过心跳中净重总值进行判断，用于故障信息里面的红灯）最大值  高于此值报警
     */
    private String inletLimitedAlarmTotalMax;
    /**
     * 设备报警总重量（通过心跳中净重总值进行判断，用于故障信息里面的红灯）最小值  低于此值报警
     */
    private String inletLimitedAlarmTotalMin;
    /**
     * 总限定投入量（根据每次差值进行累计，用于提醒排料，超过此值黄灯2亮，后面加一个重置按钮，点击后重新累计差值）
     */
    private String inletLimitedTotal;

    /**
     * 每次差值进行累计，当inletLimitedTotalAccumulation > inletLimitedTotal（提醒排料，黄灯2亮，点击重置按钮后归0，重新累计差值）
     */
    private String inletLimitedTotalAccumulation;
    /**
     * 日（24H）限定投入量（根据每次差值进行累计，累计24小时总重量不得超过此值，用处1：超过此值黄灯2亮；用处2：:计算主界面24H剩余可投入量）
     */
    private String inletLimited24H;
    /**
     * 称重报警时间 单位分钟
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
     * 加热温度设置（正常模式 normal），目标温度和浮动温度，加热温度=目标-浮动 停止加热温度=目标+浮动  报警温度
     */
    private HeartingTemperature heartingTemperature;
    /**
     * 除湿设置，目标湿度和浮动湿度  除湿湿度= 目标+浮动  停止湿度=目标-浮动
     */
    private HumiditySetting humiditySetting;
    /**
     * 投入口超时时间 单位S
     */
    private String inletTimeoutTime;
    /**
     * 投入口低速电压  共12挡
     */
    private String inletLowSpeedVoltage;

    /**
     * 风扇湿度设置，包含风扇档位及对应的湿度、电压 分为1,2,3挡
     */
    private List<FanHumiditySettingBean> fanHumiditySettingBeanList;

    /**
     * 加热模式
     * 1手动  2自动  3停止
     */
    private int heaterMode;

    /**
     * 除湿模式
     * 1手动  2自动  3停止
     */
    private int dehumidificationMode;

    /**
     * 设备标号
     */
    private String deviceId;

    /**
     * 1：自动  2：手动  3：停止  三者互斥
     */
    private int deviceMode;

    /**
     * 正转搅拌运行时间 normal
     */
    private String stirRunTimeNormal;
    /**
     * 正转搅拌间隔时间 normal
     */
    private String stirIntervalTimeNormal;
    /**
     * 反转搅拌运行时间 normal
     */
    private String stirReverseRunTimeNormal;
    /**
     * 反转搅拌间隔时间 normal
     */
    private String stirReverseIntervalTimeNormal;

    /**
     * 搅拌运行时间 节电模式 power saving
     */
    private String stirRunTimePowerSaving;
    /**
     * 搅拌间隔时间 节电模式 power saving
     */
    private String stirIntervalPowerSaving;
    /**
     * 搅拌运行时间 节电模式 power saving
     */
    private String stirReverseRunTimePowerSaving;
    /**
     * 搅拌间隔时间 节电模式 power saving
     */
    private String stirReverseIntervalPowerSaving;

    /**
     * 加热温度设置（节电模式 power saving）目标温度和浮动温度，加热温度=目标-浮动 停止加热温度=目标+浮动
     */
    private HeartingTemperaturePowerSaving heartingTemperaturePowerSaving;

    /**
     * 进入节电模式时间 1-24h
     */
    private String enterPowerSavingTime;
    /**
     * 投料前称重时间  单位:S
     */
    private String feedingBeforeWeightTime;
    /**
     * 投料后称重时间  单位:S
     */
    private String feedingAfterWeightTime;

    /**
     * 进入屏保时间 0-120分钟，间隔10分钟，设置为0分钟则无屏保
     */
    private String screensaverTime;

    /**
     * 投入口手动，登录有的时候保存的id，上传重量用
     */
    private String loginId;

    /**
     * 投入口手动，登录有的时候保存的pw，上传重量用
     */
    private String loginPW;

    /**
     * 风扇运行时间 节电模式 power saving
     */
    private String fanRunTimePowerSaving;
    /**
     * 风扇间隔时间 节电模式 power saving
     */
    private String fanIntervalPowerSaving;

    /**
     * 观察口报警  true:根据观察口状态变化，进行报警  false：不关注观察口状态
     */
    private boolean observePortAlarm;

    /**
     * 投入口开门电压
     */
    private String inletOpenVoltage;
    /**
     * 投入口关门电压
     */
    private String inletCloseVoltage;

    /**
     * 投入口快速关门时间
     */
    private String inletQuicklyCloseTime;

    /**
     * 称重源设置  1：485   2：loadcell1 直连称重1  3：loadcell2 直连称重2
     */
    private int weighingSourceSetting;

    /**
     * 重量标定数值
     */
    private String weightCalibrationNum;

    /**
     * 称重单位 1：kg  2：磅 lb
     */
    private int weighingUnit;

    /**
     * 湿度报警时间 单位分钟
     */
    private String humidityAlarmTime;

    /**
     * 湿度报警时间对应的湿度数值，百分比 %
     */
    private int humidityAlarmHumidity;
    /**
     * 湿度报警开关，默认 false
     */
    private boolean humidityAlarmSwitch;

    /**
     * 杀菌模式温度 60度~85度/间隔1度
     */
    private String sterilizationModeTemp;

    /**
     * 杀菌模式时间 时间30分钟~3小时/间隔10分钟 min
     */
    private String sterilizationModeTime;
    /**
     * 重量小数显示 true 显示小数  false 不显示小数
     */
    private boolean weightDecimalShow;

    public String getSterilizationModeTemp() {
        return sterilizationModeTemp;
    }

    public String getEnterPowerSavingTime() {
        return enterPowerSavingTime;
    }

    public void setEnterPowerSavingTime(String enterPowerSavingTime) {
        this.enterPowerSavingTime = enterPowerSavingTime;
    }

    public AdminParameterBean() {
    }

    public String getHumidityAlarmTime() {
        return humidityAlarmTime;
    }

    public void setHumidityAlarmTime(String humidityAlarmTime) {
        this.humidityAlarmTime = humidityAlarmTime;
    }

    /**
     * 除湿设置，目标湿度和浮动湿度  除湿湿度= 目标+浮动  停止湿度=目标-浮动
     */
    public static class HumiditySetting {
        /**
         * 目标除湿湿度
         */
        private String targetHumidity;
        /**
         * 浮动除湿湿度
         */
        private String floatHumidity;


        public HumiditySetting() {
        }

        public HumiditySetting(String targetHumidity, String floatHumidity) {
            this.targetHumidity = targetHumidity;
            this.floatHumidity = floatHumidity;
        }

        public String getTargetHumidity() {
            return targetHumidity;
        }

        public void setTargetHumidity(String targetHumidity) {
            this.targetHumidity = targetHumidity;
        }

        public String getFloatHumidity() {
            return floatHumidity;
        }

        public void setFloatHumidity(String floatHumidity) {
            this.floatHumidity = floatHumidity;
        }
    }

    public String getWeighAlarmTime() {
        return weighAlarmTime;
    }

    public void setWeighAlarmTime(String weighAlarmTime) {
        this.weighAlarmTime = weighAlarmTime;
    }

    public String getWeighAlarmWeight() {
        return weighAlarmWeight;
    }

    public void setWeighAlarmWeight(String weighAlarmWeight) {
        this.weighAlarmWeight = weighAlarmWeight;
    }

    public String getInletTimeoutTime() {
        return inletTimeoutTime;
    }

    public void setInletTimeoutTime(String inletTimeoutTime) {
        this.inletTimeoutTime = inletTimeoutTime;
    }

    public String getScreensaverTime() {
        return screensaverTime;
    }

    public void setScreensaverTime(String screensaverTime) {
        this.screensaverTime = screensaverTime;
    }

    public String getFeedingBeforeWeightTime() {
        return feedingBeforeWeightTime;
    }

    public void setFeedingBeforeWeightTime(String feedingBeforeWeightTime) {
        this.feedingBeforeWeightTime = feedingBeforeWeightTime;
    }

    public String getFeedingAfterWeightTime() {
        return feedingAfterWeightTime;
    }

    public void setFeedingAfterWeightTime(String feedingAfterWeightTime) {
        this.feedingAfterWeightTime = feedingAfterWeightTime;
    }

    public String getInletLimitedTotalAccumulation() {
        return inletLimitedTotalAccumulation;
    }

    public void setInletLimitedTotalAccumulation(String inletLimitedTotalAccumulation) {
        this.inletLimitedTotalAccumulation = inletLimitedTotalAccumulation;
    }

    public String getInletLowSpeedVoltage() {
        return inletLowSpeedVoltage;
    }

    public void setInletLowSpeedVoltage(String inletLowSpeedVoltage) {
        this.inletLowSpeedVoltage = inletLowSpeedVoltage;
    }

    public List<FanHumiditySettingBean> getFanHumiditySettingBeanList() {
        return fanHumiditySettingBeanList;
    }

    public void setFanHumiditySettingBeanList(List<FanHumiditySettingBean> fanHumiditySettingBeanList) {
        this.fanHumiditySettingBeanList = fanHumiditySettingBeanList;
    }

    public String getStirRunTimeNormal() {
        return stirRunTimeNormal;
    }

    public void setStirRunTimeNormal(String stirRunTimeNormal) {
        this.stirRunTimeNormal = stirRunTimeNormal;
    }

    public String getStirIntervalTimeNormal() {
        return stirIntervalTimeNormal;
    }

    public void setStirIntervalTimeNormal(String stirIntervalTimeNormal) {
        this.stirIntervalTimeNormal = stirIntervalTimeNormal;
    }

    public String getStirRunTimePowerSaving() {
        return stirRunTimePowerSaving;
    }

    public void setStirRunTimePowerSaving(String stirRunTimePowerSaving) {
        this.stirRunTimePowerSaving = stirRunTimePowerSaving;
    }

    public String getStirIntervalPowerSaving() {
        return stirIntervalPowerSaving;
    }

    public void setStirIntervalPowerSaving(String stirIntervalPowerSaving) {
        this.stirIntervalPowerSaving = stirIntervalPowerSaving;
    }

    public HeartingTemperaturePowerSaving getHeartingTemperaturePowerSaving() {
        return heartingTemperaturePowerSaving;
    }

    public void setHeartingTemperaturePowerSaving(HeartingTemperaturePowerSaving heartingTemperaturePowerSaving) {
        this.heartingTemperaturePowerSaving = heartingTemperaturePowerSaving;
    }

    public String getInletLimitedAlarmTotalMax() {
        return inletLimitedAlarmTotalMax;
    }

    public void setInletLimitedAlarmTotalMax(String inletLimitedAlarmTotalMax) {
        this.inletLimitedAlarmTotalMax = inletLimitedAlarmTotalMax;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAppMode() {
        return appMode;
    }

    public String getInletLimitedAlarmTotalMin() {
        return inletLimitedAlarmTotalMin;
    }

    public void setInletLimitedAlarmTotalMin(String inletLimitedAlarmTotalMin) {
        this.inletLimitedAlarmTotalMin = inletLimitedAlarmTotalMin;
    }

    public void setAppMode(String appMode) {
        this.appMode = appMode;
    }

    public String getWeighingMode() {
        return weighingMode;
    }

    public void setWeighingMode(String weighingMode) {
        this.weighingMode = weighingMode;
    }

    public String getLoginMode() {
        return loginMode;
    }

    public void setLoginMode(String loginMode) {
        this.loginMode = loginMode;
    }

    public String getInletMode() {
        return inletMode;
    }

    public void setInletMode(String inletMode) {
        this.inletMode = inletMode;
    }

    public HeartingTemperature getHeartingTemperature() {
        return heartingTemperature;
    }

    public void setHeartingTemperature(HeartingTemperature heartingTemperature) {
        this.heartingTemperature = heartingTemperature;
    }

    public HumiditySetting getHumiditySetting() {
        return humiditySetting;
    }

    public void setHumiditySetting(HumiditySetting humiditySetting) {
        this.humiditySetting = humiditySetting;
    }

    public int getHeaterMode() {
        return heaterMode;
    }

    public void setHeaterMode(int heaterMode) {
        this.heaterMode = heaterMode;
    }

    public int getDehumidificationMode() {
        return dehumidificationMode;
    }

    public void setDehumidificationMode(int dehumidificationMode) {
        this.dehumidificationMode = dehumidificationMode;
    }

    public String getInletLimitedTotal() {
        return inletLimitedTotal;
    }

    public void setInletLimitedTotal(String inletLimitedTotal) {
        this.inletLimitedTotal = inletLimitedTotal;
    }

    public String getInletLimited24H() {
        return inletLimited24H;
    }

    public void setInletLimited24H(String inletLimited24H) {
        this.inletLimited24H = inletLimited24H;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getDeviceMode() {
        return deviceMode;
    }

    public void setDeviceMode(int deviceMode) {
        this.deviceMode = deviceMode;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getLoginPW() {
        return loginPW;
    }

    public void setLoginPW(String loginPW) {
        this.loginPW = loginPW;
    }

    public String getFanRunTimePowerSaving() {
        return fanRunTimePowerSaving;
    }

    public void setFanRunTimePowerSaving(String fanRunTimePowerSaving) {
        this.fanRunTimePowerSaving = fanRunTimePowerSaving;
    }

    public String getFanIntervalPowerSaving() {
        return fanIntervalPowerSaving;
    }

    public void setFanIntervalPowerSaving(String fanIntervalPowerSaving) {
        this.fanIntervalPowerSaving = fanIntervalPowerSaving;
    }

    public boolean isObservePortAlarm() {
        return observePortAlarm;
    }

    public void setObservePortAlarm(boolean observePortAlarm) {
        this.observePortAlarm = observePortAlarm;
    }

    public String getInletOpenVoltage() {
        return inletOpenVoltage;
    }

    public void setInletOpenVoltage(String inletOpenVoltage) {
        this.inletOpenVoltage = inletOpenVoltage;
    }

    public String getInletCloseVoltage() {
        return inletCloseVoltage;
    }

    public void setInletCloseVoltage(String inletCloseVoltage) {
        this.inletCloseVoltage = inletCloseVoltage;
    }

    public String getInletQuicklyCloseTime() {
        return inletQuicklyCloseTime;
    }

    public void setInletQuicklyCloseTime(String inletQuicklyCloseTime) {
        this.inletQuicklyCloseTime = inletQuicklyCloseTime;
    }

    public int getWeighingSourceSetting() {
        return weighingSourceSetting;
    }

    public void setWeighingSourceSetting(int weighingSourceSetting) {
        this.weighingSourceSetting = weighingSourceSetting;
    }

    public String getWeightCalibrationNum() {
        return weightCalibrationNum;
    }

    public void setWeightCalibrationNum(String weightCalibrationNum) {
        this.weightCalibrationNum = weightCalibrationNum;
    }

    public int getWeighingUnit() {
        return weighingUnit;
    }

    public void setWeighingUnit(int weighingUnit) {
        this.weighingUnit = weighingUnit;
    }

    public int getHumidityAlarmHumidity() {
        return humidityAlarmHumidity;
    }

    public void setHumidityAlarmHumidity(int humidityAlarmHumidity) {
        this.humidityAlarmHumidity = humidityAlarmHumidity;
    }

    public boolean isHumidityAlarmSwitch() {
        return humidityAlarmSwitch;
    }

    public void setHumidityAlarmSwitch(boolean humidityAlarmSwitch) {
        this.humidityAlarmSwitch = humidityAlarmSwitch;
    }

    public String getStirReverseRunTimeNormal() {
        return stirReverseRunTimeNormal;
    }

    public void setStirReverseRunTimeNormal(String stirReverseRunTimeNormal) {
        this.stirReverseRunTimeNormal = stirReverseRunTimeNormal;
    }

    public String getStirReverseIntervalTimeNormal() {
        return stirReverseIntervalTimeNormal;
    }

    public void setStirReverseIntervalTimeNormal(String stirReverseIntervalTimeNormal) {
        this.stirReverseIntervalTimeNormal = stirReverseIntervalTimeNormal;
    }

    public String getStirReverseRunTimePowerSaving() {
        return stirReverseRunTimePowerSaving;
    }

    public void setStirReverseRunTimePowerSaving(String stirReverseRunTimePowerSaving) {
        this.stirReverseRunTimePowerSaving = stirReverseRunTimePowerSaving;
    }

    public String getStirReverseIntervalPowerSaving() {
        return stirReverseIntervalPowerSaving;
    }

    public void setStirReverseIntervalPowerSaving(String stirReverseIntervalPowerSaving) {
        this.stirReverseIntervalPowerSaving = stirReverseIntervalPowerSaving;
    }

    /**
     * 加热温度设置-normal，目标温度和浮动温度，加热温度=目标-浮动 停止加热温度=目标+浮动
     */
    public static class HeartingTemperature {
        /**
         * 目标温度 1 前
         */
        private String targetTem;
        /**
         * 浮动温度 1 前
         */
        private String floatTem;
        /**
         * 报警最大温度
         */
        private String maxTemp;
        /**
         * 报警最小温度
         */
        private String minTemp;
        /**
         * 报警温度最小部分 开机或接触急停后 忽略时间
         */
        private String ignoreTime;

        /**
         * 目标温度 2 后
         */
        private String targetTem2;
        /**
         * 浮动温度 2 后
         */
        private String floatTem2;

        public HeartingTemperature() {
        }


        public HeartingTemperature(String targetTem, String floatTem, String maxTemp,
                                   String minTemp, String ignoreTime, String targetTem2, String floatTem2) {
            this.targetTem = targetTem;
            this.floatTem = floatTem;
            this.maxTemp = maxTemp;
            this.minTemp = minTemp;
            this.ignoreTime = ignoreTime;
            this.targetTem2 = targetTem2;
            this.floatTem2 = floatTem2;
        }

        public String getIgnoreTime() {
            return ignoreTime;
        }

        public void setIgnoreTime(String ignoreTime) {
            this.ignoreTime = ignoreTime;
        }

        public String getMinTemp() {
            return minTemp;
        }

        public void setMinTemp(String minTemp) {
            this.minTemp = minTemp;
        }

        public String getTargetTem() {
            return targetTem;
        }

        public void setTargetTem(String targetTem) {
            this.targetTem = targetTem;
        }

        public String getFloatTem() {
            return floatTem;
        }

        public void setFloatTem(String floatTem) {
            this.floatTem = floatTem;
        }

        public String getMaxTemp() {
            return maxTemp;
        }

        public void setMaxTemp(String maxTemp) {
            this.maxTemp = maxTemp;
        }

        public String getTargetTem2() {
            return targetTem2;
        }

        public void setTargetTem2(String targetTem2) {
            this.targetTem2 = targetTem2;
        }

        public String getFloatTem2() {
            return floatTem2;
        }

        public void setFloatTem2(String floatTem2) {
            this.floatTem2 = floatTem2;
        }
    }

    /**
     * 加热温度设置-节电模式 power saving，目标温度和浮动温度，加热温度=目标-浮动 停止加热温度=目标+浮动
     */
    public static class HeartingTemperaturePowerSaving {
        /**
         * 目标温度 1 前
         */
        private String targetTem;
        /**
         * 浮动温度 1 前
         */
        private String floatTem;

        /**
         * 目标温度 2 后
         */
        private String targetTem2;
        /**
         * 浮动温度 2 后
         */
        private String floatTem2;


        public HeartingTemperaturePowerSaving() {
        }

        public HeartingTemperaturePowerSaving(String targetTem, String floatTem,
                                              String targetTem2, String floatTem2) {
            this.targetTem = targetTem;
            this.floatTem = floatTem;
            this.targetTem2 = targetTem2;
            this.floatTem2 = floatTem2;
        }

        public String getTargetTem() {
            return targetTem;
        }

        public void setTargetTem(String targetTem) {
            this.targetTem = targetTem;
        }

        public String getFloatTem() {
            return floatTem;
        }

        public void setFloatTem(String floatTem) {
            this.floatTem = floatTem;
        }

        public String getTargetTem2() {
            return targetTem2;
        }

        public void setTargetTem2(String targetTem2) {
            this.targetTem2 = targetTem2;
        }

        public String getFloatTem2() {
            return floatTem2;
        }

        public void setFloatTem2(String floatTem2) {
            this.floatTem2 = floatTem2;
        }
    }

    public void setSterilizationModeTemp(String sterilizationModeTemp) {
        this.sterilizationModeTemp = sterilizationModeTemp;
    }

    public String getSterilizationModeTime() {
        return sterilizationModeTime;
    }

    public void setSterilizationModeTime(String sterilizationModeTime) {
        this.sterilizationModeTime = sterilizationModeTime;
    }

    public boolean isWeightDecimalShow() {
        return weightDecimalShow;
    }

    public void setWeightDecimalShow(boolean weightDecimalShow) {
        this.weightDecimalShow = weightDecimalShow;
    }

    /**
     * 风扇湿度设置，包含风扇档位及对应的湿度、电压
     */
    public static class FanHumiditySettingBean {
        /**
         * 风扇湿度档位 分为1,2,3，每档分别有对应的湿度及电压
         * 湿度：{@link AdminParameterBean.FanHumiditySettingBean#fanHumidityNum}
         * 电压：{@link AdminParameterBean.FanHumiditySettingBean#fanHumidityVoltage}
         */
        private int fanHumidityGear;
        /**
         * 风扇湿度档位对应的湿度数值，百分比 %
         */
        private int fanHumidityNum;
        /**
         * 风扇湿度电压 单位V
         */
        private int fanHumidityVoltage;

        public FanHumiditySettingBean() {
        }

        public FanHumiditySettingBean(int fanHumidityGear,
                                      int fanHumidityNum, int fanHumidityVoltage) {
            this.fanHumidityGear = fanHumidityGear;
            this.fanHumidityNum = fanHumidityNum;
            this.fanHumidityVoltage = fanHumidityVoltage;
        }

        public int getFanHumidityGear() {
            return fanHumidityGear;
        }

        public void setFanHumidityGear(int fanHumidityGear) {
            this.fanHumidityGear = fanHumidityGear;
        }

        public int getFanHumidityNum() {
            return fanHumidityNum;
        }

        public void setFanHumidityNum(int fanHumidityNum) {
            this.fanHumidityNum = fanHumidityNum;
        }

        public int getFanHumidityVoltage() {
            return fanHumidityVoltage;
        }

        public void setFanHumidityVoltage(int fanHumidityVoltage) {
            this.fanHumidityVoltage = fanHumidityVoltage;
        }
    }
}
