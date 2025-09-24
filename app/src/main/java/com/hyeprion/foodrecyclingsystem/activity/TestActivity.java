package com.hyeprion.foodrecyclingsystem.activity;

import android.os.Handler;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hyeprion.foodrecyclingsystem.R;
import com.hyeprion.foodrecyclingsystem.base.BaseActivity;

public class TestActivity extends BaseActivity<com.hyeprion.foodrecyclingsystem.databinding.ActivityInletOpening2Binding> {
    @Override
    protected void initView() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                viewBinding.clOpeningFw2.setVisibility(View.VISIBLE);
                viewBinding.ivOpeningUpDown.setImageResource(R.mipmap.updownarrow);
                RequestOptions options = new RequestOptions()
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.DATA);
                Glide.with(mActivity)
                        .asGif()
                        .load(R.mipmap.updownarrow)
                        .apply(options)
                        .into(viewBinding.ivOpeningUpDown);
            }
        },3000);

    }

    @Override
    protected void initListener() {
//        viewBinding.btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
//        switch (view.getId()){
//            case R.id.btn_back:
//                viewBinding.btnBack.setVisibility(View.GONE);
//                break;
//        }

    }
}
