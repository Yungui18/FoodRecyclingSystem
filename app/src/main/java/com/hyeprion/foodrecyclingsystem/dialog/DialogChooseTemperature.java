package com.hyeprion.foodrecyclingsystem.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.gzuliyujiang.wheelpicker.contract.OnNumberSelectedListener;
import com.hyeprion.foodrecyclingsystem.R;
import com.hyeprion.foodrecyclingsystem.databinding.DialogTemperatureChooseBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class DialogChooseTemperature extends DialogFragment {
    private Activity mContext;
    private DialogTemperatureChooseBinding dialogTemChooseBinding;
    private String temperature;
    private String nowNum;
    private String anotherTemp;
    private String limited;
    private int type;

    private CallBackListener mFragmentListener;


    public interface CallBackListener {
        //回调方法
        void returnFragmentData(String message);
    }

    public DialogChooseTemperature() {
    }

    /**
     * @param context
     * @param temperature 点击控件的温度
     * @param anotherTemp 另一端温度
     * @param type        1：最小~(anotherTemp-1)  2:(anotherTemp+1)~最大
     */
    public DialogChooseTemperature(Activity context, String temperature, String anotherTemp, int type) {
        this.mContext = context;
        this.temperature = temperature;
        this.anotherTemp = anotherTemp;
        this.type = type;
        mFragmentListener = (CallBackListener) context;
    }

//    /**
//     * @param context
//     * @param limited 限重
//     * @param type       3 重量选择
//     */
//    public DialogChooseTemperature(Activity context, String limited, int type) {
//        this.mContext = context;
//        this.temperature = limited;
//        this.type = type;
//        mFragmentListener = (CallBackListener) context;
//    }

    /**
     * @param context
     * @param nowNum  现在数值
     * @param type    类型
     */
    public DialogChooseTemperature(Activity context, String nowNum, int type) {
        this.mContext = context;
        this.nowNum = nowNum;
        this.type = type;
        mFragmentListener = (CallBackListener) context;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogFullScreen); //dialog全屏
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dialogTemChooseBinding = DialogTemperatureChooseBinding.inflate(getLayoutInflater());
        dialogTemChooseBinding.btnCancel.setOnClickListener(view -> dismiss());
        dialogTemChooseBinding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                mFragmentListener.returnFragmentData(nowNum);
            }
        });
        initView();
        return dialogTemChooseBinding.getRoot();
    }


   /* private void initView() {
        if (type == 1) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(50, Integer.parseInt(anotherTemp) - 1, 5);
        } else if (type == 2) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(Integer.parseInt(anotherTemp) + 1, 200, 5);
        }else if (type ==3){
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(0, 1000, 10);
        }else if (type ==4){
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(Integer.parseInt(anotherTemp)+1, 100, 1);
        }else if (type ==5){
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(0, Integer.parseInt(anotherTemp) - 1, 1);
        }
        dialogTemChooseBinding.wheelPickerNumberWheel.setDefaultValue(temperature);
        dialogTemChooseBinding.wheelPickerNumberWheel.setOnNumberSelectedListener(new OnNumberSelectedListener() {
            @Override
            public void onNumberSelected(int position, Number item) {
                temperature = item.toString();
            }
        });
    }*/

    private void initView() {
        if (type == 1) { // 目标温度设置 1 前
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(20, 85, 1);
        } else if (type == 2) { // 浮动温度设置 1 前
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(1, 20, 1);
        }  else if (type == 5) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(0, 100, 1);
        } else if (type == 6) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(1, 20, 1);
        } else if (type == 7) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(5, 30, 5);
        } else if (type == 8) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(1,30,1);
        } else if (type == 9) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(1,20,1);
        } else if (type == 10) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(1, 5, 1);
        } else if (type == 11) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(1, 3, 1);
        } else if (type == 12) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(0, 100, 1);
        } else if (type == 13) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(3, 24, 1);
        }else if (type == 14) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(0, 200, 5);
        }else if (type == 15) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(20, 1000, 10);
        }else if (type == 16) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(0, 60, 1);
        }else if (type == 17) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(1, 60, 1);
        }else if (type == 18) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(0, 60, 1);
        }else if (type == 19) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(1, 60, 1);
        }else if (type == 20) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(20, 85, 1);
        }else if (type == 21) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(1, 20, 1);
        }else if (type == 22) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(1, 48, 1);
        }else if (type == 23) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(1, 10, 1);
        }else if (type == 24) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(1, 10, 1);
        }else if (type == 3) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(45, 85, 1);
        }else if (type == 25) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(10, 85, 1);
        }else if (type == 26) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(20, 120, 20);
        } else if (type == 4) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(50,1000,10);
        } else if (type == 27) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(50,500,10);
        } else if (type == 28) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(0,120,10);
        }else if (type == 29) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(0,120,10);
        }else if (type == 30) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(0,120,10);
        }else if (type == 31) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(1, 5, 1);
        }else if (type == 32) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(1, 5, 1);
        }else if (type == 33) {
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(100,6000,100);
        }else if (type == 34) { // 重量标定
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(1,255,1);
        }else if (type == 35) { // 目标温度设置 2 后
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(20, 85, 1);
        }else if (type == 36) { // 浮动温度设置 2 后
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(1, 20, 1);
        }else if (type == 37) { // 节电模式目标温度设置 2 后
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(20, 85, 1);
        }else if (type == 38) { // 节电模式浮动温度设置 2 后
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(1, 20, 1);
        }else if (type == 39) { // 湿度报警时间 单位 min
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(10, 360, 5);
        }else if (type == 40) { // 湿度报警对应的湿度 单位 %
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(10, 100, 1);
        }else if (type == 41) { // 搅拌反转 运行时间 normal
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(0, 60, 1);
        }else if (type == 42) { //  搅拌反转 间隔时间 normal
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(1, 60, 1);
        }else if (type == 43) { // 搅拌反转 运行时间 PowerSaving
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(0, 60, 1);
        }else if (type == 44) { //  搅拌反转 间隔时间 PowerSaving
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(1, 60, 1);
        }else if (type == 45) { // 杀菌模式 温度
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(60, 85, 1);
        }else if (type == 46) { // 杀菌模式 时间
            dialogTemChooseBinding.wheelPickerNumberWheel.setRange(30, 180, 10);
        }
        dialogTemChooseBinding.wheelPickerNumberWheel.setDefaultValue(nowNum);
        dialogTemChooseBinding.wheelPickerNumberWheel.setOnNumberSelectedListener(new OnNumberSelectedListener() {
            @Override
            public void onNumberSelected(int position, Number item) {
                nowNum = item.toString();
            }
        });
    }


    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        super.show(manager, tag);
        DialogFragment dialogFragment = (DialogFragment) manager.findFragmentByTag(tag);
        if (dialogFragment != null && dialogFragment.isAdded() && dialogFragment.getShowsDialog()) {
            return;
        } else {
            //在每个add事务前增加一个remove事务，防止连续的add
            manager.beginTransaction().remove(this).commitAllowingStateLoss();
            FragmentTransaction ft = manager.beginTransaction();
            //在每个add事务前增加一个remove事务，防止连续的add
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }


}
