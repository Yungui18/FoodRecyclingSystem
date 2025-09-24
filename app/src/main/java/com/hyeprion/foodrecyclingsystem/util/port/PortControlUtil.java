package com.hyeprion.foodrecyclingsystem.util.port;

import android.app.Dialog;
import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hyeprion.foodrecyclingsystem.R;
import com.hyeprion.foodrecyclingsystem.activity.LoginActivity;
import com.hyeprion.foodrecyclingsystem.base.MyApplication;
import com.hyeprion.foodrecyclingsystem.bean.AdminParameterBean;
import com.hyeprion.foodrecyclingsystem.bean.HumidityTrouble;
import com.hyeprion.foodrecyclingsystem.bean.PortStatus;
import com.hyeprion.foodrecyclingsystem.bean.ResetPowerSaving;
import com.hyeprion.foodrecyclingsystem.bean.TroubleTypeBean;
import com.hyeprion.foodrecyclingsystem.dialog.DialogManage;
import com.hyeprion.foodrecyclingsystem.util.ActivityUtil;
import com.hyeprion.foodrecyclingsystem.util.Constant;
import com.hyeprion.foodrecyclingsystem.util.DecimalFormatUtil;
import com.hyeprion.foodrecyclingsystem.util.GreenDaoUtil;
import com.hyeprion.foodrecyclingsystem.util.HTTPServerUtil;
import com.hyeprion.foodrecyclingsystem.util.SaveToSdUtil;
import com.hyeprion.foodrecyclingsystem.util.SharedPreFerUtil;
import com.hyeprion.foodrecyclingsystem.util.TimeUtil;
import com.hyeprion.foodrecyclingsystem.util.countdowntimer.CountTimer;
import com.hyeprion.foodrecyclingsystem.util.countdowntimer.MyCountDownTimer;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import top.maybesix.xhlibrary.serialport.ComPortData;
import top.maybesix.xhlibrary.serialport.SerialPortHelper;
import top.maybesix.xhlibrary.util.HexStringUtils;

/**
 * 投入口、搅拌电机、led、风扇、加热器、除湿器、称重传感器 串口 控制
 */
public class PortControlUtil implements SerialPortHelper.OnSerialPortReceivedListener {
    public static String ttyType = "ttyS3";
    //    public static String ttyType = "ttyUSB5";
    private static final String TAG = "PortControlUtil  ";
    private static volatile PortControlUtil portControlUtil;
    //    private ExceptionInfoEvent exceptionInfoEvent = new ExceptionInfoEvent();
    private SerialPortHelper serialPort;
    private String sendHex = "";
    public static TroubleTypeBean troubleTypeBean;
    private long lastResponseTime;
    /**
     * 保存待发送的串口命令
     */
    private List<String> sendPortCommandsList = Collections.synchronizedList(new ArrayList<>());
    //    private
    private String queryStatusResponse = "";

    // 倒计时总时长
    long millisInFuture = 1 * Constant.second;
    private MyCountDownTimer myCountDownTimer;
    private boolean myCountDownTimerCancel = false;
    private PortStatus portStatus;

    private DecimalFormat df = new DecimalFormat("0.0");

    private int sendPortType = 1; // 1：单条发送  2：list内多条有序发送
    private int noResponseTimes = 0; // 连续无返回的次数
    private String queryData1 = "";

    private Dialog stopDialog;
    private boolean stopDialogShow = false;
    private Date afterDate; // 指定分钟后的时间
    private boolean isAfterStop = false; // true:刚结束急停   false：
    private boolean weightLED2Red = false; // true:led2红灯，总投入量超重，需要排料   false：
    private boolean manualDoorOpenStop = false; // true:手动设备门打开，自动停止搅拌电机

    private Handler observeHandler; // 观察口
    private Runnable observeRunnable = new Runnable() {
        @Override
        public void run() {
            startAllMotor2();
        }
    }; //

    private boolean isStart = false;
    private Handler outletHandler; // 排料口
    private Runnable outletRunnable = () -> startAllMotor2(); //

    private Handler observeResponseHandler;
    private Runnable observeResponseRunnable = new Runnable() {
        @Override
        public void run() {
            observeResponseHandler.postDelayed(observeResponseRunnable, 5 * Constant.second);

            if (System.currentTimeMillis() - lastResponseTime > 1000) {
                noResponseTimes++;
                SaveToSdUtil.savePortDataToSD(TAG + "response timeout:" + (System.currentTimeMillis() - lastResponseTime), 2);
                LogUtils.e("response timeout:" + (System.currentTimeMillis() - lastResponseTime));
                if (noResponseTimes < 30) {
                    closePort();
                    handler.postDelayed(() -> openPort(), 1500);
                    handler.postDelayed(runnable, 1600);
                } else {
                    observeResponseHandler.removeCallbacks(observeResponseRunnable);
                    closePort();
                    if (DialogManage.getDialog() != null && DialogManage.getDialog().isShowing()) {
                        return;
                    }
                    ActivityUtils.getTopActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogManage.getPortTipDialog(ActivityUtils.getTopActivity(),
                                    ActivityUtils.getTopActivity().getString(R.string.port_disconnect_check) + "\n" +
                                            TimeUtil.getDateSToString()).show();
                        }
                    });
                }

            } else {
//                LogUtils.e("response timeout 未超过1S :" + (System.currentTimeMillis() - lastResponseTime));
            }
        }
    };

    private int stir = 1; // 保存搅拌电机状态
    private int inletOpenTimes = 0;//投入口持续开启次数，
    private int outletOpenTimes = 0;//排出口持续开启次数，超过2认为异常
    private int observeDoorTimes = 0;//观察口持续开启次数，超过2认为异常
    private int windPressureTimes = 0;//f风压异常次数，超过10认为异常
    private int heatingMaxTimes = 0;// 当前加热温度超过最大温度次数，超过20次认为异常
    private int heatingMinTimes = 0;// 当前加热温度小于最小温度次数，超过20次认为异常
    private CountTimer openBtnCountTimer; // 投入口打开按钮超时异常
    private CountTimer closeCountTimer; // 投入口关闭超时异常

    private boolean closeCountdownFlag = false; // 投料门关闭状态时重量跳动范围在n分钟内超过指定值计时flag  false：未计时
    private float closeCountdownBeforeWeight = 0; // 投料门关闭状态时倒计时开始前重量
    private Dialog showOutletOpenDialog;

    // 发送数据
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            queryStatusResponse = "";
            if (sendPortCommandsList != null && sendPortCommandsList.size() > 0) {
                sendCommands(sendPortCommandsList);
                return;
            }
            sendPortType = 1;
            portSendMessage(PortConstants.QUERY_STATUS1);
        }
    };

    //
    private Handler closeCountdownHandler = new Handler();
    private Runnable closeCountdownRunnable = new Runnable() {
        @Override
        public void run() {
            if ((getPortStatus().getChooseUseWeighing() - closeCountdownBeforeWeight) <=
                    Integer.parseInt(MyApplication.adminParameterBean.getWeighAlarmWeight())) {
                closeCountdownFlag = true;
                closeCountdownBeforeWeight = getPortStatus().getChooseUseWeighing();
                closeCountdownHandler.postDelayed(closeCountdownRunnable,
                        Integer.parseInt(MyApplication.adminParameterBean.getWeighAlarmTime()) * Constant.second);
                return;
            }
            // 故障6 称重报警（保持投料门关闭状态时重量跳动范围在1分钟内超过指定值）
            closeCountdownFlag = false;
            troubleTypeBean.setIsTrouble(true);
            troubleTypeBean.setTroubleWeigh(Constant.TRUE);
            troubleTypeBean.setTroubleType(Constant.TROUBLE_TYPE_WEIGH);
            HTTPServerUtil.sendHeartBeat2();
            GreenDaoUtil.getInstance().insertTrouble(6);
            sendCommands(PortConstants.LED1_ON_RED);
        }
    };

    private boolean humidityFlag = false; // 湿度报警（湿度超过95%保持10分钟以上）flag  false：未计时
    private Handler humidityHandler = new Handler();
    private Runnable humidityRunnable = new Runnable() {
        @Override
        public void run() {
            // 故障7 湿度报警（湿度超过95%保持10分钟以上）
            humidityFlag = false;
            troubleTypeBean.setIsTrouble(true);
            troubleTypeBean.setTroubleHumidity(Constant.TRUE);
            troubleTypeBean.setTroubleType(Constant.TROUBLE_TYPE_HUMIDITY);
            HTTPServerUtil.sendHeartBeat2();
            GreenDaoUtil.getInstance().insertTrouble(7);
            EventBus.getDefault().post(new HumidityTrouble(true));
//            sendCommands(PortConstants.LED1_ON_RED);
        }
    };


    private PortControlUtil() {
        // 倒计时总时长
        long millisInFuture = 1 * Constant.second;
//        long millisInFuture = 300;
        troubleTypeBean = new TroubleTypeBean(false, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0);
        myCountDownTimer = new MyCountDownTimer(millisInFuture, Constant.second) {

            @Override
            public void onFinish() {
                if (myCountDownTimerCancel) {
                    return;
                }
                // TODO: 2022/12/6  1S倒计时，没有数据返回，发送串口查询命令
//                closePort();
//                handler.postDelayed(() -> openPort(), 200);
//                handler.postDelayed(runnable, 500);
//                noResponseTimes = 0;
            }
        };
    }

    public static PortControlUtil getInstance() {
        if (portControlUtil == null) {
            synchronized (PortControlUtil.class) {
                if (portControlUtil == null) {
                    portControlUtil = new PortControlUtil();
                }
            }
        }
        return portControlUtil;
    }

    public void openPort() {

        String port = "/dev/ttyS3";
//        String port = "/dev/ttyUSB5";

//        String port = "/dev/"+ BuildConfig.ttyType;
        int baudRate = 9600;
//        int baudRate = 256000;
        //串口程序初始化
        serialPort = new SerialPortHelper(port, baudRate);
        //打开串口
        serialPort.open();
        serialPort.setSerialPortReceivedListener(this::onSerialPortDataReceived);
        if (!isStart) {
            observeResponseHandler = new Handler(Looper.getMainLooper());
            observeResponseHandler.postDelayed(observeResponseRunnable, 10 * Constant.second);
        }
        isStart = true;
    }

    public void open() {
        if (serialPort != null) {
            serialPort.open();
        }
    }

    /**
     * 获取加热板2 自动加热 串口指令
     *
     * @return
     */
    public static String getHeater2AutomaticCommands() {
        return getHeater2AutomaticCommands(
                Integer.parseInt(MyApplication.adminParameterBean.getHeartingTemperature().getTargetTem2()),
                Integer.parseInt(MyApplication.adminParameterBean.getHeartingTemperature().getFloatTem2()));
    }

    /**
     * 投入口打开或关闭超时时间及慢速电压设置的串口命令
     *
     * @param timeout 超时时间 S
     * @param voltage 慢速电压 3-24V
     * @return 组装好后的串口命令
     */
    public static String inletTimeoutVoltageHexData(int timeout, int voltage) {
        String inletTimeoutVoltageHexData = PortConstants.INLET_TIMEOUT_VOLTAGE_HEAD +
                HexadecimalDataUtil.decimalToHex2(timeout) +
                HexadecimalDataUtil.decimalToHex2(voltage);
        return inletTimeoutVoltageHexData + HexadecimalDataUtil.getCRC(inletTimeoutVoltageHexData);
    }

    /**
     * 投入口打开或关闭电压设置的串口命令
     *
     * @param openVoltage  投入口开门电压
     * @param closeVoltage 投入口关门电压
     * @return 组装好后的串口命令
     */
    public static String inletOpenCloseVoltageHexData(int openVoltage, int closeVoltage) {
        String head = PortConstants.INLET_OPEN_CLOSE_VOLTAGE_HEAD;
        if (MyApplication.getFirmwareVersionNew()) {
            head = PortConstants.INLET_OPEN_CLOSE_VOLTAGE_HEAD2;
        }
        String inletOpenCloseVoltageHexData = head +
                HexadecimalDataUtil.decimalToHex2(openVoltage) +
                HexadecimalDataUtil.decimalToHex2(closeVoltage);
        return inletOpenCloseVoltageHexData + HexadecimalDataUtil.getCRC(inletOpenCloseVoltageHexData);
    }

    /**
     * 投入口高速关闭时间
     *
     * @param quicklyCloseTime 高速关闭时间
     * @return 组装好后的串口命令
     */
    public static String inletQuicklyCloseTimeHexData(String quicklyCloseTime) {
        String inletQuicklyCloseTimeHexData = PortConstants.INLET_CLOSE_QUICK_TIME +
                HexadecimalDataUtil.decimalToHex((Integer.parseInt(quicklyCloseTime) / 100));
        return inletQuicklyCloseTimeHexData + HexadecimalDataUtil.getCRC(inletQuicklyCloseTimeHexData);
    }

    /**
     * 直连称重1 重量标定
     *
     * @param weight 重量
     * @return 组装好后的串口命令
     */
    public static String weightCalibrationLoadCell1(String weight) {
        String weightCalibrationLoadCell1HexData = PortConstants.LOAD_CELL1_WEIGHT_CALIBRATION +
                HexadecimalDataUtil.decimalToHex2(Integer.parseInt(weight));
        return weightCalibrationLoadCell1HexData + HexadecimalDataUtil.getCRC(weightCalibrationLoadCell1HexData);
    }

    /**
     * 直连称重2 重量标定
     *
     * @param weight 重量
     * @return 组装好后的串口命令
     */
    public static String weightCalibrationLoadCell2(String weight) {
        String weightCalibrationLoadCell2HexData = PortConstants.LOAD_CELL2_WEIGHT_CALIBRATION +
                HexadecimalDataUtil.decimalToHex2(Integer.parseInt(weight));
        return weightCalibrationLoadCell2HexData + HexadecimalDataUtil.getCRC(weightCalibrationLoadCell2HexData);
    }

    /**
     * 获取加热板1 自动加热 串口指令
     *
     * @return
     */
    public static String getHeater1AutomaticCommands() {
        return getHeater1AutomaticCommands(
                Integer.parseInt(MyApplication.adminParameterBean.getHeartingTemperature().getTargetTem()),
                Integer.parseInt(MyApplication.adminParameterBean.getHeartingTemperature().getFloatTem()));
    }

    /**
     * 获取加热板1 自动加热 串口指令
     *
     * @param targetTem 目标温度
     * @param floatTem  浮动温度
     * @return
     */
    public static String getHeater1AutomaticCommands(int targetTem, int floatTem) {
        String hexHeater = PortConstants.HEATER1_AUTOMATIC_HEAD
                + HexadecimalDataUtil.decimalToHex((targetTem - floatTem) * 10)
                + HexadecimalDataUtil.decimalToHex((targetTem + floatTem) * 10);
        return hexHeater + HexadecimalDataUtil.getCRC(hexHeater);
    }

    @Override
    public void onSerialPortDataReceived(ComPortData comPortData) {
        lastResponseTime = System.currentTimeMillis();
        noResponseTimes = 0;
        myCountDownTimerCancel = true;
        myCountDownTimer.cancel();
        //处理接收的串口消息
        String s = HexStringUtils.byteArray2HexString(comPortData.getRecData());
//        LogUtils.e(TAG + "onSerialPortDataReceived onReceived11: " + s);
        queryStatusResponse += s;
        if (!HexadecimalDataUtil.isRightCrc(queryStatusResponse)) {
            return;
        }
//        LogUtils.e(TAG + "onSerialPortDataReceived onReceived222: " + queryStatusResponse);
        if (sendPortType == 2) {
            LogUtils.e(TAG + "onReceived: " + queryStatusResponse);
            SaveToSdUtil.savePortDataToSD(TAG + "onSerialPortDataReceived receive:" + queryStatusResponse, 2);
            this.sendPortCommandsList.remove(0);
            queryStatusResponse = "";
            // 重量置零或是标定，下位机会进行写入操作，这个时间会持续几百毫秒，这段时间如果给下位机发送查询或其他控制命令，下位机会收不到数据
            if (sendHex.contains("01060019") || sendHex.contains("0106001A") ||
                    sendHex.equals(PortConstants.WEIGHING_SETTING_ZERO_485)) {
                handler.postDelayed(() -> sendCommands(sendPortCommandsList), 700);
                return;
            }
            sendCommands(sendPortCommandsList);
            return;
        }

        int type = Integer.parseInt(queryStatusResponse.substring(2, 4));
        if (type == 03) {
            if (sendPortCommandsList.size() > 0) {
                LogUtils.e(TAG + "time: " + TimeUtil.getDateSToString());
                queryStatusResponse = "";
                sendCommands(sendPortCommandsList);
                return;
            }
//            queryStatusResponse = s;
            if (sendHex.equals(PortConstants.QUERY_STATUS1)) { // 第一个查询返回的结果
//                queryData1 = queryStatusResponse;
                LogUtils.i("当前commands PortConstants.QUERY_STATUS1:" + PortConstants.QUERY_STATUS1 + "\ncomPortData:" + queryStatusResponse);
                dataPortStatus(PortConstants.QUERY_STATUS1);
                portSendMessage(PortConstants.QUERY_STATUS2); // 发送第二部分查询
            } else if (sendHex.equals(PortConstants.QUERY_STATUS2)) {
//                LogUtils.i("当前commands PortConstants.QUERY_STATUS2:" + PortConstants.QUERY_STATUS2 + "\ncomPortData:" + queryStatusResponse);
                dataPortStatus(PortConstants.QUERY_STATUS2);
                portSendMessage(PortConstants.QUERY_STATUS3); // 发送第三部分查询
            } else if (sendHex.equals(PortConstants.QUERY_STATUS3)) {
//                LogUtils.i("当前commands PortConstants.QUERY_STATUS3:" + PortConstants.QUERY_STATUS3 + "\ncomPortData:" + queryStatusResponse);
                dataPortStatus(PortConstants.QUERY_STATUS3);
                portSendMessage(PortConstants.QUERY_STATUS4); // 发送第四部分查询
            } else if (sendHex.equals(PortConstants.QUERY_STATUS4)) {
                LogUtils.i("当前commands PortConstants.QUERY_STATUS4:" + PortConstants.QUERY_STATUS4 + "\ncomPortData:" + queryStatusResponse);
                dataPortStatus(PortConstants.QUERY_STATUS4);


                if (Float.parseFloat(MyApplication.adminParameterBean.getInletLimitedTotalAccumulation()) >
                        Float.parseFloat(MyApplication.adminParameterBean.getInletLimitedTotal()) &&
                        getPortStatus().getLed2RGB() != PortConstants.COLOR_RED) {
                    weightLED2Red = true;
                    sendCommands(PortConstants.LED2_ON_RED);
//                    stopAllMotor(0);
                }


                // 日投入量超重或投入重量超重,并且led2 不是黄灯的情况下，发送led2黄灯命令
                //注意 getInletLimited24H = 0 时的处理，等于0时不做限制
                if (!MyApplication.adminParameterBean.getInletLimited24H().equals("0") &&
                        Float.parseFloat(GreenDaoUtil.getInstance().get24HTotalInletWeight()) >
                                Float.parseFloat(MyApplication.adminParameterBean.getInletLimited24H()) &&
                        getPortStatus().getLed2RGB() != PortConstants.COLOR_RED) {
                    if ( getPortStatus().getLed2RGB() != PortConstants.COLOR_YELLOW ){
                        sendCommands(PortConstants.LED2_ON_YELLOW);
                    }
                } else if (getPortStatus().getLed2RGB() != PortConstants.COLOR_GREEN) {
                    sendCommands(PortConstants.LED2_ON_GREEN);
                }


                // 故障1 搅拌电机异常
                if (getPortStatus().getStirStatus() == 5 && stir != 5) {
                    stir = PortControlUtil.getInstance().getPortStatus().getStirStatus();
                    troubleTypeBean.setIsTrouble(true);
                    troubleTypeBean.setTroubleStir(Constant.TRUE);
                    troubleTypeBean.setTroubleType(Constant.TROUBLE_TYPE_STIR);
                    stopAllMotor(1);
                    HTTPServerUtil.sendHeartBeat2();
                    GreenDaoUtil.getInstance().insertTrouble(1);
                } else if (getPortStatus().getStirStatus() == 6 && stir != 6) {
                    stir = PortControlUtil.getInstance().getPortStatus().getStirStatus();
                    troubleTypeBean.setIsTrouble(true);
                    troubleTypeBean.setTroubleStirError(Constant.TRUE);
                    troubleTypeBean.setTroubleType(Constant.TROUBLE_TYPE_STIR_ERROR);
                    stopAllMotor(1);
                    HTTPServerUtil.sendHeartBeat2();
                    GreenDaoUtil.getInstance().insertTrouble(9);
                } else {
                    if ((getPortStatus().getStirStatus() == 5 &&
                            troubleTypeBean.getTroubleStir() != Constant.TRUE) ||
                            (getPortStatus().getStirStatus() == 6 &&
                                    troubleTypeBean.getTroubleStirError() != Constant.TRUE)) {

                        stir = 3;
                    } else {
                        stir = PortControlUtil.getInstance().getPortStatus().getStirStatus();
                    }


                }

                // 搅拌电机异常，优先级最高
                if (troubleTypeBean.getIsTrouble() &&
                        (troubleTypeBean.getTroubleType() == Constant.TROUBLE_TYPE_STIR ||
                                troubleTypeBean.getTroubleType() == Constant.TROUBLE_TYPE_STIR_ERROR)) {
                    return;
                }

                if (getPortStatus().getInletStatus()==2||getPortStatus().getInletStatus()==7){
                    inletOpenTimes++;
                }else {
                    // 投入口由开启到关闭，通知首页重置节电模式倒计时
                    if (inletOpenTimes>0){
                        EventBus.getDefault().post(new ResetPowerSaving());
                    }
                    inletOpenTimes=0;
                }

                // 故障2 排料口未关闭
                if (getPortStatus().getOutletStatus() == 0) {
                    outletOpenTimes++;
                    // 大概0.3S一次，4S就是12次
                    if (outletOpenTimes == 7) {
                        troubleTypeBean.setIsTrouble(true);
                        troubleTypeBean.setTroubleOutlet(Constant.TRUE);
                        troubleTypeBean.setTroubleType(Constant.TROUBLE_TYPE_OUTLET);
                        stopAllMotor(1);
                        // 投入口和观察口都未开启，搅拌电机才可以运转
                        if (getPortStatus().getInletStatus() != 2 &&
                                getPortStatus().getInletStatus() != 7 &&
                                getPortStatus().getObserveDoorStatus() != 0) {
                            sendCommands(PortConstants.STIR_FORWARD); // 搅拌启动正转
                        }

                        HTTPServerUtil.sendHeartBeat2();
                        GreenDaoUtil.getInstance().insertTrouble(2);
                        if (outletHandler != null) {
                            outletHandler.removeCallbacks(outletRunnable);
                            outletHandler.removeCallbacksAndMessages(null);
                        }
                    }
                    // TODO: 2023/7/16  韩国排料口打开提示异常临时由1改为2
                    if (outletOpenTimes == 1) {
//                    if (outletOpenTimes == 2) {
                        ActivityUtils.getTopActivity().runOnUiThread(() -> DialogManage.getDialog2SDismiss(ActivityUtil.getInstance().getActivity(),
                                ActivityUtils.getTopActivity().getString(R.string.outlet_open)).show());
                    }

                    if (outletOpenTimes > 12 && troubleTypeBean.getTroubleOutlet() == Constant.FALSE) {
                        outletOpenTimes = 0;
                    }
                    if (outletOpenTimes > 100000) {
                        outletOpenTimes = 20;
                    }

                } else {
                    // 排料口打开并关闭后自动解除错误，并重置
                    if (outletOpenTimes > 0 && getPortStatus().getLed2RGB() == PortConstants.COLOR_RED) {
                        weightLED2Red = false;
                        MyApplication.adminParameterBean.setInletLimitedTotalAccumulation("0");
                        SharedPreFerUtil.saveObj(Constant.ADMIN_PARAMETER_FILENAME, Constant.ADMIN_PARAMETER_KEY,
                                JSON.toJSONString(MyApplication.adminParameterBean));
                    }
                    // 之前判定为故障，现在故障解除
                    if (outletOpenTimes >= 7) {
                        if (troubleTypeBean.getTroubleUpCheck() != Constant.TRUE) {
                            PortControlUtil.troubleTypeBean.setIsTrouble(false);
                            GreenDaoUtil.getInstance().insertTrouble(0);
                            PortControlUtil.troubleTypeBean.setTroubleType(Constant.TROUBLE_TYPE_REMOVE);
                            sendCommands(PortConstants.LED1_ON_GREEN); //灯1为绿色常亮
                        }

                        PortControlUtil.troubleTypeBean.setTroubleOutlet(Constant.FALSE);

                        if (DialogManage.getDialog() != null && DialogManage.dialog.isShowing() && DialogManage.getShowContent().equals(
                                ActivityUtils.getTopActivity().getString(R.string.please_close_outlet))) {
                            LoginActivity.outletDialogShow = false;
                            DialogManage.cancelLoading();
                        }


                        // 投入口和观察口都未打开的情况下，3秒后开启
                        if (getPortStatus().getInletStatus() != 2 &&
                                getPortStatus().getInletStatus() != 7 &&
                                getPortStatus().getObserveDoorStatus() != 0) {
                            //接收到排料门关闭后延时3秒自动启动
                            outletHandler = new Handler(ActivityUtils.getTopActivity().getMainLooper());
                            outletHandler.postDelayed(outletRunnable, 3 * Constant.second);
                        }
                    }
                    outletOpenTimes = 0;
                }

                // 故障3 观察口未关闭
                if (getPortStatus().getObserveDoorStatus() == 0) {
                    observeDoorTimes++;
                    // 大概0.3S一次，2S就是6次
                    if (observeDoorTimes == 6) {
                        if (MyApplication.adminParameterBean.isObservePortAlarm()) {
                            troubleTypeBean.setIsTrouble(true);
                            troubleTypeBean.setTroubleUpCheck(Constant.TRUE);
                            troubleTypeBean.setTroubleType(Constant.TROUBLE_TYPE_OBSERVE);
                            stopAllMotor(1);
                            HTTPServerUtil.sendHeartBeat2();
                            GreenDaoUtil.getInstance().insertTrouble(3);
                            if (observeHandler != null) {
                                observeHandler.removeCallbacks(observeRunnable);
                                observeHandler.removeCallbacksAndMessages(null);
                            }
                        } else {
                            stopAllMotor(0);
                        }

                    }

                    if (observeDoorTimes > 12 && troubleTypeBean.getTroubleUpCheck() == Constant.FALSE) {
                        observeDoorTimes = 0;
                    }
                    if (observeDoorTimes > 100000) {
                        observeDoorTimes = 20;
                    }

                    if (PortControlUtil.getInstance().getPortStatus().getOutletStatus() == 0 &&
                            (showOutletOpenDialog == null || !showOutletOpenDialog.isShowing())) {
                        ActivityUtils.getTopActivity().runOnUiThread(() -> {
                            showOutletOpenDialog = DialogManage.getDialog(ActivityUtil.getInstance().getActivity(),
                                    ActivityUtils.getTopActivity().getString(R.string.please_close_outlet));
                            showOutletOpenDialog.show();
                        });
                    }
                } else {
                    // 观察口由打开变为关闭，通知首页节电倒计时重置
                    if (observeDoorTimes>0){
                        EventBus.getDefault().post(new ResetPowerSaving());
                    }

                    // 之前判定为故障，现在故障解除
                    if (observeDoorTimes >= 6) {
                        if (MyApplication.adminParameterBean.isObservePortAlarm()) {
                            PortControlUtil.troubleTypeBean.setTroubleUpCheck(Constant.FALSE);

                            if (troubleTypeBean.getTroubleOutlet() != Constant.TRUE) {
                                PortControlUtil.troubleTypeBean.setIsTrouble(false);
                                GreenDaoUtil.getInstance().insertTrouble(0);
                                PortControlUtil.troubleTypeBean.setTroubleType(Constant.TROUBLE_TYPE_REMOVE);
                            }
                        }

                        // 投入口和排料都未打开的情况下，10秒后开启
                        if (getPortStatus().getInletStatus() != 2 &&
                                getPortStatus().getInletStatus() != 7 &&
                                getPortStatus().getOutletStatus() != 0) {
                            //接收到观察口关闭后延时10秒自动启动
                            observeHandler = new Handler(ActivityUtils.getTopActivity().getMainLooper());
                            observeHandler.postDelayed(observeRunnable, 10 * Constant.second);
                        } else if (getPortStatus().getOutletStatus() != 0) {
                            sendCommands(PortConstants.LED1_ON_GREEN); //灯1为绿色常亮
                        }

                        if (getPortStatus().getOutletStatus() == 0 && getPortStatus().getInletStatus() != 2 &&
                                getPortStatus().getInletStatus() != 7 ) {
                            sendCommands(PortConstants.STIR_FORWARD); // 搅拌启动正转
                        }

                    }
                    //  判断排料口状态，如果为打开状态，或是弹窗显示，弹窗消失，不称重
                    if (PortControlUtil.getInstance().getPortStatus().getOutletStatus() == 0 &&
                            getPortStatus().getInletStatus() != 2 &&
                            getPortStatus().getInletStatus() != 7) {
                        if (showOutletOpenDialog != null && showOutletOpenDialog.isShowing()) {
                            ActivityUtils.getTopActivity().runOnUiThread(() -> showOutletOpenDialog.dismiss());
                        }
                    }
                    observeDoorTimes = 0;
                }

//                // 排料口未关闭或是观察口未关闭
//                if (troubleTypeBean.getIsTrouble() &&
//                        (troubleTypeBean.getTroubleType() == Constant.TROUBLE_TYPE_OUTLET ||
//                                troubleTypeBean.getTroubleType() == Constant.TROUBLE_TYPE_OBSERVE)) {
//                    return;
//                }

                // 故障6 称重报警（保持投料门关闭状态时重量跳动范围在1分钟内超过指定值）
               /* if (getPortStatus().getInletStatus() == 5 && !closeCountdownFlag && troubleTypeBean.getTroubleWeigh() != Constant.TRUE) {
                    closeCountdownFlag = true;
                    closeCountdownBeforeWeight = getPortStatus().getChooseUseWeighing();
                    closeCountdownHandler.postDelayed(closeCountdownRunnable,
                            Integer.parseInt(MyApplication.adminParameterBean.getWeighAlarmTime()) * Constant.second);
                }
                if (getPortStatus().getInletStatus() != 5) {
                    closeCountdownFlag = false;
                    closeCountdownHandler.removeCallbacks(closeCountdownRunnable);
                    closeCountdownHandler.removeCallbacksAndMessages(null);
                }*/
                // 故障8 风压异常
                if (getPortStatus().getWindPressure() == 1 && getPortStatus().getFan1() != 0) {
                    windPressureTimes++;
                    // 连续10S异常，大概0.32 S一次，30大概为10 S
                    if (windPressureTimes == 30) {
                        troubleTypeBean.setIsTrouble(true);
                        troubleTypeBean.setTroublePA(Constant.TRUE);
                        troubleTypeBean.setTroubleType(Constant.TROUBLE_TYPE_WIND_PRESSURE);
                        stopAllMotor(1);
                        HTTPServerUtil.sendHeartBeat2();
                        GreenDaoUtil.getInstance().insertTrouble(8);
                    }

                } else {
                    // 之前判定为故障，现在故障解除
                   /* if (windPressureTimes >= 10) {
                        PortControlUtil.troubleTypeBean.setIsTrouble(false);
                        PortControlUtil.troubleTypeBean.setTroubleUpCheck(Constant.FALSE);
                        PortControlUtil.troubleTypeBean.setTroubleType(Constant.TROUBLE_TYPE_REMOVE);
                        GreenDaoUtil.getInstance().insertTrouble(0);
                        //接收到排料门关闭后延时10秒自动启动
                        new Handler(ActivityUtils.getTopActivity().getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startAllMotor2();
                            }
                        }, 10 * Constant.second);

                    }*/
                    windPressureTimes = 0;
                }

                if (MyApplication.adminParameterBean.getInletMode().equals(Constant.MANUAL) &&
                        !ActivityUtils.getTopActivity().getClass().getSimpleName().equals(LoginActivity.class.getSimpleName())) {
                    if (getPortStatus().getInletStatus() == 7 && getPortStatus().getStirStatus() != 3) {
                        // 判断排料口状态，如果为打开状态，弹窗提示，一直显示
                        if (PortControlUtil.getInstance().getPortStatus().getOutletStatus() == 0 &&
                                (showOutletOpenDialog == null ||
                                        !showOutletOpenDialog.isShowing())) {

                            ActivityUtils.getTopActivity().runOnUiThread(() -> {
                                showOutletOpenDialog = DialogManage.getDialog(ActivityUtil.getInstance().getActivity(),
                                        ActivityUtils.getTopActivity().getString(R.string.please_close_outlet));
                                showOutletOpenDialog.show();
                            });
                        }
                        manualDoorOpenStop = true;
                        sendCommands(PortConstants.STIR_STOP); // 搅拌停止
                        sendCommands(PortConstants.FAN1_STOP); //风扇停止
                        sendCommands(PortConstants.DEHUMIDIFICATION_STOP); //除湿停止
                    } else if (getPortStatus().getInletStatus() == 5 && manualDoorOpenStop) {
                        //  判断排料口状态，如果为打开状态，或是弹窗显示，弹窗消失，不称重
                        if (PortControlUtil.getInstance().getPortStatus().getOutletStatus() == 0) {
                            if (showOutletOpenDialog != null && showOutletOpenDialog.isShowing()) {
                                ActivityUtils.getTopActivity().runOnUiThread(() -> showOutletOpenDialog.dismiss());
                            }
                        }
                        manualDoorOpenStop = false;
                        if (troubleTypeBean.getTroubleOutlet() == Constant.TRUE) {
                            // 打开搅拌电机命令
                            sendCommands(PortConstants.STIR_FORWARD);
                        }

                        if (!troubleTypeBean.getIsTrouble() || (troubleTypeBean.getTroubleType()== Constant.TROUBLE_TYPE_HUMIDITY
                                && troubleTypeBean.getTroubleHumidity()==Constant.TRUE)) {
                            // 打开搅拌电机命令
                            sendCommands(PortConstants.STIR_FORWARD);
                            sendCommands(PortConstants.FAN1_AUTOMATIC); //风扇启动
                            sendCommands(PortControlUtil.getInstance().getDehumidificationAutomaticCommands()); //烘干机自动
                            sendPortCommandsList.add(getHeater1AutomaticCommands()); //加热1自动
                            sendPortCommandsList.add(getHeater2AutomaticCommands()); //加热2自动
                        }


                    }
                }
            }
            return;
        }
        // 控制命令返回结果2S后发送查询
        handler.postDelayed(runnable, 100);
//        handler.postDelayed(runnable, 150);
//        handler.postDelayed(runnable, Constant.second);

    }

    /**
     * 当急停解除、设备模式重新变为自动，或是点击解除报警并且设备模式为自动时，重置最小温度报警忽略时间，
     */
    public void resetTempIgnoreTime() {
        MyApplication.isStartUp = true;
        afterDate = null;
    }

    public PortStatus getPortStatus() {
        if (portStatus == null) {
            portStatus = new PortStatus();
            portStatus.setInletSensorStatus(2);
            portStatus.setInletStatus(5);
            portStatus.setObserveDoorStatus(1);
            portStatus.setOutletStatus(1);
            portStatus.setStirStatus(3);
            portStatus.setPressureSetting(2);
            portStatus.setLed1(1);
            portStatus.setLed1RGB(010);
            portStatus.setLed2(1);
            portStatus.setLed2RGB(010);
            portStatus.setFan1(1);
            portStatus.setFan2(1);
            portStatus.setHeater1(1);
            portStatus.setHeater2(1);
            portStatus.setTemperature(77.34f);
            portStatus.setHumidity(45.22f);
            portStatus.setDehumidifier(2);
            portStatus.setGrossWeight(23.2f);
            portStatus.setNetWeight(20.3f);
            portStatus.setTare(3.1f);
            portStatus.setHeaterTemperature1(50);
            portStatus.setHeaterTemperature2(50);
            portStatus.setLighting(1);
            portStatus.setStop(0);
            portStatus.setOpenDoorBtn(0);
            portStatus.setWindPressure(0);
            portStatus.setLock1(2);
            portStatus.setLock2(2);
            portStatus.setDirectWeighing1(2.22f);
            portStatus.setDirectWeighing2(2.22f);
            portStatus.setFirmwareVersion("2.0");
        }

        return portStatus;
    }

    /**
     * 获取故障内容
     *
     * @return
     */
    public TroubleTypeBean getTroubleTypeBean() {
        return troubleTypeBean;
    }

    /**
     * 启动所有电机
     * 搅拌启动正转，风扇启动，加热自动，除湿自动，灯为绿色常亮
     */
    public List<String> startAllMotor() {
        //自动:搅拌启动正转，风扇启动，加热自动，除湿自动，此模式下，控制tab不可操作，不可点击，灯为绿色常亮
        List<String> sendPortCommandsList = new ArrayList<>();
        sendPortCommandsList.add(PortConstants.STIR_FORWARD); // 搅拌启动正转
        sendPortCommandsList.add(PortConstants.LED1_ON_GREEN); //灯1为绿色常亮
        sendPortCommandsList.add(PortConstants.LED2_ON_GREEN); //灯2为绿色常亮
        sendPortCommandsList.add(PortConstants.FAN1_AUTOMATIC); //风扇启动
        sendPortCommandsList.add(getHeater1AutomaticCommands()); //加热1自动
        sendPortCommandsList.add(getHeater2AutomaticCommands()); //加热2自动
        sendPortCommandsList.add(getDehumidificationAutomaticCommands()); //烘干机自动
        return sendPortCommandsList;
//        PortControlUtil.getInstance().sendCommands(sendPortCommandsList);
    }

    /**
     * 启动所有电机
     * 搅拌启动正转，风扇启动，加热自动，除湿自动，灯为绿色常亮
     */
    public List<String> startAllMotor2() {
        //自动:搅拌启动正转，风扇启动，加热自动，除湿自动，此模式下，控制tab不可操作，不可点击，灯为绿色常亮
        List<String> sendPortCommandsList = new ArrayList<>();
        sendCommands(PortConstants.STIR_FORWARD); // 搅拌启动正转
        sendCommands(PortConstants.LED1_ON_GREEN); //灯1为绿色常亮
        sendCommands(PortConstants.FAN1_AUTOMATIC); //风扇启动
        sendCommands(getHeater1AutomaticCommands()); //加热1自动
        sendCommands(getHeater2AutomaticCommands()); //加热2自动
        sendCommands(getDehumidificationAutomaticCommands()); //烘干机自动
        PortControlUtil.getInstance().sendCommands(PortConstants.LIGHTING_AUTO); //照明自动
        return sendPortCommandsList;
//        PortControlUtil.getInstance().sendCommands(sendPortCommandsList);
    }

    /**
     * 停止所有电机
     *
     * @param type 1 设备故障停机，需要添加led红灯
     */
    public List<String> stopAllMotor(int type) {
        //投入口停止，搅拌停止，风扇停止，加热停止，除湿停止
        List<String> sendPortCommandsStopList = new ArrayList<>();
        sendCommands(PortConstants.INLET_STOP); // 投入口停止
        sendCommands(PortConstants.STIR_STOP); // 搅拌停止
        sendCommands(PortConstants.FAN1_STOP); //风扇停止
        sendCommands(PortConstants.HEATER1_STOP); //加热1停止
        sendCommands(PortConstants.HEATER2_STOP); //加热2停止
        sendCommands(PortConstants.DEHUMIDIFICATION_STOP); //除湿停止
        if (type == 1) {
            sendCommands(PortConstants.LED1_ON_RED); //led1 红灯
        }
      /*  sendPortCommandsStopList.add(PortConstants.INLET_STOP); // 投入口停止
        sendPortCommandsStopList.add(PortConstants.STIR_STOP); // 搅拌停止
        sendPortCommandsStopList.add(PortConstants.FAN1_STOP); //风扇停止
        sendPortCommandsStopList.add(PortConstants.HEATER1_STOP); //加热1停止
        sendPortCommandsStopList.add(PortConstants.HEATER2_STOP); //加热2停止
        sendPortCommandsStopList.add(PortConstants.DEHUMIDIFICATION_STOP); //除湿停止
        if (type == 1) {
            sendPortCommandsStopList.add(PortConstants.LED1_ON_RED); //led1 红灯
        }*/
        return sendPortCommandsList;
//        PortControlUtil.getInstance().sendCommands(sendPortCommandsStopList);
    }

    private void dataPortStatus(String commands) {
        if (portStatus == null) {
            portStatus = new PortStatus();
        }
        if (((commands.equals(PortConstants.QUERY_STATUS1) ||
                commands.equals(PortConstants.QUERY_STATUS2) ||
                commands.equals(PortConstants.QUERY_STATUS3)) &&
                queryStatusResponse.length() != 52)
                ||
                (commands.equals(PortConstants.QUERY_STATUS4) &&
                        queryStatusResponse.length() != 60)) {
            SaveToSdUtil.saveLog(TAG + "now commands:" + commands + "\t\t response:" + queryStatusResponse);
            closePort();
            handler.postDelayed(() -> openPort(), 1500);
            handler.postDelayed(runnable, 1600);
            return;
        }
        if (commands.equals(PortConstants.QUERY_STATUS1)) { // 处理第一部分查询，截止到FAN1
            portStatus.setInletSensorStatus(HexadecimalDataUtil.
                    hexToDecimal(queryStatusResponse.substring(8, 10))); // 投入口传感器状态
            portStatus.setInletStatus(HexadecimalDataUtil.
                    hexToDecimal(queryStatusResponse.substring(10, 12))); // 投入口状态
            portStatus.setObserveDoorStatus(HexadecimalDataUtil.
                    hexToDecimal(queryStatusResponse.substring(12, 16))); // 观察口
            // TODO: 2023/7/17 为了发货，临时修改
//            portStatus.setOutletStatus(1);
            portStatus.setOutletStatus(HexadecimalDataUtil.
                    hexToDecimal(queryStatusResponse.substring(16, 20)));// 排出口
            portStatus.setStirStatus(HexadecimalDataUtil.
                    hexToDecimal(queryStatusResponse.substring(20, 24)));// 搅拌电机
            portStatus.setPressureSetting(HexadecimalDataUtil.
                    hexToDecimal(queryStatusResponse.substring(24, 28)));// 称重设置 去皮 置零
            portStatus.setLed1(HexadecimalDataUtil.
                    hexToDecimal(queryStatusResponse.substring(28, 32)));// led1模式
            portStatus.setLed1RGB(Integer.parseInt(queryStatusResponse.substring(32, 36)));// led1 RGB
            portStatus.setLed2(HexadecimalDataUtil.
                    hexToDecimal(queryStatusResponse.substring(36, 40)));// led2模式
            portStatus.setLed2RGB(Integer.parseInt(queryStatusResponse.substring(40, 44)));// led2 RGB
//            portStatus.setFan1(HexadecimalDataUtil.
//                    hexToDecimal(queryStatusResponse.substring(44, 48)));// 风扇1
            portStatus.setFan1(Integer.parseInt(queryStatusResponse.substring(44, 48)));// 风扇1
//            EventBus.getDefault().post(portStatus);

        } else if (commands.equals(PortConstants.QUERY_STATUS2)) { //  查询FAN2-毛重 10
            // 处理第二查询部分数据，查询FAN2-毛重 10
//            portStatus.setFan2(HexadecimalDataUtil.
//                    hexToDecimal(queryStatusResponse.substring(8, 12))); // 风扇2
//            portStatus.setHeater1(HexadecimalDataUtil.
//                    hexToDecimal(queryStatusResponse.substring(12, 16)));// 加热1模式
//            portStatus.setHeater2(HexadecimalDataUtil.
//                    hexToDecimal(queryStatusResponse.substring(16, 20)));// 加热2模式
            portStatus.setFan2(Integer.parseInt(queryStatusResponse.substring(8, 12))); // 风扇2
            portStatus.setHeater1(Integer.parseInt(queryStatusResponse.substring(12, 16)));// 加热1模式
            portStatus.setHeater2(Integer.parseInt(queryStatusResponse.substring(16, 20)));// 加热2模式
            portStatus.setTemperature((float) HexadecimalDataUtil.
                    hexToDecimal(queryStatusResponse.substring(20, 24)) / 10);// 当前温度
            portStatus.setHumidity((float) HexadecimalDataUtil.
                    hexToDecimal(queryStatusResponse.substring(24, 28)) / 10);// 当前湿度

            // 湿度报警开关为打开，进行湿度报警判断
            if (MyApplication.adminParameterBean.isHumidityAlarmSwitch()){
                // 故障7 湿度报警（湿度超过设置的湿度保持设置的时间以上）
                if (portStatus.getHumidity() > MyApplication.adminParameterBean.getHumidityAlarmHumidity() && !humidityFlag &&
                        troubleTypeBean.getTroubleHumidity() == Constant.FALSE && (
                        troubleTypeBean.getTroubleType() != Constant.TROUBLE_TYPE_STIR ||
                                troubleTypeBean.getTroubleType() != Constant.TROUBLE_TYPE_OUTLET ||
                                troubleTypeBean.getTroubleType() != Constant.TROUBLE_TYPE_OBSERVE)) {
                    humidityFlag = true;
                    humidityHandler.postDelayed(humidityRunnable,
                            Integer.parseInt(MyApplication.adminParameterBean.getHumidityAlarmTime()) * 60 * Constant.second);
                } else if (portStatus.getHumidity() <= MyApplication.adminParameterBean.getHumidityAlarmHumidity()){
                    if (troubleTypeBean.getTroubleHumidity() == Constant.TRUE) { // 湿度低于报警范围，并且之前处于湿度报警状态，取消湿度报警
                        sendCommands(PortConstants.LED1_ON_GREEN);
                        PortControlUtil.troubleTypeBean.setTroubleHumidity(Constant.FALSE);
                        PortControlUtil.troubleTypeBean.setIsTrouble(false);
                        GreenDaoUtil.getInstance().insertTrouble(0);
                        PortControlUtil.troubleTypeBean.setTroubleType(Constant.TROUBLE_TYPE_REMOVE);
                        EventBus.getDefault().post(new HumidityTrouble(false));
                    }
                    humidityFlag = false;
                    humidityHandler.removeCallbacks(humidityRunnable);
                    humidityHandler.removeCallbacksAndMessages(null);
                }
            }else {
                humidityHandler.removeCallbacks(humidityRunnable);
                humidityHandler.removeCallbacksAndMessages(null);
            }


//            portStatus.setDehumidifier(HexadecimalDataUtil.
//                    hexToDecimal(queryStatusResponse.substring(28, 32)));// 除湿模式 烘干机
            portStatus.setDehumidifier(Integer.parseInt(queryStatusResponse.substring(28, 32)));// 除湿模式 烘干机
            portStatus.setGrossWeight(DecimalFormatUtil.DecimalFormatTwo(hex2ASCIIStr(queryStatusResponse.substring(32, 48))));
//            LogUtils.e(TAG+"GrossWeight:" +portStatus.getGrossWeight());
        } else if (commands.equals(PortConstants.QUERY_STATUS3)) { // 查询净重-加热温度 10
            portStatus.setNetWeight(DecimalFormatUtil.DecimalFormatTwo(hex2ASCIIStr(queryStatusResponse.substring(8, 24)))); // 净重，前两位为符号位
//            LogUtils.e(TAG+"NetWeight:" +portStatus.getNetWeight());
//            EventBus.getDefault().post(portStatus);
            portStatus.setTare(HexadecimalDataUtil.
                    hex2Float(queryStatusResponse.substring(24, 40))); // 皮重
            portStatus.setHeaterTemperature1((float) HexadecimalDataUtil.
                    hexToDecimal(queryStatusResponse.substring(40, 44)) / 10); // 油温加热温度1
            portStatus.setHeaterTemperature2((float) HexadecimalDataUtil.
                    hexToDecimal(queryStatusResponse.substring(44, 48)) / 10);// 油温加热温度2


            // 故障5 加热异常，超过最高报警温度
            if ((portStatus.getHeaterTemperature1() > Float.parseFloat(MyApplication.adminParameterBean.getHeartingTemperature().getMaxTemp()) ||
                    portStatus.getHeaterTemperature2() > Float.parseFloat(MyApplication.adminParameterBean.getHeartingTemperature().getMaxTemp())) &&
                    troubleTypeBean.getTroubleHeaterMax() != Constant.TRUE) {
                if (heatingMaxTimes < 20) {
                    heatingMaxTimes++;
                    return;
                }
                SaveToSdUtil.savePortDataToSD(TAG + "加热异常，温度过高：" + queryStatusResponse, 2);
                troubleTypeBean.setIsTrouble(true);
                troubleTypeBean.setTroubleHeaterMax(Constant.TRUE);
                troubleTypeBean.setTroubleType(Constant.TROUBLE_TYPE_HEATING_MAX);
                stopAllMotor(1);
                HTTPServerUtil.sendHeartBeat2();
                GreenDaoUtil.getInstance().insertTrouble(Constant.TROUBLE_TYPE_HEATING_MAX);
                heatingMaxTimes = 0;
            } else {
                heatingMaxTimes = 0;
            }

            // 加热1 停止
            if (getPortStatus().getHeater1() == 2 || getPortStatus().getHeater2() == 2 ||
                    MyApplication.adminParameterBean.getDeviceMode() == Constant.DEVICE_MODE_MANUAL ||
                    MyApplication.adminParameterBean.getDeviceMode() == Constant.DEVICE_MODE_STOP) {
                return;
            }

            // 首次打开程序，指定时间内忽略最低温度判断
            if (MyApplication.isStartUp) {
                if (afterDate == null) {
                    afterDate = TimeUtil.getAfterXMin(MyApplication.adminParameterBean.getHeartingTemperature().getIgnoreTime());
                }
                if (TimeUtil.nowTimeIsAfter(afterDate)) {
                    return;
                } else {
                    MyApplication.isStartUp = false;
                    afterDate = null;
                }
            }

            if (isAfterStop) {
                if (afterDate == null) {
                    afterDate = TimeUtil.getAfterXMin(MyApplication.adminParameterBean.getHeartingTemperature().getIgnoreTime());
                }
                if (TimeUtil.nowTimeIsAfter(afterDate)) {
                    return;
                } else {
                    isAfterStop = false;
                    afterDate = null;
                }
            }
            // 故障5 加热异常，超过最低报警温度
            if ((portStatus.getHeaterTemperature1() < Float.parseFloat(MyApplication.adminParameterBean.getHeartingTemperature().getMinTemp()) ||
                    portStatus.getHeaterTemperature2() < Float.parseFloat(MyApplication.adminParameterBean.getHeartingTemperature().getMinTemp())) &&
                    troubleTypeBean.getTroubleHeaterMin() != Constant.TRUE) {
                if (heatingMinTimes < 20) {
                    heatingMinTimes++;
                    return;
                }
                SaveToSdUtil.savePortDataToSD(TAG + "加热异常，温度过低：" + queryStatusResponse, 2);
                troubleTypeBean.setIsTrouble(true);
                troubleTypeBean.setTroubleHeaterMin(Constant.TRUE);
                troubleTypeBean.setTroubleType(Constant.TROUBLE_TYPE_HEATING_MIN);
                stopAllMotor(1);
                HTTPServerUtil.sendHeartBeat2();
                GreenDaoUtil.getInstance().insertTrouble(Constant.TROUBLE_TYPE_HEATING_MIN);
                heatingMinTimes = 0;
            } else {
                heatingMinTimes = 0;
            }
        } else if (commands.equals(PortConstants.QUERY_STATUS4)) { // 照明-风压 4
            portStatus.setLighting(Integer.parseInt(queryStatusResponse.substring(8, 12))); // 照明
            portStatus.setStop(HexadecimalDataUtil.
                    hexToDecimal(queryStatusResponse.substring(12, 16))); // 紧急开关
            portStatus.setOpenDoorBtn(HexadecimalDataUtil.
                    hexToDecimal(queryStatusResponse.substring(16, 20))); // 开门按钮
//            portStatus.setWindPressure(new DecimalFormat("0.00").format((float) HexadecimalDataUtil.
//                    byteToShortBig(queryStatusResponse.substring(20, 24)) / 100) + ""); // 风压值
            portStatus.setWindPressure(Integer.parseInt(queryStatusResponse.substring(20, 24))); // 风压
            portStatus.setLock1(Integer.parseInt(queryStatusResponse.substring(24, 28))); // lock1状态
            portStatus.setLock2(Integer.parseInt(queryStatusResponse.substring(28, 32))); // lock1状态
            portStatus.setDirectWeighing1(directWeighing(
                    HexadecimalDataUtil.hexToDecimal(queryStatusResponse.substring(32, 36)),
                    HexadecimalDataUtil.hexToDecimal(queryStatusResponse.substring(36, 40)))); // 直连重量1
            portStatus.setDirectWeighing2(directWeighing(
                    HexadecimalDataUtil.hexToDecimal(queryStatusResponse.substring(40, 44)),
                    HexadecimalDataUtil.hexToDecimal(queryStatusResponse.substring(44, 48)))); // 直连重量2
            portStatus.setFirmwareVersion(
                    HexadecimalDataUtil.hexToDecimal(queryStatusResponse.substring(48, 50)) + "." +
                            HexadecimalDataUtil.hexToDecimal(queryStatusResponse.substring(50, 52)));

            // 非开机未设置标志位 & 下位机重启标记位 为 0  表示下位机重启过了，需要重新控制下位机初始化
            if (!MyApplication.HWRestart && Integer.parseInt(queryStatusResponse.substring(52, 56)) ==0){

                List<AdminParameterBean.FanHumiditySettingBean> fanHumiditySettingBeans = MyApplication.adminParameterBean.getFanHumiditySettingBeanList();
                for (AdminParameterBean.FanHumiditySettingBean a : fanHumiditySettingBeans) {
                    String fanGearHumidityVoltage = PortControlUtil.getFan1GearHumidityVoltageCommands(
                            a.getFanHumidityGear(), a.getFanHumidityVoltage(), a.getFanHumidityNum());
                    PortControlUtil.getInstance().sendCommands(fanGearHumidityVoltage);
                }

                // 设置开、关门电压
                String inletOpenCloseVoltage = PortControlUtil.inletOpenCloseVoltageHexData(
                        Integer.parseInt(MyApplication.adminParameterBean.getInletOpenVoltage()),
                        Integer.parseInt(MyApplication.adminParameterBean.getInletCloseVoltage()));
                PortControlUtil.getInstance().sendCommands(inletOpenCloseVoltage);

//            if (MyApplication.getFirmwareVersionNew()){
                // 设置高速关门时间
                String inletQuicklyCloseTime = PortControlUtil.inletQuicklyCloseTimeHexData(
                        MyApplication.adminParameterBean.getInletQuicklyCloseTime());
                PortControlUtil.getInstance().sendCommands(inletQuicklyCloseTime);
//            }

                // 设置开关门超时时间、低速电压
                String inletTimeoutVoltage = PortControlUtil.inletTimeoutVoltageHexData(
                        Integer.parseInt(MyApplication.adminParameterBean.getInletTimeoutTime()),
                        Integer.parseInt(MyApplication.adminParameterBean.getInletLowSpeedVoltage()));
                PortControlUtil.getInstance().sendCommands(inletTimeoutVoltage);
                PortControlUtil.getInstance().sendCommands(PortConstants.LED1_ON_GREEN); //灯1为绿色常亮
                PortControlUtil.getInstance().sendCommands(PortConstants.LED2_ON_GREEN); //灯2为绿色常亮
                PortControlUtil.getInstance().sendCommands(PortConstants.LIGHTING_AUTO); //照明自动

                PortControlUtil.getInstance().sendCommands(PortConstants.JUDGE_RESTART); // 设置下位机重启判断标志位


                // 投入口或是观察口打开状态下，电机不运转
                if (PortControlUtil.getInstance().getPortStatus().getInletStatus() == 2 ||
                        PortControlUtil.getInstance().getPortStatus().getInletStatus() == 7 ||
                        PortControlUtil.getInstance().getPortStatus().getObserveDoorStatus() == 0) {
                    return;
                }
                PortControlUtil.getInstance().sendCommands(PortConstants.STIR_FORWARD); // 搅拌启动正转
                PortControlUtil.getInstance().sendCommands(PortConstants.FAN1_AUTOMATIC); //风扇启动
                PortControlUtil.getInstance().sendCommands(PortControlUtil.getInstance().getHeater1AutomaticCommands()); //加热1自动
                PortControlUtil.getInstance().sendCommands(PortControlUtil.getInstance().getHeater2AutomaticCommands()); //加热2自动
                PortControlUtil.getInstance().sendCommands(PortControlUtil.getInstance().getDehumidificationAutomaticCommands()); //烘干机自动
            }

            // 根据选择的称重源 设置使用的重量
            if (MyApplication.adminParameterBean.getWeighingSourceSetting() == Constant.WEIGHING_SOURCE_485) {
                portStatus.setChooseUseWeighing(portStatus.getNetWeight());
            } else if (MyApplication.adminParameterBean.getWeighingSourceSetting() == Constant.WEIGHING_SOURCE_LOADCELL1) {
                portStatus.setChooseUseWeighing(portStatus.getDirectWeighing1());
            } else if (MyApplication.adminParameterBean.getWeighingSourceSetting() == Constant.WEIGHING_SOURCE_LOADCELL2) {
                portStatus.setChooseUseWeighing(portStatus.getDirectWeighing2());
            }

            // 判断当前称重单位，如果是kg，忽略，如果是磅，转换
            if (MyApplication.adminParameterBean.getWeighingUnit() == Constant.WEIGHING_UNIT_POUND) {
                portStatus.setChooseUseWeighing(DecimalFormatUtil.mul(
                        portStatus.getChooseUseWeighing(), Constant.POUND));
            }


            // 故障6 称重故障 （总重量超过指定最下或最大重量）
           /* if ((portStatus.getChooseUseWeighing() < Integer.parseInt(MyApplication.adminParameterBean.getInletLimitedAlarmTotalMin()) ||
                    portStatus.getChooseUseWeighing() > Integer.parseInt(MyApplication.adminParameterBean.getInletLimitedAlarmTotalMax())) &&
                    troubleTypeBean.getTroubleWeigh() == 0 &&
                    (troubleTypeBean.getTroubleType() != Constant.TROUBLE_TYPE_STIR ||
                            troubleTypeBean.getTroubleType() != Constant.TROUBLE_TYPE_OUTLET ||
                            troubleTypeBean.getTroubleType() != Constant.TROUBLE_TYPE_OBSERVE)) {
                troubleTypeBean.setIsTrouble(true);
                troubleTypeBean.setTroubleWeigh(Constant.TRUE);
                troubleTypeBean.setTroubleType(Constant.TROUBLE_TYPE_WEIGH);
                HTTPServerUtil.sendHeartBeat2();
                GreenDaoUtil.getInstance().insertTrouble(6);
                sendCommands(PortConstants.LED1_ON_RED);
            }*/


            // 紧急开关打开并且没有显示急停dialog的情况下，显示dialog
            if (portStatus.getStop() == 1 && !stopDialogShow) {
//                Looper.prepare();
                ActivityUtil.getInstance().getActivity().runOnUiThread(() -> {
                    stopDialogShow = true;
                    stopDialog = DialogManage.getDialog(ActivityUtil.getInstance().getActivity(),
                            ActivityUtils.getTopActivity().getString(R.string.stop_manual_remove_stop));
                    stopDialog.show();
                    handler.post(runnable);
                });

//                handler.postDelayed(runnable, 2 * Constant.second);

//                Looper.loop();
//                new Handler(Looper.getMainLooper()).post(runnable);
                return;
            } else if (portStatus.getStop() == 0 && stopDialogShow) { //紧急开关关闭并且显示急停dialog的情况下，
                isAfterStop = true;
                Looper.prepare();
                stopDialogShow = false;

                stopDialog.dismiss();
                handler.postDelayed(() -> DialogManage.getDialogConfirm(ActivityUtil.getInstance().getActivity(),
                        ActivityUtils.getTopActivity().getString(R.string.click_remove_stop)).show(), 100);
                handler.post(runnable);
                Looper.loop();
                LogUtils.e("显示解除急停确认对话框");

                return;
            }
            sendHex = "";
            handler.postDelayed(runnable, 100);
//            handler.postDelayed(runnable, 150);
//            handler.postDelayed(runnable, Constant.second);
        }
    }

    /**
     * 获取加热板2 自动加热 串口指令
     *
     * @param targetTem 目标温度
     * @param floatTem  浮动温度
     * @return
     */
    public static String getHeater2AutomaticCommands(int targetTem, int floatTem) {
        String hexHeater = PortConstants.HEATER2_AUTOMATIC_HEAD
                + HexadecimalDataUtil.decimalToHex((targetTem - floatTem) * 10)
                + HexadecimalDataUtil.decimalToHex((targetTem + floatTem) * 10);
        return hexHeater + HexadecimalDataUtil.getCRC(hexHeater);
    }


    /**
     * 获取烘干器（除湿） 自动除湿 串口指令
     *
     * @return
     */
    public static String getDehumidificationAutomaticCommands() {
        return getDehumidificationAutomaticCommands(
                Integer.parseInt(MyApplication.adminParameterBean.getHumiditySetting().getTargetHumidity()),
                Integer.parseInt(MyApplication.adminParameterBean.getHumiditySetting().getFloatHumidity()));
    }

    /**
     * 获取烘干器（除湿） 自动除湿 串口指令
     *
     * @param targetHumidity 目标湿度
     * @param floatHumidity  浮动湿度
     * @return
     */
    public static String getDehumidificationAutomaticCommands(int targetHumidity, int floatHumidity) {
        String hexHeater = PortConstants.DEHUMIDIFICATION_AUTOMATIC_HEAD
                + HexadecimalDataUtil.decimalToHex((targetHumidity - floatHumidity) * 10)
                + HexadecimalDataUtil.decimalToHex((targetHumidity + floatHumidity) * 10);
        return hexHeater + HexadecimalDataUtil.getCRC(hexHeater);
    }

    /**
     * 获取风扇1 档位及对应电压，湿度设置
     *
     * @param gear        风扇1 档位 1-3
     * @param fanVoltage  风扇电压 3-24V
     * @param fanHumidity 风扇温度 0-100 %
     * @return
     */
    public static String getFan1GearHumidityVoltageCommands(int gear, int fanVoltage, int fanHumidity) {
        String hexHeater = "";
        if (gear == 1) {
            hexHeater = PortConstants.FAN1_SETTING_VOLTAGE_HUMIDITY_GEAR_HEAD_1 +
                    HexadecimalDataUtil.decimalToHex2(fanVoltage) +
                    HexadecimalDataUtil.decimalToHex2(fanHumidity);
        } else if (gear == 2) {
            hexHeater = PortConstants.FAN1_SETTING_VOLTAGE_HUMIDITY_GEAR_HEAD_2 +
                    HexadecimalDataUtil.decimalToHex2(fanVoltage) +
                    HexadecimalDataUtil.decimalToHex2(fanHumidity);
        } else if (gear == 3) {
            hexHeater = PortConstants.FAN1_SETTING_VOLTAGE_HUMIDITY_GEAR_HEAD_3 +
                    HexadecimalDataUtil.decimalToHex2(fanVoltage) +
                    HexadecimalDataUtil.decimalToHex2(fanHumidity);
        }
        return hexHeater + HexadecimalDataUtil.getCRC(hexHeater);
    }


    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    private static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public void cancelHandler() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
//            handler = null;
        }
    }

    /**
     * 向消息队列插入post指令
     *
     * @param hexData
     */
    public void sendCommand(String hexData) {

        if (hexData.equals(PortConstants.QUERY_STATUS1) ||
                hexData.equals(PortConstants.QUERY_STATUS2) ||
                hexData.equals(PortConstants.QUERY_STATUS3) ||
                hexData.equals(PortConstants.QUERY_STATUS4)) {
            portSendMessage(hexData);
            return;
        }

        handler.removeCallbacks(runnable);
        portSendMessage(hexData);
//        if (sendPortCommandsList != null) {
//            sendPortCommandsList.add(hexData);
//        }
    }

    /**
     * 向消息队列插入post指令
     *
     * @param sendPortCommandsList
     */
    public void sendCommands(List<String> sendPortCommandsList) {
        LogUtils.e(TAG + "sendPortCommandsList2：" + sendPortCommandsList.size());
        if (sendPortCommandsList.size() == 0) {
            sendPortType = 1;
            handler.postDelayed(runnable, 100);
//            handler.postDelayed(runnable, 150);
//            handler.postDelayed(runnable, Constant.second);
            return;
        }
        sendPortType = 2;
        this.sendPortCommandsList = sendPortCommandsList;
        handler.removeCallbacks(runnable);

        sendHex = sendPortCommandsList.get(0);
        SaveToSdUtil.savePortDataToSD(TAG + "portSendMessage2 发送数据:" + sendHex, 2);
        LogUtils.e(TAG + "发送数据2：" + sendHex);
        myCountDownTimerCancel = true;
        myCountDownTimer.cancel();
        if (serialPort != null && serialPort.isOpen()) {
            // TODO: 2023/3/13 暂时注释
            if (sendHex.equals(PortConstants.INLET_CLOSE)/* || sendHex.equals(PortConstants.INLET_UNLOCK)*/) {
                closeCountTimerSet();
            }
            myCountDownTimerCancel = false;
            myCountDownTimer.start();
//            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                @Override
//                public void run() {
            queryStatusResponse = "";
            serialPort.sendHex(sendPortCommandsList.get(0));
//                }
//            }, 100);

        }
    }

    /**
     * 向消息队列插入post指令
     *
     * @param command
     */
    public void sendCommands(String command) {
        LogUtils.e(TAG + "sendPortCommandsList3：" + sendPortCommandsList.size() + "\t\tcommand:" + command);
        if (sendHex.equals(PortConstants.QUERY_STATUS1) || sendHex.equals(PortConstants.QUERY_STATUS2)
                || sendHex.equals(PortConstants.QUERY_STATUS3) || sendHex.equals(PortConstants.QUERY_STATUS4)) {
            LogUtils.e(TAG + "当前正在执行的命令：" + sendHex);
            this.sendPortCommandsList.add(command);
            return;
        }
        sendPortType = 2;
        if (sendPortCommandsList != null && sendPortCommandsList.size() != 0) {
            sendPortCommandsList.add(command);
            return;
        }
        handler.removeCallbacks(runnable);
        sendPortCommandsList.add(command);
        sendHex = command;
        SaveToSdUtil.savePortDataToSD(TAG + "portSendMessage3 发送数据:" + command, 2);
        LogUtils.e(TAG + "发送数据3：" + command);
        myCountDownTimerCancel = true;
        myCountDownTimer.cancel();
        if (serialPort != null && serialPort.isOpen()) {
            // TODO: 2023/3/13 暂时注释
            if (sendHex.equals(PortConstants.INLET_CLOSE) /*|| sendHex.equals(PortConstants.INLET_UNLOCK)*/) {
                closeCountTimerSet();
            }
            myCountDownTimerCancel = false;
            myCountDownTimer.start();
            queryStatusResponse = "";
            serialPort.sendHex(command);

        }
    }


    public void portSendMessage(String hexData) {
        queryStatusResponse = "";
        sendPortType = 1;
        sendHex = hexData;
//        SaveToSdUtil.savePortDataToSD(TAG + "portSendMessage1 发送数据:" + hexData, 1);
//        LogUtils.e(TAG + "portSendMessage1 发送数据:" + hexData, 1);
        myCountDownTimerCancel = true;
        myCountDownTimer.cancel();
        if (serialPort != null && serialPort.isOpen()) {
            myCountDownTimerCancel = false;
            myCountDownTimer.start();
//            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                @Override
//                public void run() {
            serialPort.sendHex(hexData);
//                }
//            },100);

        }
    }

    public void closePort() {
        queryStatusResponse = "";
        if (myCountDownTimer != null) {
            myCountDownTimerCancel = true;
            myCountDownTimer.cancel();
        }
        if (serialPort != null /*&& serialPort.isOpen()*/) {
            serialPort.close();
            serialPort = null;
        }
    }


    /**
     * 故障4 投入口发送打开或关闭命令N秒（N秒（最好4S以上）可在参数设置界面进行设置）后未打开或未关闭
     */
    private void closeCountTimerSet() {
        if (troubleTypeBean.getIsTrouble() &&
                (troubleTypeBean.getTroubleType() == Constant.TROUBLE_TYPE_STIR ||
                        troubleTypeBean.getTroubleType() == Constant.TROUBLE_TYPE_OUTLET ||
                        troubleTypeBean.getTroubleType() == Constant.TROUBLE_TYPE_OBSERVE)) {
            return;
        }
        if (closeCountTimer != null) {
            closeCountTimer.start();
            return;
        }
        closeCountTimer = new CountTimer(Constant.second) {
            @Override
            protected void onTick(long millisFly) {
                super.onTick(millisFly);

                int times = (int) (millisFly / 1000);
                if (Integer.parseInt(MyApplication.adminParameterBean.getInletTimeoutTime()) > times) {
                    if ((getPortStatus().getInletStatus() == 5 /*|| getPortStatus().getInletStatus() == 2*/)
                            && closeCountTimer != null) {
                        closeCountTimer.cancel();
                    }
                    return;
                }
                if (troubleTypeBean.getTroubleInlet() == Constant.TRUE) {
                    closeCountTimer.cancel();
//                    closeCountTimer = null;
                    return;
                }
                troubleTypeBean.setIsTrouble(true);
                troubleTypeBean.setTroubleInlet(Constant.TRUE);
                troubleTypeBean.setTroubleType(Constant.TROUBLE_TYPE_INLET);
                HTTPServerUtil.sendHeartBeat2();
                GreenDaoUtil.getInstance().insertTrouble(4);
                sendCommands(PortConstants.LED1_ON_RED);
                closeCountTimer.cancel();
            }
        };
        closeCountTimer.start();
//        new Handler(ActivityUtils.getTopActivity().getMainLooper()).postDelayed(() ->
//                        closeCountTimer.cancel(),
//                (Integer.parseInt(MyApplication.adminParameterBean.getInletTimeoutTime()) + 3) * Constant.second);
    }

    /**
     * 16进制重量数据 转换为对应ASCII
     *
     * @param hex 14位hex数据
     * @return
     */
    private float hex2ASCIIStr(String hex) {
        float height = 0.0f;
        if (hex.equals("0000000000000000")) {
            return height;
        }
        StringBuffer sb = new StringBuffer();
        try {
            if (Integer.parseInt(hex) == 0) {
                ToastUtils.showShort(R.string.serial_connection_error);
                return height;
            }
        } catch (Exception e) {
        }
        sb.append((char) Integer.parseInt(hex.substring(0, 2), 16));
        sb.append((char) Integer.parseInt(hex.substring(2, 4), 16));
        sb.append((char) Integer.parseInt(hex.substring(4, 6), 16));
        sb.append((char) Integer.parseInt(hex.substring(6, 8), 16));
        sb.append((char) Integer.parseInt(hex.substring(8, 10), 16));
        sb.append((char) Integer.parseInt(hex.substring(10, 12), 16));
        sb.append((char) Integer.parseInt(hex.substring(12, 14), 16));
        sb.append((char) Integer.parseInt(hex.substring(14, 16), 16));
        height = Float.parseFloat(sb.toString());


        return height;

    }

    /**
     * 获取直连称重
     *
     * @param Integer 整数部分
     * @param decimal 小数部分
     * @return
     */
    private float directWeighing(int Integer, int decimal) {
        return DecimalFormatUtil.DecimalFormatThree(Integer + decimal / 1000f);
    }
}
