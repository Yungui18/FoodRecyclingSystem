package com.hyeprion.foodrecyclingsystem.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 月度投入垃圾总量 表
 */
@Entity
public class MonthlyTotalInlet {
    @Id(autoincrement = true)
    public Long id;

    /**
     * 当前时间，精度到月
     */
    private String time;
    /**
     * 月投垃圾总量
     */
    private float monthlyTotalInlet;
    @Generated(hash = 1222008450)
    public MonthlyTotalInlet(Long id, String time, float monthlyTotalInlet) {
        this.id = id;
        this.time = time;
        this.monthlyTotalInlet = monthlyTotalInlet;
    }
    @Generated(hash = 1194393652)
    public MonthlyTotalInlet() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public float getMonthlyTotalInlet() {
        return this.monthlyTotalInlet;
    }
    public void setMonthlyTotalInlet(float monthlyTotalInlet) {
        this.monthlyTotalInlet = monthlyTotalInlet;
    }

}
