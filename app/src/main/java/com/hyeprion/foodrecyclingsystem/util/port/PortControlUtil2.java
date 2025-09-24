package com.hyeprion.foodrecyclingsystem.util.port;

import android.app.Dialog;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hyeprion.foodrecyclingsystem.R;
import com.hyeprion.foodrecyclingsystem.base.MyApplication;
import com.hyeprion.foodrecyclingsystem.bean.PortStatus;
import com.hyeprion.foodrecyclingsystem.bean.TroubleTypeBean;
import com.hyeprion.foodrecyclingsystem.dialog.DialogManage;
import com.hyeprion.foodrecyclingsystem.util.ActivityUtil;
import com.hyeprion.foodrecyclingsystem.util.Constant;
import com.hyeprion.foodrecyclingsystem.util.GreenDaoUtil;
import com.hyeprion.foodrecyclingsystem.util.HTTPServerUtil;
import com.hyeprion.foodrecyclingsystem.util.SaveToSdUtil;
import com.hyeprion.foodrecyclingsystem.util.TimeUtil;
import com.hyeprion.foodrecyclingsystem.util.countdowntimer.CountTimer;
import com.hyeprion.foodrecyclingsystem.util.countdowntimer.MyCountDownTimer;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import top.maybesix.xhlibrary.serialport.ComPortData;
import top.maybesix.xhlibrary.serialport.SerialPortHelper;
import top.maybesix.xhlibrary.util.HexStringUtils;

/**
 * 投入口、搅拌电机、led、风扇、加热器、除湿器、称重传感器 串口 控制
 */
public class PortControlUtil2 implements SerialPortHelper.OnSerialPortReceivedListener {
    private static final String TAG = "PortControlUtil  ";
    private static volatile PortControlUtil2 portControlUtil;
    //    private ExceptionInfoEvent exceptionInfoEvent = new ExceptionInfoEvent();
    private SerialPortHelper serialPort;
    private String sendHex = "";
    public static TroubleTypeBean troubleTypeBean;
    /**
     * 保存待发送的串口命令
     */
    private List<String> sendPortCommandsList = new ArrayList<>();
    //    private
    private String queryStatusResponse = "";
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (sendPortType == 1) {
                portSendMessage(PortConstants.QUERY_STATUS1);
            } else if (sendPortType == 2) {
                sendCommands(sendPortCommandsList);
            }

//            portSendMessage(PortConstants.QUERY_STATUS1);
        }
    };
    // 倒计时总时长
    long millisInFuture = 1 * Constant.second;
    private MyCountDownTimer myCountDownTimer;
    private boolean myCountDownTimerCancel = false;
    private PortStatus portStatus;

    private DecimalFormat df = new DecimalFormat("0.0");


    private int myStir = 1;
    private int myHeater = 1;
    private int myFan = 1;
    private int myInlet = 1;

    private int sendPortType = 1; // 1：单条发送  2：list内多条有序发送

    private Dialog stopDialog;
    private boolean stopDialogShow = false;

    private int stir = 1; // 保存搅拌电机状态
    private int outletOpenTimes = 0;//排出口持续开启次数，超过2认为异常
    private int observeDoorTimes = 0;//观察口持续开启次数，超过2认为异常
    private CountTimer openBtnCountTimer; // 投入口打开按钮超时异常
    private CountTimer closeCountTimer; // 投入口关闭超时异常

    private boolean closeCountdownFlag = false; // 投料门关闭状态时重量跳动范围在n分钟内超过指定值计时flag  false：未计时
    private float closeCountdownBeforeWeight = 0; // 投料门关闭状态时倒计时开始前重量
    private Handler closeCountdownHandler = new Handler();
    private Runnable closeCountdownRunnable = new Runnable() {
        @Override
        public void run() {
            if ((getPortStatus().getNetWeight() - closeCountdownBeforeWeight) <=
                    Integer.parseInt(MyApplication.adminParameterBean.getWeighAlarmWeight())) {
                closeCountdownFlag = true;
                closeCountdownBeforeWeight = getPortStatus().getNetWeight();
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
            sendCommands(PortConstants.LED1_ON_RED);
        }
    };


    private PortControlUtil2() {
    }

    public static PortControlUtil2 getInstance() {
        if (portControlUtil == null) {
            synchronized (PortControlUtil2.class) {
                if (portControlUtil == null) {
                    portControlUtil = new PortControlUtil2();
                }
            }
        }
        return portControlUtil;
    }

    public void openPort() {
        String port = "/dev/ttyS3";
        int baudRate = 9600;
        //串口程序初始化
        serialPort = new SerialPortHelper(port, baudRate);
        //打开串口
        serialPort.open();
        serialPort.setSerialPortReceivedListener(this::onSerialPortDataReceived);
        // 倒计时总时长
        long millisInFuture = 1 * Constant.second;
//        long millisInFuture = 300;
        troubleTypeBean = new TroubleTypeBean(false, 0, 0, 0,0,
                0, 0, 0, 0, 0, 0);
        myCountDownTimer = new MyCountDownTimer(millisInFuture, Constant.second) {

            @Override
            public void onFinish() {
                if (myCountDownTimerCancel) {
                    return;
                }
                closePort();
                handler.postDelayed(() -> openPort(), 200);
                handler.postDelayed(runnable, 500);
            }
        };
    }

    public void open() {
        if (serialPort != null) {
            serialPort.open();
        }
    }

    @Override
    public void onSerialPortDataReceived(ComPortData comPortData) {
        myCountDownTimerCancel = true;
        myCountDownTimer.cancel();
        //处理接收的串口消息
        String s = HexStringUtils.byteArray2HexString(comPortData.getRecData());
//        LogUtils.e(TAG + "onReceived: " + s);
        if (sendPortType == 2) {
            LogUtils.e(TAG + "onReceived: " + s);
            this.sendPortCommandsList.remove(0);
            sendCommands(sendPortCommandsList);
            return;
        }

        int type = Integer.parseInt(s.substring(2, 4));
        if (type == 03) {
            if (sendPortCommandsList.size() > 0) {
                LogUtils.e(TAG + "time: " + TimeUtil.getDateSToString());
                sendCommands(sendPortCommandsList);
                return;
            }
            queryStatusResponse = s;
            if (sendHex.equals(PortConstants.QUERY_STATUS1)) { // 第一个查询返回的结果
//                LogUtils.e("当前commands PortConstants.QUERY_STATUS1:" + PortConstants.QUERY_STATUS1);
                dataPortStatus(PortConstants.QUERY_STATUS1);
                portSendMessage(PortConstants.QUERY_STATUS2); // 发送第二部分查询
            } else if (sendHex.equals(PortConstants.QUERY_STATUS2)) {
//                LogUtils.e("当前commands PortConstants.QUERY_STATUS2:" + PortConstants.QUERY_STATUS2 + "\ncomPortData:" + queryStatusResponse);

                dataPortStatus(PortConstants.QUERY_STATUS2);
                portSendMessage(PortConstants.QUERY_STATUS3); // 发送第三部分查询
            } else if (sendHex.equals(PortConstants.QUERY_STATUS3)) {
//                LogUtils.e("当前commands PortConstants.QUERY_STATUS3:" + PortConstants.QUERY_STATUS3+ "\ncomPortData:" + queryStatusResponse);

                dataPortStatus(PortConstants.QUERY_STATUS3);
                portSendMessage(PortConstants.QUERY_STATUS4); // 发送第四部分查询
            } else if (sendHex.equals(PortConstants.QUERY_STATUS4)) {
                LogUtils.e("当前commands PortConstants.QUERY_STATUS4:" + PortConstants.QUERY_STATUS4 + "\ncomPortData:" + queryStatusResponse);

                dataPortStatus(PortConstants.QUERY_STATUS4);


//                sendHex = "";
//                handler.postDelayed(runnable, 2 * Constant.second);
                // 日投入量超重或投入重量超重,并且led2 不是黄灯的情况下，发送led2黄灯命令
               /* if ((Float.parseFloat(GreenDaoUtil.getInstance().get24HTotalInletWeight()) >
                        Float.parseFloat(MyApplication.adminParameterBean.getInletLimited24H()) ||
                        Float.parseFloat(MyApplication.adminParameterBean.getInletLimitedTotalAccumulation()) >
                                Float.parseFloat(MyApplication.adminParameterBean.getInletLimitedTotal())) &&
                        getPortStatus().getLed2RGB() != PortConstants.COLOR_YELLOW) {
                    sendCommands(PortConstants.LED2_ON_YELLOW);
                } else if (getPortStatus().getLed2RGB() != PortConstants.COLOR_GREEN) {
                    sendCommands(PortConstants.LED2_ON_GREEN);
                }
*/

/*

                // 故障1 搅拌电机异常
                if (getPortStatus().getStirStatus() == 5 && stir != 5) {
                    stir = PortControlUtil.getInstance().getPortStatus().getStirStatus();
                    troubleTypeBean.setIsTrouble(true);
                    troubleTypeBean.setTroubleStir(Constant.TRUE);
                    troubleTypeBean.setTroubleType(Constant.TROUBLE_TYPE_STIR);
                    stopAllMotor(1);
                    HTTPServerUtil.sendHeartBeat2();
                    GreenDaoUtil.getInstance().insertTrouble(1);
                } else {
                    stir = PortControlUtil.getInstance().getPortStatus().getStirStatus();
                }

                // 搅拌电机异常，优先级最高
                if (troubleTypeBean.getIsTrouble() && troubleTypeBean.getTroubleType() == Constant.TROUBLE_TYPE_STIR) {
                    return;
                }

                // 故障2 排料口未关闭
                if (getPortStatus().getOutletStatus() == 0) {
                    outletOpenTimes++;
                    if (outletOpenTimes == 2) {
                        troubleTypeBean.setIsTrouble(true);
                        troubleTypeBean.setTroubleOutlet(Constant.TRUE);
                        troubleTypeBean.setTroubleType(Constant.TROUBLE_TYPE_OUTLET);
                        stopAllMotor(1);
                        HTTPServerUtil.sendHeartBeat2();
                        GreenDaoUtil.getInstance().insertTrouble(2);
                    }

                } else {
                    // 之前判定为故障，现在故障解除
                    if (outletOpenTimes >= 2) {
                        PortControlUtil.troubleTypeBean.setIsTrouble(false);
                        PortControlUtil.troubleTypeBean.setTroubleOutlet(Constant.FALSE);
                        PortControlUtil.troubleTypeBean.setTroubleType(Constant.TROUBLE_TYPE_REMOVE);
                        GreenDaoUtil.getInstance().insertTrouble(0);
                        //接收到排料门关闭后延时10秒自动启动
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startAllMotor();
                            }
                        }, 10 * Constant.second);

                    }
                    outletOpenTimes = 0;
                }

                // 故障3 观察口未关闭
                if (getPortStatus().getObserveDoorStatus() == 0) {
                    observeDoorTimes++;
                    if (observeDoorTimes == 2) {
                        troubleTypeBean.setIsTrouble(true);
                        troubleTypeBean.setTroubleUpCheck(Constant.TRUE);
                        troubleTypeBean.setTroubleType(Constant.TROUBLE_TYPE_OBSERVE);
                        stopAllMotor(1);
                        HTTPServerUtil.sendHeartBeat2();
                        GreenDaoUtil.getInstance().insertTrouble(3);
                    }

                } else {
                    // 之前判定为故障，现在故障解除
                    if (observeDoorTimes >= 2) {
                        PortControlUtil.troubleTypeBean.setIsTrouble(false);
                        PortControlUtil.troubleTypeBean.setTroubleUpCheck(Constant.FALSE);
                        PortControlUtil.troubleTypeBean.setTroubleType(Constant.TROUBLE_TYPE_REMOVE);
                        GreenDaoUtil.getInstance().insertTrouble(0);
                        //接收到排料门关闭后延时10秒自动启动
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startAllMotor();
                            }
                        }, 10 * Constant.second);

                    }
                    observeDoorTimes = 0;
                }

                // 排料口未关闭或是观察口未关闭
                if (troubleTypeBean.getIsTrouble() &&
                        (troubleTypeBean.getTroubleType() == Constant.TROUBLE_TYPE_OUTLET ||
                                troubleTypeBean.getTroubleType() == Constant.TROUBLE_TYPE_OBSERVE)) {
                    return;
                }


                // 故障4 投入口发送打开或关闭命令N秒（N秒（最好4S以上）可在参数设置界面进行设置）后未打开或未关闭
                if (getPortStatus().getOpenDoorBtn() == 1) {
                    openBtnCountTimer = new CountTimer(Constant.second) {
                        @Override
                        protected void onTick(long millisFly) {
                            super.onTick(millisFly);

                            int times = (int) (millisFly / 1000);
                            if (Integer.parseInt(MyApplication.adminParameterBean.getInletTimeoutTime()) < times) {
                                if (getPortStatus().getInletStatus() == 2) {
                                    openBtnCountTimer.cancel();
                                }
                                return;
                            }
                            troubleTypeBean.setIsTrouble(true);
                            troubleTypeBean.setTroubleInlet(Constant.TRUE);
                            troubleTypeBean.setTroubleType(Constant.TROUBLE_TYPE_INLET);
                            HTTPServerUtil.sendHeartBeat2();
                            GreenDaoUtil.getInstance().insertTrouble(4);
                            sendCommands(PortConstants.LED1_ON_RED);
                            openBtnCountTimer.cancel();
                        }
                    };
                    openBtnCountTimer.start();
                }

                // 故障6 称重报警（保持投料门关闭状态时重量跳动范围在1分钟内超过指定值）
                if (getPortStatus().getInletStatus() == 5 && !closeCountdownFlag && troubleTypeBean.getTroubleWeigh() != Constant.TRUE) {
                    closeCountdownFlag = true;
                    closeCountdownBeforeWeight = getPortStatus().getNetWeight();
                    closeCountdownHandler.postDelayed(closeCountdownRunnable,
                            Integer.parseInt(MyApplication.adminParameterBean.getWeighAlarmTime()) * Constant.second);
                }
                if (getPortStatus().getInletStatus() != 5) {
                    closeCountdownFlag = false;
                    closeCountdownHandler.removeCallbacks(closeCountdownRunnable);
                    closeCountdownHandler.removeCallbacksAndMessages(null);
                }


                int heater = 1;
                int fan = 1;
                int inlet = 1;
*/


            }
//            LogUtils.e(TAG + "onReceived: " + s);

            return;
        }
        // 控制命令返回结果2S后发送查询
        handler.postDelayed(runnable, 2 * Constant.second);

    }

    private void dataPortStatus(String commands) {
        if (portStatus == null) {
            portStatus = new PortStatus();
        }
        if (commands.equals(PortConstants.QUERY_STATUS1)) { // 处理第一部分查询，截止到FAN1
            portStatus.setInletSensorStatus(HexadecimalDataUtil.
                    hexToDecimal(queryStatusResponse.substring(8, 10))); // 投入口传感器状态
            portStatus.setInletStatus(HexadecimalDataUtil.
                    hexToDecimal(queryStatusResponse.substring(10, 12))); // 投入口状态
            portStatus.setObserveDoorStatus(HexadecimalDataUtil.
                    hexToDecimal(queryStatusResponse.substring(12, 16))); // 观察口
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
//            portStatus.setHeater2(Integer.parseInt(queryStatusResponse.substring(16, 20)));// 加热2模式
            portStatus.setTemperature((float) HexadecimalDataUtil.
                    hexToDecimal(queryStatusResponse.substring(20, 24)) / 10);// 当前温度
            portStatus.setHumidity((float) HexadecimalDataUtil.
                    hexToDecimal(queryStatusResponse.substring(24, 28)) / 10);// 当前湿度

            // 故障7 湿度报警（湿度超过95%保持10分钟以上）
            if (portStatus.getHumidity() > 95 && !humidityFlag &&
                    troubleTypeBean.getTroubleHumidity() == Constant.FALSE &&
                    troubleTypeBean.getTroubleType() != Constant.TROUBLE_TYPE_STIR ||
                    troubleTypeBean.getTroubleType() != Constant.TROUBLE_TYPE_OUTLET ||
                    troubleTypeBean.getTroubleType() != Constant.TROUBLE_TYPE_OBSERVE) {
                humidityFlag = true;
                humidityHandler.postDelayed(humidityRunnable, 10 * 60 * Constant.second);
            } else {
                humidityFlag = false;
                humidityHandler.removeCallbacks(humidityRunnable);
                humidityHandler.removeCallbacksAndMessages(null);
            }

//            portStatus.setDehumidifier(HexadecimalDataUtil.
//                    hexToDecimal(queryStatusResponse.substring(28, 32)));// 除湿模式 烘干机
            portStatus.setDehumidifier(Integer.parseInt(queryStatusResponse.substring(28, 32)));// 除湿模式 烘干机
            portStatus.setGrossWeight(HexadecimalDataUtil.
                    hex2Float(queryStatusResponse.substring(32, 48)));
        } else if (commands.equals(PortConstants.QUERY_STATUS3)) { // 查询净重-加热温度 10
            portStatus.setNetWeight(hex2ASCIIStr(queryStatusResponse.substring(8, 24))); // 净重，前两位为符号位
            portStatus.setTare(HexadecimalDataUtil.
                    hex2Float(queryStatusResponse.substring(24, 40))); // 皮重
            portStatus.setHeaterTemperature1((float) HexadecimalDataUtil.
                    hexToDecimal(queryStatusResponse.substring(40, 44)) / 10); // 油温加热温度1
            portStatus.setHeaterTemperature2((float) HexadecimalDataUtil.
                    hexToDecimal(queryStatusResponse.substring(44, 48)) / 10);// 油温加热温度2
        } else if (commands.equals(PortConstants.QUERY_STATUS4)) { // 照明-风压 4
            portStatus.setLighting(HexadecimalDataUtil.
                    hexToDecimal(queryStatusResponse.substring(8, 12))); // 照明
            portStatus.setStop(HexadecimalDataUtil.
                    hexToDecimal(queryStatusResponse.substring(12, 16))); // 紧急开关
            portStatus.setOpenDoorBtn(HexadecimalDataUtil.
                    hexToDecimal(queryStatusResponse.substring(16, 20))); // 开门按钮
//            portStatus.setWindPressure( new DecimalFormat("0.00").format((float)HexadecimalDataUtil.
//                    byteToShortBig(queryStatusResponse.substring(20, 24))/100)+""); // 风压值
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
                Looper.prepare();
                stopDialogShow = false;

                stopDialog.dismiss();
                handler.postDelayed(() -> DialogManage.getDialogConfirm(ActivityUtil.getInstance().getActivity(),
                        ActivityUtils.getTopActivity().getString(R.string.click_remove_stop)).show(), 100);
                handler.post(runnable);
                Looper.loop();
                LogUtils.e("显示解除急停确认对话框");
//                handler.postDelayed(runnable, 2 * Constant.second);

                return;
            }
            sendHex = "";
//            handler.postDelayed(runnable, 2 * Constant.second);
            handler.postDelayed(runnable, Constant.second);
        }


    }

    public PortStatus getPortStatus() {
        if (portStatus == null) {
            portStatus = new PortStatus();
            portStatus.setInletSensorStatus(2);
            portStatus.setInletStatus(1);
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
     * 停止所有电机
     *
     * @param type 1 设备故障停机，需要添加led红灯
     */
    public List<String> stopAllMotor(int type) {
        //投入口停止，搅拌停止，风扇停止，加热停止，除湿停止
        List<String> sendPortCommandsStopList = new ArrayList<>();
        sendPortCommandsStopList.add(PortConstants.INLET_STOP); // 投入口停止
        sendPortCommandsStopList.add(PortConstants.STIR_STOP); // 搅拌停止
        sendPortCommandsStopList.add(PortConstants.FAN1_STOP); //风扇停止
        sendPortCommandsStopList.add(PortConstants.HEATER1_STOP); //加热1停止
        sendPortCommandsStopList.add(PortConstants.HEATER2_STOP); //加热2停止
        sendPortCommandsStopList.add(PortConstants.DEHUMIDIFICATION_STOP); //除湿停止
        if (type == 1) {
            sendPortCommandsStopList.add(PortConstants.LED1_ON_RED); //led1 红灯
        }
        return sendPortCommandsList;
//        PortControlUtil.getInstance().sendCommands(sendPortCommandsStopList);
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
//            handler.postDelayed(runnable, 2 * Constant.second);
            handler.postDelayed(runnable, Constant.second);
            return;
        }
        sendPortType = 2;
        this.sendPortCommandsList = sendPortCommandsList;
        handler.removeCallbacks(runnable);

        sendHex = sendPortCommandsList.get(0);
        SaveToSdUtil.savePortDataToSD(TAG + "portSendMessage2 发送数据:" + sendHex,2);
        LogUtils.e(TAG + "发送数据2：" + sendHex);
        myCountDownTimerCancel = true;
        myCountDownTimer.cancel();
        if (serialPort != null && serialPort.isOpen()) {
            // TODO: 2023/3/13 暂时注释
            /*if (sendHex.equals(PortConstants.INLET_CLOSE)) {
                closeCountTimerSet();
            }*/
            myCountDownTimerCancel = false;
            myCountDownTimer.start();
            serialPort.sendHex(sendPortCommandsList.get(0));
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
                || sendHex.equals(PortConstants.QUERY_STATUS) || sendHex.equals(PortConstants.QUERY_STATUS4)) {
            LogUtils.e(TAG + "当前正在执行的命令：" + sendHex);
            this.sendPortCommandsList.add(command);
            return;
        }
        sendPortType = 2;
        if (sendPortCommandsList != null && sendPortCommandsList.size() != 0) {
            this.sendPortCommandsList.add(command);
            return;
        }
        handler.removeCallbacks(runnable);
        this.sendPortCommandsList.add(command);
        sendHex = command;
        SaveToSdUtil.savePortDataToSD(TAG + "portSendMessage3 发送数据:" + command,2);
        LogUtils.e(TAG + "发送数据3：" + command);
        myCountDownTimerCancel = true;
        myCountDownTimer.cancel();
        if (serialPort != null && serialPort.isOpen()) {
            // TODO: 2023/3/13 暂时注释
           /* if (sendHex.equals(PortConstants.INLET_CLOSE)) {
                closeCountTimerSet();
            }*/
            myCountDownTimerCancel = false;
            myCountDownTimer.start();
            serialPort.sendHex(command);
        }
    }


    private void portSendMessage(String hexData) {
        sendPortType = 1;
        sendHex = hexData;
        SaveToSdUtil.savePortDataToSD(TAG + "portSendMessage1 发送数据:" + hexData,1);

        if (!hexData.equals(PortConstants.QUERY_STATUS1) && !hexData.equals(PortConstants.QUERY_STATUS2) &&
                !hexData.equals(PortConstants.QUERY_STATUS3) &&
                !hexData.equals(PortConstants.QUERY_STATUS4)) {
            LogUtils.e(TAG + "发送数据1：" + hexData);
        }

        Message message = new Message();
        myCountDownTimerCancel = true;
        myCountDownTimer.cancel();
        if (serialPort != null && serialPort.isOpen()) {
            myCountDownTimerCancel = false;
            myCountDownTimer.start();
            serialPort.sendHex(hexData);
        }
    }

    public void closePort() {
        if (myCountDownTimer != null) {
            myCountDownTimerCancel = true;
            myCountDownTimer.cancel();
        }
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.close();
        }
    }


    private void closeCountTimerSet() {
        if (troubleTypeBean.getIsTrouble() &&
                (troubleTypeBean.getTroubleType() == Constant.TROUBLE_TYPE_STIR ||
                        troubleTypeBean.getTroubleType() == Constant.TROUBLE_TYPE_OUTLET ||
                        troubleTypeBean.getTroubleType() == Constant.TROUBLE_TYPE_OBSERVE)) {
            return;
        }
        closeCountTimer = new CountTimer(Constant.second) {
            @Override
            protected void onTick(long millisFly) {
                super.onTick(millisFly);

                int times = (int) (millisFly / 1000);
                if (Integer.parseInt(MyApplication.adminParameterBean.getInletTimeoutTime()) < times) {
                    if (getPortStatus().getInletStatus() == 5) {
                        closeCountTimer.cancel();
                    }
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
        // 故障6 称重故障 （总重量为负值，或总重量超过指定重量）
        // TODO: 2023/3/13 暂时注释
      /*  if ((height < 0 || height > Integer.parseInt(MyApplication.adminParameterBean.getInletLimitedAlarmTotal())) &&
                troubleTypeBean.getTroubleWeigh() == 0 && troubleTypeBean.getTroubleType() != Constant.TROUBLE_TYPE_STIR ||
                troubleTypeBean.getTroubleType() != Constant.TROUBLE_TYPE_OUTLET ||
                troubleTypeBean.getTroubleType() != Constant.TROUBLE_TYPE_OBSERVE) {
            troubleTypeBean.setIsTrouble(true);
            troubleTypeBean.setTroubleWeigh(Constant.TRUE);
            troubleTypeBean.setTroubleType(Constant.TROUBLE_TYPE_WEIGH);
            HTTPServerUtil.sendHeartBeat2();
            GreenDaoUtil.getInstance().insertTrouble(6);
            sendCommands(PortConstants.LED1_ON_RED);
        }*/

        return height;

    }
}
