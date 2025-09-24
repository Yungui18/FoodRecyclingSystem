package com.hyeprion.foodrecyclingsystem.bean;

import java.io.Serializable;

/**
 * 设备状态（type=1）返回
 */
public class DeviceStatusResponse implements Serializable {
    private boolean success;
    private String message;

    public DeviceStatusResponse() {
    }

    public DeviceStatusResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "DeviceStatusResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}
