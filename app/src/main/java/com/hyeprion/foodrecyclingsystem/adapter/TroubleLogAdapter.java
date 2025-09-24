package com.hyeprion.foodrecyclingsystem.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hyeprion.foodrecyclingsystem.R;
import com.hyeprion.foodrecyclingsystem.bean.TroubleLog;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 故障发生与解除记录 {@link TroubleLog} adapter
 */
public class TroubleLogAdapter extends BaseQuickAdapter<TroubleLog, BaseViewHolder> {
    public TroubleLogAdapter(int layoutResId, @Nullable List<TroubleLog> data) {
        super(layoutResId, data);
    }

    public TroubleLogAdapter(@Nullable List<TroubleLog> data) {
        super(R.layout.item_rl_trouble_log, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, TroubleLog item) {
        helper.setText(R.id.item_tv_trouble_time, item.getTime());
        helper.setText(R.id.item_tv_trouble_type, item.getTroubleType()+"");
        helper.setText(R.id.item_tv_trouble, item.getTrouble());
    }
}
