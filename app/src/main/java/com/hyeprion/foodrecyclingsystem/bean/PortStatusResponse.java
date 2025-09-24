package com.hyeprion.foodrecyclingsystem.bean;

/**
 * 通过串口查询当前各电机状态
 */
public class PortStatusResponse {
    /**
     * 投入口电机状态
     * 0关闭
     * 1打开
     */
    private int inletStatus;
    /**
     * 称重板电机状态
     * 0关闭
     * 1打开
     */
    private int weighingStatus;
    /**
     * 搅拌电机状态
     * 0反转
     * 1正转
     * 2停止
     */
    private int stirStatus;
    /**
     * 排出电机状态
     * 0反转
     * 1正转
     * 2停止
     */
    private int outputStatus = 1;
    /**
     * 风扇状态
     * 0停止
     * 1打开
     */
    private int fanStatus = 1;
    /**
     * 灯光状态
     * 1-7 分别为 红、绿、蓝、黄、青、紫、白
     */
    private int lightStatus = 1;
//    /**
//     * 投入口状态 0:关闭 1：开启
//     */
//    private int inletStatus;

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

    private String stsTimer;
    private String errorNoti;
    private String controlerSettings;
}
