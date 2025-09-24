package com.hyeprion.foodrecyclingsystem.bean;

import com.hyeprion.foodrecyclingsystem.R;

import java.util.ArrayList;
import java.util.List;

public class BannerDataBean {
    public Integer imageRes;
    public String imageUrl;
    public String title;
    public int viewType;

    public BannerDataBean(Integer imageRes, String title, int viewType) {
        this.imageRes = imageRes;
        this.title = title;
        this.viewType = viewType;
    }

    public static List<BannerDataBean> getTestData() {
        List<BannerDataBean> list = new ArrayList<>();
        list.add(new BannerDataBean(R.mipmap.screen_banner1, "相信自己,你努力的样子真的很美", 1));
        list.add(new BannerDataBean(R.mipmap.screen_banner2, "极致简约,梦幻小屋", 3));
        list.add(new BannerDataBean(R.mipmap.screen_banner3, "超级卖梦人", 3));
//        list.add(new BannerDataBean(R.mipmap.screen_banner4, "超级卖梦人", 3));
        return list;
    }


}
