package com.hyeprion.foodrecyclingsystem.util;


import android.content.Context;
import android.content.Intent;

import com.ys.rkapi.MyManager;

import org.greenrobot.greendao.annotation.NotNull;

public class BarUtil {
    /**
     * @param context
     * @param hide    true：隐藏  false:显示
     */
    public static final void hideNavBar(@NotNull Context context, boolean hide) {
        // 亿晟 平板
        MyManager manager = MyManager.getInstance(context);
        manager.setSlideShowNavBar(!hide);
        manager.setSlideShowNotificationBar(!hide);
        manager.hideNavBar(hide);

        //
        Intent intent = new Intent("com.xbh.action.HIDE_STATUS_BAR");
        intent.putExtra("hide", hide);//true则隐藏，false则显示
        context.sendBroadcast(intent);
    }


}
