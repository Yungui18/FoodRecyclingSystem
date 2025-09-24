package com.hyeprion.foodrecyclingsystem.bean;

import java.io.Serializable;

/**
 * 读卡器信号输入，记录id和密码
 */
public class CardReaderInfoEvent implements Serializable {
    /**
     * 用户的id
     */
    private String cardUserId;
    /**
     * 用户的密码
     */
    private String cardUserPW;

    public CardReaderInfoEvent() {
    }

    public CardReaderInfoEvent(String cardUserId, String cardUserPW) {
        this.cardUserId = cardUserId;
        this.cardUserPW = cardUserPW;
    }

    public String getCardUserId() {
        return cardUserId;
    }

    public void setCardUserId(String cardUserId) {
        this.cardUserId = cardUserId;
    }

    public String getCardUserPW() {
        return cardUserPW;
    }

    public void setCardUserPW(String cardUserPW) {
        this.cardUserPW = cardUserPW;
    }
}
