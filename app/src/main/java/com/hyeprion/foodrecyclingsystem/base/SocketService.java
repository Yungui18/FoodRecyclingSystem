package com.hyeprion.foodrecyclingsystem.base;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

import com.hyeprion.foodrecyclingsystem.util.Constant;
import com.hyeprion.foodrecyclingsystem.util.HTTPServerUtil;


/**
 * Created by ljj on 2019/8/26.
 * Describe:
 */
public class SocketService extends Service {
    private HandlerThread handlerThread;
    private Handler handler;
    private Runnable runnable;
    public class MyBinder extends Binder {

        public SocketService getService() {
            return SocketService.this;
        }
    }

    //通过binder实现调用者client与Service之间的通信
    private SocketService.MyBinder binder = new SocketService.MyBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        handlerThread = new HandlerThread("SocketService");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        handler.removeCallbacks(runnable);
        status();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        handler.removeCallbacksAndMessages(null);
        return super.onUnbind(intent);
    }

    /**
     * 每隔1分钟发送一次设备状态
     */
    private void status() {
        // 先清除原来的，防止出现多次开始
        handler.removeCallbacksAndMessages(null);
        runnable = new Runnable() {
            @Override
            public void run() {
                // 在这里做轮询的事情
                // TODO: 2022/11/17 发送消息给串口，获取当前各状态
//                HTTPServerUtil.sendHeartBeat();
                HTTPServerUtil.sendHeartBeat2();
                // 60秒后下一次轮询开始
                handler.postDelayed(this, 60 * Constant.second);
            }
        };
        handler.postDelayed(runnable, 10 * Constant.second);
    }

    @Override
    public void onDestroy() {
        handlerThread.quitSafely();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }
}
