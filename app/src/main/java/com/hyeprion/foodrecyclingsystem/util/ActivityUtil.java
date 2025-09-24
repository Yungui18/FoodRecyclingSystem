package com.hyeprion.foodrecyclingsystem.util;

import android.app.Activity;
import android.content.Intent;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.hyeprion.foodrecyclingsystem.base.SocketService;

import java.util.ArrayList;
import java.util.List;

public class ActivityUtil {
    private List<Activity> activityList = new ArrayList<>();
    private static ActivityUtil instance;

    // 单例模式中获取唯一的ExitApplication实例
    public static synchronized ActivityUtil getInstance() {
        if (null == instance) {
            instance = new ActivityUtil();
        }
        return instance;
    }

    // 添加Activity到容器中
    public void addActivity(Activity activity) {
        if (activityList == null)
            activityList = new ArrayList<>();
        activityList.add(activity);
        LogUtils.i(activityList.toString());
    }

    public Activity getActivity(){
        if (activityList.size() >0){
            return activityList.get(activityList.size()-1);
        }
       return null;
    }
    public List<Activity> getActivityList(){
        if (activityList.size() > 0){
            return activityList;
        }
        return null;
    }

    /**
     * SystemCheckActivity 是否运行
     * @return true 运行
     */
    public boolean haveSystemCheckActivity(){
        boolean have = false;
        if (activityList.size() == 0) {
            return have;
        }
        for (Activity activity : activityList) {
            if (activity.getClass().getSimpleName().equals("SystemCheckActivity")) {
                have = true;
            }
        }
        return have;
    }

    // 移除Activity
    public void removeActivity(Activity activity) {
        if (activityList != null)
            activityList.remove(activity);
    }

    // 遍历所有Activity并finish
    public void exitSystem() {
        BarUtil.hideNavBar(ActivityUtils.getTopActivity(), false);
        Intent intent = new Intent(ActivityUtils.getTopActivity(), SocketService.class);
        ActivityUtils.getTopActivity().stopService(intent);
        for (Activity activity : activityList) {
            if (activity != null)
                activity.finish();
        }
        // 退出进程
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
