package com.st.cms.entity;

/**
 * Created by jt on 2016/8/31.
 */
public class Consume {
    private int id;
    private int prioritiseId;
    private MedicalRes medicalRes;
    private int useQty;
    private long timestamp;

    public Consume(int id, MedicalRes medicalRes, int useQty, long timestamp) {
        this.id = id;
        this.medicalRes = medicalRes;
        this.timestamp = timestamp;
    }
}
