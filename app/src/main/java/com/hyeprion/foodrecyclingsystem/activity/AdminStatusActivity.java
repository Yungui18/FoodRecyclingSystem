package com.hyeprion.foodrecyclingsystem.activity;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.hyeprion.foodrecyclingsystem.R;
import com.hyeprion.foodrecyclingsystem.base.BaseActivity;
import com.hyeprion.foodrecyclingsystem.base.MyApplication;
import com.hyeprion.foodrecyclingsystem.databinding.ActivityAdminStatus2Binding;
import com.hyeprion.foodrecyclingsystem.util.Constant;
import com.hyeprion.foodrecyclingsystem.util.GreenDaoUtil;
import com.hyeprion.foodrecyclingsystem.util.port.PortConstants;
import com.hyeprion.foodrecyclingsystem.util.port.PortControlUtil;

import java.util.Objects;

/**
 * 管理员页面-状态确认页面
 * from {@link AdminActivity}
 */
public class AdminStatusActivity extends BaseActivity<ActivityAdminStatus2Binding> {
    private Handler myHandler;
    protected Runnable myRunnable;

    @Override
    protected void initView() {
        // TODO: 2023/1/17   设置各个状态
        myHandler = new Handler(Objects.requireNonNull(Looper.myLooper()));
        setStatus();
        refresh();
    }

    private void setStatus() {
        viewBinding.tv1PowerSource.setText(R.string.on);
        viewBinding.tv2RunMode.setText(MyApplication.adminParameterBean.getDeviceMode() == Constant.DEVICE_MODE_AUTO ? R.string.automatic : R.string.manual);

        if (PortControlUtil.getInstance().getPortStatus().getStirStatus() == 1 ||
                PortControlUtil.getInstance().getPortStatus().getStirStatus() == 2) {
            viewBinding.tv3StirMotor.setText(R.string.on);
        } else {
            viewBinding.tv3StirMotor.setText(R.string.off);
        }

        // 5 搅拌电机异常
        if (PortControlUtil.getInstance().getPortStatus().getStirStatus() == 5) {
            viewBinding.tv4StirMotorError.setText(R.string.error);
        } else {
            viewBinding.tv4StirMotorError.setText(R.string.normal);
        }

        // 空气温度
        viewBinding.tv5AirTemperature.setText(PortControlUtil.getInstance().getPortStatus().getTemperature() + "℃");

        // 加热1：1手动 11自动（运行中）
        if (PortControlUtil.getInstance().getPortStatus().getHeater1() == 1
                || PortControlUtil.getInstance().getPortStatus().getHeater1() == 11) {
            viewBinding.tv6Heater1.setText(R.string.on);
        } else {
            viewBinding.tv6Heater1.setText(R.string.off);
        }

        // 加热1 温度
        viewBinding.tv7Heater1Temperature.setText(PortControlUtil.getInstance().getPortStatus().getHeaterTemperature1() + "℃");

        // 加热2：1手动 11自动（运行中）
        if (PortControlUtil.getInstance().getPortStatus().getHeater2() == 1
                || PortControlUtil.getInstance().getPortStatus().getHeater2() == 11) {
            viewBinding.tv8Heater2.setText(R.string.on);
        } else {
            viewBinding.tv8Heater2.setText(R.string.off);
        }
        // 加热2 温度
        viewBinding.tv9Heater2Temperature.setText(PortControlUtil.getInstance().getPortStatus().getHeaterTemperature2() + "℃");

        // 搅拌轴 状态 搅拌电机 ！= 6 normal   =6 error
        if (PortControlUtil.getInstance().getPortStatus().getStirStatus() != 6) {
            viewBinding.tv10MotorShaftStatus.setText(R.string.normal);
        } else {
            viewBinding.tv10MotorShaftStatus.setText(R.string.error);
        }

        // 空气温度
        viewBinding.tv11AirHumidity.setText(PortControlUtil.getInstance().getPortStatus().getHumidity() + "%");

        // 照明 1亮  2关
        if (PortControlUtil.getInstance().getPortStatus().getLighting() == 1 ||
                PortControlUtil.getInstance().getPortStatus().getLighting() == 11) {
            viewBinding.tv12Light.setText(R.string.on);
        } else if (PortControlUtil.getInstance().getPortStatus().getLighting() == 2 ||
                PortControlUtil.getInstance().getPortStatus().getLighting() == 12 ||
                PortControlUtil.getInstance().getPortStatus().getLighting() == 0) {
            viewBinding.tv12Light.setText(R.string.off);
        }

        // 开门传感器触发04；关门传感器被触发02；减速传感器被触发01；关门传感器和减速传感器同时触发03
        if (PortControlUtil.getInstance().getPortStatus().getInletSensorStatus() == 1) {
            viewBinding.tv13InletSensor.setText(getString(R.string.off) + "," + getString(R.string.off) + "," + getString(R.string.on));
        } else if (PortControlUtil.getInstance().getPortStatus().getInletSensorStatus() == 2) {
            viewBinding.tv13InletSensor.setText(getString(R.string.off) + "," + getString(R.string.on) + "," + getString(R.string.off));
        } else if (PortControlUtil.getInstance().getPortStatus().getInletSensorStatus() == 3) {
            viewBinding.tv13InletSensor.setText(getString(R.string.off) + "," + getString(R.string.on) + "," + getString(R.string.on));
        } else if (PortControlUtil.getInstance().getPortStatus().getInletSensorStatus() == 4) {
            viewBinding.tv13InletSensor.setText(getString(R.string.on) + "," + getString(R.string.off) + "," + getString(R.string.off));
        } else {
            viewBinding.tv13InletSensor.setText(getString(R.string.off) + "," + getString(R.string.off) + "," + getString(R.string.off));
        }

        // 投入口按钮 0未触发  1触发
        if (PortControlUtil.getInstance().getPortStatus().getOpenDoorBtn() == 0) {
            viewBinding.tv14InletButton.setText(R.string.off);
        } else if (PortControlUtil.getInstance().getPortStatus().getOpenDoorBtn() == 1) {
            viewBinding.tv14InletButton.setText(R.string.on);
        }

        // 观察口状态 0未关闭  1关闭
        if (PortControlUtil.getInstance().getPortStatus().getObserveDoorStatus() == 0) {
            viewBinding.tv15Measuring.setText(R.string.off);
        } else if (PortControlUtil.getInstance().getPortStatus().getObserveDoorStatus() == 1) {
            viewBinding.tv15Measuring.setText(R.string.on);
        }

        // 排出口状态 0未关闭  1关闭
        if (PortControlUtil.getInstance().getPortStatus().getOutletStatus() == 0) {
            viewBinding.tv16Outlet.setText(R.string.off);
        } else if (PortControlUtil.getInstance().getPortStatus().getOutletStatus() == 1) {
            viewBinding.tv16Outlet.setText(R.string.on);
        }

        // 风扇 01：手动（1档）；02：手动（2档）；03：手动（3档）；00：停止；
        // 11：自动（1档)；12：自动（2档)；13：自动（3档)；10：自动（停止中）；05：异常
        // 风扇电路板接的除湿（烘干机），所以这里判断除湿的状态：01：手动；02：停止；11：自动（运行中)；10：自动（停止中）；05：异常
        if (PortControlUtil.getInstance().getPortStatus().getDehumidifier() == 2 ||
                PortControlUtil.getInstance().getPortStatus().getDehumidifier() == 10) {
            viewBinding.tv17Fan.setText(R.string.off);
        } else {
            viewBinding.tv17Fan.setText(R.string.on);
        }/*else *//*if (PortControlUtil.getInstance().getPortStatus().getFan1() == 1 ||
                PortControlUtil.getInstance().getPortStatus().getFan1() == 11) {
            viewBinding.tv17Fan.setText("1");
        } else if (PortControlUtil.getInstance().getPortStatus().getFan1() == 2 ||
                PortControlUtil.getInstance().getPortStatus().getFan1() == 12) {
            viewBinding.tv17Fan.setText("2");
        } else if (PortControlUtil.getInstance().getPortStatus().getFan1() == 3 ||
                PortControlUtil.getInstance().getPortStatus().getFan1() == 13) {
            viewBinding.tv17Fan.setText("3");
        }*/


        //  LED1   01：常亮；02：常灭；03：闪烁；04：呼吸
        if (PortControlUtil.getInstance().getPortStatus().getLed1RGB() == PortConstants.COLOR_GREEN) {
            viewBinding.tv18Led1.setText(R.string.G);
        } else if (PortControlUtil.getInstance().getPortStatus().getLed1RGB() == PortConstants.COLOR_YELLOW) {
            viewBinding.tv18Led1.setText(R.string.Y);
        } else if (PortControlUtil.getInstance().getPortStatus().getLed1RGB() == PortConstants.COLOR_RED) {
            viewBinding.tv18Led1.setText(R.string.R);
        }/*if (PortControlUtil.getInstance().getPortStatus().getLed1() == 2)*/ else {
            viewBinding.tv18Led1.setText(R.string.off);
        }

        //  LED2  01：常亮；02：常灭；03：闪烁；04：呼吸
        if (PortControlUtil.getInstance().getPortStatus().getLed2RGB() == PortConstants.COLOR_GREEN) {
            viewBinding.tv19Led2.setText(R.string.G);
        } else if (PortControlUtil.getInstance().getPortStatus().getLed2RGB() == PortConstants.COLOR_YELLOW) {
            viewBinding.tv19Led2.setText(R.string.Y);
        } else if (PortControlUtil.getInstance().getPortStatus().getLed2RGB() == PortConstants.COLOR_RED) {
            viewBinding.tv19Led2.setText(R.string.R);
        }/*if (PortControlUtil.getInstance().getPortStatus().getLed2() == 2) */ else {
            viewBinding.tv19Led2.setText(R.string.off);
        }

        // 风压
//        viewBinding.tv20WindPresssure.setText(PortControlUtil.getInstance().getPortStatus().getWindPressure() + "");
        // 错误
        viewBinding.tv21Cuowu.setText((PortControlUtil.troubleTypeBean.isTrouble() ? PortControlUtil.troubleTypeBean.getTroubleType() : 000) + "");
        // 当日总投入
        viewBinding.tv22DailyTotalInlet.setText(GreenDaoUtil.getInstance().getTodayInletWeight() + MyApplication.weighingUnit);

        viewBinding.tv23NowWeight.setText(PortControlUtil.getInstance().getPortStatus().getChooseUseWeighing() + MyApplication.weighingUnit);

        viewBinding.tv24LockStatus.setText(PortControlUtil.getInstance().getPortStatus().
                getLock1() == 1 ? R.string.lock_unlock : R.string.lock_lock);

    }

    @Override
    protected void initListener() {
        viewBinding.btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
        }
    }


    private void refresh() {
        //每隔2s获取所有状态
        myRunnable = new Runnable() {
            @Override
            public void run() {
                setStatus();
                //要做的事情，这里再次调用此Runnable对象，以实现每秒实现一次的定时器操作
//                myHandler.postDelayed(this, Constant.second);
                myHandler.postDelayed(this, 200);
            }
        };
        //3，使用PostDelayed方法，调用此Runnable对象
//        myHandler.postDelayed(myRunnable, Constant.second);
        myHandler.postDelayed(myRunnable, 200);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myHandler != null) {
            myHandler.removeCallbacks(myRunnable);
            myHandler.removeCallbacksAndMessages(null);
        }
    }
}
