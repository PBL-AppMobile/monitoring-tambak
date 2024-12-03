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

    public SensorData(String time, double ph, double salinity, double temp) {
        this.time = time;
        this.ph = ph;
        this.salinity = salinity;
        this.temp = temp;
    }

    public String getTimeFormatted() {
        // Formatkan waktu ke format yang lebih mudah dibaca
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(time);
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return time; // Kembalikan original jika gagal
        }
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
