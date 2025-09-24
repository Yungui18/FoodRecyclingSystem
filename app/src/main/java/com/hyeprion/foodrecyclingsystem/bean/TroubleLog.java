package com.hyeprion.foodrecyclingsystem.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 故障记录
 */
@Entity
public class TroubleLog {
    @Id(autoincrement = true)
    public Long id;

    /**
     * 故障时间，精度到S
     */
    private String time;
    /**
     * 故障
     */
    private String trouble;
    /**
     * 故障代号
     */
    private int troubleType;
    @Generated(hash = 2032610246)
    public TroubleLog(Long id, String time, String trouble, int troubleType) {
        this.id = id;
        this.time = time;
        this.trouble = trouble;
        this.troubleType = troubleType;
    }
    @Generated(hash = 1714191595)
    public TroubleLog() {
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
    public String getTrouble() {
        return this.trouble;
    }
    public void setTrouble(String trouble) {
        this.trouble = trouble;
    }
    public int getTroubleType() {
        return this.troubleType;
    }
    public void setTroubleType(int troubleType) {
        this.troubleType = troubleType;
    }
}
