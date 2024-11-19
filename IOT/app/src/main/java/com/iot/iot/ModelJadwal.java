package com.iot.iot;

public class ModelJadwal {
    private String hari;
    private String jam;
    private String keterangan;
    private boolean isActive;

    public ModelJadwal(String hari, String jam, String keterangan, boolean isActive) {
        this.hari = hari;
        this.jam = jam;
        this.keterangan = keterangan;
        this.isActive = isActive;
    }

    public String getHari() { return hari; }
    public String getJam() { return jam; }
    public String getKeterangan() { return keterangan; }
    public boolean isActive() { return isActive; }

    public void setActive(boolean active) { isActive = active; }
}

