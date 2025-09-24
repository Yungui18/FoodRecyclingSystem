package com.hyeprion.foodrecyclingsystem.bean;

import java.io.Serializable;

/**
 * 通过串口获取的设备状态
 */
public class DeviceSerialPortEvent implements Serializable {
    /**
     * 1:自动状态  0:手动状态
     */
    private int isAuto;
    /**
     * 搅拌电机 0:关闭  1：开启
     * <li>{@link com.hyeprion.foodrecyclingsystem.util.Constant#TRUE}
     * <li>{@link com.hyeprion.foodrecyclingsystem.util.Constant#FALSE}
     */
    private int stsMotor;
    /**
     * 加热系统是否在工作 0:关闭 1：开启
     */
    private int stsWormLine;
    /**
     * fan风扇是否在工作 0:关闭 1：开启
     */
    private int stsVentilator;
    /**
     * 投入口状态 0:关闭 1：开启
     */
    private int stsDoor;
    private String stsTimer;
    private String errorNoti;
    private String controlerSettings;

    public DeviceSerialPortEvent() {
    }

    public DeviceSerialPortEvent(int isAuto, int stsMotor, int stsWormLine, int stsVentilator, int stsDoor) {
        this.isAuto = isAuto;
        this.stsMotor = stsMotor;
        this.stsWormLine = stsWormLine;
        this.stsVentilator = stsVentilator;
        this.stsDoor = stsDoor;
    }

    public DeviceSerialPortEvent(int isAuto, int stsMotor, int stsWormLine, int stsVentilator,
                                 int stsDoor, String stsTimer, String errorNoti, String controlerSettings) {
        this.isAuto = isAuto;
        this.stsMotor = stsMotor;
        this.stsWormLine = stsWormLine;
        this.stsVentilator = stsVentilator;
        this.stsDoor = stsDoor;
        this.stsTimer = stsTimer;
        this.errorNoti = errorNoti;
        this.controlerSettings = controlerSettings;
    }

    public int getIsAuto() {
        return isAuto;
    }

    public void setIsAuto(int isAuto) {
        this.isAuto = isAuto;
    }

    public int getStsMotor() {
        return stsMotor;
    }

    public void setStsMotor(int stsMotor) {
        this.stsMotor = stsMotor;
    }

    public int getStsWormLine() {
        return stsWormLine;
    }

    public void setStsWormLine(int stsWormLine) {
        this.stsWormLine = stsWormLine;
    }

    public int getStsVentilator() {
        return stsVentilator;
    }

    public void setStsVentilator(int stsVentilator) {
        this.stsVentilator = stsVentilator;
    }

    public int getStsDoor() {
        return stsDoor;
    }

    public void setStsDoor(int stsDoor) {
        this.stsDoor = stsDoor;
    }

    public String getStsTimer() {
        return stsTimer;
    }

    public void setStsTimer(String stsTimer) {
        this.stsTimer = stsTimer;
    }

    public String getErrorNoti() {
        return errorNoti;
    }

    public void setErrorNoti(String errorNoti) {
        this.errorNoti = errorNoti;
    }

    public String getControlerSettings() {
        return controlerSettings;
    }

    public void setControlerSettings(String controlerSettings) {
        this.controlerSettings = controlerSettings;
    }

    @Override
    public String toString() {
        return "DeviceSerialPortEvent{" +
                "isAuto=" + isAuto +
                ", stsMotor=" + stsMotor +
                ", stsWormLine=" + stsWormLine +
                ", stsVentilator=" + stsVentilator +
                ", stsDoor=" + stsDoor +
                ", stsTimer='" + stsTimer + '\'' +
                ", errorNoti='" + errorNoti + '\'' +
                ", controlerSettings='" + controlerSettings + '\'' +
                '}';
    }
}
