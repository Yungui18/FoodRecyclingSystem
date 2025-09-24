package com.hyeprion.foodrecyclingsystem.bean;

import java.io.Serializable;

/**
 * 卡片绑定（type=4）返回
 */
public class CardBindingResponse implements Serializable {


    /**
     * success : true
     * message : Success!!
     */

    private boolean success;
    private String message;

    public CardBindingResponse() {
    }

    public CardBindingResponse(boolean success, String message) {
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
        return "CardBindingResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}
