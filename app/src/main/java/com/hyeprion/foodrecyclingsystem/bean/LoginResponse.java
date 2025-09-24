package com.hyeprion.foodrecyclingsystem.bean;

import java.io.Serializable;

/**
 * 设备登录(type=3)返回结果
 */
public class LoginResponse implements Serializable {
    /**
     * 设备编码
     */
    private String systemNo;
    /**
     * ”几幢几号
     */
    private String loginId;
    /**
     * 0:id错误  1:id、pw正确  2：id正确，pw错误
     */
    private String result;
    /**
     * 登录类型 0=ID登录, 1=RFID登录
     */
    private String loginType;
    /**
     * 栋
     */
    private String dong;
    /**
     * 号数
     */
    private String houseNo;


    public LoginResponse(String systemNo, String loginId, String result,
                         String loginType, String dong, String houseNo) {
        this.systemNo = systemNo;
        this.loginId = loginId;
        this.result = result;
        this.loginType = loginType;
        this.dong = dong;
        this.houseNo = houseNo;
    }

    public LoginResponse() {
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getDong() {
        return dong;
    }

    public void setDong(String dong) {
        this.dong = dong;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }
}
