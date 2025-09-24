package com.hyeprion.foodrecyclingsystem.util;

import android.os.Handler;

import com.blankj.utilcode.util.LogUtils;
import com.hyeprion.foodrecyclingsystem.base.MyApplication;
import com.hyeprion.foodrecyclingsystem.util.port.PortConstants;
import com.hyeprion.foodrecyclingsystem.util.port.PortControlUtil;

/**
 * 首页4个倒计时任务
 * 搅拌电机运转逻辑：正转-停止-反转-停止-正转
 */
public class LoginCountTimerTask {
    private static LoginCountTimerTask instance = new LoginCountTimerTask();
    private Handler stirRunNormalHandler; // 搅拌正转运行 Normal
    private Runnable stirRunNormalRunnable;
    private Handler stirReverseRunNormalHandler;// 搅拌反转运行 Normal
    private Runnable stirReverseRunNormalRunnable;
    private Handler stirIntervalNormalHandler; // 搅拌正转间隔 Normal
    private Runnable stirIntervalNormalRunnable;
    private Handler stirReverseIntervalNormalHandler;// 搅拌反转间隔 Normal
    private Runnable stirReverseIntervalNormalRunnable;

    private Handler stirRunPowerSavingHandler;// 搅拌正转运行 PowerSaving
    private Runnable stirRunPowerSavingRunnable;
    private Handler stirReverseRunPowerSavingHandler;// 搅拌反转运行 PowerSaving
    private Runnable stirReverseRunPowerSavingRunnable;
    private Handler stirIntervalPowerSavingHandler;// 搅拌正转间隔 PowerSaving
    private Runnable stirIntervalPowerSavingRunnable;
    private Handler stirReverseIntervalPowerSavingHandler;// 搅拌反转间隔 PowerSaving
    private Runnable stirReverseIntervalPowerSavingRunnable;

    private Handler fanRunPowerSavingHandler;
    private Runnable fanRunPowerSavingRunnable;
    private Handler fanIntervalPowerSavingHandler;
    private Runnable fanIntervalPowerSavingRunnable;
    private boolean cancelFlag = false;

    private LoginCountTimerTask() {
    }

    public static LoginCountTimerTask getInstance() {
        return instance;
    }

    /**
     * 搅拌正转运行 normal 1
     */
    public void stirRunNormal() {
        LogUtils.e("stirRunNormal 正转开始运行了");
        if (stirRunNormalHandler == null) {
            stirRunNormalHandler = new Handler();
        }
        if (stirRunNormalRunnable == null) {
            stirRunNormalRunnable = () -> {
//                judgeInlet();
                if (judgeInlet()) {
                    stirIntervalNormal();
                }else {
                    PortControlUtil.getInstance().sendCommands(PortConstants.STIR_STOP);
                    LogUtils.e("stirRunNormal 正转运行了" + MyApplication.adminParameterBean.getStirRunTimeNormal() + "分钟，开始正转停止");
                    stirIntervalNormal();
                }

            };
        }
        stirRunNormalHandler.postDelayed(stirRunNormalRunnable,
                Integer.parseInt(MyApplication.adminParameterBean.getStirRunTimeNormal()) * Constant.minute);
//        stirRunNormalHandler.postDelayed(stirRunNormalRunnable,20*Constant.second);
    }

    /**
     * 搅拌正转间隔 normal 2
     */
    private void stirIntervalNormal() {
        LogUtils.e("stirIntervalNormal 正转开始停止了");
        if (stirIntervalNormalHandler == null) {
            stirIntervalNormalHandler = new Handler();
        }
        if (stirIntervalNormalRunnable == null) {
            stirIntervalNormalRunnable = () -> {
                if (judgeInlet()) {
                    stirReverseRunNormal();
                }else {
                    // 倒计时结束，搅拌反转开始
                    PortControlUtil.getInstance().sendCommands(PortConstants.STIR_REVERSES);
                    LogUtils.e("stirIntervalNormal 正转停止了" + MyApplication.adminParameterBean.getStirIntervalTimeNormal() + "分钟，开始反转运行");
                    stirReverseRunNormal();
                }

            };
        }
        stirIntervalNormalHandler.postDelayed(stirIntervalNormalRunnable,
                Integer.parseInt(MyApplication.adminParameterBean.getStirIntervalTimeNormal()) * Constant.minute);
//        stirIntervalNormalHandler.postDelayed(stirIntervalNormalRunnable,3*Constant.second);
    }

    /**
     * 搅拌反转运行 normal 3
     */
    private void stirReverseRunNormal() {
        LogUtils.e("stirReverseRunNormal 反转开始运行了");
        if (stirReverseRunNormalHandler == null) {
            stirReverseRunNormalHandler = new Handler();
        }
        if (stirReverseRunNormalRunnable == null) {
            stirReverseRunNormalRunnable = () -> {
                if (judgeInlet()) {
                    stirReverseIntervalNormal();
                }else {
                    PortControlUtil.getInstance().sendCommands(PortConstants.STIR_STOP);
                    LogUtils.e("stirReverseRunNormal 反转运行了" + MyApplication.adminParameterBean.getStirReverseRunTimeNormal() + "分钟，开始反转停止");
                    stirReverseIntervalNormal();
                }
            };
        }
        stirReverseRunNormalHandler.postDelayed(stirReverseRunNormalRunnable,
                Integer.parseInt(MyApplication.adminParameterBean.getStirReverseRunTimeNormal()) * Constant.minute);
//        stirReverseRunNormalHandler.postDelayed(stirReverseRunNormalRunnable,20*Constant.second);
    }

    /**
     * 搅拌反转间隔 normal 4
     */
    private void stirReverseIntervalNormal() {
        LogUtils.e("stirReverseIntervalNormal 反转开始停止了");
        if (stirReverseIntervalNormalHandler == null) {
            stirReverseIntervalNormalHandler = new Handler();
        }
        if (stirReverseIntervalNormalRunnable == null) {
            stirReverseIntervalNormalRunnable = () -> {
                if (judgeInlet()) {
                    stirRunNormal();
                }else {
                    // 倒计时结束，搅拌正转开始
                    PortControlUtil.getInstance().sendCommands(PortConstants.STIR_FORWARD);
                    LogUtils.e("stirReverseIntervalNormal 反转停止了" + MyApplication.adminParameterBean.getStirReverseIntervalTimeNormal() + "分钟，开始正转运行");
                    stirRunNormal();
                }

            };
        }
        stirReverseIntervalNormalHandler.postDelayed(stirReverseIntervalNormalRunnable,
                Integer.parseInt(MyApplication.adminParameterBean.getStirReverseIntervalTimeNormal()) * Constant.minute);
//        stirReverseIntervalNormalHandler.postDelayed(stirReverseIntervalNormalRunnable,3*Constant.second);
    }


    /**
     * 搅拌正转运行 PowerSaving 1
     */
    public void stirRunPowerSaving() {
        LogUtils.e("stirRunPowerSaving 正转开始运行了");

        if (stirRunPowerSavingHandler == null) {
            stirRunPowerSavingHandler = new Handler();
        }
        if (stirRunPowerSavingRunnable == null) {
            stirRunPowerSavingRunnable = () -> {
                if (judgeInlet()) {
                    stirIntervalPowerSaving();
                }else {
                    // 倒计时结束，搅拌正转停止
                    PortControlUtil.getInstance().sendCommands(PortConstants.STIR_STOP);
                    LogUtils.e("stirRunPowerSaving 运行了" + MyApplication.adminParameterBean.getStirRunTimePowerSaving() + "分钟，开始正转停止");
                    stirIntervalPowerSaving();
                }

            };
        }
        stirRunPowerSavingHandler.postDelayed(stirRunPowerSavingRunnable, Integer.parseInt(MyApplication.adminParameterBean.getStirRunTimePowerSaving()) * Constant.minute);
//        stirRunPowerSavingHandler.postDelayed(stirRunPowerSavingRunnable,10*Constant.second);
    }

    /**
     * 搅拌正转间隔 PowerSaving 2
     */
    private void stirIntervalPowerSaving() {
        LogUtils.e("stirIntervalPowerSaving 正转开始停止了");

        if (stirIntervalPowerSavingHandler == null) {
            stirIntervalPowerSavingHandler = new Handler();
        }
        if (stirIntervalPowerSavingRunnable == null) {
            stirIntervalPowerSavingRunnable = () -> {
                if (judgeInlet()) {
                    stirReverseRunPowerSaving();
                }else {
                    // 倒计时结束，搅拌反转开始
                    PortControlUtil.getInstance().sendCommands(PortConstants.STIR_REVERSES);
                    LogUtils.e("stirIntervalPowerSaving 正转停止了" + MyApplication.adminParameterBean.getStirIntervalPowerSaving() + "分钟，开始反转运行");
                    stirReverseRunPowerSaving();
                }

            };
        }
        stirIntervalPowerSavingHandler.postDelayed(stirIntervalPowerSavingRunnable, Integer.parseInt(MyApplication.adminParameterBean.getStirIntervalPowerSaving()) * Constant.minute);
//        stirIntervalPowerSavingHandler.postDelayed(stirIntervalPowerSavingRunnable,10*Constant.second);
    }

    /**
     * 搅拌反转运行 PowerSaving 3
     */
    private void stirReverseRunPowerSaving() {
        LogUtils.e("stirReverseRunPowerSaving 反转开始运行了");

        if (stirReverseRunPowerSavingHandler == null) {
            stirReverseRunPowerSavingHandler = new Handler();
        }
        if (stirReverseRunPowerSavingRunnable == null) {
            stirReverseRunPowerSavingRunnable = () -> {
                if (judgeInlet()) {
                    stirReverseIntervalPowerSaving();
                }else {
                    // 倒计时结束，搅拌反转停止
                    PortControlUtil.getInstance().sendCommands(PortConstants.STIR_STOP);
                    LogUtils.e("stirReverseRunPowerSaving 反转运行了" + MyApplication.adminParameterBean.getStirReverseRunTimePowerSaving() + "分钟，开始反转停止");
                    stirReverseIntervalPowerSaving();
                }

            };
        }
        stirReverseRunPowerSavingHandler.postDelayed(stirReverseRunPowerSavingRunnable,
                Integer.parseInt(MyApplication.adminParameterBean.getStirReverseRunTimePowerSaving()) * Constant.minute);
//        stirReverseRunPowerSavingHandler.postDelayed(stirReverseRunPowerSavingRunnable,10*Constant.second);
    }

    /**
     * 搅拌反转间隔 PowerSaving 4
     */
    private void stirReverseIntervalPowerSaving() {
        LogUtils.e("stirReverseIntervalPowerSaving 开始反转停止了");

        if (stirReverseIntervalPowerSavingHandler == null) {
            stirReverseIntervalPowerSavingHandler = new Handler();
        }
        if (stirReverseIntervalPowerSavingRunnable == null) {
            stirReverseIntervalPowerSavingRunnable = () -> {
                if (judgeInlet()) {
                    stirRunPowerSaving();
                }else {
                    // 倒计时结束，搅拌正转开始
                    PortControlUtil.getInstance().sendCommands(PortConstants.STIR_FORWARD);
                    LogUtils.e("stirReverseIntervalPowerSaving 反转停止了" + MyApplication.adminParameterBean.getStirReverseIntervalPowerSaving() + "分钟，开始正转运行");
                    stirRunPowerSaving();
                }

            };
        }
        stirReverseIntervalPowerSavingHandler.postDelayed(stirReverseIntervalPowerSavingRunnable,
                Integer.parseInt(MyApplication.adminParameterBean.getStirReverseIntervalPowerSaving()) * Constant.minute);
//        stirIntervalPowerSavingHandler.postDelayed(stirIntervalPowerSavingRunnable,10*Constant.second);
    }


    public void fanRunPowerSaving() {
        LogUtils.e("fanRunPowerSaving 开始运行了");

        if (fanRunPowerSavingHandler == null) {
            fanRunPowerSavingHandler = new Handler();
        }
        if (fanRunPowerSavingRunnable == null) {
            fanRunPowerSavingRunnable = () -> {
                if (judgeInlet()) {
                    fanIntervalPowerSaving();
                }else {
                    // 倒计时结束，风扇停止
                    PortControlUtil.getInstance().sendCommands(PortConstants.FAN1_STOP);
                    LogUtils.e("fanRunPowerSaving 运行了" + MyApplication.adminParameterBean.getFanRunTimePowerSaving() + "分钟，开始停止");
                    fanIntervalPowerSaving();
                }


            };
        }
        fanRunPowerSavingHandler.postDelayed(fanRunPowerSavingRunnable, Integer.parseInt(MyApplication.adminParameterBean.getFanRunTimePowerSaving()) * Constant.minute);
//        fanRunPowerSavingHandler.postDelayed(fanRunPowerSavingRunnable,10*Constant.second);
    }

    private void fanIntervalPowerSaving() {
        LogUtils.e("fanIntervalPowerSaving 开始停止了");

        if (fanIntervalPowerSavingHandler == null) {
            fanIntervalPowerSavingHandler = new Handler();
        }
        if (fanIntervalPowerSavingRunnable == null) {
            fanIntervalPowerSavingRunnable = () -> {
                if (judgeInlet()) {
                    fanRunPowerSaving();
                }else {
                    // 倒计时结束，风扇开始
                    PortControlUtil.getInstance().sendCommands(PortConstants.FAN1_AUTOMATIC);
                    LogUtils.e("fanIntervalPowerSaving 停止了" + MyApplication.adminParameterBean.getFanIntervalPowerSaving() + "分钟，开始运行");
                    fanRunPowerSaving();
                }

            };
        }
        fanIntervalPowerSavingHandler.postDelayed(fanIntervalPowerSavingRunnable, Integer.parseInt(MyApplication.adminParameterBean.getFanIntervalPowerSaving()) * Constant.minute);
//        fanIntervalPowerSavingHandler.postDelayed(fanIntervalPowerSavingRunnable,10*Constant.second);
    }


    /*
    判断投入口或是观察口是否打开
     */
    public boolean judgeInlet() {
        return PortControlUtil.getInstance().getPortStatus().getInletStatus() == 2 ||
                PortControlUtil.getInstance().getPortStatus().getInletStatus() == 7||
                PortControlUtil.getInstance().getPortStatus().getObserveDoorStatus()==0;
    }


    public void cancel() {
        if (stirRunNormalHandler != null) {
            stirRunNormalHandler.removeCallbacks(stirRunNormalRunnable);
            stirRunNormalHandler.removeCallbacksAndMessages(null);
        }
        if (stirIntervalNormalHandler != null) {
            stirIntervalNormalHandler.removeCallbacks(stirIntervalNormalRunnable);
            stirIntervalNormalHandler.removeCallbacksAndMessages(null);
        }
        if (stirReverseRunNormalHandler != null) {
            stirReverseRunNormalHandler.removeCallbacks(stirReverseRunNormalRunnable);
            stirReverseRunNormalHandler.removeCallbacksAndMessages(null);
        }
        if (stirReverseIntervalNormalHandler != null) {
            stirReverseIntervalNormalHandler.removeCallbacks(stirReverseIntervalNormalRunnable);
            stirReverseIntervalNormalHandler.removeCallbacksAndMessages(null);
        }
        if (stirRunPowerSavingHandler != null) {
            stirRunPowerSavingHandler.removeCallbacks(stirRunPowerSavingRunnable);
            stirRunPowerSavingHandler.removeCallbacksAndMessages(null);
        }
        if (stirIntervalPowerSavingHandler != null) {
            stirIntervalPowerSavingHandler.removeCallbacks(stirIntervalPowerSavingRunnable);
            stirIntervalPowerSavingHandler.removeCallbacksAndMessages(null);
        }
        if (stirReverseRunPowerSavingHandler != null) {
            stirReverseRunPowerSavingHandler.removeCallbacks(stirReverseRunPowerSavingRunnable);
            stirReverseRunPowerSavingHandler.removeCallbacksAndMessages(null);
        }
        if (stirReverseIntervalPowerSavingHandler != null) {
            stirReverseIntervalPowerSavingHandler.removeCallbacks(stirReverseIntervalPowerSavingRunnable);
            stirReverseIntervalPowerSavingHandler.removeCallbacksAndMessages(null);
        }


        if (fanRunPowerSavingHandler != null) {
            fanRunPowerSavingHandler.removeCallbacks(fanRunPowerSavingRunnable);
            fanRunPowerSavingHandler.removeCallbacksAndMessages(null);
        }
        if (fanIntervalPowerSavingHandler != null) {
            fanIntervalPowerSavingHandler.removeCallbacks(fanIntervalPowerSavingRunnable);
            fanIntervalPowerSavingHandler.removeCallbacksAndMessages(null);
        }

    }
}
