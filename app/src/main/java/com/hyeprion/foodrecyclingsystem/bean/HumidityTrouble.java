package com.hyeprion.foodrecyclingsystem.bean;

import java.io.Serializable;

/**
 * 湿度报警。通知首页，显示雨滴图片标识
 */
public class HumidityTrouble implements Serializable {
    boolean show = false;

    public HumidityTrouble() {
    }

    public HumidityTrouble(boolean show) {
        this.show = show;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }
}
