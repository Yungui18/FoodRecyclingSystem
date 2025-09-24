package com.hyeprion.foodrecyclingsystem.util;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hyeprion.foodrecyclingsystem.R;

/**
 * Describe:布局管理类
 */
public class MyLayoutManager {


    /*
     * 列表空布局(没有点击事件的)
     * */
    public static View getEmptyView(Context context) {
        View inflate = View.inflate(context, R.layout.view_general_empty, null);
        return inflate;
    }

    /*
     * 列表空布局(有点击事件的)
     * */
    public static View getEmptyView(Context context, View.OnClickListener onClickListener) {
        View inflate = View.inflate(context, R.layout.view_general_empty, null);
        if (onClickListener != null) {
            setOnClick(inflate, onClickListener);
        }
        return inflate;
    }


    /*
     * 自定义空布局  布局文件大小必须设置填充母布局
     * */
    public static View getEmptyView(Context context, String desc, int id, View.OnClickListener onClickListener) {
        View inflate = View.inflate(context, id, null);
        TextView tv = inflate.findViewById(R.id.tv_setting_destination_tips);
        tv.setText(desc);
        if (onClickListener != null) {
            setOnClick(inflate, onClickListener);
        }
        return inflate;
    }

    /**
     * 给空布局添加点击事件
     *
     * @param emptyView
     * @param onClickListener
     */
    private static void setOnClick(View emptyView, View.OnClickListener onClickListener) {
        emptyView.setOnClickListener(onClickListener);
    }
}
