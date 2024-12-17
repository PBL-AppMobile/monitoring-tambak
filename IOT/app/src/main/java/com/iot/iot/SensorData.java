package com.iot.iot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class SensorData {
    private String time;
    private double ph;
    private double salinity;
    private double temp;
    private String formattedDate; // Store the formatted date
    private String formattedTime; // Store the formatted time

    public SensorData(String time, double ph, double salinity, double temp) {
        this.time = time;
        this.ph = ph;
        this.salinity = salinity;
        this.temp = temp;
        formatTime();
    }

    private void formatTime() {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat outputFormatDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            SimpleDateFormat outputFormatTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

            Date date = inputFormat.parse(time);
            formattedDate = outputFormatDate.format(date);
            formattedTime = outputFormatTime.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public double getPh() {
        return ph;
    }

    public double getSalinity() {
        return salinity;
    }

    public double getTemp() {
        return temp;
    }
}
