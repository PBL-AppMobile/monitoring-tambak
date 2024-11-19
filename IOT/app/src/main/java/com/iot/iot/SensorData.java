package com.iot.iot;

public class SensorData {
    private String time;
    private double value;

    public SensorData(String time, double value) {
        this.time = time;
        this.value = value;
    }

    public String getTime() {
        return time;
    }

    public double getValue() {
        return value;
    }
}
