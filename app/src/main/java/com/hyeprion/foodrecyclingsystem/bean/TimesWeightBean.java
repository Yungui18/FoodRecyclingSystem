package com.hyeprion.foodrecyclingsystem.bean;

import java.io.Serializable;

public class TimesWeightBean implements Serializable {
    /**
     * 次数，本天第几次称重
     */
    private int times;
    /**
     * 时间 以天为单位
     */
    private String time;
    /**
     * 本次重量
     */
    private String weight;

    public TimesWeightBean() {
    }

    public TimesWeightBean(int times, String time, String weight) {
        this.times = times;
        this.time = time;
        this.weight = weight;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "TimesWeightBean{" +
                "times=" + times +
                ", time='" + time + '\'' +
                ", weight='" + weight + '\'' +
                '}';
    }
}
