package com.hyeprion.foodrecyclingsystem.activity;

import android.view.View;

import com.hyeprion.foodrecyclingsystem.R;
import com.hyeprion.foodrecyclingsystem.base.BaseActivity;
import com.hyeprion.foodrecyclingsystem.databinding.ActivityAdminTroubleTestBinding;
import com.hyeprion.foodrecyclingsystem.util.port.PortConstants;
import com.hyeprion.foodrecyclingsystem.util.port.PortControlUtil;


public class AdminTroubleTest extends BaseActivity<ActivityAdminTroubleTestBinding> {
    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
        viewBinding.btnBack.setOnClickListener(this);
        viewBinding.troubleTestBtn1StirForward.setOnClickListener(this);
        viewBinding.troubleTestBtn1StirOff.setOnClickListener(this);
        viewBinding.troubleTestBtn2InletOpen.setOnClickListener(this);
        viewBinding.troubleTestBtn2InletClose.setOnClickListener(this);
        viewBinding.troubleTestBtn2InletLock.setOnClickListener(this);
        viewBinding.troubleTestBtn2InletUnlock.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                this.finish();
                break;
            case R.id.trouble_test_btn1_stir_forward:
                PortControlUtil.getInstance().sendCommands(PortConstants.STIR_FORWARD);
                break;
            case R.id.trouble_test_btn1_stir_off:
//                PortControlUtil.getInstance().sendCommands(PortConstants.STIR_error);
                break;
            case R.id.trouble_test_btn2_inlet_open:
                PortControlUtil.getInstance().sendCommands(PortConstants.INLET_OPEN);
                break;
            case R.id.trouble_test_btn2_inlet_close:
                PortControlUtil.getInstance().sendCommands(PortConstants.INLET_CLOSE);
                break;
            case R.id.trouble_test_btn2_inlet_lock:
                PortControlUtil.getInstance().sendCommands(PortConstants.INLET_LOCK);
                break;
            case R.id.trouble_test_btn2_inlet_unlock:
                PortControlUtil.getInstance().sendCommands(PortConstants.INLET_UNLOCK);
                break;
        }
    }
}
