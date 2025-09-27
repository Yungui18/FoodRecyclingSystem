package com.hyeprion.foodrecyclingsystem.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.hyeprion.foodrecyclingsystem.R;
import com.hyeprion.foodrecyclingsystem.base.BaseActivity;
import com.hyeprion.foodrecyclingsystem.base.MyApplication;
import com.hyeprion.foodrecyclingsystem.bean.AdminParameterBean;
import com.hyeprion.foodrecyclingsystem.bean.BannerDataBean;
import com.hyeprion.foodrecyclingsystem.bean.CardBindingResponse;
import com.hyeprion.foodrecyclingsystem.bean.HumidityTrouble;
import com.hyeprion.foodrecyclingsystem.bean.LoginResponse;
import com.hyeprion.foodrecyclingsystem.bean.PutIntoGarbageResponse;
import com.hyeprion.foodrecyclingsystem.bean.ResetPowerSaving;
import com.hyeprion.foodrecyclingsystem.bean.StartUpStopEvent;
import com.hyeprion.foodrecyclingsystem.databinding.ActivityLogin2Binding;
import com.hyeprion.foodrecyclingsystem.dialog.DialogManage;
import com.hyeprion.foodrecyclingsystem.util.ActivityUtil;
import com.hyeprion.foodrecyclingsystem.util.Constant;
import com.hyeprion.foodrecyclingsystem.util.DecimalFormatUtil;
import com.hyeprion.foodrecyclingsystem.util.GreenDaoUtil;
import com.hyeprion.foodrecyclingsystem.util.HTTPServerUtil;
import com.hyeprion.foodrecyclingsystem.util.LoginCountTimerTask;
import com.hyeprion.foodrecyclingsystem.util.ScanKeyManager;
import com.hyeprion.foodrecyclingsystem.util.SharedPreFerUtil;
import com.hyeprion.foodrecyclingsystem.util.countdowntimer.CountTimer;
import com.hyeprion.foodrecyclingsystem.util.countdowntimer.State;
import com.hyeprion.foodrecyclingsystem.util.port.PortConstants;
import com.hyeprion.foodrecyclingsystem.util.port.PortControlUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Apartment-Must/Shop—Option
 * 用户或管理员登录页面
 */
public class LoginActivity extends BaseActivity<ActivityLogin2Binding> {
    /**
     * 0 普通用户
     * 1 管理员
     * 2 卡片绑定
     * 3 投入口手动，有登录，绑定使用者
     */
    private int mode = 0;
    private long[] mHits = new long[5];// 记录连续点击
    private Handler handler;
    private boolean clickInputFlag = true;
    private int type = -1; // -1 无  1 card  2 id  3 pw 记录当前焦点所在位置
    private CountTimer countTimer; // 当投入口为手动时，每2S获取一次门状态，关门后获取当前重量-开门获取重量 = 本次投入重量
    private float openDoorWeight = 0; // 当投入口为手动时，开门时的重量
    private int inletStatus = 5; // 投入口手动状态： 5关门  7未关门
//    private CountTimer openDoorWeight; // 当投入口为手动时，关门后获取当前重量

    private Handler powerSavingHandler; // 48 H 后无操作进入节电模式
    private Runnable powerSavingRunnable;

    private Handler screensaverHandler; // 屏保
    private Runnable screensaverRunnable;

    private int currentRunMode = 1; // 当前运行模式  1 普通模式 normal  2节电模式 power saving
    private boolean doorIsOpen = false; // 手动门为开的状态
    private boolean isLogging = false; // 是否正在登录中
    private boolean isFront = false;
    private boolean isFirst = true; // 首次进入程序
    public static boolean outletDialogShow = false; // 手动模式下，排料口打开，投入口打卡后弹窗提示 true：弹窗 false 无弹窗
    private Dialog showOutletOpenDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    protected void initView() {
        scanKeyManager = new ScanKeyManager(new ScanKeyManager.OnScanValueListener() {
            @Override
            public void onScanValue(String value) {
                LogUtils.e(value);
                if (!isFront) {
                    return;
                }
                if (mode == 0) { // 普通用户刷卡登录
                    if (MyApplication.adminParameterBean.getDeviceMode() !=
                            Constant.DEVICE_MODE_AUTO && mode != 1) {
                        ToastUtils.showShort(R.string.non_automatic_only_admin_can_login);
                        return;
                    }
                    if (PortControlUtil.troubleTypeBean.getIsTrouble() || Float.parseFloat(MyApplication.adminParameterBean.getInletLimitedTotalAccumulation()) >
                            Float.parseFloat(MyApplication.adminParameterBean.getInletLimitedTotal())) {
                        String showText = getString(R.string.error_toast);
                   /* if (PortControlUtil.troubleTypeBean.getTroubleStir() == Constant.TRUE) {
                        showText = getString(R.string.trouble_info_stir);
                    } else if (PortControlUtil.troubleTypeBean.getTroubleOutlet() == Constant.TRUE) {
                        showText = getString(R.string.trouble_info_outlet);
                    } else if (PortControlUtil.troubleTypeBean.getTroubleUpCheck() == Constant.TRUE) {
                        showText = getString(R.string.trouble_info_observe);
                    } else if (PortControlUtil.troubleTypeBean.getTroubleInlet() == Constant.TRUE) {
                        showText = getString(R.string.trouble_info_inlet);
                    } else if (PortControlUtil.troubleTypeBean.getTroubleHeaterMax() == Constant.TRUE ||
                            PortControlUtil.troubleTypeBean.getTroubleHeaterMin() == Constant.TRUE) {
                        showText = getString(R.string.trouble_info_heating);
                    } else if (PortControlUtil.troubleTypeBean.getTroubleWeigh() == Constant.TRUE) {
                        showText = getString(R.string.trouble_info_weigh);
                    } else if (PortControlUtil.troubleTypeBean.getTroubleHumidity() == Constant.TRUE) {
                        showText = getString(R.string.trouble_info_humidity);
                    } else if (PortControlUtil.troubleTypeBean.getTroublePA() == Constant.TRUE) {
                        showText = getString(R.string.trouble_info_wind_pressure);
                    }*/
                        if (PortControlUtil.troubleTypeBean.getTroubleOutlet() == Constant.TRUE) {
                            showText = getString(R.string.please_close_outlet);
                        }
                        DialogManage.getDialog2SDismiss(LoginActivity.this, showText).show();
                        return;
                    }

                   /* if (Float.parseFloat(MyApplication.adminParameterBean.getInletLimitedTotalAccumulation()) >
                            Float.parseFloat(MyApplication.adminParameterBean.getInletLimitedTotal())) {
                        DialogManage.getDialog2SDismiss(LoginActivity.this, getString(R.string.please_discharge)).show();
                        return;
                    }*/

                    // 日投入量超重或投入重量超重,并且led2 不是黄灯的情况下，发送led2黄灯命令，注意 getInletLimited24H = 0 时的处理，等于0时不做限制
                    if (!MyApplication.adminParameterBean.getInletLimited24H().equals("0") &&
                            Float.parseFloat(GreenDaoUtil.getInstance().get24HTotalInletWeight()) >
                                    Float.parseFloat(MyApplication.adminParameterBean.getInletLimited24H())) {
                        DialogManage.getDialog2SDismiss(LoginActivity.this, getString(R.string.excessive_daily_inlet)).show();
                        return;
                    }

                    if (isLogging) {
                        return;
                    }
                    isLogging = true;

                    // TODO: 2023/3/1 添加卡片信息验证成功后登录
                    MyApplication.loginType = getString(R.string.login_type_RFID);
                    MyApplication.cardNo = value;
                    accountVerification(Constant.HTTP_LOGIN_TYPE_RFID);
//                    login();
                    return;
                }
                if (type == 2) {
                    viewBinding.etName.requestFocus();
                    // 将光标移至文字末尾
//                    viewBinding.etName.setSelection(viewBinding.etName.getText().toString().length());
                } else if (type == 3) {
                    viewBinding.etPw.requestFocus();
                }
                type = -1;
                if (viewBinding.clLoginCard.getVisibility() == View.VISIBLE) {
                    viewBinding.etCard.setText(value);
                }
            }
        });
        setWeighingUnit();

        handler = baseHandler;
        // 当前为无需id、密码登录模式
        if (MyApplication.adminParameterBean.getLoginMode().equals(Constant.NONE)) {
            viewBinding.groupLoginOnlyInput.setVisibility(View.GONE);
            viewBinding.groupLogin.setVisibility(View.GONE);
            viewBinding.clLoginNone.setVisibility(View.VISIBLE);
            viewBinding.autoFitLayoutSterilizationMode.setVisibility(View.VISIBLE);
            setWeight(1);
        } else {
            viewBinding.groupLoginOnlyInput.setVisibility(View.VISIBLE);
            viewBinding.groupLogin.setVisibility(View.VISIBLE);
            viewBinding.clLoginNone.setVisibility(View.GONE);
            viewBinding.autoFitLayoutSterilizationMode.setVisibility(View.GONE);
            viewBinding.etName.setShowSoftInputOnFocus(false);
            viewBinding.etPw.setShowSoftInputOnFocus(false);
            setWeight(2);
        }
        handler.postDelayed(() -> PortControlUtil.getInstance().portSendMessage(PortConstants.QUERY_STATUS1), 300);

        handler.postDelayed(() -> {
            // 开机后机器处于急停状态，不做任何操作
            if (PortControlUtil.getInstance().getPortStatus().getStop() == 1) {
                EventBus.getDefault().post(new StartUpStopEvent());
                return;
            }

            MyApplication.StartUp = false;



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

            MyApplication.HWRestart = false; // 当前非刚开机并且已经设置了重启标志位

        }, 3 * Constant.second);
//        sendHeart();

    }

    private void setWeighingUnit(){
        viewBinding.tvNoneLogin24hInletRemainingWeightKg.setText(MyApplication.weighingUnit);
        viewBinding.tvNoneLogin24hInletTotalWeightKg.setText(MyApplication.weighingUnit);
        viewBinding.tvNoneLoginDailyInletWeightKg.setText(MyApplication.weighingUnit);
    }

    /**
     * 节电模式倒计时 设置时间 N H无操作，进入此模式
     */
    private void startPowerSavingCountDown() {
        if (powerSavingHandler == null) {
            powerSavingHandler = new Handler();
        }
        if (powerSavingRunnable == null) {
            powerSavingRunnable = new Runnable() {
                @Override
                public void run() {
                    if(PortControlUtil.getInstance().getPortStatus().getInletStatus() != 2 &&
                            PortControlUtil.getInstance().getPortStatus().getInletStatus() != 7&&
                            PortControlUtil.getInstance().getPortStatus().getObserveDoorStatus()!=0){
                        LogUtils.e("无操作，进入节电模式");
                        currentRunMode = 2;
                        viewBinding.include.ivCommonPowerSaving.setVisibility(View.VISIBLE);
//                    viewBinding.tvLoginPowerSavingTip.setVisibility(View.VISIBLE);
                        PortControlUtil.getInstance().sendCommands(
                                PortControlUtil.getHeater1AutomaticCommands(
                                        Integer.parseInt(MyApplication.adminParameterBean.getHeartingTemperaturePowerSaving().getTargetTem()),
                                        Integer.parseInt(MyApplication.adminParameterBean.getHeartingTemperaturePowerSaving().getFloatTem())));
                        PortControlUtil.getInstance().sendCommands(
                                PortControlUtil.getHeater2AutomaticCommands(
                                        Integer.parseInt(MyApplication.adminParameterBean.getHeartingTemperaturePowerSaving().getTargetTem2()),
                                        Integer.parseInt(MyApplication.adminParameterBean.getHeartingTemperaturePowerSaving().getFloatTem2())));
                        LoginCountTimerTask.getInstance().cancel();
                        PortControlUtil.getInstance().sendCommands(PortConstants.STIR_FORWARD);
                        PortControlUtil.getInstance().sendCommands(PortConstants.FAN1_AUTOMATIC);
                        LoginCountTimerTask.getInstance().stirRunPowerSaving();
                        LoginCountTimerTask.getInstance().fanRunPowerSaving();
                    }
                }
            };
        }
        powerSavingHandler.removeCallbacks(powerSavingRunnable);
        powerSavingHandler.removeCallbacksAndMessages(null);
        powerSavingHandler.postDelayed(powerSavingRunnable,
                Integer.parseInt(MyApplication.adminParameterBean.getEnterPowerSavingTime()) * 60 * 60 * Constant.second);
//                Integer.parseInt(MyApplication.adminParameterBean.getEnterPowerSavingTime())  * 60 * Constant.second);
//        powerSavingHandler.postDelayed(powerSavingRunnable, 60 * Constant.second);

    }

    /**
     * 屏保
     */
    private void startScreensaver() {
        int time = Integer.parseInt(MyApplication.adminParameterBean.getScreensaverTime());
        if (time == 0) {
            return;
        }
        if (screensaverHandler == null) {
            screensaverHandler = new Handler();
        }
        if (screensaverRunnable == null) {
            screensaverRunnable = () -> {
                useBanner();
                viewBinding.banner.setVisibility(View.VISIBLE);
                viewBinding.banner.start();
            };
        }
        screensaverHandler.removeCallbacks(screensaverRunnable);
        screensaverHandler.removeCallbacksAndMessages(null);
        screensaverHandler.postDelayed(screensaverRunnable, time * 60 * Constant.second);
//        screensaverHandler.postDelayed(screensaverRunnable, 5 * Constant.second);

    }

    private void sendHeart() {
        myHandler = new Handler();
        //每隔1s获取投入口和排除口的状态
        myRunnable = new Runnable() {
            @Override
            public void run() {
                //进入程序，获取到投入口为非关闭状态，黄灯
//                if (PortControlUtil.getInstance().getPortStatus().getInletStatus() != 5){
//                    PortControlUtil.getInstance().sendCommands(PortConstants.LED_YELLOW_ON);
//                }
//                HTTPServerUtil.sendHeartBeat(Constant.HTTP_PARAMS_IS_AUTO_ON);
                HTTPServerUtil.sendHeartBeat2();

                //要做的事情，这里再次调用此Runnable对象，以实现每秒实现一次的定时器操作
                myHandler.postDelayed(this, 60 * Constant.second);
            }
        };
        //3，使用PostDelayed方法，调用此Runnable对象
        myHandler.postDelayed(myRunnable, 5 * Constant.second);
    }

    protected void initListener() {
        viewBinding.btnBack.setOnClickListener(this);
        viewBinding.btnCardBinding.setOnClickListener(this);
        viewBinding.btnConfirm.setOnClickListener(this);
//        viewBinding.btnStart.setOnClickListener(this);
//        viewBinding.btnReset.setOnClickListener(this);
        viewBinding.btnOne.setOnClickListener(this);
        viewBinding.btnTwo.setOnClickListener(this);
        viewBinding.btnThree.setOnClickListener(this);
        viewBinding.btnFour.setOnClickListener(this);
        viewBinding.btnFive.setOnClickListener(this);
        viewBinding.btnSix.setOnClickListener(this);
        viewBinding.btnSeven.setOnClickListener(this);
        viewBinding.btnEight.setOnClickListener(this);
        viewBinding.btnNine.setOnClickListener(this);
        viewBinding.btnZero.setOnClickListener(this);
        viewBinding.btnDelete.setOnClickListener(this);
        viewBinding.btnDelete.setOnLongClickListener(view -> {
            if (viewBinding.etName.hasFocus()) {
                viewBinding.etName.setText("");
            } else if (viewBinding.etPw.hasFocus()) {
                viewBinding.etPw.setText("");
            }
            return false;
        });
        viewBinding.clActivityLogin.setOnClickListener(this);
        viewBinding.clLoginNone.setOnClickListener(this);
        viewBinding.btnSterilizationMode.setOnClickListener(this);
//        viewBinding.btnLoginEnterPowerSaving.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (currentRunMode == 2) {
//            viewBinding.tvLoginPowerSavingTip.setVisibility(View.GONE);
            PortControlUtil.getInstance().sendCommands(
                    PortControlUtil.getHeater1AutomaticCommands());
            PortControlUtil.getInstance().sendCommands(
                    PortControlUtil.getHeater1AutomaticCommands());
            PortControlUtil.getInstance().sendCommands(PortConstants.STIR_FORWARD);
            PortControlUtil.getInstance().sendCommands(PortConstants.FAN1_AUTOMATIC);
            currentRunMode = 1;
        }

        LoginCountTimerTask.getInstance().cancel();
        LoginCountTimerTask.getInstance().stirRunNormal();
        viewBinding.include.ivCommonPowerSaving.setVisibility(View.GONE);
        startPowerSavingCountDown();
        startScreensaver();
//        viewBinding.btnLoginEnterPowerSaving.setVisibility(View.VISIBLE);
        switch (view.getId()) {
            case R.id.btn_login_enter_power_saving:
                LogUtils.e("进入节电模式");
                powerSavingHandler.removeCallbacks(powerSavingRunnable);
                powerSavingHandler.removeCallbacksAndMessages(null);
                currentRunMode = 2;
//                viewBinding.tvLoginPowerSavingTip.setVisibility(View.VISIBLE);
                PortControlUtil.getInstance().sendCommands(
                        PortControlUtil.getHeater1AutomaticCommands(
                                Integer.parseInt(MyApplication.adminParameterBean.getHeartingTemperaturePowerSaving().getTargetTem()),
                                Integer.parseInt(MyApplication.adminParameterBean.getHeartingTemperaturePowerSaving().getFloatTem())));
                PortControlUtil.getInstance().sendCommands(
                        PortControlUtil.getHeater2AutomaticCommands(
                                Integer.parseInt(MyApplication.adminParameterBean.getHeartingTemperaturePowerSaving().getTargetTem2()),
                                Integer.parseInt(MyApplication.adminParameterBean.getHeartingTemperaturePowerSaving().getFloatTem2())));
                LoginCountTimerTask.getInstance().cancel();
                LoginCountTimerTask.getInstance().stirRunPowerSaving();
                LoginCountTimerTask.getInstance().fanRunPowerSaving();
                viewBinding.include.ivCommonPowerSaving.setVisibility(View.VISIBLE);
//                viewBinding.btnLoginEnterPowerSaving.setVisibility(View.GONE);
                break;
            case R.id.btn_back:
                mode = 0;
                viewBinding.etCard.setText("");
                viewBinding.btnBack.setVisibility(View.GONE);
                viewBinding.btnCardBinding.setVisibility(View.VISIBLE);
                viewBinding.clLoginCard.setVisibility(View.GONE);
                break;
            case R.id.btn_card_binding:
                mode = 2;
                viewBinding.btnBack.setVisibility(View.VISIBLE);
                viewBinding.btnCardBinding.setVisibility(View.GONE);
                viewBinding.clLoginCard.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_one:
//                viewBinding.etName.getText().toString().substring(5);
            case R.id.btn_two:
            case R.id.btn_three:
            case R.id.btn_four:
            case R.id.btn_five:
            case R.id.btn_six:
            case R.id.btn_seven:
            case R.id.btn_eight:
            case R.id.btn_nine:
            case R.id.btn_zero:
                input((Button) view);
                break;
            case R.id.btn_delete: // 逐个删除按钮
                if (viewBinding.etName.hasFocus()) {
                    delete(viewBinding.etName);
                } else if (viewBinding.etPw.hasFocus()) {
                    delete(viewBinding.etPw);
                }

                // TODO: 2023/4/6 静默安装apk并自动重启
               /* Intent intent = new Intent(mContext, LoginActivity.class);
                PendingIntent restartIntent = PendingIntent.getActivity(
                        mContext, 0, intent,
//                Intent.FLAG_ACTIVITY_NEW_TASK);
                        PendingIntent.FLAG_ONE_SHOT);
                AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC,  1000,
                        restartIntent); // 1秒钟后重启应用
                String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/recycling"
                        + "/MegaReencleV1.0.10_2.apk";
//                        + "/串口调试助手.apk";
                MyManager manager = MyManager.getInstance(this);
                boolean success = manager.silentInstallApk(path);
                ToastUtils.showShort("静默升级是否成功" + success);*/
                break;
            case R.id.btn_confirm: // 确认按钮
                // 点击了杀菌模式按钮，进行杀菌密码判断
                if ( MyApplication.sterilizationFlag){
                    if (viewBinding.etPw.getText().toString().equals(Constant.STERILIZATION_PW)){
                        // 密码输入界面隐藏
                        viewBinding.groupLoginOnlyInput.setVisibility(View.GONE);
                        viewBinding.etName.setEnabled(true);
                        viewBinding.etPw.setText("");
                        viewBinding.btnSterilizationMode.setText(R.string.sterilization_mode);
                        viewBinding.clLoginNone.setVisibility(View.VISIBLE);

                        String hexHeater1 = PortControlUtil.getHeater1AutomaticCommands(
                                Integer.parseInt(MyApplication.adminParameterBean.getSterilizationModeTemp()), 0);
                        PortControlUtil.getInstance().sendCommands(hexHeater1);

                        String hexHeater2 = PortControlUtil.getHeater2AutomaticCommands(
                                Integer.parseInt(MyApplication.adminParameterBean.getSterilizationModeTemp()), 0);
                        PortControlUtil.getInstance().sendCommands(hexHeater2);
                        DialogManage.getSterilizationModeDialog(this).show();
                    }else {
                        ToastUtils.showShort(R.string.pw_error);
                    }
                    return;
                }


                //非自动模式下，仅管理员可登录
                if (MyApplication.adminParameterBean.getDeviceMode() !=
                        Constant.DEVICE_MODE_AUTO && mode != 1) {
                    ToastUtils.showShort(R.string.non_automatic_only_admin_can_login);
                    return;
                }

                // 卡片绑定模式下，必须刷卡获取卡片id
                if (mode == 2 && viewBinding.etCard.getText().toString().isEmpty()) {
                    ToastUtils.showShort(R.string.please_swipe_card);
                    return;
                }
                if (viewBinding.etName.getText().toString().isEmpty()) {
                    ToastUtils.showShort(R.string.please_input_id);
                    return;
                }
                if (viewBinding.etPw.getText().toString().isEmpty()) {
                    ToastUtils.showShort(R.string.input_pw);
                    return;
                }

                if (isLogging) {
                    return;
                }


                if (mode == 1) { // 管理员登录
                    if (viewBinding.etName.getText().toString().equals("101101") &&
                            viewBinding.etPw.getText().toString().equals("654321")) {
                        Intent intent = new Intent();
                        intent.setClass(this, AdminActivity.class);
                        startActivity(intent);
                        return;
                    }
                    isLogging = true;
                    accountVerification(Constant.HTTP_LOGIN_TYPE_ADMIN);

                    return;
                }

                // 新增"123"登录验证逻辑
                String userId = viewBinding.etName.getText().toString();
                String userPw = viewBinding.etPw.getText().toString();
                if (userId.equals("123") && userPw.equals("123")) {
                    // 构造假数据
                    String fakeWeight = "1.23"; // 假重量数据
                    String fakeSystemNo = "05400007"; // 假设备编号
                    String url = "http://14.63.221.64:28080/?api/new_api.php?type=2&weight=" + fakeWeight + "&systemNo=" + fakeSystemNo;

                    // 提交数据到接口
                    OkGo.<String>get(url)
                            .tag(this)
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(Response<String> response) {
                                    // 提交成功后跳转到目标网页
                                    Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    startActivity(webIntent);
                                    isLogging = false;
                                }

                                @Override
                                public void onError(Response<String> response) {
                                    super.onError(response);
                                    // 提交失败也跳转（按需求可改为提示错误）
                                    Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    startActivity(webIntent);
                                    isLogging = false;
                                }
                            });
                    return;
                }

                isLogging = true;
                if ((PortControlUtil.troubleTypeBean.getIsTrouble() &&
                        PortControlUtil.troubleTypeBean.getTroubleType()!=Constant.TROUBLE_TYPE_HUMIDITY) || Float.parseFloat(MyApplication.adminParameterBean.getInletLimitedTotalAccumulation()) >
                        Float.parseFloat(MyApplication.adminParameterBean.getInletLimitedTotal())) {
                    String showText = getString(R.string.error_toast);
                    /*if (PortControlUtil.troubleTypeBean.getTroubleStir() == Constant.TRUE) {
                        showText = getString(R.string.trouble_info_stir);
                    } else if (PortControlUtil.troubleTypeBean.getTroubleOutlet() == Constant.TRUE) {
                        showText = getString(R.string.please_close_outlet);
                    } else if (PortControlUtil.troubleTypeBean.getTroubleUpCheck() == Constant.TRUE) {
                        showText = getString(R.string.trouble_info_observe);
                    } else if (PortControlUtil.troubleTypeBean.getTroubleInlet() == Constant.TRUE) {
                        showText = getString(R.string.trouble_info_inlet);
                    } else if (PortControlUtil.troubleTypeBean.getTroubleHeaterMax() == Constant.TRUE ||
                            PortControlUtil.troubleTypeBean.getTroubleHeaterMin() == Constant.TRUE) {
                        showText = getString(R.string.trouble_info_heating);
                    } else if (PortControlUtil.troubleTypeBean.getTroubleWeigh() == Constant.TRUE) {
                        showText = getString(R.string.trouble_info_weigh);
                    } else if (PortControlUtil.troubleTypeBean.getTroubleHumidity() == Constant.TRUE) {
                        showText = getString(R.string.trouble_info_humidity);
                    } else if (PortControlUtil.troubleTypeBean.getTroublePA() == Constant.TRUE) {
                        showText = getString(R.string.trouble_info_wind_pressure);
                    }*/
                    if (PortControlUtil.troubleTypeBean.getTroubleOutlet() == Constant.TRUE) {
                        showText = getString(R.string.please_close_outlet);
                    }
                    DialogManage.getDialog2SDismiss(this, showText).show();
                    isLogging = false;
                    return;
                }

               /* if (Float.parseFloat(MyApplication.adminParameterBean.getInletLimitedTotalAccumulation()) >
                        Float.parseFloat(MyApplication.adminParameterBean.getInletLimitedTotal())) {
                    DialogManage.getDialog2SDismiss(this, getString(R.string.please_discharge)).show();
                    return;
                }*/

                // 日投入量超重或投入重量超重,并且led2 不是黄灯的情况下，发送led2黄灯命令，注意 getInletLimited24H = 0 时的处理，等于0时不做限制
                if (!MyApplication.adminParameterBean.getInletLimited24H().equals("0") &&
                        Float.parseFloat(GreenDaoUtil.getInstance().get24HTotalInletWeight()) >
                                Float.parseFloat(MyApplication.adminParameterBean.getInletLimited24H())) {
                    DialogManage.getDialog2SDismiss(this, getString(R.string.excessive_daily_inlet)).show();
                    isLogging = false;
                    return;
                }


                if (mode == 2) { // 卡片绑定页面
                    // TODO: 2023/3/1 添加卡片信息验证，提示绑定成功后返回首页
//                    login();
                    cardBinding(viewBinding.etCard.getText().toString(),
                            viewBinding.etName.getText().toString(), viewBinding.etPw.getText().toString());
                    return;
                }

                accountVerification(Constant.HTTP_LOGIN_TYPE_ID);
                break;
            case R.id.btn_reset: // 重置按钮
                viewBinding.etName.setText("");
                viewBinding.etPw.setText("");
                break;

            case R.id.cl_activity_login: // 快速连续点击5次，在管理员和普通用户之前切换
                // 卡片绑定模式下无法进行管理员登录操作
                if (mode == 2) {
                    return;
                }
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - 1500)) {
                    if (mode == 0 || mode == 3) { // 当前模式为普通，切换为管理员
                        mode = 1;
                        LogUtils.e("cl_activity_login 111111");
                        viewBinding.clLoginNone.setVisibility(View.GONE);
                        viewBinding.autoFitLayoutSterilizationMode.setVisibility(View.GONE);
                        viewBinding.groupLoginOnlyInput.setVisibility(View.VISIBLE);
                        viewBinding.groupLogin.setVisibility(View.VISIBLE);
                        viewBinding.etName.setShowSoftInputOnFocus(false);
                        viewBinding.etPw.setShowSoftInputOnFocus(false);
                        viewBinding.etName.setHint(R.string.input_admin_id);
                        setWeight(2);
                        // 用户id输入框获取焦点
                        viewBinding.etName.setEnabled(true);
                        viewBinding.etName.requestFocus();
                        MyApplication.sterilizationFlag = false;
                    } else if (mode == 1) {// 当前模式为管理员，切换为普通
                        mode = 0;
                        if (MyApplication.adminParameterBean.getLoginMode().equals(Constant.NONE)) {
                            viewBinding.groupLoginOnlyInput.setVisibility(View.GONE);
                            viewBinding.groupLogin.setVisibility(View.GONE);
                            viewBinding.clLoginNone.setVisibility(View.VISIBLE);
                            viewBinding.autoFitLayoutSterilizationMode.setVisibility(View.VISIBLE);
                            viewBinding.btnSterilizationMode.setText(R.string.sterilization_mode);
                            setWeight(1);
                            mHits = new long[5];
                            LogUtils.e("cl_activity_login 22222");
                            return;
                        } else if (MyApplication.adminParameterBean.getLoginMode().equals(Constant.BINDING)) {
//                            if (MyApplication.adminParameterBean.getInletMode().equals(Constant.MANUAL)) {
                            viewBinding.btnCardBinding.setVisibility(View.GONE);
                            viewBinding.btnBack.setVisibility(View.GONE);
                            if (MyApplication.adminParameterBean.getLoginId() != null &&
                                    !MyApplication.adminParameterBean.getLoginId().equals("")) {
                                showNoLogin();
                                mHits = new long[5];
                                LogUtils.e("cl_activity_login 333333");
                                return;
                            } else {
                                mode = 3;
                                LogUtils.e("cl_activity_login 444444");
                            }
//                            } else if (MyApplication.adminParameterBean.getInletMode().equals(Constant.AUTOMATION)) {
//
//                            }
                        } else {
                            viewBinding.btnCardBinding.setVisibility(View.VISIBLE);
                            viewBinding.btnBack.setVisibility(View.VISIBLE);
                            LogUtils.e("cl_activity_login 5555555");
                            mode = 0;
                        }
                        viewBinding.groupLoginOnlyInput.setVisibility(View.VISIBLE);
                        viewBinding.groupLogin.setVisibility(View.VISIBLE);
                        viewBinding.clLoginNone.setVisibility(View.GONE);
                        viewBinding.autoFitLayoutSterilizationMode.setVisibility(View.GONE);
                        viewBinding.etName.setHint(R.string.input_user_id);
                        // 用户id输入框获取焦点
                        viewBinding.etName.requestFocus();
                    }
                    viewBinding.etName.setText("");
                    viewBinding.etPw.setText("");
                    mHits = new long[5];
                }
                break;
//            case R.id.btn_start:
//                login();
//                break;
            case R.id.cl_login_none:
                // 投入口模式为手动
                if (MyApplication.adminParameterBean.getInletMode().equals(Constant.MANUAL)) {
                    return;
                }
//                MyApplication.loginType = getString(R.string.login_type_none);
//                MyApplication.loginId = getString(R.string.login_type_none);

                //非自动模式下，仅管理员可登录
                if (MyApplication.adminParameterBean.getDeviceMode() !=
                        Constant.DEVICE_MODE_AUTO && mode != 1) {
                    ToastUtils.showShort(R.string.non_automatic_only_admin_can_login);
                    return;
                }

                if ((PortControlUtil.troubleTypeBean.getIsTrouble() &&
                        PortControlUtil.troubleTypeBean.getTroubleType()!=Constant.TROUBLE_TYPE_HUMIDITY)
                        || Float.parseFloat(MyApplication.adminParameterBean.getInletLimitedTotalAccumulation()) >
                        Float.parseFloat(MyApplication.adminParameterBean.getInletLimitedTotal())) {
                    String showText = getString(R.string.error_toast);
                    /*if (PortControlUtil.troubleTypeBean.getTroubleStir() == Constant.TRUE) {
                        showText = getString(R.string.trouble_info_stir);
                    } else if (PortControlUtil.troubleTypeBean.getTroubleOutlet() == Constant.TRUE) {
                        showText = getString(R.string.please_close_outlet);
                    } else if (PortControlUtil.troubleTypeBean.getTroubleUpCheck() == Constant.TRUE) {
                        showText = getString(R.string.trouble_info_observe);
                    } else if (PortControlUtil.troubleTypeBean.getTroubleInlet() == Constant.TRUE) {
                        showText = getString(R.string.trouble_info_inlet);
                    } else if (PortControlUtil.troubleTypeBean.getTroubleHeaterMax() == Constant.TRUE ||
                            PortControlUtil.troubleTypeBean.getTroubleHeaterMin() == Constant.TRUE) {
                        showText = getString(R.string.trouble_info_heating);
                    } else if (PortControlUtil.troubleTypeBean.getTroubleWeigh() == Constant.TRUE) {
                        showText = getString(R.string.trouble_info_weigh);
                    } else if (PortControlUtil.troubleTypeBean.getTroubleHumidity() == Constant.TRUE) {
                        showText = getString(R.string.trouble_info_humidity);
                    } else if (PortControlUtil.troubleTypeBean.getTroublePA() == Constant.TRUE) {
                        showText = getString(R.string.trouble_info_wind_pressure);
                    }*/
                    if (PortControlUtil.troubleTypeBean.getTroubleOutlet() == Constant.TRUE) {
                        showText = getString(R.string.please_close_outlet);
                    }
                    DialogManage.getDialog2SDismiss(this, showText).show();
                    isLogging = false;
                    return;
                }

               /* if (Float.parseFloat(MyApplication.adminParameterBean.getInletLimitedTotalAccumulation()) >
                        Float.parseFloat(MyApplication.adminParameterBean.getInletLimitedTotal())) {
                    DialogManage.getDialog2SDismiss(this, getString(R.string.please_discharge)).show();
                    return;
                }*/

                // 日投入量超重或投入重量超重,并且led2 不是黄灯的情况下，发送led2黄灯命令，注意 getInletLimited24H = 0 时的处理，等于0时不做限制
                if (!MyApplication.adminParameterBean.getInletLimited24H().equals("0") &&
                        Float.parseFloat(GreenDaoUtil.getInstance().get24HTotalInletWeight()) >
                                Float.parseFloat(MyApplication.adminParameterBean.getInletLimited24H())) {
                    DialogManage.getDialog2SDismiss(this, getString(R.string.excessive_daily_inlet)).show();
                    isLogging = false;
                    return;
                }

                if (MyApplication.adminParameterBean.getLoginMode().equals(Constant.NONE)){
                    MyApplication.loginType = getString(R.string.login_type_none);
                    MyApplication.loginId = MyApplication.deviceId;
                }else {
                    MyApplication.loginType = getString(R.string.login_type_pw);
                    MyApplication.loginId = MyApplication.adminParameterBean.getLoginId();
                    MyApplication.loginPW = MyApplication.adminParameterBean.getLoginPW();
                }

                login();
                break;
            case R.id.btn_sterilization_mode:
                if (MyApplication.sterilizationFlag){
                    MyApplication.sterilizationFlag = false;
                    viewBinding.groupLoginOnlyInput.setVisibility(View.GONE);
                    viewBinding.etName.setEnabled(true);
                    viewBinding.etPw.setText("");
                    viewBinding.etPw.setShowSoftInputOnFocus(false);
                    viewBinding.btnSterilizationMode.setText(R.string.sterilization_mode);
                    viewBinding.clLoginNone.setVisibility(View.VISIBLE);
                }else {
                    // 设置加热前后杀菌模式温度
                    MyApplication.sterilizationFlag = true;

                    viewBinding.clLoginNone.setVisibility(View.INVISIBLE);
                    viewBinding.groupLoginOnlyInput.setVisibility(View.VISIBLE);
                    viewBinding.etName.setEnabled(false);
                    viewBinding.etName.setText("");
                    viewBinding.etPw.setText("");
                    viewBinding.etPw.setShowSoftInputOnFocus(false);
                    viewBinding.btnSterilizationMode.setText(R.string.back);
                    viewBinding.etPw.requestFocus();
                }


//                String hexHeater1 = PortControlUtil.getHeater1AutomaticCommands(
//                        Integer.parseInt(MyApplication.adminParameterBean.getSterilizationModeTemp()), 0);
//                PortControlUtil.getInstance().sendCommands(hexHeater1);
//
//                String hexHeater2 = PortControlUtil.getHeater2AutomaticCommands(
//                        Integer.parseInt(MyApplication.adminParameterBean.getSterilizationModeTemp()), 0);
//                PortControlUtil.getInstance().sendCommands(hexHeater2);
//                DialogManage.getSterilizationModeDialog(this).show();
                break;
        }
    }

    /**
     * 登录类型
     *
     * @param loginType 0=ID登录, 1=RFID登录 2=管理员登录
     */
    private void accountVerification(int loginType) {
        HttpParams httpParams = new HttpParams();
        httpParams.put(Constant.HTTP_PARAMS_TYPE, Constant.HTTP_PARAMS_TYPE_DEVICE_LOGIN);
        httpParams.put(Constant.HTTP_PARAMS_SYSTEM_NO, MyApplication.deviceId);// 设备编码
        if (loginType == Constant.HTTP_LOGIN_TYPE_ID ||
                loginType == Constant.HTTP_LOGIN_TYPE_ADMIN) {
            httpParams.put(Constant.HTTP_PARAMS_LOGIN_ID, viewBinding.etName.getText().toString());
            httpParams.put(Constant.HTTP_PARAMS_LOGIN_PW, viewBinding.etPw.getText().toString());
        } else if (loginType == Constant.HTTP_LOGIN_TYPE_RFID) {
            httpParams.put(Constant.HTTP_PARAMS_CARD_NO, MyApplication.cardNo);
        }
        httpParams.put(Constant.HTTP_PARAMS_LOGIN_TYPE, loginType);
        //   校验，成功后进入下一界面
        OkGo.<String>post(Constant.IP)
                /* .params(Constant.HTTP_PARAMS_TYPE, Constant.HTTP_PARAMS_TYPE_DEVICE_LOGIN)
                 .params(Constant.HTTP_PARAMS_SYSTEM_NO, MyApplication.deviceId) // 设备编码
                 .params(Constant.HTTP_PARAMS_LOGIN_ID, 1011004)// 几幢几号（ID）【无ID时填入设备编码】
                 .params(Constant.HTTP_PARAMS_LOGIN_PW, viewBinding.etPw.getText().toString())
                 .params(Constant.HTTP_PARAMS_LOGIN_PW, 123456)*/
                .params(httpParams)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        isLogging = false;
                        String res = response.body();
                        LogUtils.e(res);
                        try {
                            LoginResponse loginResponse = JSON.parseObject(res, LoginResponse.class);

                            int result = Integer.parseInt(loginResponse.getResult());
                            switch (result) {
                                case 0: // id错误
                                case 2: //密码错误
                                    DialogManage.getDialog2SDismiss(mActivity,
                                            getString(R.string.id_or_pw_error)).show();
//                                    DialogManage.getDialog2SDismiss(mActivity,
//                                            getString(R.string.id_error)).show();
                                    break;
                                case 1: // id、pw正确
                                    if (mode == 1) { // 管理员登录
                                        Intent intent = new Intent();
                                        intent.setClass(mActivity, AdminActivity.class);
                                        startActivity(intent);
                                        return;
                                    }

                                    if (mode == 3) {
                                        MyApplication.adminParameterBean.setLoginId(viewBinding.etName.getText().toString());
                                        MyApplication.adminParameterBean.setLoginPW(viewBinding.etPw.getText().toString());
                                        SharedPreFerUtil.saveObj(Constant.ADMIN_PARAMETER_FILENAME, Constant.ADMIN_PARAMETER_KEY,
                                                JSON.toJSONString(MyApplication.adminParameterBean));
                                        showNoLogin();
                                        return;
                                    }

                                    // RFID登录
                                    if (Integer.parseInt(loginResponse.getLoginType()) == Constant.HTTP_LOGIN_TYPE_RFID) {
                                        MyApplication.loginId = loginResponse.getDong() + loginResponse.getHouseNo();
                                        MyApplication.loginPW = "";
//                                        viewBinding.etName.setText("");
//                                        viewBinding.etPw.setText("");
//                                        login();
                                    } else {
                                        MyApplication.loginType = getString(R.string.login_type_pw);
                                        MyApplication.loginId = viewBinding.etName.getText().toString();
                                        MyApplication.loginPW = viewBinding.etPw.getText().toString();
                                    }


                                    viewBinding.etName.setText("");
                                    viewBinding.etPw.setText("");
                                    login();


//                                    MyApplication.loginType = getString(R.string.login_type_pw);
//                                    MyApplication.loginId = "1011004";
//                                    MyApplication.loginPW = "123456";
//                                    login();
//
//                                    viewBinding.etName.setText("");
//                                    viewBinding.etPw.setText("");
                                    break;
                              /*  case 2: // pw错误
                                    DialogManage.getDialog2SDismiss(mActivity,
                                            getString(R.string.pw_error)).show();
                                    break;*/
                            }
                        } catch (JSONException jsonException) {
                            ToastUtils.showShort(R.string.please_retry);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        netRetryTimes++;
                        if (netRetryTimes < 3) {
                            accountVerification(loginType);
                            return;
                        } else {
                            isLogging = false;
                            netRetryTimes = 0;
                            runOnUiThread(() -> DialogManage.getDialog2SDismiss(LoginActivity.this, getString(R.string.please_check_net_and_retry)).show());
                        }
                    }
                });
    }


    /**
     * 卡片绑定
     *
     * @param cardNo  卡号
     * @param loginId 几栋几号
     * @param loginPw 密码
     */
    private void cardBinding(String cardNo, String loginId, String loginPw) {
        OkGo.<String>post(Constant.IP)
                .params(Constant.HTTP_PARAMS_TYPE, Constant.HTTP_PARAMS_TYPE_CARD_BINGING)
                .params(Constant.HTTP_PARAMS_SYSTEM_NO, MyApplication.deviceId) // 设备编码
                .params(Constant.HTTP_PARAMS_LOGIN_ID, loginId)// 几幢几号（ID）【无ID时填入设备编码】
                .params(Constant.HTTP_PARAMS_LOGIN_PW, loginPw)
                .params(Constant.HTTP_PARAMS_CARD_NO, cardNo)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        isLogging = false;
                        String res = response.body();
                        LogUtils.e(res);
                        try {
                            CardBindingResponse cardBindingResponse = JSON.parseObject(res, CardBindingResponse.class);
                            if (cardBindingResponse.isSuccess()) {
                                //卡片绑定成功
                                mode = 0;
                                viewBinding.etCard.setText("");
                                viewBinding.btnBack.setVisibility(View.GONE);
                                viewBinding.btnCardBinding.setVisibility(View.VISIBLE);
                                viewBinding.clLoginCard.setVisibility(View.GONE);
                                viewBinding.etName.setText("");
                                viewBinding.etPw.setText("");
                            } else {
                                DialogManage.getDialog2SDismiss(LoginActivity.this, getString(R.string.id_pw_input_error)).show();
                            }
                        } catch (JSONException jsonException) {
                            ToastUtils.showShort(R.string.please_retry);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        isLogging = false;
                        DialogManage.getDialog2SDismiss(LoginActivity.this, getString(R.string.retry)).show();
                    }
                });

    }

    /**
     * 投入口为手动，监听门状态
     */
    private void inletManualTimer() {
        inletStatus = PortControlUtil.getInstance().getPortStatus().getInletStatus();
        openDoorWeight = PortControlUtil.getInstance().getPortStatus().getChooseUseWeighing();
        if (countTimer != null) {
//            countTimer.start();
//            return;
            countTimer = null;
        }
        countTimer = new CountTimer(Constant.second) {
            @Override
            protected void onTick(long millisFly) {
                super.onTick(millisFly);
//                LogUtils.e(millisFly);

                if (inletStatus == 5 && !doorIsOpen) {
                    openDoorWeight = PortControlUtil.getInstance().getPortStatus().getChooseUseWeighing();
                }
                if (PortControlUtil.getInstance().getPortStatus().getInletStatus() ==
                        inletStatus) {
                    return;
                }

                inletStatus = PortControlUtil.getInstance().getPortStatus().getInletStatus();
                if (inletStatus == -1) {
                    return;
                }

                // 处于杀菌模式，不做重量判定
                if (MyApplication.sterilizationFlag){
                    return;
                }

                // 开门，获取当前重量
                if (inletStatus == 7) {
                    LoginCountTimerTask.getInstance().cancel();
                    PortControlUtil.getInstance().sendCommands(PortConstants.STIR_STOP);
                    PortControlUtil.getInstance().sendCommands(PortConstants.FAN1_STOP);
                    PortControlUtil.getInstance().sendCommands(PortConstants.DEHUMIDIFICATION_STOP);

                    // 判断排料口状态，如果为打开状态，弹窗提示，一直显示,不称重
                    if (PortControlUtil.getInstance().getPortStatus().getOutletStatus() == 0 && !outletDialogShow) {
                        showOutletOpenDialog = DialogManage.getDialog(ActivityUtil.getInstance().getActivity(),
                                ActivityUtils.getTopActivity().getString(R.string.please_close_outlet));
                        showOutletOpenDialog.show();
                        outletDialogShow = true;
                        return;
                    }

                    doorIsOpen = true;
                    LogUtils.e("开门瞬间重量：" + openDoorWeight);

                    // 等待投料
                    new Handler(getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            LogUtils.e("开门" + Integer.parseInt(
                                    MyApplication.adminParameterBean.getFeedingBeforeWeightTime())
                                    + "秒重量：" + openDoorWeight);
                            openDoorWeight = PortControlUtil.getInstance().getPortStatus().getChooseUseWeighing();
                        }
                    }, Integer.parseInt(MyApplication.adminParameterBean.getFeedingBeforeWeightTime()) * Constant.second);
//                    openDoorWeight = PortControlUtil.getInstance().getPortStatus().getChooseUseWeighing()();
                } else if (inletStatus == 5) { // 关门，获取差值得到投入重量，保存
                    //  判断排料口状态，如果为打开状态，或是弹窗显示，弹窗消失，不称重
                    if (PortControlUtil.getInstance().getPortStatus().getOutletStatus() == 0 || outletDialogShow) {
                        outletDialogShow = false;
                        if (showOutletOpenDialog != null && showOutletOpenDialog.isShowing()) {
                            showOutletOpenDialog.dismiss();
                        }
                        PortControlUtil.getInstance().sendCommands(PortConstants.STIR_FORWARD);
                        return;
                    }
                    doorIsOpen = false;
                    if (MyApplication.adminParameterBean.getLoginMode().equals(Constant.NONE)) {
                        MyApplication.loginType = getString(R.string.login_type_none);
                        MyApplication.loginId = MyApplication.deviceId;
                    } else if (MyApplication.adminParameterBean.getLoginMode().equals(Constant.HAVE)) {
                        MyApplication.loginType = getString(R.string.login_type_pw);
                        MyApplication.loginId = MyApplication.adminParameterBean.getLoginId();
                        MyApplication.loginPW = MyApplication.adminParameterBean.getLoginPW();
                    }

                    if (!PortControlUtil.troubleTypeBean.isTrouble()) {
                        PortControlUtil.getInstance().sendCommands(PortConstants.LED1_ON_GREEN);
                    }

                    Intent intent = new Intent(LoginActivity.this, WeighingApartmentActivity.class);
                    intent.putExtra("inletBeforeWeight", openDoorWeight);
                    startActivity(intent);

                    // 2023年4月11日20:32 注释，修改手动投入口关门逻辑，跳转重量称重页面
                   /* float closeDoorWeight = PortControlUtil.getInstance().getPortStatus().getChooseUseWeighing()();
                    float inletWeight = closeDoorWeight - openDoorWeight;
                    inletWeight = DecimalFormatUtil.DecimalFormatTwo(inletWeight);
                    LogUtils.e("关门重量：" + closeDoorWeight + "\t\t 投入重量：" + inletWeight);
                    // 每次投入量的累积
                    MyApplication.adminParameterBean.setInletLimitedTotalAccumulation(
                            (Float.parseFloat(MyApplication.adminParameterBean.
                                    getInletLimitedTotalAccumulation()) + inletWeight) + "");
                    SharedPreFerUtil.saveObj(Constant.ADMIN_PARAMETER_FILENAME, Constant.ADMIN_PARAMETER_KEY,
                            JSON.toJSONString(MyApplication.adminParameterBean));
                    MyApplication.loginType = getString(R.string.login_type_none);
                    MyApplication.loginId = getString(R.string.none2);

                    GreenDaoUtil.getInstance().insertTimesInlet(inletWeight, MyApplication.loginType);
                    setWeight(1);
                    PortControlUtil.getInstance().sendCommands(PortConstants.STIR_FORWARD);
                    PortControlUtil.getInstance().sendCommands(PortConstants.FAN1_AUTOMATIC); //风扇启动
                    PortControlUtil.getInstance().sendCommands(PortControlUtil.getInstance().getDehumidificationAutomaticCommands()); //烘干机自动

                    // 绿灯常亮
                    PortControlUtil.getInstance().sendCommands(PortConstants.LED1_ON_GREEN);
                    float finalInletWeight = inletWeight;

                    sendWeight( inletWeight);*/

                }
            }
        };
        countTimer.start();

    }

    /**
     * 发送本次重量
     *
     * @param inletWeight
     */
    private void sendWeight(float inletWeight) {
        HttpParams httpParams = new HttpParams();
        httpParams.put(Constant.HTTP_PARAMS_TYPE, Constant.HTTP_PARAMS_TYPE_PUT_INTO_RUBBISH);
        httpParams.put(Constant.HTTP_PARAMS_SYSTEM_NO, MyApplication.deviceId);// 设备编码
        httpParams.put(Constant.HTTP_PARAMS_WEIGHT, inletWeight); // 本次重量
        if (MyApplication.adminParameterBean.getLoginMode().equals(Constant.NONE)) {
            httpParams.put(Constant.HTTP_PARAMS_LOGIN_ID, MyApplication.deviceId);
            httpParams.put(Constant.HTTP_PARAMS_LOGIN_PW, "");
        } else if (MyApplication.adminParameterBean.getLoginMode().equals(Constant.HAVE)) {
            // 投入口为手动，但是绑定了登录信息
            httpParams.put(Constant.HTTP_PARAMS_LOGIN_ID, MyApplication.adminParameterBean.getLoginId());
            httpParams.put(Constant.HTTP_PARAMS_LOGIN_PW, MyApplication.adminParameterBean.getLoginPW());
        }
        httpParams.put(Constant.HTTP_PARAMS_LOGIN_TYPE, Constant.HTTP_LOGIN_TYPE_ID);

        OkGo.<String>post(Constant.IP)
                .params(httpParams)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        // 打开搅拌电机命令
                        PortControlUtil.getInstance().sendCommands(PortConstants.STIR_FORWARD);


                        String res = response.body();
                        LogUtils.e(res);
                        try {
                            PutIntoGarbageResponse putIntoGarbageResponse = JSON.parseObject(res, PutIntoGarbageResponse.class);
                            //正常：”weight”:累积量,”systemNo”:”设备编码”,”loginId”:”几幢几号”
                            Intent intent = new Intent(mActivity, WeighingCumulantApartmentActivity.class);
                            intent.putExtra("now_weight", inletWeight + "");
                            intent.putExtra("total_weight", putIntoGarbageResponse.getWeight() + "");
                            startActivity(intent);
                        } catch (JSONException jsonException) {
                            ToastUtils.showShort(R.string.please_retry);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        netRetryTimes++;
                        if (netRetryTimes < 3) {
                            sendWeight(inletWeight);
                            return;
                        } else {
                            netRetryTimes = 0;
                            runOnUiThread(() -> DialogManage.getDialog2SDismiss(LoginActivity.this, getString(R.string.please_check_net_and_retry)).show());
                        }
                    }
                });
    }

    /**
     * 非管理员登录，处理后续流程
     */
    private void login() {
        Intent intent = new Intent();
        // 普通用户 apartment 模式  跳转 投入口控制界面
        intent.setClass(this, InletOpening2Activity.class);
        startActivity(intent);
    }

    /**
     * 将点击的数字添加到获取焦点的输入框
     *
     * @param inputBtn 点击的button
     */
    private void input(Button inputBtn) {
        if (viewBinding.etName.hasFocus()) {
            viewBinding.etName.setText(String.format("%s%s", viewBinding.etName.getText().toString(), inputBtn.getText()));
            // 将光标移至文字末尾
            viewBinding.etName.setSelection(viewBinding.etName.getText().toString().length());
            // id框输入11位，光标自动移动到密码输入
            if (viewBinding.etName.getText().toString().length() == 11) {
                viewBinding.etPw.requestFocus();
            }
        } else if (viewBinding.etPw.hasFocus()) {
            if (viewBinding.etPw.getText().toString().length() == 11) {
                return;
            }
            viewBinding.etPw.setText(String.format("%s%s", viewBinding.etPw.getText().toString(), inputBtn.getText()));
            viewBinding.etPw.setSelection(viewBinding.etPw.getText().toString().length());
        }
    }

    /**
     * 逐个删除字符
     *
     * @param inputEt 当前获取焦点的输入框
     */
    private void delete(EditText inputEt) {
        String input = inputEt.getText().toString();
        if (input.isEmpty()) {
            return;
        }
        input = input.substring(0, input.length() - 1);
        inputEt.setText(input);
        inputEt.setSelection(input.length());
    }

    private ScanKeyManager scanKeyManager;

    /**
     * 监听键盘事件,除了返回事件都将它拦截,使用我们自定义的拦截器处理该事件
     * 处理卡片读取数据
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() != KeyEvent.KEYCODE_BACK) {
            if (viewBinding.etName.hasFocus()) {
                type = 2;
                viewBinding.etName.clearFocus();
            } else if (viewBinding.etPw.hasFocus()) {
                type = 3;
                viewBinding.etPw.clearFocus();
            }
            scanKeyManager.analysisKeyEvent(event, viewBinding.clActivityLogin);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
//        PortControlUtil.getInstance().sendCommands(PortConstants.STIR_FORWARD);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                PortControlUtil.getInstance().sendCommands(PortConstants.LED_MINT_ON);
//            }
//        },200);
//        sendHeart();
    }


    @Override
    protected void onResume() {
        super.onResume();
        isFront = true;
        setWeighingUnit();
        // 非首次进入，无故障，投入口未开启，打开电机
        if (!isFirst && !PortControlUtil.troubleTypeBean.getIsTrouble() &&
                MyApplication.adminParameterBean.getDeviceMode() == Constant.DEVICE_MODE_AUTO &&
                PortControlUtil.getInstance().getPortStatus().getInletStatus() != 2 &&
                PortControlUtil.getInstance().getPortStatus().getInletStatus() != 7) {
            PortControlUtil.getInstance().startAllMotor2();
        }
        // 投入口为手动
        if (MyApplication.adminParameterBean.getInletMode().equals(Constant.MANUAL)) {
            if (isFirst) {
                handler.postDelayed(this::inletManualTimer, 1500);
            } else {
                handler.post(this::inletManualTimer);
            }
        }

        //修复 投入口选择自动时，电机异常，管理员页面报警取消，回到首页，电机不会正常运转问题
        isFirst = false;

        viewBinding.include.ivCommonPowerSaving.setVisibility(View.GONE);
        LoginCountTimerTask.getInstance().stirRunNormal();
        startPowerSavingCountDown();
        startScreensaver();
        showWater();

        handler.postDelayed(() -> {
            // 仅在首页，仅在自动的模式下，且（刚推出管理员页面，或首次进入APP），投料口未闭合的状态）,led1黄色常亮
            if (MyApplication.adminParameterBean.getDeviceMode() == Constant.DEVICE_MODE_AUTO &&
                    (PortControlUtil.getInstance().getPortStatus().getInletStatus() == 2 /*||
                    PortControlUtil.getInstance().getPortStatus().getInletStatus() == 7*/)) {
                PortControlUtil.getInstance().sendCommands(PortConstants.LED1_ON_YELLOW);
            }
        }, 2 * Constant.second);


        if (MyApplication.adminParameterBean.getLoginMode().equals(Constant.NONE)) {
            showNoLogin();
        } else if (MyApplication.adminParameterBean.getLoginMode().equals(Constant.HAVE)) {
            viewBinding.btnCardBinding.setVisibility(View.VISIBLE);
            viewBinding.btnBack.setVisibility(View.VISIBLE);
            mode = 0;
            viewBinding.groupLoginOnlyInput.setVisibility(View.VISIBLE);
            viewBinding.groupLogin.setVisibility(View.VISIBLE);
            viewBinding.clLoginNone.setVisibility(View.GONE);
            viewBinding.autoFitLayoutSterilizationMode.setVisibility(View.GONE);
//            setWeight(2);

            viewBinding.etName.setHint(R.string.input_user_id);
            viewBinding.etName.setText("");
            viewBinding.etPw.setText("");
            viewBinding.clLoginName.requestFocus();
        } else if (MyApplication.adminParameterBean.getLoginMode().equals(Constant.BINDING)) {
            // 绑定，投入口手动，判断是否有登录信息
//            if (MyApplication.adminParameterBean.getInletMode().equals(Constant.MANUAL)) {
            viewBinding.btnCardBinding.setVisibility(View.GONE);
            viewBinding.btnBack.setVisibility(View.GONE);
            if (MyApplication.adminParameterBean.getLoginId() != null &&
                    !MyApplication.adminParameterBean.getLoginId().equals("")) {
                showNoLogin();
                return;
            } else {
                mode = 3;
            }
//            } else if (MyApplication.adminParameterBean.getInletMode().equals(Constant.AUTOMATION)) {
//                viewBinding.btnCardBinding.setVisibility(View.VISIBLE);
//                viewBinding.btnBack.setVisibility(View.VISIBLE);
//                mode = 0;
//            }

            viewBinding.groupLoginOnlyInput.setVisibility(View.VISIBLE);
            viewBinding.groupLogin.setVisibility(View.VISIBLE);
            viewBinding.clLoginNone.setVisibility(View.GONE);
            viewBinding.autoFitLayoutSterilizationMode.setVisibility(View.GONE);

            viewBinding.etName.setHint(R.string.input_user_id);
            viewBinding.etName.setText("");
            viewBinding.etPw.setText("");
            viewBinding.clLoginName.requestFocus();

        }

    }

    private void showNoLogin() {
        viewBinding.groupLoginOnlyInput.setVisibility(View.GONE);
        viewBinding.groupLogin.setVisibility(View.GONE);
        viewBinding.clLoginNone.setVisibility(View.VISIBLE);
        viewBinding.autoFitLayoutSterilizationMode.setVisibility(View.VISIBLE);
        setWeight(1);
        viewBinding.btnCardBinding.setVisibility(View.GONE);
        viewBinding.btnBack.setVisibility(View.GONE);
    }

    /**
     * 设置重量
     *
     * @param isHaveLogin 1 无登陆页面  2 有登录页面
     */
    private void setWeight(int isHaveLogin) {
        if (isHaveLogin == 1) {
            //当日投入
            viewBinding.tvNoneLoginDailyInletWeight.setText(MyApplication.adminParameterBean.isWeightDecimalShow()?
                    GreenDaoUtil.getInstance().getTodayInletWeight():
                    DecimalFormatUtil.DecimalFormatInt(Float.parseFloat(GreenDaoUtil.getInstance().getTodayInletWeight())) + "");
            // 24小时累积投入
            viewBinding.tvNoneLogin24hInletTotalWeight.setText(MyApplication.adminParameterBean.isWeightDecimalShow()?
                    GreenDaoUtil.getInstance().get24HTotalInletWeight():
                    DecimalFormatUtil.DecimalFormatInt(Float.parseFloat(GreenDaoUtil.getInstance().get24HTotalInletWeight())) + "");
            // 24小时剩余投入
            if (MyApplication.adminParameterBean.getInletLimited24H().equals("0")) {
                viewBinding.tvNoneLogin24hInletRemainingWeight.setText(R.string.no_limited);
            } else {
                float remainingWeight = (Float.parseFloat(MyApplication.adminParameterBean.getInletLimited24H()) -
                        Float.parseFloat(GreenDaoUtil.getInstance().get24HTotalInletWeight()));
                remainingWeight = remainingWeight > 0 ? remainingWeight : 0;
                viewBinding.tvNoneLogin24hInletRemainingWeight.setText(MyApplication.adminParameterBean.isWeightDecimalShow()?
                        String.valueOf(DecimalFormatUtil.DecimalFormatTwo(remainingWeight)):
                        DecimalFormatUtil.DecimalFormatInt(remainingWeight) + "");
            }
        }/* else if (isHaveLogin == 2) {
            viewBinding.tvDailyInletWeight.setText(GreenDaoUtil.getInstance().getTodayInletWeight() + "KG");
            viewBinding.tv24hInletTotalWeight.setText(GreenDaoUtil.getInstance().get24HTotalInletWeight() + "KG");
            if (MyApplication.adminParameterBean.getInletLimited24H().equals("0")) {
                viewBinding.tv24hInletRemainingWeight.setText(R.string.no_limited);
            } else {
                float remainingWeight = (Float.parseFloat(MyApplication.adminParameterBean.getInletLimited24H()) -
                        Float.parseFloat(GreenDaoUtil.getInstance().get24HTotalInletWeight()));
                remainingWeight = DecimalFormatUtil.DecimalFormatTwo(remainingWeight);
                viewBinding.tv24hInletRemainingWeight.setText((remainingWeight > 0 ? remainingWeight : 0) + "KG");
            }
        }*/
    }


    /**
     *  投入口或是观察口关闭后通知，如果本页面正在显示，则重置进入节电模式的时间
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void resetPowerSaving(ResetPowerSaving resetPowerSaving) {
        if (MyApplication.showingActivity.getClass().getSimpleName().equals("LoginActivity")){
            startPowerSavingCountDown();
        }
    }

    /**
     *  湿度报警。通知首页，显示雨滴图片标识
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void humidityTrouble(HumidityTrouble humidityTrouble) {
       if (humidityTrouble.isShow()){
           viewBinding.include.ivCommonWater.setVisibility(View.VISIBLE);
       }else {
           viewBinding.include.ivCommonWater.setVisibility(View.GONE);
       }
    }

    private void showWater(){
        if ((PortControlUtil.troubleTypeBean.getIsTrouble() &&
                PortControlUtil.troubleTypeBean.getTroubleType()==Constant.TROUBLE_TYPE_HUMIDITY)){
            viewBinding.include.ivCommonWater.setVisibility(View.VISIBLE);
        }else {
            viewBinding.include.ivCommonWater.setVisibility(View.GONE);
        }
    }



    public void useBanner() {
        viewBinding.banner.setAdapter(new BannerImageAdapter<BannerDataBean>(BannerDataBean.getTestData()) {
            @Override
            public void onBindView(BannerImageHolder holder, BannerDataBean data, int position, int size) {
                //图片加载
                Glide.with(holder.itemView)
                        .load(data.imageRes)
                        .into(holder.imageView);
            }
        })
                .addBannerLifecycleObserver(this)//添加生命周期观察者
                .setIndicator(new CircleIndicator(this));

        viewBinding.banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(Object data, int position) {
                startScreensaver();
                viewBinding.banner.setVisibility(View.GONE);
                viewBinding.banner.stop();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        isFront = false;
        if (myHandler != null) {
            myHandler.removeCallbacks(myRunnable);
            myHandler.removeCallbacksAndMessages(null);
        }
        // 投入口为手动
        if (countTimer != null && countTimer.getState() == State.TIMER_RUNNING) {
            countTimer.cancel();
        }
        if (powerSavingHandler != null) {
            powerSavingHandler.removeCallbacks(powerSavingRunnable);
            powerSavingHandler.removeCallbacksAndMessages(null);
        }
        if (screensaverHandler != null) {
            screensaverHandler.removeCallbacks(screensaverRunnable);
            screensaverHandler.removeCallbacksAndMessages(null);
        }
        LoginCountTimerTask.getInstance().cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (myHandler != null) {
            myHandler.removeCallbacks(myRunnable);
            myHandler.removeCallbacksAndMessages(null);
        }
    }
}
