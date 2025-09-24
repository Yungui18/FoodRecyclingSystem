package com.hyeprion.foodrecyclingsystem.bean;

/**
 * 通过串口查询当前各电机状态
 */
public class PortStatus {
    /**
     * 投入口传感器状态
     * 开门传感器触发04；关门传感器被触发02；减速传感器被触发01；关门传感器和减速传感器同时触发03
     */
    private int inletSensorStatus;
    /**
     * 投入口电机状态
     *  01：开门中，02：开门完成；03：低速关门中；04：高速关门中；05：关门完成；06：夹手;07：未关门
     */
    private int inletStatus;
    /**
     * 观察口关闭与否  01：关闭  00：未关闭
     */
    private int observeDoorStatus;
    /**
     * 排出口关闭与否  01：关闭  00：未关闭
     */
    private int outletStatus;
    /**
     * 搅拌电机状态 01：正转中；02：反转中；03：停止；05:异常
     */
    private int stirStatus;
    /**
     * 称重模式设置 01 ：置零，02：去皮，
     */
    private int pressureSetting;
    /**
     * LED1灯模式 01：常亮；02：常灭；03：闪烁；04：呼吸
     */
    private int led1;
    /**
     * 灯 RGB 0111,其中后三位分别代表R,G,B  1代表FF,0代表00  0111=FFFFFF  0100=FF0000
     */
    private int led1RGB;
    /**
     * LED2灯模式 01：常亮；02：常灭；03：闪烁；04：呼吸
     */
    private int led2;
    /**
     * 灯 RGB 0111,其中后三位分别代表R,G,B  1代表FF,0代表00  0111=FFFFFF  0100=FF0000
     */
    private int led2RGB;

    /**
     * 风扇状态
     * 01：手动；02：停止；11：自动（1档)；12：自动（2档)；13：自动（3档)；10：自动（停止中）；05：异常
     */
    private int fan1 = 1;
    /**
     * 风扇状态
     * 01：手动；02：停止；11：自动（1档)；12：自动（2档)；13：自动（3档)；10：自动（停止中）；05：异常
     */
    private int fan2 = 1;
    /**
     * 加热1模式 01：手动；02：停止；11：自动（运行中)；10：自动（停止中）；05：异常
     */
    private int heater1;
    /**
     * 加热2模式 01：手动；02：停止；11：自动（运行中)；10：自动（停止中）；05：异常
     */
    private int heater2;
    /**
     * 温度值（精确至0.1度）
     */
    private float temperature;
    /**
     * 湿度值（精确至0.01%）
     */
    private float humidity;
    /**
     * 除湿器(烘干机)模式 01：手动；02：停止；11：自动（运行中)；10：自动（停止中）；05：异常
     */
    private int dehumidifier;


    /**
     * 毛重 Float
     */
    private float grossWeight;
    /**
     * 净重 Float
     */
    private float netWeight;
    /**
     * 皮重 Float
     */
    private float tare;
    /**
     * 加热温度-油温1
     */
    private float heaterTemperature1;
    /**
     * 加热温度-油温2
     */
    private float heaterTemperature2;
    /**
     * 照明 01：常亮；02：长灭
     */
    private int lighting;
    /**
     * 紧急停止开关 01：紧急停止；00：未紧急停止
     */
    private int stop;

    /**
     * 开门按钮  01:触发传感器； 00 未触发
     */
    private int openDoorBtn;

    /**
     * 风压值 01异常 00正常
     *
     */
    private int windPressure;

    /**
     * lock1 状态  01：解锁；02：上锁
     */
    private int lock1;

    /**
     * lock2 状态  01：解锁；02：上锁
     */
    private int lock2;
    /**
     * 直连称重1 重量 = 直连称重1整数部分 + "." + 直连称重1小数部分
     */
    private float directWeighing1;
    /**
     * 直连称重2 重量 = 直连称重2整数部分 + "." + 直连称重2小数部分
     */
    private float directWeighing2;

    /**
     * 下位机固件版本号 V+XX+"."+YY
     */
    private String firmwareVersion;

    /**
     *  当前选择用作计量的重量：netWeight  directWeighing1  directWeighing2 中的一个
     */
    private float chooseUseWeighing;

    /**
     * 判断下位机是否重启
     * 0：初始状态未设置或是重启
     * 1：手动设置，为此值表示下位机未重启
     */
    private int judgeRestart;

    public PortStatus() {
    }

    public PortStatus(int inletSensorStatus, int inletStatus, int observeDoorStatus,
                      int outletStatus, int stirStatus, int pressureSetting, int led1,
                      int led1RGB, int led2, int led2RGB, int fan1, int fan2, int heater1,
                      int heater2, float temperature, float humidity, int dehumidifier,
                      float grossWeight, float netWeight, float tare, float heaterTemperature1,
                      float heaterTemperature2, int lighting, int stop, int openDoorBtn, int windPressure) {
        this.inletSensorStatus = inletSensorStatus;
        this.inletStatus = inletStatus;
        this.observeDoorStatus = observeDoorStatus;
        this.outletStatus = outletStatus;
        this.stirStatus = stirStatus;
        this.pressureSetting = pressureSetting;
        this.led1 = led1;
        this.led1RGB = led1RGB;
        this.led2 = led2;
        this.led2RGB = led2RGB;
        this.fan1 = fan1;
        this.fan2 = fan2;
        this.heater1 = heater1;
        this.heater2 = heater2;
        this.temperature = temperature;
        this.humidity = humidity;
        this.dehumidifier = dehumidifier;
        this.grossWeight = grossWeight;
        this.netWeight = netWeight;
        this.tare = tare;
        this.heaterTemperature1 = heaterTemperature1;
        this.heaterTemperature2 = heaterTemperature2;
        this.lighting = lighting;
        this.stop = stop;
        this.openDoorBtn = openDoorBtn;
        this.windPressure = windPressure;
    }

    public int getInletStatus() {
        return inletStatus;
    }

    public void setInletStatus(int inletStatus) {
        this.inletStatus = inletStatus;
    }

    public int getObserveDoorStatus() {
        return observeDoorStatus;
    }

    public void setObserveDoorStatus(int observeDoorStatus) {
        this.observeDoorStatus = observeDoorStatus;
    }

    public int getOutletStatus() {
        return outletStatus;
    }

    public void setOutletStatus(int outletStatus) {
        this.outletStatus = outletStatus;
    }

    public int getStirStatus() {
        return stirStatus;
    }

    public void setStirStatus(int stirStatus) {
        this.stirStatus = stirStatus;
    }

    public int getPressureSetting() {
        return pressureSetting;
    }

    public void setPressureSetting(int pressureSetting) {
        this.pressureSetting = pressureSetting;
    }

    public int getLed1() {
        return led1;
    }

    public void setLed1(int led1) {
        this.led1 = led1;
    }

    public int getLed1RGB() {
        return led1RGB;
    }

    public void setLed1RGB(int led1RGB) {
        this.led1RGB = led1RGB;
    }

    public int getLed2() {
        return led2;
    }

    public void setLed2(int led2) {
        this.led2 = led2;
    }

    public int getLed2RGB() {
        return led2RGB;
    }

    public void setLed2RGB(int led2RGB) {
        this.led2RGB = led2RGB;
    }

    public int getFan1() {
        return fan1;
    }

    public void setFan1(int fan1) {
        this.fan1 = fan1;
    }

    public int getFan2() {
        return fan2;
    }

    public void setFan2(int fan2) {
        this.fan2 = fan2;
    }

    public int getHeater1() {
        return heater1;
    }

    public void setHeater1(int heater1) {
        this.heater1 = heater1;
    }

    public int getHeater2() {
        return heater2;
    }

    public void setHeater2(int heater2) {
        this.heater2 = heater2;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public int getDehumidifier() {
        return dehumidifier;
    }

    public void setDehumidifier(int dehumidifier) {
        this.dehumidifier = dehumidifier;
    }

    public float getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(float grossWeight) {
        this.grossWeight = grossWeight;
    }

    public float getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(float netWeight) {
        this.netWeight = netWeight;
    }

    public float getTare() {
        return tare;
    }

    public void setTare(float tare) {
        this.tare = tare;
    }

    public float getHeaterTemperature1() {
        return heaterTemperature1;
    }

    public void setHeaterTemperature1(float heaterTemperature1) {
        this.heaterTemperature1 = heaterTemperature1;
    }

    public float getHeaterTemperature2() {
        return heaterTemperature2;
    }

    public void setHeaterTemperature2(float heaterTemperature2) {
        this.heaterTemperature2 = heaterTemperature2;
    }

    public int getLighting() {
        return lighting;
    }

    public void setLighting(int lighting) {
        this.lighting = lighting;
    }

    public int getStop() {
        return stop;
    }

    public void setStop(int stop) {
        this.stop = stop;
    }

    public int getOpenDoorBtn() {
        return openDoorBtn;
    }

    public void setOpenDoorBtn(int openDoorBtn) {
        this.openDoorBtn = openDoorBtn;
    }

    public int getWindPressure() {
        return windPressure;
    }

    public void setWindPressure(int windPressure) {
        this.windPressure = windPressure;
    }

    public int getInletSensorStatus() {
        return inletSensorStatus;
    }

    public void setInletSensorStatus(int inletSensorStatus) {
        this.inletSensorStatus = inletSensorStatus;
    }

    public int getLock1() {
        return lock1;
    }

    public void setLock1(int lock1) {
        this.lock1 = lock1;
    }

    public int getLock2() {
        return lock2;
    }

    public void setLock2(int lock2) {
        this.lock2 = lock2;
    }

    public float getDirectWeighing1() {
        return directWeighing1;
    }

    public void setDirectWeighing1(float directWeighing1) {
        this.directWeighing1 = directWeighing1;
    }

    public float getDirectWeighing2() {
        return directWeighing2;
    }

    public void setDirectWeighing2(float directWeighing2) {
        this.directWeighing2 = directWeighing2;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public float getChooseUseWeighing() {
        return chooseUseWeighing;
    }

    public void setChooseUseWeighing(float chooseUseWeighing) {
        this.chooseUseWeighing = chooseUseWeighing;
    }

    public int getJudgeRestart() {
        return judgeRestart;
    }

    public void setJudgeRestart(int judgeRestart) {
        this.judgeRestart = judgeRestart;
    }

    @Override
    public String toString() {
        return "PortStatus{" +
                "inletSensorStatus=" + inletSensorStatus +
                ", inletStatus=" + inletStatus +
                ", observeDoorStatus=" + observeDoorStatus +
                ", outletStatus=" + outletStatus +
                ", stirStatus=" + stirStatus +
                ", pressureSetting=" + pressureSetting +
                ", led1=" + led1 +
                ", led1RGB=" + led1RGB +
                ", led2=" + led2 +
                ", led2RGB=" + led2RGB +
                ", fan1=" + fan1 +
                ", fan2=" + fan2 +
                ", heater1=" + heater1 +
                ", heater2=" + heater2 +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", dehumidifier=" + dehumidifier +
                ", grossWeight=" + grossWeight +
                ", netWeight=" + netWeight +
                ", tare=" + tare +
                ", heaterTemperature1=" + heaterTemperature1 +
                ", heaterTemperature2=" + heaterTemperature2 +
                ", lighting=" + lighting +
                ", stop=" + stop +
                ", openDoorBtn=" + openDoorBtn +
                ", windPressure=" + windPressure +
                ", lock1=" + lock1 +
                ", lock2=" + lock2 +
                ", directWeighing1=" + directWeighing1 +
                ", directWeighing2=" + directWeighing2 +
                ", firmwareVersion='" + firmwareVersion + '\'' +
                ", chooseUseWeighing=" + chooseUseWeighing +
                ", judgeRestart=" + judgeRestart +
                '}';
    }
}
