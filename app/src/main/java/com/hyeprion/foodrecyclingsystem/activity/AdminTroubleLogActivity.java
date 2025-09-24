package com.hyeprion.foodrecyclingsystem.activity;

import android.annotation.SuppressLint;
import android.view.View;

import com.hyeprion.foodrecyclingsystem.R;
import com.hyeprion.foodrecyclingsystem.adapter.TroubleLogAdapter;
import com.hyeprion.foodrecyclingsystem.base.BaseActivity;
import com.hyeprion.foodrecyclingsystem.bean.TroubleLog;
import com.hyeprion.foodrecyclingsystem.databinding.ActivityAdminTroubleLog2Binding;
import com.hyeprion.foodrecyclingsystem.util.GreenDaoUtil;
import com.hyeprion.foodrecyclingsystem.util.MyLayoutManager;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;

public class AdminTroubleLogActivity extends BaseActivity<ActivityAdminTroubleLog2Binding> {
    private List<TroubleLog> troubleLogs = new ArrayList<>();
    private TroubleLogAdapter troubleLogAdapter;

    @Override
    protected void initView() {
        troubleLogs = GreenDaoUtil.getInstance().getTroubleList();
        initRL();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initListener() {
        viewBinding.btnBack.setOnClickListener(this);
       /* viewBinding.etDeviceId.setText(MyApplication.deviceId);

        viewBinding.etDeviceId.setOnFocusChangeListener((view, b) -> {
            if (b) {
                // 搜索框获取焦点后，显示输入法
                KeyboardUtils.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            } else {
                KeyboardUtils.hideSoftInput(viewBinding.etDeviceId);
            }
        });

        viewBinding.activityAdminDeviceInfo.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                // 点击fragment空白部分，隐藏输入法，搜索框失去焦点
                if (mActivity.getCurrentFocus() != null
                        && mActivity.getCurrentFocus().getWindowToken() != null) {
                    KeyboardUtils.hideSoftInput(viewBinding.activityAdminDeviceInfo);
                    viewBinding.etDeviceId.clearFocus();
                }
            }
            return false;
        });*/
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_back){
//            MyApplication.deviceId = viewBinding.etDeviceId.getText().toString();
//            MyApplication.adminParameterBean.setDeviceId(viewBinding.etDeviceId.getText().toString());
//            SharedPreFerUtil.saveObj(
//                    Constant.ADMIN_PARAMETER_FILENAME,Constant.ADMIN_PARAMETER_KEY,
//                    JSON.toJSONString(MyApplication.adminParameterBean));
            finish();
        }
    }

    /**
     * 初始化recycleView
     */
    private void initRL() {
        troubleLogAdapter = new TroubleLogAdapter(troubleLogs);
        // 设置空布局
        troubleLogAdapter.setEmptyView(MyLayoutManager.getEmptyView(mActivity, mActivity.getResources().getString(R.string.empty_data), R.layout.view_general_empty, null));

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        viewBinding.rlTroubleLog.setLayoutManager(layoutManager);
        viewBinding.rlTroubleLog.setAdapter(troubleLogAdapter);
    }
}
