package com.hyeprion.foodrecyclingsystem.activity;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.hyeprion.foodrecyclingsystem.R;
import com.hyeprion.foodrecyclingsystem.adapter.InletLogAdapter;
import com.hyeprion.foodrecyclingsystem.base.BaseActivity;
import com.hyeprion.foodrecyclingsystem.base.MyApplication;
import com.hyeprion.foodrecyclingsystem.bean.TimesInlet;
import com.hyeprion.foodrecyclingsystem.databinding.ActivityAdminInletLog2Binding;
import com.hyeprion.foodrecyclingsystem.util.GreenDaoUtil;
import com.hyeprion.foodrecyclingsystem.util.MyLayoutManager;
import com.hyeprion.foodrecyclingsystem.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * 投入记录
 */
public class AdminInletLogActivity extends BaseActivity<ActivityAdminInletLog2Binding> {
    private List<TimesInlet> timesInlets24H = new ArrayList<>();
    private InletLogAdapter inletLogAdapter;
    private int flag = 30;

    @Override
    protected void initView() {
//
//        for(int i =0;i<300;i++){
//            TimesInlet timesInlet = new TimesInlet();
//            timesInlet.setLoginType("password");
//            timesInlet.setTime("2024-2-5 12:12:12");
//            timesInlet.setInletId("14523654");
//            timesInlet.setTimesInlet(1+i);
//            timesInlets24H.add(timesInlet);
//        }
        timesInlets24H = GreenDaoUtil.getInstance().getTimesInletsByDays(30);
        setButtonSelect(viewBinding.btnInlet30day);
        setButtonUnSelect(viewBinding.btnInlet60day);
        setButtonUnSelect(viewBinding.btnInlet90day);
       /* viewBinding.tvDailyInletWeight.setText(GreenDaoUtil.getInstance().getTodayInletWeight() + "KG");
        viewBinding.tv24hInletTotalWeight.setText(GreenDaoUtil.getInstance().get24HTotalInletWeight() + "KG");
        if (MyApplication.adminParameterBean.getInletLimited24H().equals("0")) {
            viewBinding.tv24hInletRemainingWeight.setText(R.string.no_limited);
        } else {
            float remainingWeight = (Float.parseFloat(MyApplication.adminParameterBean.getInletLimited24H()) -
                    Float.parseFloat(GreenDaoUtil.getInstance().get24HTotalInletWeight()));
            viewBinding.tv24hInletRemainingWeight.setText((remainingWeight > 0 ? remainingWeight : 0) + "KG");
        }*/
        initRL();
    }

    @Override
    protected void initListener() {
        viewBinding.btnBack.setOnClickListener(this);
        viewBinding.btnInlet30day.setOnClickListener(this);
        viewBinding.btnInlet60day.setOnClickListener(this);
        viewBinding.btnInlet90day.setOnClickListener(this);
        viewBinding.btnInletDelete.setOnClickListener(this);
//        viewBinding.include.ivCommonMega.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                this.finish();
                break;


            case R.id.btn_inlet_30day:
                if (flag == 30) {
                    return;
                }
                flag = 30;
                timesInlets24H = GreenDaoUtil.getInstance().getTimesInletsByDays(30);
                inletLogAdapter.setList(timesInlets24H);

                setButtonSelect(viewBinding.btnInlet30day);
                setButtonUnSelect(viewBinding.btnInlet60day);
                setButtonUnSelect(viewBinding.btnInlet90day);
                break;
            case R.id.btn_inlet_60day:
                if (flag == 60) {
                    return;
                }
                flag = 60;
                timesInlets24H = GreenDaoUtil.getInstance().getTimesInletsByDays(60);
                inletLogAdapter.setList(timesInlets24H);

                setButtonSelect(viewBinding.btnInlet60day);
                setButtonUnSelect(viewBinding.btnInlet30day);
                setButtonUnSelect(viewBinding.btnInlet90day);
                break;
            case R.id.btn_inlet_90day:
                if (flag == 90) {
                    return;
                }
                flag = 90;
                timesInlets24H = GreenDaoUtil.getInstance().getTimesInletsByDays(90);
                inletLogAdapter.setList(timesInlets24H);

                setButtonSelect(viewBinding.btnInlet90day);
                setButtonUnSelect(viewBinding.btnInlet60day);
                setButtonUnSelect(viewBinding.btnInlet30day);
                break;
            case R.id.btn_inlet_delete:
                getDialogDeleteInletLog(this);
                break;
            case R.id.iv_common_mega:
                for (int i = 0; i < 3; i++) {
                    TimesInlet timesInlet = new TimesInlet();
                    timesInlet.setTimesInlet(1 + i);
                    timesInlet.setTime(TimeUtil.getDateSToString());
                    timesInlet.setInletId("123456");
                    timesInlet.setLoginType("password");
                    timesInlet.setTimeStamp(TimeUtil.getDate());
                    MyApplication.getInstance().getDaoSession().getTimesInletDao().insert(timesInlet);
                }

                for (int i = 0; i < 3; i++) {
                    TimesInlet timesInlet = new TimesInlet();
                    timesInlet.setTimesInlet(1 + i);
                    timesInlet.setTime("2024-04-12 13:12:16");
                    timesInlet.setInletId("123456");
                    timesInlet.setLoginType("password");
                    timesInlet.setTimeStamp(1712898736000L);
                    MyApplication.getInstance().getDaoSession().getTimesInletDao().insert(timesInlet);
                }

                for (int i = 0; i < 3; i++) {
                    TimesInlet timesInlet = new TimesInlet();
                    timesInlet.setTimesInlet(1 + i);
                    timesInlet.setTime("2024-03-04 13:12:16");
                    timesInlet.setInletId("123456");
                    timesInlet.setLoginType("password");
                    timesInlet.setTimeStamp(1709529136000L);
                    MyApplication.getInstance().getDaoSession().getTimesInletDao().insert(timesInlet);
                }
                break;
        }
    }

    /**
     * 初始化recycleView
     */
    private void initRL() {
        inletLogAdapter = new InletLogAdapter(timesInlets24H);
        // 设置空布局
        inletLogAdapter.setEmptyView(MyLayoutManager.getEmptyView(mActivity, mActivity.getResources().getString(R.string.empty_data), R.layout.view_general_empty, null));

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        viewBinding.rlInletLog.setLayoutManager(layoutManager);
        viewBinding.rlInletLog.setAdapter(inletLogAdapter);
    }


    /**
     * 删除用户投入记录弹窗
     *
     * @param context
     * @return
     */
    public void getDialogDeleteInletLog(Activity context) {
        Dialog dialog;

        View view = View.inflate(context, R.layout.dialog_toast, null);
        TextView tv_desc = view.findViewById(R.id.tv_dialog);
        Button confirmBtn = view.findViewById(R.id.btn_dialog_confirm);
        Button cancelBtn = view.findViewById(R.id.btn_dialog_cancel);
        confirmBtn.setVisibility(View.VISIBLE);
        cancelBtn.setVisibility(View.VISIBLE);
        tv_desc.setText(R.string.delete_all_inlet_log);

        dialog = new Dialog(context, R.style.dialog);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        confirmBtn.setOnClickListener(view12 -> {
            confirmBtn.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.GONE);
            // 删除所有投入记录
            GreenDaoUtil.getInstance().deleteAllInletLog();
            timesInlets24H.clear();
            inletLogAdapter.setList(timesInlets24H);
            dialog.dismiss();
        });

        cancelBtn.setOnClickListener(view1 -> {
            confirmBtn.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.GONE);
            dialog.dismiss();
        });
        dialog.show();
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


}
