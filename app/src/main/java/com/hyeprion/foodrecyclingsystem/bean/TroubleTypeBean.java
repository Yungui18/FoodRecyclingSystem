package com.hyeprion.foodrecyclingsystem.bean;

/**
 * 故障内容
 * 1:搅拌电机异常-搅拌电机（IO 信号，高电平即报警）可以
 * 2:排料口未关闭-排料门打开（排料门传感器无信号持续3秒以上）4s
 * 3:观察口未关闭-上部检修门打开（排料门传感器无信号持续0.5秒以上）2s
 * 4:电动门（投入口）异常-电动门（触发动作后极限传感器感应时间超时）超时可设定 调成手动模式
 * 5:加热异常-加热报警（2通道独立报警）（加热启动后温度5分钟内不上升） 调成自动后5分钟，加热棒启动5分钟.
 * 6:称重异常-称重报警（总重量为负值，或总重量超过指定重量，保持投料门关闭状态时重量跳动范围在1分钟内超过指定值）
 * 7:湿度异常-湿度报警（湿度超过95%保持10分钟以上）
 * 8：风压异常，请确认过滤网-风压报警（IO信号，1分钟报警）
 */
public class TroubleTypeBean {
    /**
     * 整体是否存在故障  false 无故障  true
     */
    private boolean isTrouble = false;

    /**
     * 搅拌电机故障有无  0 无故障  1有故障  stir = 4
     */
    private int troubleStir;
    /**
     * 排出口故障有无  0 无故障  1有故障
     */
    private int troubleOutlet;
    /**
     * 上部检修门打开  0 未打开  1打开
     */
    private int troubleUpCheck;
    /**
     * 电动门故障有无  0 无故障  1有故障
     */
    private int troubleInlet;
    /**
     * 加热报警故障有无  0 无故障  1有故障
     */
    private int troubleHeaterMax;
    /**
     * 加热报警故障有无  0 无故障  1有故障
     */
    private int troubleHeaterMin;
    /**
     * 称重报警故障有无  0 无故障  1有故障
     */
    private int troubleWeigh;
    /**
     * 湿度报警故障有无  0 无故障  1有故障
     */
    private int troubleHumidity;
    /**
     * 风压报警（IO信号，1分钟报警）
     */
    private int troublePA;
    /**
     * 搅拌故障有无  0 无故障  1有故障  stir = 5
     */
    private int troubleStirError;
    /**
     * 无故障为0，其余1-7对应以下故障
     * 1:搅拌电机异常-搅拌电机（IO 信号，高电平即报警）可以
     * 2:排料口未关闭-排料门打开（排料门传感器无信号持续3秒以上）4s
     * 3:观察口未关闭-上部检修门打开（排料门传感器无信号持续0.5秒以上）2s
     * 4:电动门（投入口）异常-电动门（触发动作后极限传感器感应时间超时）超时可设定 调成手动模式
     * 5:加热异常-加热报警（2通道独立报警）（加热启动后温度5分钟内不上升） 调成自动后5分钟，加热棒启动5分钟.
     * 6:称重异常-称重报警（总重量为负值，或总重量超过指定重量，保持投料门关闭状态时重量跳动范围在1分钟内超过指定值）
     * 7:湿度异常-湿度报警（湿度超过95%保持10分钟以上）
     * 8：风压异常，请确认过滤网-风压报警（IO信号，1分钟报警）
     */
    private int troubleType = 0;

    public TroubleTypeBean() {
    }

    public TroubleTypeBean(boolean isTrouble, int troubleStir, int troubleOutlet,
                           int troubleUpCheck, int troubleInlet, int troubleHeaterMax,
                           int troubleHeaterMin, int troubleWeigh, int troubleHumidity,
                           int troublePA, int troubleType) {
        this.isTrouble = isTrouble;
        this.troubleStir = troubleStir;
        this.troubleOutlet = troubleOutlet;
        this.troubleUpCheck = troubleUpCheck;
        this.troubleInlet = troubleInlet;
        this.troubleHeaterMax = troubleHeaterMax;
        this.troubleHeaterMin = troubleHeaterMin;
        this.troubleWeigh = troubleWeigh;
        this.troubleHumidity = troubleHumidity;
        this.troublePA = troublePA;
        this.troubleType = troubleType;
    }

    public boolean isTrouble() {
        return isTrouble;
    }

    public void setTrouble(boolean trouble) {
        isTrouble = trouble;
    }

    public int getTroubleHeaterMax() {
        return troubleHeaterMax;
    }

    public void setTroubleHeaterMax(int troubleHeaterMax) {
        this.troubleHeaterMax = troubleHeaterMax;
    }

    public int getTroubleHeaterMin() {
        return troubleHeaterMin;
    }

    public void setTroubleHeaterMin(int troubleHeaterMin) {
        this.troubleHeaterMin = troubleHeaterMin;
    }

    public boolean getIsTrouble() {
        return isTrouble;
    }

    public void setIsTrouble(boolean isTrouble) {
        this.isTrouble = isTrouble;
    }

    public int getTroublePA() {
        return troublePA;
    }

    public void setTroublePA(int troublePA) {
        this.troublePA = troublePA;
    }

    public int getTroubleType() {
        return troubleType;
    }

    public void setTroubleType(int troubleType) {
        this.troubleType = troubleType;
    }

    public int getTroubleStir() {
        return troubleStir;
    }

    public void setTroubleStir(int troubleStir) {
        this.troubleStir = troubleStir;
    }

    public int getTroubleOutlet() {
        return troubleOutlet;
    }

    public void setTroubleOutlet(int troubleOutlet) {
        this.troubleOutlet = troubleOutlet;
    }

    public int getTroubleUpCheck() {
        return troubleUpCheck;
    }

    public void setTroubleUpCheck(int troubleUpCheck) {
        this.troubleUpCheck = troubleUpCheck;
    }

    public int getTroubleInlet() {
        return troubleInlet;
    }

    public void setTroubleInlet(int troubleInlet) {
        this.troubleInlet = troubleInlet;
    }

    public int getTroubleWeigh() {
        return troubleWeigh;
    }

    public void setTroubleWeigh(int troubleWeigh) {
        this.troubleWeigh = troubleWeigh;
    }

    public int getTroubleHumidity() {
        return troubleHumidity;
    }

    public void setTroubleHumidity(int troubleHumidity) {
        this.troubleHumidity = troubleHumidity;
    }

    public int getTroubleStirError() {
        return troubleStirError;
    }

    public void setTroubleStirError(int troubleStirError) {
        this.troubleStirError = troubleStirError;
    }
}
