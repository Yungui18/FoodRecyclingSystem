package com.hyeprion.foodrecyclingsystem.activity;

import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.alibaba.fastjson.JSON;
import com.hyeprion.foodrecyclingsystem.R;
import com.hyeprion.foodrecyclingsystem.base.BaseActivity;
import com.hyeprion.foodrecyclingsystem.base.MyApplication;
import com.hyeprion.foodrecyclingsystem.bean.PortStatus;
import com.hyeprion.foodrecyclingsystem.databinding.ActivityAdminControl2Binding;
import com.hyeprion.foodrecyclingsystem.dialog.DialogChooseTemperature;
import com.hyeprion.foodrecyclingsystem.util.Constant;
import com.hyeprion.foodrecyclingsystem.util.SharedPreFerUtil;
import com.hyeprion.foodrecyclingsystem.util.port.PortConstants;
import com.hyeprion.foodrecyclingsystem.util.port.PortControlUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 管理员页面-电机控制页面
 * from {@link AdminActivity}
 */
public class AdminControlActivity extends BaseActivity<ActivityAdminControl2Binding> implements DialogChooseTemperature.CallBackListener {
    private String sendHex; // 要发送的串口命令
    /**
     * 投入口电机状态
     * 5关闭
     * 2打开
     * 其余停止
     */
    private int inletStatus;
    /**
     * 称重板电机状态
     * 0关闭
     * 1打开
     * 2停止
     */
    private int weighingStatus;
    /**
     * 搅拌电机状态
     * 1正转
     * 2反转
     * 3停止
     */
    private int stirStatus;
    /**
     * 排出电机状态
     * 0反转
     * 1正转
     * 2停止
     */
    private int outputStatus;
    /**
     * 风扇状态
     * 1手动
     * 2停止
     * 其余自动
     */
    private int fanStatus;
    /**
     * 置零设置
     * 1 置零
     * 2 去皮
     */
    private int zeroSetting;

    /**
     * 加热1设置
     * 1 手动
     * 2 停止
     * 其余自动
     */
    private int heaterSetting1;
    /**
     * 加热2设置
     * 1 手动
     * 2 停止
     * 其余自动
     */
    private int heaterSetting2;

    /**
     * 除湿（烘干）设置
     * 1 手动
     * 2 停止
     * 其余自动
     */
    private int dehumidificationSetting;

    /**
     * 灯光1状态
     * 1-3 分别为 红、绿、黄
     */
    private int led1;
    /**
     * 灯光2状态
     * 1-3 分别为 红、绿、黄
     */
    private int led2;

    /**
     * 照明 状态
     * 01：常亮；02：长灭
     */
    private int lighting;
    /**
     * lock1 状态 1：解锁  2：上锁
     */
    private int lock1;
    private int myGear = 1; // 风扇手动湿度档位 1-3

    private Handler doorHandler;
    private Runnable doorRunnable;

    private int type = -1; // 选择器类型


    @Override
    protected void initView() {
        EventBus.getDefault().register(this);

        // TODO: 2022/11/11 串口获取各电机状态
        inletStatus = PortControlUtil.getInstance().getPortStatus().getInletStatus();
//        weighingStatus = 1;
        stirStatus = PortControlUtil.getInstance().getPortStatus().getStirStatus();
//        outputStatus = 1;
        fanStatus = PortControlUtil.getInstance().getPortStatus().getFan1();
        zeroSetting = PortControlUtil.getInstance().getPortStatus().getPressureSetting();
        heaterSetting1 = PortControlUtil.getInstance().getPortStatus().getHeater1();
        heaterSetting2 = PortControlUtil.getInstance().getPortStatus().getHeater2();
        dehumidificationSetting = PortControlUtil.getInstance().getPortStatus().getDehumidifier();
        led1 = nowColor(1);
        led2 = nowColor(2);
        lighting = PortControlUtil.getInstance().getPortStatus().getLighting();
        lock1 = PortControlUtil.getInstance().getPortStatus().getLock1();



        switch (inletStatus) {
            case 5:
                setButtonSelect(viewBinding.btn1InletClose);
                break;
            case 2:
                setButtonSelect(viewBinding.btn1InletOpen);
                break;
            default:
                setButtonSelect(viewBinding.btn1InletStop);
        }

        // 投入口为手动
        if (MyApplication.adminParameterBean.getInletMode().equals(Constant.MANUAL)){
            viewBinding.groupInletControl.setVisibility(View.GONE);

        }
        /*switch (weighingStatus) {
            case 0:
                setButtonSelect(viewBinding.btn2WeighingClose);
                break;
            case 1:
                setButtonSelect(viewBinding.btn2WeighingOpen);
                break;
            case 2:
                setButtonSelect(viewBinding.btn2WeighingStop);
                break;
        }*/
        switch (stirStatus) {
            case 1: // 正转
                setButtonSelect(viewBinding.btn3StirForward);
                break;
            case 2: // 反转
                setButtonSelect(viewBinding.btn3StirReverses);
                break;
            case 3: // 停止
                setButtonSelect(viewBinding.btn3StirStop);
                break;
        }
//        switch (outputStatus) {
//            case 0:
//                setButtonSelect(viewBinding.btn4OutputReverses);
//                break;
//            case 1:
//                setButtonSelect(viewBinding.btn4OutputForward);
//                break;
//            case 2:
//                setButtonSelect(viewBinding.btn4OutputStop);
//                break;
//        }

        switch (fanStatus) {
            case 0:// 风扇停止
                setButtonSelect(viewBinding.btn5FanStop);
                break;
            case 1: // 风扇手动1挡
//                setButtonSelect(viewBinding.btn5FanManual1);
//                break;
            case 2: // 风扇手动2挡
//                setButtonSelect(viewBinding.btn5FanManual2);
//                break;
            case 3: // 风扇手动3挡
                setButtonSelect(viewBinding.btn5FanManual3);
                break;

            default:
                setButtonSelect(viewBinding.btn5FanAutomatic);

        }

//        switch (zeroSetting) {
//            case 2: // 去皮 净重
//                setButtonSelect(viewBinding.btn7NetWeight);
//                break;
//            case 4: // 置零
//                setButtonSelect(viewBinding.btn7Zero);
//                break;
//
//        }

        switch (heaterSetting1) {
            case 1: // 加热模式 手动
                setButtonSelect(viewBinding.btn8HeaterManual);
                break;
            case 2: // 加热模式 停止
                setButtonSelect(viewBinding.btn8HeaterStop);
                break;
            default: // 加热模式 自动
                setButtonSelect(viewBinding.btn8HeaterAutomatic);
        }

        switch (heaterSetting2) {
            case 1: // 加热模式 手动
                setButtonSelect(viewBinding.btn8HeaterManual2);
                break;
            case 2: // 加热模式 停止
                setButtonSelect(viewBinding.btn8HeaterStop2);
                break;
            default: // 加热模式 自动
                setButtonSelect(viewBinding.btn8HeaterAutomatic2);
        }


        switch (dehumidificationSetting) {
            case 1: // 除湿模式 手动
                setButtonSelect(viewBinding.btn9DehumidificationManual);
                break;
            case 2: // 除湿模式 停止
                setButtonSelect(viewBinding.btn9DehumidificationStop);
                break;
            default: // 除湿模式 自动
                setButtonSelect(viewBinding.btn9DehumidificationAutomatic);
        }

        switch (led1) {
            case 1:
                setButtonSelect(viewBinding.btn6Led1Red);
                break;
            case 2:
                setButtonSelect(viewBinding.btn6Led1Green);
                break;
            case 3:
                setButtonSelect(viewBinding.btn6Led1Yellow);
                break;
        }

        switch (led2) {
            case 1:
                setButtonSelect(viewBinding.btn10Led2Red);
                break;
            case 2:
                setButtonSelect(viewBinding.btn10Led2Green);
                break;
            case 3:
                setButtonSelect(viewBinding.btn10Led2Yellow);
                break;
        }

        /**
         * 01：手动常亮；02：手动长灭 11：自动常亮  12：自动常灭
         */
        if (lighting == 1) {
            setButtonSelect(viewBinding.btn11LightingOpen);
        } else if (lighting == 11 || lighting == 12) {
            setButtonSelect(viewBinding.btn11LightingAuto);
        } else {
            setButtonSelect(viewBinding.btn11LightingClose);
        }

       /* if (!MyApplication.getFirmwareVersionNew()) {
            return;
        }*/

        viewBinding.groupLock.setVisibility(View.VISIBLE);
        viewBinding.autoFitLayout7ZeroLoadcell1.setVisibility(View.VISIBLE);
        viewBinding.autoFitLayout7ZeroLoadcell2.setVisibility(View.VISIBLE);
        viewBinding.cl14WeightCalibrationNum.setVisibility(View.VISIBLE);
        viewBinding.tv14WeightCalibrationNum.setText(MyApplication.adminParameterBean.getWeightCalibrationNum() + MyApplication.weighingUnit);
        /**
         * 01：解锁；02：上锁
         */
        if (lock1 == 1) {
            setButtonSelect(viewBinding.btn13LockUnlock);
        } else {
            setButtonSelect(viewBinding.btn13LockLock);
        }
    }

    /**
     * 判断当前颜色
     * 红：100  绿：10  黄：110
     *
     * @param led 1 led1  2 led2
     * @return 1-3 分别为 红、绿、黄
     */
    private int nowColor(int led) {
        int nowColor = 2;
        int ledRGB = 10; // ledRGB数值
        if (led == 1) {
            ledRGB = PortControlUtil.getInstance().getPortStatus().getLed1RGB();
        } else if (led == 2) {
            ledRGB = PortControlUtil.getInstance().getPortStatus().getLed2RGB();
        }
        if (ledRGB == 100) {
            nowColor = 1;
        } else if (ledRGB == 10) {
            nowColor = 2;
        } else if (ledRGB == 110) {
            nowColor = 3;
        }
        return nowColor;
    }

    /**
     * 设置按钮为选中状态
     *
     * @param button 要设置的按钮
     */
    private void setButtonSelect(Button button) {
//        button.setBackground(getDrawable(R.drawable.bg_button_15_blue));
//        button.setTextColor(getResources().getColor(R.color.white));
        button.setBackgroundResource(R.mipmap.btn_admin_control_select);
    }

    /**
     * 设置按钮为未选中状态
     *
     * @param button 要设置的按钮
     */
    private void setButtonUnSelect(Button button) {
//        button.setBackground(getDrawable(R.drawable.bg_button_15_white));
//        button.setTextColor(getResources().getColor(R.color.blue_5BB8E8));
        button.setBackgroundResource(R.mipmap.btn_admin_control_unselect);
    }

    @Override
    protected void initListener() {
        viewBinding.btnBack.setOnClickListener(this);
        viewBinding.btn1InletOpen.setOnClickListener(this);
        viewBinding.btn1InletClose.setOnClickListener(this);
        viewBinding.btn1InletStop.setOnClickListener(this);
//        viewBinding.btn2WeighingOpen.setOnClickListener(this);
//        viewBinding.btn2WeighingClose.setOnClickListener(this);
//        viewBinding.btn2WeighingStop.setOnClickListener(this);
        viewBinding.btn3StirForward.setOnClickListener(this);
        viewBinding.btn3StirReverses.setOnClickListener(this);
        viewBinding.btn3StirStop.setOnClickListener(this);
//        viewBinding.btn4OutputForward.setOnClickListener(this);
//        viewBinding.btn4OutputReverses.setOnClickListener(this);
//        viewBinding.btn4OutputStop.setOnClickListener(this);
        viewBinding.btn5FanManual1.setOnClickListener(this);
        viewBinding.btn5FanManual2.setOnClickListener(this);
        viewBinding.btn5FanManual3.setOnClickListener(this);
        viewBinding.btn5FanStop.setOnClickListener(this);
        viewBinding.btn5FanAutomatic.setOnClickListener(this);
        viewBinding.btn6Led1Red.setOnClickListener(this);
        viewBinding.btn6Led1Green.setOnClickListener(this);
        viewBinding.btn6Led1Yellow.setOnClickListener(this);
        viewBinding.btn6Led1Off.setOnClickListener(this);
        viewBinding.btn7NetWeight.setOnClickListener(this);
        viewBinding.btn7Zero.setOnClickListener(this);
        viewBinding.btn7Zero485.setOnClickListener(this);
        viewBinding.btn7ZeroLoadcell1.setOnClickListener(this);
        viewBinding.btn7ZeroLoadcell2.setOnClickListener(this);
        viewBinding.btn8HeaterManual.setOnClickListener(this);
        viewBinding.btn8HeaterAutomatic.setOnClickListener(this);
        viewBinding.btn8HeaterStop.setOnClickListener(this);
        viewBinding.btn8HeaterManual2.setOnClickListener(this);
        viewBinding.btn8HeaterAutomatic2.setOnClickListener(this);
        viewBinding.btn8HeaterStop2.setOnClickListener(this);
        viewBinding.btn9DehumidificationManual.setOnClickListener(this);
        viewBinding.btn9DehumidificationAutomatic.setOnClickListener(this);
        viewBinding.btn9DehumidificationStop.setOnClickListener(this);
        viewBinding.btn10Led2Red.setOnClickListener(this);
        viewBinding.btn10Led2Green.setOnClickListener(this);
        viewBinding.btn10Led2Yellow.setOnClickListener(this);
        viewBinding.btn10Led2Off.setOnClickListener(this);
        viewBinding.btn11LightingOpen.setOnClickListener(this);
        viewBinding.btn11LightingClose.setOnClickListener(this);
        viewBinding.btn11LightingAuto.setOnClickListener(this);
        viewBinding.btn13LockUnlock.setOnClickListener(this);
        viewBinding.btn13LockLock.setOnClickListener(this);
        viewBinding.btn14WeightCalibrationLoadcell1.setOnClickListener(this);
        viewBinding.btn14WeightCalibrationLoadcell2.setOnClickListener(this);
        viewBinding.cl14WeightCalibrationNum.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn1_inlet_open:
                btn1InletControl(2, (Button) view);
                break;
            case R.id.btn1_inlet_close:
                btn1InletControl(5, (Button) view);
                break;
            case R.id.btn1_inlet_stop:
                btn1InletControl(8, (Button) view);
                break;
            case R.id.btn3_stir_forward:
                btn3StirControl(1, (Button) view);
                break;
            case R.id.btn3_stir_reverses:
                btn3StirControl(2, (Button) view);
                break;
            case R.id.btn3_stir_stop:
                btn3StirControl(3, (Button) view);
                break;
            case R.id.btn5_fan_manual1:
                btn5FanControl(1, 1, (Button) view);
                break;
            case R.id.btn5_fan_manual2:
                btn5FanControl(1, 2, (Button) view);
                break;
            case R.id.btn5_fan_manual3:
                btn5FanControl(1, 3, (Button) view);
                break;
            case R.id.btn5_fan_stop:
                btn5FanControl(2, -1, (Button) view);
                break;
            case R.id.btn5_fan_automatic:
                btn5FanControl(3, -1, (Button) view);
                break;
            case R.id.btn6_led1_red:
                btn6LightControl(1, (Button) view);
                break;
            case R.id.btn6_led1_green:
                btn6LightControl(2, (Button) view);
                break;
            case R.id.btn6_led1_yellow:
                btn6LightControl(3, (Button) view);
                break;
            case R.id.btn6_led1_off:
                btn6LightControl(4, (Button) view);
                break;
            case R.id.btn7_net_weight:
                btn7ZeroSetting(2, (Button) view);
                break;
            case R.id.btn7_zero:
                btn7ZeroSetting(1, (Button) view);
                break;
            case R.id.btn7_zero_485:
                btn7ZeroSetting(3, (Button) view);
                break;
            case R.id.btn7_zero_loadcell1:
                btn7ZeroSetting(4, (Button) view);
                break;
            case R.id.btn7_zero_loadcell2:
                btn7ZeroSetting(5, (Button) view);
                break;
            case R.id.btn8_heater_manual:
                btn8HeaterSetting(1, (Button) view);
                break;
            case R.id.btn8_heater_stop:
                btn8HeaterSetting(2, (Button) view);
                break;
            case R.id.btn8_heater_automatic:
                btn8HeaterSetting(3, (Button) view);
                break;
            case R.id.btn8_heater_manual2:
                btn8Heater2Setting(1, (Button) view);
                break;
            case R.id.btn8_heater_stop2:
                btn8Heater2Setting(2, (Button) view);
                break;
            case R.id.btn8_heater_automatic2:
                btn8Heater2Setting(3, (Button) view);
                break;
            case R.id.btn9_dehumidification_manual:
                btn9DehumidificationSetting(1, (Button) view);
                break;
            case R.id.btn9_dehumidification_stop:
                btn9DehumidificationSetting(2, (Button) view);
                break;
            case R.id.btn9_dehumidification_automatic:
                btn9DehumidificationSetting(3, (Button) view);
                break;
            case R.id.btn10_led2_red:
                btn10Led2Control(1, (Button) view);
                break;
            case R.id.btn10_led2_green:
                btn10Led2Control(2, (Button) view);
                break;
            case R.id.btn10_led2_yellow:
                btn10Led2Control(3, (Button) view);
                break;
            case R.id.btn10_led2_off:
                btn10Led2Control(4, (Button) view);
                break;
            case R.id.btn11_lighting_open:
                btn11LightingControl(1, (Button) view);
                break;
            case R.id.btn11_lighting_close:
                btn11LightingControl(2, (Button) view);
                break;
            case R.id.btn11_lighting_auto:
                btn11LightingControl(3, (Button) view);
                break;
            case R.id.btn13_lock_unlock:
                btn13Lock1Control(1, (Button) view);
                break;
            case R.id.btn13_lock_lock:
                btn13Lock1Control(2, (Button) view);
                break;
            case R.id.btn14_weight_calibration_loadcell1:
                btn14WeightCalibration(1, (Button) view);
                break;
            case R.id.btn14_weight_calibration_loadcell2:
                btn14WeightCalibration(2, (Button) view);
                break;
            case R.id.cl14_weight_calibration_num: // 重量标定
                type = 34;
                showDialog(viewBinding.tv14WeightCalibrationNum.getText().toString().split(MyApplication.weighingUnit)[0]);
                break;

        }

    }

    /**
     * 显示选择器
     *
     * @param nowNum
     */
    private void showDialog(String nowNum) {
        DialogChooseTemperature dialogChoose = new DialogChooseTemperature(mActivity, nowNum, type);
        dialogChoose.show(getSupportFragmentManager(), "choose_temperature");
    }

    /**
     * 投入口电机按钮控制
     *
     * @param status      2开门 5关门 8停止
     * @param inletButton <li>{@link R.id#btn1_inlet_open}
     *                    <li>{@link  R.id#btn1_inlet_close}
     *                    <li>{@link  R.id#btn1_inlet_stop}
     */
    private void btn1InletControl(int status, Button inletButton) {
        if (MyApplication.adminParameterBean.getInletMode().equals(Constant.MANUAL)){
            return;
        }
//        if (status == inletStatus) {
//            return;
//        }
        if (doorHandler == null) {
            doorHandler = new Handler();
        }
        //每隔300ms获取投入口的状态
        doorRunnable = new Runnable() {
            @Override
            public void run() {
//                    if (status == PortControlUtil.getInstance().getPortStatus().getInletStatus()) {
//                        return;
//                    }
                if (status == 2) {
                    if (PortControlUtil.getInstance().getPortStatus().getOpenDoorBtn() == 1) {
//                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    LogUtils.e("发送停止命令");
//                                    PortControlUtil.getInstance().sendCommands(PortConstants.STIR_STOP);
//                            PortControlUtil.getInstance().sendCommands(PortConstants.FAN1_STOP); //风扇停止
//                            PortControlUtil.getInstance().sendCommands(PortConstants.DEHUMIDIFICATION_STOP);
//                                }
//                            },100);
                        PortControlUtil.getInstance().sendCommands(PortConstants.STIR_STOP);
                        PortControlUtil.getInstance().sendCommands(PortConstants.FAN1_STOP); //风扇停止
                        PortControlUtil.getInstance().sendCommands(PortConstants.DEHUMIDIFICATION_STOP); //烘干机停止
                        return;
                    }
                } else if (status == 5) {
                    if (PortControlUtil.getInstance().getPortStatus().getInletStatus() == 5 &&
                            PortControlUtil.getInstance().getPortStatus().getObserveDoorStatus() != 0) {
                        if (PortControlUtil.getInstance().getPortStatus().getOutletStatus() == 1) {

                            PortControlUtil.getInstance().sendCommands(PortConstants.FAN1_AUTOMATIC); //风扇启动
                            PortControlUtil.getInstance().sendCommands(PortControlUtil.getInstance().getHeater1AutomaticCommands()); //加热1自动
                            PortControlUtil.getInstance().sendCommands(PortControlUtil.getInstance().getHeater2AutomaticCommands()); //加热2自动
                            PortControlUtil.getInstance().sendCommands(PortControlUtil.getInstance().getDehumidificationAutomaticCommands()); //烘干机自动
                        }
                        // 打开搅拌电机命令
                        PortControlUtil.getInstance().sendCommands(PortConstants.STIR_FORWARD);
                        PortControlUtil.getInstance().sendCommands(PortConstants.INLET_LOCK);
                        return;
                    }
                }
                doorHandler.postDelayed(this, 300);
            }
        };

        doorHandler.removeCallbacks(doorRunnable);
        doorHandler.removeCallbacksAndMessages(null);

        this.inletStatus = status;
        setButtonUnSelect(viewBinding.btn1InletOpen);
        setButtonUnSelect(viewBinding.btn1InletClose);
        setButtonUnSelect(viewBinding.btn1InletStop);
        setButtonSelect(inletButton);
        if (status == 2) { // 拼接投入口电机开门指令
//            sendHex = PortConstants.INLET_OPEN;
            sendHex = PortConstants.INLET_UNLOCK;
            if (MyApplication.getFirmwareVersionNew()) { // 新pcb板
                sendHex = PortConstants.INLET_OPEN;
            }
            PortControlUtil.getInstance().sendCommands(PortConstants.LOCK1_CONTROL_UNLOCK);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    PortControlUtil.getInstance().sendCommands(PortConstants.LOCK1_CONTROL_LOCK);
                }
            },Constant.second);
            doorHandler.postDelayed(doorRunnable, 300);
        } else if (status == 5) {// 拼接投入口电机关门指令
            sendHex = PortConstants.INLET_CLOSE;
            doorHandler.postDelayed(doorRunnable, 300);
        } else if (status == 8) {// 拼接投入口电机停止指令
            sendHex = PortConstants.INLET_STOP;
        }
        PortControlUtil.getInstance().sendCommands(sendHex);
    }

    /**
     * 称重板电机按钮控制
     *
     * @param status         1开门 0关门 2停止
     * @param weighingButton <li>{@link R.id#btn2_weighing_open}
     *                       <li>{@link  R.id#btn2_weighing_close}
     *                       <li>{@link  R.id#btn2_weighing_stop}
     */
   /* private void btn2WeighingControl(int status, Button weighingButton) {
        if (status == weighingStatus) {
            return;
        }
        this.weighingStatus = status;

        setButtonUnSelect(viewBinding.btn2WeighingOpen);
        setButtonUnSelect(viewBinding.btn2WeighingClose);
        setButtonUnSelect(viewBinding.btn2WeighingStop);
        setButtonSelect(weighingButton);
//
        if (status == 1) { // 拼接称重板电机开门指令
        } else if (status == 0) {// 拼接称重板电机关门指令

        } else if (status == 2) {// 拼接称重板电机停止指令
        }

        // TODO: 2022/11/7 串口发送相应命令

    }*/

    /**
     * 搅拌电机按钮控制
     *
     * @param status     1正转 2反转 3停止
     * @param stirButton <li>{@link R.id#btn3_stir_forward}
     *                   <li>{@link  R.id#btn3_stir_reverses}
     *                   <li>{@link  R.id#btn3_stir_stop}
     */
    private void btn3StirControl(int status, Button stirButton) {
        if (status == stirStatus) {
            return;
        }
        this.stirStatus = status;
        setButtonUnSelect(viewBinding.btn3StirForward);
        setButtonUnSelect(viewBinding.btn3StirReverses);
        setButtonUnSelect(viewBinding.btn3StirStop);
        setButtonSelect(stirButton);

        if (status == 1) { // 拼接搅拌电机正转指令
            sendHex = PortConstants.STIR_FORWARD;
        } else if (status == 2) {// 拼接搅拌电机反转指令
            sendHex = PortConstants.STIR_REVERSES;
        } else if (status == 3) {// 拼接搅拌电机停止指令
            sendHex = PortConstants.STIR_STOP;
        }
        PortControlUtil.getInstance().sendCommands(sendHex);
    }

    /**
     * 排出口电机按钮控制
     *
     * @param status       1正转 0反转 2停止
     * @param outputButton <li>{@link R.id#btn4_output_forward}
     *                     <li>{@link R.id#btn4_output_reverses}
     *                     <li>{@link R.id#btn4_output_stop}
     */
   /* private void btn4OutputControl(int status, Button outputButton) {
        if (status == outputStatus) {
            return;
        }
        this.outputStatus = status;
        setButtonUnSelect(viewBinding.btn4OutputForward);
        setButtonUnSelect(viewBinding.btn4OutputReverses);
        setButtonUnSelect(viewBinding.btn4OutputStop);
        setButtonSelect(outputButton);

        if (status == 1) { // 拼接搅拌电机正转指令
//            viewBinding.btn1InletOpen.setBackground(getDrawable(R.drawable.bg_button_15_blue));
//            viewBinding.btn1InletOpen.setTextColor(getResources().getColor(R.color.white));
        } else if (status == 0) {// 拼接搅拌电机反转指令

        } else if (status == 2) {//  拼接搅拌电机停止指令
        }
    }
*/

    /**
     * 风扇电机按钮控制
     *
     * @param status    1手动 2停止 3自动
     * @param gear      1-3档
     * @param fanButton <li>{@link R.id#btn5_fan_manual1}
     *                  <li>{@link R.id#btn5_fan_manual2}
     *                  <li>{@link R.id#btn5_fan_manual3}
     *                  <li>{@link R.id#btn5_fan_stop}
     *                  <li>{@link R.id#btn5_fan_automatic}
     */
    private void btn5FanControl(int status, int gear, Button fanButton) {
        if (status == fanStatus && myGear == gear) {
            return;
        }
        this.fanStatus = status;
        this.myGear = gear;
        setButtonUnSelect(viewBinding.btn5FanManual1);
        setButtonUnSelect(viewBinding.btn5FanManual2);
        setButtonUnSelect(viewBinding.btn5FanManual3);
        setButtonUnSelect(viewBinding.btn5FanAutomatic);
        setButtonUnSelect(viewBinding.btn5FanStop);
        setButtonSelect(fanButton);

        if (status == 1) { // 风扇手动指令
            if (gear == 1) { // 手动1档
                sendHex = PortConstants.FAN1_MANUAL_GEAR_1;
            } else if (gear == 2) { // 手动2档
                sendHex = PortConstants.FAN1_MANUAL_GEAR_2;
            } else if (gear == 3) { // 手动3档
                sendHex = PortConstants.FAN1_MANUAL_GEAR_3;
            }
            PortControlUtil.getInstance().sendCommands(sendHex);
//            baseHandler.postDelayed(() -> {
//                sendHex = PortConstants.FAN2_MANUAL;
//                PortControlUtil.getInstance().sendCommands(sendHex);
//            },300);
        } else if (status == 2) {// 风扇停止指令
            sendHex = PortConstants.FAN1_STOP;
            PortControlUtil.getInstance().sendCommands(sendHex);
//            baseHandler.postDelayed(() -> {
//                sendHex = PortConstants.FAN2_STOP;
//                PortControlUtil.getInstance().sendCommands(sendHex);
//            },300);
        } else { // 风扇自动
            sendHex = PortConstants.FAN1_AUTOMATIC;
            PortControlUtil.getInstance().sendCommands(sendHex);
//            baseHandler.postDelayed(() -> {
//                sendHex = PortConstants.FAN2_AUTOMATIC;
//                PortControlUtil.getInstance().sendCommands(sendHex);
//            },300);
        }
    }

    /**
     * led1 颜色按钮控制
     *
     * @param status     1-3 分别为 红、绿、黄
     * @param led1Button <li>{@link R.id#btn6_led1_red}
     *                   <li>{@link R.id#btn6_led1_green}
     *                   <li>{@link R.id#btn6_led1_yellow}
     */
    private void btn6LightControl(int status, Button led1Button) {
        if (status == led1) {
            return;
        }
        this.led1 = status;
        setButtonUnSelect(viewBinding.btn6Led1Red);
        setButtonUnSelect(viewBinding.btn6Led1Green);
        setButtonUnSelect(viewBinding.btn6Led1Yellow);
        setButtonUnSelect(viewBinding.btn6Led1Off);
        setButtonSelect(led1Button);
        String s = ""; // 灯控命令未交CRC之前
        if (status == 1) { // 拼接led1灯控指令 红
            sendHex = PortConstants.LED1_ON_RED;
        } else if (status == 2) {// 拼接led1灯控指令 绿
            sendHex = PortConstants.LED1_ON_GREEN;
        } else if (status == 3) {// 拼接led1灯控指令 黄
            sendHex = PortConstants.LED1_ON_YELLOW;
        } else if (status == 4) {// 拼接led1灯控指令 关闭
            sendHex = PortConstants.LED1_OFF;
        } /*else if (status == 4) {// 拼接灯控指令
            s = PortConstants.LED_HEAD + "000" + PortControlUtil.getInstance().getPortStatus().getLed1() + PortConstants.COLOR_YELLOW;
        } */
//        sendHex = s + HexadecimalDataUtil.getCRC(s);

        // TODO: 2022/11/7 串口发送相应命令
        PortControlUtil.getInstance().sendCommands(sendHex);
    }

    /**
     * 置零设置
     *
     * @param status     2 去皮 1 置零  3 485置零   4 loadcell1置零  5 loadcell2置零
     * @param zeroButton <li>{@link R.id#btn7_net_weight}
     *                   <li>{@link R.id#btn7_zero}
     */
    private void btn7ZeroSetting(int status, Button zeroButton) {
//        if (status == zeroSetting) {
//            return;
//        }
//        this.zeroSetting = status;
        setButtonUnSelect(viewBinding.btn7NetWeight);
        setButtonUnSelect(viewBinding.btn7Zero);
        setButtonUnSelect(viewBinding.btn7Zero485);
        setButtonUnSelect(viewBinding.btn7ZeroLoadcell1);
        setButtonUnSelect(viewBinding.btn7ZeroLoadcell2);
        setButtonSelect(zeroButton);
        if (status == 2) { // 拼接去皮指令
            sendHex = PortConstants.WEIGHING_SETTING_NETWEIGHT;
//        } else if (status == 1) {// 拼接置零指令
//            sendHex = PortConstants.WEIGHING_SETTING_ZERO;
//        }
        } else if (status == 3) {
            sendHex = PortConstants.WEIGHING_SETTING_ZERO_485;
        } else if (status == 4) {
            sendHex = PortConstants.LOAD_CELL1_ZERO;
        } else if (status == 5) {
            sendHex = PortConstants.LOAD_CELL2_ZERO;
        }
        PortControlUtil.getInstance().sendCommands(sendHex);
    }

    /**
     * 加热1设置
     *
     * @param status       1 手动 2停止 3自动
     * @param heaterButton <li>{@link R.id#btn8_heater_manual}
     *                     <li>{@link R.id#btn8_heater_automatic}
     *                     <li>{@link R.id#btn8_heater_stop}
     */
    private void btn8HeaterSetting(int status, Button heaterButton) {
        if (status == heaterSetting1) {
            return;
        }
        this.heaterSetting1 = status;
        setButtonUnSelect(viewBinding.btn8HeaterManual);
        setButtonUnSelect(viewBinding.btn8HeaterAutomatic);
        setButtonUnSelect(viewBinding.btn8HeaterStop);
        setButtonSelect(heaterButton);
        if (status == 1) { // 拼接加热手动模式指令
            sendHex = PortConstants.HEATER1_MANUAL;
        } else if (status == 2) {// 拼接加热停止模式指令
            sendHex = PortConstants.HEATER1_STOP;
        } else if (status == 3) {// 拼接加热自动模式指令
            sendHex = PortControlUtil.getHeater1AutomaticCommands();
        }
        PortControlUtil.getInstance().sendCommands(sendHex);
    }

    /**
     * 加热2设置
     *
     * @param status        1 手动 2停止 3自动
     * @param heater2Button <li>{@link R.id#btn8_heater_manual2}
     *                      <li>{@link R.id#btn8_heater_automatic2}
     *                      <li>{@link R.id#btn8_heater_stop2}
     */
    private void btn8Heater2Setting(int status, Button heater2Button) {
        if (status == heaterSetting2) {
            return;
        }
        this.heaterSetting2 = status;
        setButtonUnSelect(viewBinding.btn8HeaterManual2);
        setButtonUnSelect(viewBinding.btn8HeaterAutomatic2);
        setButtonUnSelect(viewBinding.btn8HeaterStop2);
        setButtonSelect(heater2Button);
        if (status == 1) { // 加热手动模式指令
            sendHex = PortConstants.HEATER2_MANUAL;
        } else if (status == 2) {// 加热停止模式指令
            sendHex = PortConstants.HEATER2_STOP;
        } else if (status == 3) {// 加热自动模式指令
            sendHex = PortControlUtil.getHeater2AutomaticCommands();
        }
        PortControlUtil.getInstance().sendCommands(sendHex);
    }


    /**
     * 除湿设置
     *
     * @param status                 1 手动  3自动 2停止
     * @param dehumidificationButton <li>{@link R.id#btn9_dehumidification_manual}
     *                               <li>{@link R.id#btn9_dehumidification_automatic}
     *                               <li>{@link R.id#btn9_dehumidification_stop}
     */
    private void btn9DehumidificationSetting(int status, Button dehumidificationButton) {
        if (status == dehumidificationSetting) {
            return;
        }
        this.dehumidificationSetting = status;
        setButtonUnSelect(viewBinding.btn9DehumidificationManual);
        setButtonUnSelect(viewBinding.btn9DehumidificationAutomatic);
        setButtonUnSelect(viewBinding.btn9DehumidificationStop);
        setButtonSelect(dehumidificationButton);
        if (status == 1) { //除湿手动模式指令
            sendHex = PortConstants.DEHUMIDIFICATION_MANUAL;
        } else if (status == 2) {// 除湿停止模式指令
            sendHex = PortConstants.DEHUMIDIFICATION_STOP;
        } else if (status == 3) {// 除湿自动模式指令
            sendHex = PortControlUtil.getDehumidificationAutomaticCommands();
        }

        PortControlUtil.getInstance().sendCommands(sendHex);
    }

    /**
     * led2 颜色按钮控制
     *
     * @param status     1-3 分别为 红、绿、黄
     * @param led2Button <li>{@link R.id#btn10_led2_red}
     *                   <li>{@link R.id#btn10_led2_green}
     *                   <li>{@link R.id#btn10_led2_yellow}
     */
    private void btn10Led2Control(int status, Button led2Button) {
        if (status == led2) {
            return;
        }
        this.led2 = status;
        setButtonUnSelect(viewBinding.btn10Led2Red);
        setButtonUnSelect(viewBinding.btn10Led2Green);
        setButtonUnSelect(viewBinding.btn10Led2Yellow);
        setButtonUnSelect(viewBinding.btn10Led2Off);
        setButtonSelect(led2Button);
        if (status == 1) { // led2灯控指令 红
            sendHex = PortConstants.LED2_ON_RED;
        } else if (status == 2) {// led2灯控指令 绿
            sendHex = PortConstants.LED2_ON_GREEN;
        } else if (status == 3) {// led2灯控指令 黄
            sendHex = PortConstants.LED2_ON_YELLOW;
        } else if (status == 4) {// led2灯控指令 灭 关闭
            sendHex = PortConstants.LED2_OFF;
        }
        PortControlUtil.getInstance().sendCommands(sendHex);
    }

    /**
     * lighting 照明 控制开关
     *
     * @param status         lighting 1开 2 关  3 自动
     * @param lightingButton <li>{@link R.id#btn11_lighting_open}
     *                       <li>{@link R.id#btn11_lighting_close}
     *                       <li>{@link R.id#btn11_lighting_auto}
     */
    private void btn11LightingControl(int status, Button lightingButton) {
        if (status == lighting) {
            return;
        }
        this.lighting = status;
        setButtonUnSelect(viewBinding.btn11LightingOpen);
        setButtonUnSelect(viewBinding.btn11LightingClose);
        setButtonUnSelect(viewBinding.btn11LightingAuto);
        setButtonSelect(lightingButton);
        if (status == 1) { // 照明 开
            sendHex = PortConstants.LIGHTING_ON;
        } else if (status == 2) {// 照明 关
            sendHex = PortConstants.LIGHTING_OFF;
        } else if (status == 3) {// 照明 自动
            sendHex = PortConstants.LIGHTING_AUTO;
        }
        PortControlUtil.getInstance().sendCommands(sendHex);
    }

    /**
     * lock1 控制开关
     *
     * @param status     01：解锁；02：上锁
     * @param lockButton <li>{@link R.id#btn13_lock_unlock}
     *                   <li>{@link R.id#btn13_lock_lock}
     */
    private void btn13Lock1Control(int status, Button lockButton) {
        if (status == lock1) {
            return;
        }
        this.lock1 = status;
        setButtonUnSelect(viewBinding.btn13LockUnlock);
        setButtonUnSelect(viewBinding.btn13LockLock);
        setButtonSelect(lockButton);
        if (status == 1) { // 解锁
            sendHex = PortConstants.LOCK1_CONTROL_UNLOCK;
        } else if (status == 2) {// 上锁
            sendHex = PortConstants.LOCK1_CONTROL_LOCK;
        }
        PortControlUtil.getInstance().sendCommands(sendHex);
    }

    /**
     * 重量标定
     *
     * @param status                  01：loadcell1  02：loadcell2
     * @param weightCalibrationButton <li>{@link R.id#btn14_weight_calibration_loadcell1}
     *                                <li>{@link R.id#btn14_weight_calibration_loadcell1}
     */
    private void btn14WeightCalibration(int status, Button weightCalibrationButton) {
        setButtonUnSelect(viewBinding.btn14WeightCalibrationLoadcell1);
        setButtonUnSelect(viewBinding.btn14WeightCalibrationLoadcell1);
        setButtonSelect(weightCalibrationButton);
//        String weight = viewBinding.tv14WeightCalibrationNum.getText().toString().split("KG")[0];
        String weight = viewBinding.tv14WeightCalibrationNum.getText().toString().split(MyApplication.weighingUnit)[0];
        if (status == 1) { // loadcell1 重量标定
            sendHex = PortControlUtil.weightCalibrationLoadCell1(weight);
        } else if (status == 2) {// loadcell2 重量标定
            sendHex = PortControlUtil.weightCalibrationLoadCell2(weight);
        }
        PortControlUtil.getInstance().sendCommands(sendHex);
    }


    /**
     * @param
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getWeight(PortStatus portStatus) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewBinding.tvNetweight.setText(portStatus.getChooseUseWeighing() + "");
                viewBinding.tvGrossweight.setText(portStatus.getGrossWeight() + "");
            }
        });
    }

    @Override
    public void returnFragmentData(String message) {
        if (type == 34) {
            // 重量标定
//            viewBinding.tv14WeightCalibrationNum.setText(message + "KG");
            viewBinding.tv14WeightCalibrationNum.setText(message + MyApplication.weighingUnit);
            MyApplication.adminParameterBean.setWeightCalibrationNum(message);
        }
        SharedPreFerUtil.saveObj(Constant.ADMIN_PARAMETER_FILENAME, Constant.ADMIN_PARAMETER_KEY,
                JSON.toJSONString(MyApplication.adminParameterBean));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
