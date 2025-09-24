package com.hyeprion.foodrecyclingsystem.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.blankj.utilcode.util.LogUtils;
import com.hyeprion.foodrecyclingsystem.activity.LoginActivity;

/**
 * 监测系统开机，开机后打开程序
 */
public class MyMonitorBootReceiver extends BroadcastReceiver {
    private final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";

    /**
     * 接收广播消息后都会进入 onReceive 方法，然后要做的就是对相应的消息做出相应的处理
     *
     * @param context 表示广播接收器所运行的上下文
     * @param intent  表示广播接收器收到的Intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {

//        Toast.makeText(context, intent.getAction(), Toast.LENGTH_LONG).show();

        /**
         * 如果 系统 启动的消息，则启动 APP 主页活动
         */
        String action=intent.getAction();
        if(action.equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED) || action.equalsIgnoreCase(Intent.ACTION_MEDIA_MOUNTED )) //Boot Completed
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LogUtils.e("MyMonitorBootReceiver"+"系统开机");
                    Intent intentMainActivity = new Intent(context, LoginActivity.class);
                    intentMainActivity.putExtra("first",true);
                    intentMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intentMainActivity);
                }
            },100);

//            Toast.makeText(context, "开机完毕~", Toast.LENGTH_LONG).show();
        }
    }
}