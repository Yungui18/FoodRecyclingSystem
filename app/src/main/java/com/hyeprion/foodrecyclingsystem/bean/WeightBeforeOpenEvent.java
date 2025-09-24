package com.hyeprion.foodrecyclingsystem.bean;

import java.io.Serializable;

/**
 * 开门前获取的重量
 */
public class WeightBeforeOpenEvent implements Serializable {
    private float weight;

    public WeightBeforeOpenEvent() {
    }

    public WeightBeforeOpenEvent(float weight) {
        this.weight = weight;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}
