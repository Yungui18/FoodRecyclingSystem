package com.hyeprion.foodrecyclingsystem.bean;

import java.io.Serializable;

/**
 * 投入垃圾(type=2)返回信息
 */
public class PutIntoGarbageResponse implements Serializable {
    /**
     * 投入垃圾累积量
     */
    private float weight;
    /**
     * 设备编码
     */
    private String systemNo;
    /**
     * 几幢几号
     */
    private String loginId;

    public PutIntoGarbageResponse() {
    }

    public PutIntoGarbageResponse(float weight, String systemNo, String loginId) {
        this.weight = weight;
        this.systemNo = systemNo;
        this.loginId = loginId;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getSystemNo() {
        return systemNo;
    }

    public void setSystemNo(String systemNo) {
        this.systemNo = systemNo;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    @Override
    public String toString() {
        return "PutIntoGarbageResponse{" +
                "weight=" + weight +
                ", systemNo='" + systemNo + '\'' +
                ", loginId='" + loginId + '\'' +
                '}';
    }
}
