package com.hyeprion.foodrecyclingsystem.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.hyeprion.foodrecyclingsystem.R;
import com.hyeprion.foodrecyclingsystem.base.BaseActivity;
import com.hyeprion.foodrecyclingsystem.base.MyApplication;
import com.hyeprion.foodrecyclingsystem.bean.TroubleLog;
import com.hyeprion.foodrecyclingsystem.databinding.ActivityAdmin2Binding;
import com.hyeprion.foodrecyclingsystem.dialog.DialogManage;
import com.hyeprion.foodrecyclingsystem.util.ActivityUtil;
import com.hyeprion.foodrecyclingsystem.util.BarUtil;
import com.hyeprion.foodrecyclingsystem.util.Constant;
import com.hyeprion.foodrecyclingsystem.util.GreenDaoUtil;
import com.hyeprion.foodrecyclingsystem.util.HTTPServerUtil;
import com.hyeprion.foodrecyclingsystem.util.SharedPreFerUtil;
import com.hyeprion.foodrecyclingsystem.util.port.PortConstants;
import com.hyeprion.foodrecyclingsystem.util.port.PortControlUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;

/**
 * 管理员页面
 */
public class AdminActivity extends BaseActivity<ActivityAdmin2Binding> {
    private int oldMode = -1;
    private long[] mHits = new long[5];// 记录连续点击
    private Dialog exitAPPDialog;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        HTTPServerUtil.sendHeartBeat2();
        oldMode = MyApplication.adminParameterBean.getDeviceMode();
        if (oldMode == Constant.DEVICE_MODE_AUTO) {
            viewBinding.btnControlOptions.setEnabled(false);
            setButtonSelect(viewBinding.btn1ModeAuto);
        } else if (oldMode == Constant.DEVICE_MODE_MANUAL) {
            setButtonSelect(viewBinding.btn2ModeManual);
        } else if (oldMode == Constant.DEVICE_MODE_STOP) {
            setButtonSelect(viewBinding.btn3ModeStop);
        }

        // 设置上位机app 版本号
        viewBinding.tvSwVersion.setText(String.format(getString(R.string.vervison), AppUtils.getAppVersionName()));
//        if (MyApplication.getFirmwareVersionNew()) { // 新pcb板
            viewBinding.groupFw.setVisibility(View.VISIBLE);
            // 设置下位机pcb 固件版本号
            viewBinding.tvFwVersion.setText(String.format(getString(R.string.vervison),
                    PortControlUtil.getInstance().getPortStatus().getFirmwareVersion()));
//        }

        // TODO: 2023/2/4 报警不可手动点亮，仅发生问题时，进入此界面，此灯为亮，可手动点灭，报警解除
        if (PortControlUtil.troubleTypeBean.getIsTrouble()) {
            setButtonSelect(viewBinding.btn4ModeAlarm);
        }
        myHandler = new Handler();

        viewBinding.etDeviceId.setText(MyApplication.deviceId);

        viewBinding.etDeviceId.setOnFocusChangeListener((view, b) -> {
            if (b) {
                // 搜索框获取焦点后，显示输入法
                KeyboardUtils.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            } else {
                KeyboardUtils.hideSoftInput(viewBinding.etDeviceId);
            }
        });

        viewBinding.activityAdmin.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                // 点击空白部分，隐藏输入法，输入框失去焦点
                if (mActivity.getCurrentFocus() != null
                        && mActivity.getCurrentFocus().getWindowToken() != null) {
                    KeyboardUtils.hideSoftInput(viewBinding.activityAdmin);
                    viewBinding.etDeviceId.clearFocus();
                    MyApplication.deviceId = viewBinding.etDeviceId.getText().toString();
                    MyApplication.adminParameterBean.setDeviceId(viewBinding.etDeviceId.getText().toString());
                    SharedPreFerUtil.saveObj(
                            Constant.ADMIN_PARAMETER_FILENAME, Constant.ADMIN_PARAMETER_KEY,
                            JSON.toJSONString(MyApplication.adminParameterBean));
                }
            }
            return false;
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initListener() {
        viewBinding.btnBack.setOnClickListener(this);
        viewBinding.btnParameterSetting.setOnClickListener(this);
        viewBinding.btnControlOptions.setOnClickListener(this);
        viewBinding.btnStatus.setOnClickListener(this);
        viewBinding.btnTroubleLog.setOnClickListener(this);
        viewBinding.btnInletLog.setOnClickListener(this);
        viewBinding.btnTroubleTest.setOnClickListener(this);
        viewBinding.btn1ModeAuto.setOnClickListener(this);
        viewBinding.btn2ModeManual.setOnClickListener(this);
        viewBinding.btn3ModeStop.setOnClickListener(this);
        viewBinding.btn4ModeAlarm.setOnClickListener(this);
        viewBinding.scrollView.setOnScrollChangeListener(
                (NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    //监听滚动状态
                    if (scrollY > oldScrollY) {//向下滚动
                    }
                    if (scrollY < oldScrollY) {//向上滚动
                    }
                    if (scrollY == 0) {// 滚动到顶
                    }
                    // 滚动到底
                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    }
                    //判断某个控件是否可见
                    Rect scrollBounds = new Rect();
                    viewBinding.scrollView.getHitRect(scrollBounds);
                    if (viewBinding.btnInletLog.getLocalVisibleRect(scrollBounds)) {//可见
                        viewBinding.ivDrop.setVisibility(View.GONE);
                    } else {//完全不可见
                        viewBinding.ivDrop.setVisibility(View.VISIBLE);
                    }
                });

        viewBinding.include.ivCommonLeft.setOnClickListener(this);
        viewBinding.include.ivCommonMega.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
//                HTTPServerUtil.sendHeartBeat(Constant.HTTP_PARAMS_IS_AUTO_ON);
                HTTPServerUtil.sendHeartBeat2();
                MyApplication.deviceId = viewBinding.etDeviceId.getText().toString();
                MyApplication.adminParameterBean.setDeviceId(viewBinding.etDeviceId.getText().toString());
                SharedPreFerUtil.saveObj(
                        Constant.ADMIN_PARAMETER_FILENAME, Constant.ADMIN_PARAMETER_KEY,
                        JSON.toJSONString(MyApplication.adminParameterBean));
                this.finish();
                break;
            case R.id.btn_parameter_setting: // 参数设置
                Intent parameterIntent = new Intent(this, AdminParameterActivity.class);
                startActivity(parameterIntent);
                break;
            case R.id.btn_control_options: // 控制选项
                Intent controlIntent = new Intent(this, AdminControlActivity.class);
                startActivity(controlIntent);
                break;
            case R.id.btn_status: // 状态确认
                Intent statusIntent = new Intent(this, AdminStatusActivity.class);
                startActivity(statusIntent);
                break;
            case R.id.btn_trouble_log: // 故障记录
                Intent deviceInfoIntent = new Intent(this, AdminTroubleLogActivity.class);
                startActivity(deviceInfoIntent);
                break;
            case R.id.btn_inlet_log: // 投入记录
                Intent inletLogIntent = new Intent(this, AdminInletLogActivity.class);
                startActivity(inletLogIntent);
                break;
            case R.id.btn_trouble_test:
                Intent troubleTestIntent = new Intent(this, AdminTroubleTest.class);
                startActivity(troubleTestIntent);
                break;
            case R.id.btn1_mode_auto://自动模式
                if (oldMode == Constant.DEVICE_MODE_AUTO) {
                    return;
                }
                oldMode = Constant.DEVICE_MODE_AUTO;

                MyApplication.adminParameterBean.setDeviceMode(Constant.DEVICE_MODE_AUTO);
                SharedPreFerUtil.saveObj(Constant.ADMIN_PARAMETER_FILENAME, Constant.ADMIN_PARAMETER_KEY,
                        JSON.toJSONString(MyApplication.adminParameterBean));
                setButtonSelect(viewBinding.btn1ModeAuto);
                setButtonUnSelect(viewBinding.btn2ModeManual);
                setButtonUnSelect(viewBinding.btn3ModeStop);
                viewBinding.btnControlOptions.setEnabled(false);

                PortControlUtil.getInstance().resetTempIgnoreTime();
                HTTPServerUtil.sendHeartBeat2();
                if (PortControlUtil.troubleTypeBean.isTrouble()) {
                    return;
                }

                // t投入口打开 或是 观察口打开，所有电机不运转
                if (PortControlUtil.getInstance().getPortStatus().getInletStatus() == 2 ||
                        PortControlUtil.getInstance().getPortStatus().getInletStatus() == 7 ||
                        PortControlUtil.getInstance().getPortStatus().getObserveDoorStatus() == 0) {
                    return;
                }

                // 投入口关闭 且 观察口关闭，仅排料口打开，只有搅拌电机运转
                if (PortControlUtil.getInstance().getPortStatus().getInletStatus() != 2 &&
                        PortControlUtil.getInstance().getPortStatus().getInletStatus() != 7 &&
                        PortControlUtil.getInstance().getPortStatus().getObserveDoorStatus() != 0 &&
                        PortControlUtil.getInstance().getPortStatus().getOutletStatus() == 0) {
                    PortControlUtil.getInstance().sendCommands(PortConstants.STIR_FORWARD); // 搅拌启动正转
                    return;
                }

                //自动:搅拌启动正转，风扇启动，加热自动，除湿自动，此模式下，控制tab不可操作，不可点击，灯为绿色常亮
                PortControlUtil.getInstance().sendCommands(PortConstants.STIR_FORWARD); // 搅拌启动正转
                PortControlUtil.getInstance().sendCommands(PortConstants.FAN1_AUTOMATIC); //风扇启动
                PortControlUtil.getInstance().sendCommands(PortControlUtil.getInstance().getHeater1AutomaticCommands()); //加热1自动
                PortControlUtil.getInstance().sendCommands(PortControlUtil.getInstance().getHeater2AutomaticCommands()); //加热2自动
                PortControlUtil.getInstance().sendCommands(PortControlUtil.getInstance().getDehumidificationAutomaticCommands()); //烘干机自动
                PortControlUtil.getInstance().sendCommands(PortConstants.LIGHTING_AUTO); //照明自动
                break;
            case R.id.btn2_mode_manual://手动模式 无需控制，点亮后控制tab可操作，灯为黄色常亮
                if (oldMode == Constant.DEVICE_MODE_MANUAL) {
                    return;
                }
                oldMode = Constant.DEVICE_MODE_MANUAL;
                MyApplication.adminParameterBean.setDeviceMode(Constant.DEVICE_MODE_MANUAL);
                SharedPreFerUtil.saveObj(Constant.ADMIN_PARAMETER_FILENAME, Constant.ADMIN_PARAMETER_KEY,
                        JSON.toJSONString(MyApplication.adminParameterBean));
                viewBinding.btnControlOptions.setEnabled(true);
//
                setButtonUnSelect(viewBinding.btn1ModeAuto);
                setButtonSelect(viewBinding.btn2ModeManual);
                setButtonUnSelect(viewBinding.btn3ModeStop);
                HTTPServerUtil.sendHeartBeat2();
//                PortControlUtil.getInstance().sendCommands(PortConstants.LED1_YELLOW_ON);
                break;
            case R.id.btn3_mode_stop://停止 投入口停止，搅拌停止，风扇停止，加热停止，除湿停止
                if (oldMode == Constant.DEVICE_MODE_STOP) {
                    return;
                }
                oldMode = Constant.DEVICE_MODE_STOP;
                viewBinding.btnControlOptions.setEnabled(true);
                MyApplication.adminParameterBean.setDeviceMode(Constant.DEVICE_MODE_STOP);
                setButtonUnSelect(viewBinding.btn1ModeAuto);
                setButtonUnSelect(viewBinding.btn2ModeManual);
                setButtonSelect(viewBinding.btn3ModeStop);

                //停止:投入口停止，搅拌停止，风扇停止，加热停止，除湿停止
                PortControlUtil.getInstance().stopAllMotor(0);
                HTTPServerUtil.sendHeartBeat2();
                break;
            case R.id.btn4_mode_alarm://报警
                //报警不可手动点亮，仅发生问题时，进入此界面，此灯为亮，可手动点灭，报警接触
                if (!PortControlUtil.troubleTypeBean.getIsTrouble()) {
                    return;
                }
                // 手动解除报警，设备模式为自动，重置最低温度忽略时间
                if (oldMode == Constant.DEVICE_MODE_AUTO) {
                    PortControlUtil.getInstance().resetTempIgnoreTime();
                }
                PortControlUtil.getInstance().resetTempIgnoreTime();
                setButtonUnSelect(viewBinding.btn4ModeAlarm);
                PortControlUtil.troubleTypeBean.setIsTrouble(false);
                PortControlUtil.troubleTypeBean.setTroubleStir(Constant.FALSE);
                PortControlUtil.troubleTypeBean.setTroubleOutlet(Constant.FALSE);
                PortControlUtil.troubleTypeBean.setTroubleUpCheck(Constant.FALSE);
                PortControlUtil.troubleTypeBean.setTroubleInlet(Constant.FALSE);
                PortControlUtil.troubleTypeBean.setTroubleHeaterMax(Constant.FALSE);
                PortControlUtil.troubleTypeBean.setTroubleHeaterMin(Constant.FALSE);
                PortControlUtil.troubleTypeBean.setTroubleWeigh(Constant.FALSE);
                PortControlUtil.troubleTypeBean.setTroubleHumidity(Constant.FALSE);
                PortControlUtil.troubleTypeBean.setTroublePA(Constant.FALSE);
                PortControlUtil.troubleTypeBean.setTroubleType(Constant.TROUBLE_TYPE_REMOVE);
                GreenDaoUtil.getInstance().insertTrouble(0);
                HTTPServerUtil.sendHeartBeat2();
                PortControlUtil.getInstance().sendCommands(PortConstants.LED1_ON_GREEN);
                break;

            case R.id.iv_common_left:
            case R.id.iv_common_mega:
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - 1500)) {
                    mHits = new long[5];
                    exitAPPDialog = DialogManage.getDialogConfirm(this, getString(R.string.confirm_exit_program), view1 -> {
                        exitAPPDialog.dismiss();
                        BarUtil.hideNavBar(AdminActivity.this, false);
                        new Handler().postDelayed(() -> ActivityUtil.getInstance().exitSystem(), Constant.second);
                    });
                    exitAPPDialog.show();
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    /**
     * 设置按钮为选中状态
     *
     * @param button 要设置的按钮
     */
    private void setButtonSelect(Button button) {
        button.setBackground(getDrawable(R.drawable.bg_button_15_blue));
        button.setTextColor(getResources().getColor(R.color.white));
    }

    /**
     * 设置按钮为未选中状态
     *
     * @param button 要设置的按钮
     */
    private void setButtonUnSelect(Button button) {
        button.setBackground(getDrawable(R.drawable.bg_button_15_white));
        button.setTextColor(getResources().getColor(R.color.green_304129));
    }


    /**
     * @param
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void haveTrouble(TroubleLog troubleLog) {
        if (troubleLog.getTroubleType() == 0) {
            setButtonUnSelect(viewBinding.btn4ModeAlarm);
        } else {
            setButtonSelect(viewBinding.btn4ModeAlarm);
        }
    }

    /**
     * @param
     */
   /* @Subscribe(threadMode = ThreadMode.MAIN)
    public void portStatus(PortStatus portStatus) {
        runOnUiThread(() -> viewBinding.tvOutletStatus.setText("排料口："+portStatus.getOutletStatus()));
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
