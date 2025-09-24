package com.hyeprion.foodrecyclingsystem.base;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.hyeprion.foodrecyclingsystem.dialog.DialogManage;
import com.hyeprion.foodrecyclingsystem.util.ActivityUtil;
import com.hyeprion.foodrecyclingsystem.util.SaveToSdUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

public abstract class BaseActivity<T extends ViewBinding>  extends AppCompatActivity implements View.OnClickListener {
    protected T viewBinding;
    protected Activity mActivity;
    protected Context mContext;
    private int sum;
    protected Handler baseHandler;

    //1，首先创建一个Handler对象
    protected Handler myHandler ;
    protected Runnable myRunnable;

    protected int netRetryTimes = 0; //网络重试次数

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Type superclass = getClass().getGenericSuperclass();
        Class<?> aClass = (Class<?>) ((ParameterizedType) superclass).getActualTypeArguments()[0];
        try {
            Method method = aClass.getDeclaredMethod("inflate", LayoutInflater.class);
            viewBinding = (T) method.invoke(null, getLayoutInflater());
            setContentView(viewBinding.getRoot());
        } catch (NoSuchMethodException | IllegalAccessException| InvocationTargetException e) {
            e.printStackTrace();
        }

        netRetryTimes = 0;
        mActivity = this;
        mContext = this;
        MyApplication.showingActivity = this;
        ActivityUtil.getInstance().addActivity(this);
        baseHandler = new Handler(Objects.requireNonNull(Looper.myLooper()));
        LogUtils.e("当前Activity：" + mActivity.getClass().getSimpleName(), true);
        //初始化控件
        initView();
        initListener();
    }

    /**
     * 初始化控件
     */
    protected abstract void initView();
    /**
     * 初始化点击事件
     */
    protected abstract void initListener();

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        return;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int num = event.getActionIndex();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                sum = 1;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerId(num) == 1) {
                    LogUtils.i("2个手指按下" + sum);
                    sum = 2;
                    return false;
                } else if (event.getPointerId(num) == 3) {
                    sum = 4;
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (sum == 1) {
                    sum = 0;
                    return true;
                } else if (sum == 2) {
                    sum = 0;
                } else if (sum == 4) {
                    sum = 0;

                }
                break;
            default:
                break;

        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        PackageManager packageManager = this.getApplication().getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(this.getPackageName());
        ComponentName launchComponentName = intent.getComponent();
        ComponentName componentName = this.getComponentName();
        if (componentName.toString().equals(launchComponentName.toString())) {
            this.finish();
        } else {
           ActivityUtil.getInstance().exitSystem();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.showingActivity = this;
        SaveToSdUtil.savePortDataToSD(  "当前Activity：" + mActivity.getClass().getSimpleName(), 2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityUtil.getInstance().removeActivity(this);
        if (viewBinding != null){
            viewBinding = null;
        }

        if (DialogManage.dialog != null && DialogManage.dialog.isShowing()) {
            DialogManage.dialog.dismiss();
        }
    }
}




