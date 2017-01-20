package com.st.cms.entity;

/**
 * Created by jt on 2016/9/2.
 */
public class VitalSign {
    private int id;
    private int prioritiseId;
    private int spo2;
    private int bpm;
    private long timestamp;

    public VitalSign(int spo2, int bpm, long timestamp) {
        this.spo2 = spo2;
        this.bpm = bpm;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrioritiseId() {
        return prioritiseId;
    }

    public void setPrioritiseId(int prioritiseId) {
        this.prioritiseId = prioritiseId;
    }

    public int getSpo2() {
        return spo2;
    }

    public void setSpo2(int spo2) {
        this.spo2 = spo2;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
