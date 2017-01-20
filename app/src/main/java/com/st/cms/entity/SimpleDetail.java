package com.st.cms.entity;

import java.util.List;

/**
 * Created by jt on 2016/9/19.
 */
public class SimpleDetail {
    private String tagId;
    private List<MedicalRes> consumes;
    private int hospital_id;

    public SimpleDetail() {
    }

    public SimpleDetail(String tagId, List<MedicalRes> consumes, int hospital_id) {
        this.tagId = tagId;
        this.consumes = consumes;
        this.hospital_id = hospital_id;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }


    public int getHospital_id() {
        return hospital_id;
    }

    public void setHospital_id(int hospital_id) {
        this.hospital_id = hospital_id;
    }

    @Override
    public String toString() {
        return "SimpleDetail{" +
                "tagId='" + tagId + '\'' +
                ", consumes=" + consumes +
                ", hospital_id=" + hospital_id +
                '}';
    }

    public List<MedicalRes> getConsumes() {
        return consumes;
    }

    public void setConsumes(List<MedicalRes> consumes) {
        this.consumes = consumes;
    }
}
