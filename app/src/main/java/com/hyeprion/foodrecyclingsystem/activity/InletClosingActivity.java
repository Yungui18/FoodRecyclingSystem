package com.hyeprion.foodrecyclingsystem.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;

import com.hyeprion.foodrecyclingsystem.base.BaseActivity;
import com.hyeprion.foodrecyclingsystem.base.MyApplication;
import com.hyeprion.foodrecyclingsystem.databinding.ActivityInletClosing2Binding;
import com.hyeprion.foodrecyclingsystem.util.Constant;
import com.hyeprion.foodrecyclingsystem.util.HTTPServerUtil;
import com.hyeprion.foodrecyclingsystem.util.port.PortConstants;
import com.hyeprion.foodrecyclingsystem.util.port.PortControlUtil;

/**
 * apartment- must / shop-option
 * 状态页面 放置口关闭中
 * from {@link InletControlActivity}
 */
public class InletClosingActivity extends BaseActivity<ActivityInletClosing2Binding> implements View.OnClickListener {

    @Override
    protected void initView() {

        myHandler = new Handler();
        //每隔1s获取投入口的状态
        myRunnable = new Runnable() {
            @Override
            public void run() {
                //关门完成，进入称重页面
                if (PortControlUtil.getInstance().getPortStatus().getInletStatus() == 5) {
                    HTTPServerUtil.sendHeartBeat2();
                    // 绿灯常亮
                    PortControlUtil.getInstance().sendCommands(PortConstants.LED1_ON_GREEN);
                    // 无称重模式
                    if (MyApplication.adminParameterBean.getWeighingMode().equals(Constant.NONE)) {
                        InletClosingActivity.this.finish();
                        return;
                    }

//                    // 关门时重量 - 开门前重量 = 本次投入量
//                    float nowWeight = (PortControlUtil.getInstance().getPortStatus().getNetWeight() -
//                            getIntent().getFloatExtra("inletBeforeWeight", 0));
//                    nowWeight = DecimalFormatUtil.DecimalFormatTwo(nowWeight);
//                    // 每次投入量的累积
//                    MyApplication.adminParameterBean.setInletLimitedTotalAccumulation(
//                            (Float.parseFloat(MyApplication.adminParameterBean.getInletLimitedTotalAccumulation())+nowWeight)+"");
//                    SharedPreFerUtil.saveObj(Constant.ADMIN_PARAMETER_FILENAME,Constant.ADMIN_PARAMETER_KEY,
//                            JSON.toJSONString(MyApplication.adminParameterBean));
//
//                    GreenDaoUtil.getInstance().insertTimesInlet(nowWeight, MyApplication.loginType);

                    Intent intent = new Intent(InletClosingActivity.this, WeighingApartmentActivity.class);
                    intent.putExtra("inletBeforeWeight", getIntent().getFloatExtra("inletBeforeWeight", 0));
                    startActivity(intent);
                    InletClosingActivity.this.finish();
                    return;
                }
                //要做的事情，这里再次调用此Runnable对象，以实现每秒实现一次的定时器操作
                myHandler.postDelayed(this, Constant.second);

            }
        };
        //3，使用PostDelayed方法，调用此Runnable对象
        myHandler.postDelayed(myRunnable, Constant.second);
        // 投料口开关时,黄灯闪烁
        PortControlUtil.getInstance().sendCommands(PortConstants.LED1_TOGGLE_YELLOW);
        PortControlUtil.getInstance().sendCommands(PortConstants.INLET_LOCK);
        PortControlUtil.getInstance().sendCommands(PortConstants.INLET_CLOSE);
    }

    protected void initListener() {
    }

    @Override
    public void onClick(View view) {

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
