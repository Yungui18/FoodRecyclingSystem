package com.hyeprion.foodrecyclingsystem.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.hyeprion.foodrecyclingsystem.R;
import com.hyeprion.foodrecyclingsystem.base.MyApplication;
import com.hyeprion.foodrecyclingsystem.bean.AdminParameterBean;
import com.hyeprion.foodrecyclingsystem.util.Constant;
import com.hyeprion.foodrecyclingsystem.util.GreenDaoUtil;
import com.hyeprion.foodrecyclingsystem.util.countdowntimer.MyCountDownTimer;
import com.hyeprion.foodrecyclingsystem.util.port.PortConstants;
import com.hyeprion.foodrecyclingsystem.util.port.PortControlUtil;

import java.util.List;

import androidx.constraintlayout.widget.Group;


/**
 * @date 2021/4/19
 * Describe:
 */
public class DialogManage {
    private static String preActivity = "";

    public static Dialog dialog;
    //    private static Timer mTimer;
    private static Handler mHandler;
    private static Runnable runnable;
    private static String showContent = ""; // 显示的内容

    /**
     * 2S后消失的dialog
     *
     * @param context
     * @param desc
     * @return
     */
    public static Dialog getDialog2SDismiss(Activity context, String desc) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        } else {
            showContent = desc;
            View view = View.inflate(context, R.layout.dialog_toast, null);
            TextView tv_desc = view.findViewById(R.id.tv_dialog);
            tv_desc.setText(desc);
            new Handler().postDelayed(() -> {
                dialog.dismiss();
            }, 2 * Constant.second); // 延时2秒
            dialog = new Dialog(context, R.style.dialog);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(view);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }
        return dialog;
    }

    /**
     * 不会自动消失的dialog
     *
     * @param context
     * @param desc
     * @return
     */
    public static Dialog getDialog(Activity context, String desc) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        } else {
            showContent = desc;
            if (mHandler != null) {
                mHandler.removeCallbacks(runnable);
                mHandler.removeCallbacksAndMessages(null);
                mHandler = null;
            }
            View view = View.inflate(context, R.layout.dialog_toast, null);
            TextView tv_desc = view.findViewById(R.id.tv_dialog);
            tv_desc.setText(desc);

            dialog = new Dialog(context, R.style.dialog);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(view);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }
        return dialog;
    }

    /**
     * 不会自动消失的dialog
     *
     * @param context
     * @param desc
     * @return
     */
    public static Dialog getPortTipDialog(Activity context, String desc) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        } else {
            showContent = desc;
            if (mHandler != null) {
                mHandler.removeCallbacks(runnable);
                mHandler.removeCallbacksAndMessages(null);
                mHandler = null;
            }
            View view = View.inflate(context, R.layout.dialog_toast, null);
            TextView tv_desc = view.findViewById(R.id.tv_dialog);
            Button confirmBtn = view.findViewById(R.id.btn_dialog_confirm);

            tv_desc.setText(desc);
            confirmBtn.setVisibility(View.VISIBLE);
            confirmBtn.setOnClickListener(view1 -> dialog.dismiss());

            dialog = new Dialog(context, R.style.dialog);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(view);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }
        return dialog;
    }

    /**
     * 急停解除后 用户确认按钮
     *
     * @param context
     * @param desc
     * @return
     */
    public static Dialog getDialogConfirm(Activity context, String desc) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        } else {
            showContent = desc;
            View view = View.inflate(context, R.layout.dialog_toast, null);
            TextView tv_desc = view.findViewById(R.id.tv_dialog);
            Button confirmBtn = view.findViewById(R.id.btn_dialog_confirm);
            Button cancelBtn = view.findViewById(R.id.btn_dialog_cancel);
            confirmBtn.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.VISIBLE);
            tv_desc.setText(desc);

            confirmBtn.setOnClickListener(view12 -> {
                confirmBtn.setVisibility(View.GONE);
                cancelBtn.setVisibility(View.GONE);
                PortControlUtil.getInstance().resetTempIgnoreTime();
                PortControlUtil.getInstance().sendCommands(PortConstants.STOP_RELEASE);
                // 开机时为急停状态，解除急停后启动所有电机，设置自动
                if (MyApplication.StartUp) {

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

                    // 投入口或是观察口打开状态下，电机不运转
                    if (PortControlUtil.getInstance().getPortStatus().getInletStatus() == 2 ||
                            PortControlUtil.getInstance().getPortStatus().getInletStatus() == 7 ||
                            PortControlUtil.getInstance().getPortStatus().getObserveDoorStatus() == 0) {

                    }else {
                        PortControlUtil.getInstance().sendCommands(PortConstants.STIR_FORWARD); // 搅拌启动正转
                        PortControlUtil.getInstance().sendCommands(PortConstants.FAN1_AUTOMATIC); //风扇启动
                        PortControlUtil.getInstance().sendCommands(PortControlUtil.getInstance().getHeater1AutomaticCommands()); //加热1自动
                        PortControlUtil.getInstance().sendCommands(PortControlUtil.getInstance().getHeater2AutomaticCommands()); //加热2自动
                        PortControlUtil.getInstance().sendCommands(PortControlUtil.getInstance().getDehumidificationAutomaticCommands()); //烘干机自动
                    }
                    MyApplication.StartUp = false;
                }
                dialog.dismiss();
            });

            cancelBtn.setOnClickListener(view1 -> {
                confirmBtn.setVisibility(View.GONE);
                cancelBtn.setVisibility(View.GONE);
                dialog.dismiss();
                if (mHandler == null) {
                    mHandler = new Handler(ActivityUtils.getTopActivity().getMainLooper());
                }
                if (runnable == null) {
                    runnable = () -> getDialogConfirm(ActivityUtils.getTopActivity(), desc).show();
                }
                mHandler.postDelayed(runnable, 10 * Constant.second);
            });

            dialog = new Dialog(context, R.style.dialog);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(view);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }
        return dialog;
    }

    /**
     * 急停解除后 用户确认按钮
     *
     * @param context
     * @param desc
     * @return
     */
    public static Dialog getDialogConfirm(Activity context, String desc, View.OnClickListener confirmClickListener) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        } else {
            showContent = desc;
            View view = View.inflate(context, R.layout.dialog_toast, null);
            TextView tv_desc = view.findViewById(R.id.tv_dialog);
            Button confirmBtn = view.findViewById(R.id.btn_dialog_confirm);
            Button cancelBtn = view.findViewById(R.id.btn_dialog_cancel);
            confirmBtn.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.VISIBLE);
            tv_desc.setText(desc);

            confirmBtn.setOnClickListener(confirmClickListener);

            cancelBtn.setOnClickListener(view1 -> {
                confirmBtn.setVisibility(View.GONE);
                cancelBtn.setVisibility(View.GONE);
                dialog.dismiss();

            });

            dialog = new Dialog(context, R.style.dialog);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(view);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }
        return dialog;
    }


    /**
     * 删除用户投入记录弹窗
     *
     * @param context
     * @param desc
     * @return
     */
    public static Dialog getDialogDeleteInletLog(Activity context, String desc) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        } else {
            showContent = desc;
            View view = View.inflate(context, R.layout.dialog_toast, null);
            TextView tv_desc = view.findViewById(R.id.tv_dialog);
            Button confirmBtn = view.findViewById(R.id.btn_dialog_confirm);
            Button cancelBtn = view.findViewById(R.id.btn_dialog_cancel);
            confirmBtn.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.VISIBLE);
            tv_desc.setText(desc);

            confirmBtn.setOnClickListener(view12 -> {
                confirmBtn.setVisibility(View.GONE);
                cancelBtn.setVisibility(View.GONE);
                // 删除所有投入记录
                GreenDaoUtil.getInstance().deleteAllInletLog();
                dialog.dismiss();
            });

            cancelBtn.setOnClickListener(view1 -> {
                confirmBtn.setVisibility(View.GONE);
                cancelBtn.setVisibility(View.GONE);
                dialog.dismiss();
            });

            dialog = new Dialog(context, R.style.dialog);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(view);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }
        return dialog;
    }







    private static int hour = 0;
    private static int minute= 0;
    private static int second= 0;
    private static MyCountDownTimer myCountDownTimer;

    /**
     * 杀菌模式弹窗 当温度高于设置温度后开始计时
     *
     * @param context
     * @return
     */
    public static Dialog getSterilizationModeDialog(Activity context) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        } else {
            View view = View.inflate(context, R.layout.dialog_toast, null);
            TextView tv_desc = view.findViewById(R.id.tv_dialog);
            TextView tv_countdown = view.findViewById(R.id.tv_countdown);
            TextView tv_heating = view.findViewById(R.id.tv_heating);
            tv_heating.setVisibility(View.VISIBLE);
            Group groupCountdown = view.findViewById(R.id.group_countdown);

            Button confirmBtn = view.findViewById(R.id.btn_dialog_confirm);
            confirmBtn.setText(R.string.stop_sterilization);
            confirmBtn.setVisibility(View.VISIBLE);
            tv_desc.setText(R.string.Sterilization_mode_in_progress);
            tv_countdown.setText("加热中");

            Handler judgeHeating = new Handler(context.getMainLooper());
            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    // 判断当前加热温度是否大于设置的杀菌模式温度，大于后开始进行杀菌模式倒计时
                    if (PortControlUtil.getInstance().getPortStatus().getHeaterTemperature1()>
                            Integer.parseInt(MyApplication.adminParameterBean.getSterilizationModeTemp())||
                            PortControlUtil.getInstance().getPortStatus().getHeaterTemperature2()>
                                    Integer.parseInt(MyApplication.adminParameterBean.getSterilizationModeTemp())){
                        // 进行杀菌模式倒计时
                        tv_heating.setVisibility(View.GONE);
                        groupCountdown.setVisibility(View.VISIBLE);
                        myCountDownTimer(context,tv_desc,tv_heating,groupCountdown,tv_countdown,confirmBtn);
                        return;
                    }
                    judgeHeating.postDelayed(this, Constant.second);
                }
            };
            judgeHeating.post(runnable);
           /* judgeHeating.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tv_heating.setVisibility(View.GONE);
                    groupCountdown.setVisibility(View.VISIBLE);
                    myCountDownTimer(context,tv_desc,tv_heating,groupCountdown,tv_countdown,confirmBtn);
                }
            }, 3000);*/
            confirmBtn.setOnClickListener(view12 -> {
                judgeHeating.removeCallbacks(runnable);
                judgeHeating.removeCallbacksAndMessages(null);
                PortControlUtil.getInstance().sendCommands(PortControlUtil.getInstance().getHeater1AutomaticCommands()); //加热1自动
                PortControlUtil.getInstance().sendCommands(PortControlUtil.getInstance().getHeater2AutomaticCommands()); //加热2自动
                groupCountdown.setVisibility(View.GONE);
                if (myCountDownTimer != null)
                myCountDownTimer.cancel();
                dialog.dismiss();
                MyApplication.sterilizationFlag = false;
            });


            dialog = new Dialog(context, R.style.dialog);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(view);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }
        return dialog;
    }

    private static void myCountDownTimer(Activity activity,TextView desc,TextView heating,
                                         Group groupCountdown,TextView countdown,Button confirmBtn) {

        myCountDownTimer = new MyCountDownTimer(
                Integer.parseInt(MyApplication.adminParameterBean.getSterilizationModeTime()) * 60 * Constant.second, Constant.second) {
//                 20* Constant.second, Constant.second) {
            @Override
            public void onTick(long millisUntilFinished) {
                //剩余时间
                hour = (int) (millisUntilFinished / (60 * 60 * Constant.second));
                minute = (int) ((millisUntilFinished-hour*60 * 60 * Constant.second) / (60 * Constant.second));
                second = (int) (((millisUntilFinished % (60 * Constant.second))) / Constant.second);
                activity.runOnUiThread(() -> countdown.setText(hour+":"+minute+":"+second));
            }

            @Override
            public void onFinish() {
                groupCountdown.setVisibility(View.GONE);
                desc.setText(R.string.Sterilization_mode_completed);
                heating.setVisibility(View.VISIBLE);
                heating.setText(R.string.arrange_materials);
                confirmBtn.setText(R.string.btn_confirm);


//                PortControlUtil.getInstance().sendCommands(PortControlUtil.getInstance().getHeater1AutomaticCommands()); //加热1自动
//                PortControlUtil.getInstance().sendCommands(PortControlUtil.getInstance().getHeater2AutomaticCommands()); //加热2自动
//                dialog.dismiss();
            }
        };
        myCountDownTimer.start();
    }





    public static Dialog getDialog() {
        if (dialog != null) {
            return dialog;
        }
        return null;
    }

    public static String getShowContent() {
        return showContent;
    }

    public static void cancelLoading() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
