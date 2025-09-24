package com.hyeprion.foodrecyclingsystem.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.hyeprion.foodrecyclingsystem.base.BaseActivity;
import com.hyeprion.foodrecyclingsystem.base.MyApplication;
import com.hyeprion.foodrecyclingsystem.databinding.ActivityInletOpeningBinding;
import com.hyeprion.foodrecyclingsystem.util.Constant;
import com.hyeprion.foodrecyclingsystem.util.SaveToSdUtil;
import com.hyeprion.foodrecyclingsystem.util.port.PortConstants;
import com.hyeprion.foodrecyclingsystem.util.port.PortControlUtil;

import androidx.annotation.Nullable;

/**
 * apartment- must / shop-option
 * 状态页面 放置口开启中
 * from {@link InletControlActivity}
 */
public class InletOpeningActivity extends BaseActivity<ActivityInletOpeningBinding> implements View.OnClickListener {
    private boolean first = true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 停止搅拌电机命令
        PortControlUtil.getInstance().sendCommands(PortConstants.STIR_STOP);
        PortControlUtil.getInstance().sendCommands(PortConstants.FAN1_STOP);
        PortControlUtil.getInstance().sendCommands(PortConstants.DEHUMIDIFICATION_STOP);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtils.e("InletOpeningActivity feeding before weight:" + PortControlUtil.getInstance().getPortStatus().getChooseUseWeighing());
                SaveToSdUtil.savePortDataToSD(  "InletOpeningActivity feeding before weight:" + PortControlUtil.getInstance().getPortStatus().getChooseUseWeighing(), 2);
//                EventBus.getDefault().post(new WeightBeforeOpenEvent(PortControlUtil.getInstance().getPortStatus().getNetWeight()));
                // 投料口开关时,黄灯闪烁
                PortControlUtil.getInstance().sendCommands(PortConstants.LED1_TOGGLE_YELLOW);
                PortControlUtil.getInstance().sendCommands(PortConstants.INLET_UNLOCK);
            }
        },Integer.parseInt(MyApplication.adminParameterBean.getFeedingBeforeWeightTime())*Constant.second);


        myHandler = new Handler();
        //每隔1s获取投入口的状态
        myRunnable = new Runnable() {
            @Override
            public void run() {
//                LogUtils.e("getInletStatus："+PortControlUtil.getInstance().getPortStatus().getInletStatus());
                //开门完成，返回门控界面
                if (PortControlUtil.getInstance().getPortStatus().getInletStatus() == 2/*||
                        PortControlUtil.getInstance().getPortStatus().getInletStatus() == 7*/) {
                    // 绿灯常亮
                    PortControlUtil.getInstance().sendCommands(PortConstants.LED1_ON_GREEN);
                   InletOpeningActivity.this.finish();
                    return;
                }
                //要做的事情，这里再次调用此Runnable对象，以实现每秒实现一次的定时器操作
                myHandler.postDelayed(this, Constant.second);

            }
        };
        //3，使用PostDelayed方法，调用此Runnable对象
        myHandler.postDelayed(myRunnable, Constant.second);




    }

    @Override
    protected void initView() {

    }

    protected void initListener() {
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (myHandler != null){
            myHandler.removeCallbacks(myRunnable);
            myHandler.removeCallbacksAndMessages(null);
        }

    }
}
