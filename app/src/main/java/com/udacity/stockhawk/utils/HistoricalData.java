package com.udacity.stockhawk.utils;

/**
 * Created by mariona on 22/3/17.
 */

public class HistoricalData {
    private float date;
    private float value;

    public HistoricalData(float date, float value) {
        this.date = date;
        this.value = value;
    }

    public float getDate() {
        return date;
    }

    public float getValue() {
        return value;
    }
}
