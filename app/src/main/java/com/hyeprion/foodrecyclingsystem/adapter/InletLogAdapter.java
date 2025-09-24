package com.hyeprion.foodrecyclingsystem.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hyeprion.foodrecyclingsystem.R;
import com.hyeprion.foodrecyclingsystem.base.MyApplication;
import com.hyeprion.foodrecyclingsystem.bean.TimesInlet;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * 投入记录 {@link TimesInlet} adapter
 */
public class InletLogAdapter extends BaseQuickAdapter<TimesInlet, BaseViewHolder> {
    public InletLogAdapter(int layoutResId, @Nullable List<TimesInlet> data) {
        super(layoutResId, data);
    }

    public InletLogAdapter(@Nullable List<TimesInlet> data) {
        super(R.layout.item_rl_inlet_log, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, TimesInlet item) {
        helper.setText(R.id.item_tv_inlet_time, item.getTime());
        helper.setText(R.id.item_tv_inlet_weight, item.getTimesInlet()+ MyApplication.weighingUnit);
        helper.setText(R.id.item_tv_login_type, item.getLoginType());
        helper.setText(R.id.item_tv_inlet_id, item.getInletId());
    }
}
