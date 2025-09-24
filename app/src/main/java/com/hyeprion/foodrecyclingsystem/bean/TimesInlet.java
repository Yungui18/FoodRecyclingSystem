package com.hyeprion.foodrecyclingsystem.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 月度投入垃圾总量 表
 */
@Entity
public class TimesInlet {
    @Id(autoincrement = true)
    public Long id;

    /**
     * 投入垃圾时间，精度到S
     */
    private String time;
    /**
     * 每次投入垃圾重量
     */
    private float timesInlet;
    /**
     * 投入ID
     */
    private String inletId;
    /**
     * 登录方式  密码登录、RFID登录、无
     */
    private String loginType;
    /**
     * 时间戳
     */
    private long timeStamp;
    @Generated(hash = 2074290507)
    public TimesInlet(Long id, String time, float timesInlet, String inletId,
            String loginType, long timeStamp) {
        this.id = id;
        this.time = time;
        this.timesInlet = timesInlet;
        this.inletId = inletId;
        this.loginType = loginType;
        this.timeStamp = timeStamp;
    }
    @Generated(hash = 964230944)
    public TimesInlet() {
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
    public float getTimesInlet() {
        return this.timesInlet;
    }
    public void setTimesInlet(float timesInlet) {
        this.timesInlet = timesInlet;
    }
    public String getInletId() {
        return this.inletId;
    }
    public void setInletId(String inletId) {
        this.inletId = inletId;
    }
    public String getLoginType() {
        return this.loginType;
    }
    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }
    public long getTimeStamp() {
        return this.timeStamp;
    }
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
   
}
