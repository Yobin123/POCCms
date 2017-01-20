package com.st.cms.entity;

import java.util.List;

/**
 * Created by jt on 2016/9/19.
 */
public class Detail {
    private String tagId;
    private String nric;
    private int plv;
    private String name;
    private int age;
    private int gender;
    private MedicalRes bloodGroup;
    private String zone;
    //说明，暂定为病史
    private String profile;
    private List<Injury> injuries;
    private List<MedicalRes> treatments;
    private List<MedicalRes> drugs;
    private GcsScore gcsScore;
    private String designated_hospital;
    private int hospital_id;
    private String operating_theatre;
    private String wardType;
    private String vehicle_plate_no;
    private int ambulance_id;
    private String ambulance_num;
    private VitalSign vitalSign;
    private boolean isArrived;

///////////////////////////////////////////////////////////////////////////
// 这是新增字段用于显示图像和
///////////////////////////////////////////////////////////////////////////
    private int  isSuspect;
    private String bagId;

    public int getIsSuspect() {
        return isSuspect;
    }

    public void setIsSuspect(int isSuspect) {
        this.isSuspect = isSuspect;
    }

    public String getBagId() {
        return bagId;
    }

    public void setBagId(String bagId) {
        this.bagId = bagId;
    }

//    public Detail() {
//        this.tagId = "";
//        this.nric = "";
//        this.name = "";
//        this.profile = "";
//        this.designated_hospital = "";
//        this.operating_theatre = "";
//        this.wardType = "";
//        this.vehicle_plate_no = "";
//        this.ambulance_num = "";
//    }


    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getNric() {
        return nric;
    }

    public void setNric(String nric) {
        this.nric = nric;
    }

    public int getPlv() {
        return plv;
    }

    public void setPlv(int plv) {
        this.plv = plv;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public MedicalRes getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(MedicalRes bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public List<Injury> getInjuries() {
        return injuries;
    }

    public void setInjuries(List<Injury> injuries) {
        this.injuries = injuries;
    }

    public List<MedicalRes> getTreatments() {
        return treatments;
    }

    public void setTreatments(List<MedicalRes> treatments) {
        this.treatments = treatments;
    }

    public List<MedicalRes> getDrugs() {
        return drugs;
    }

    public void setDrugs(List<MedicalRes> drugs) {
        this.drugs = drugs;
    }

    public GcsScore getGcsScore() {
        return gcsScore;
    }

    public void setGcsScore(GcsScore gcsScore) {
        this.gcsScore = gcsScore;
    }

    public String getDesignated_hospital() {
        return designated_hospital;
    }

    public void setDesignated_hospital(String designated_hospital) {
        this.designated_hospital = designated_hospital;
    }

    public String getOperating_theatre() {
        return operating_theatre;
    }

    public void setOperating_theatre(String operating_theatre) {
        this.operating_theatre = operating_theatre;
    }

    public String getWardType() {
        return wardType;
    }

    public void setWardType(String wardType) {
        this.wardType = wardType;
    }

    public String getVehicle_plate_no() {
        return vehicle_plate_no;
    }

    public void setVehicle_plate_no(String vehicle_plate_no) {
        this.vehicle_plate_no = vehicle_plate_no;
    }

    public int getHospital_id() {
        return hospital_id;
    }

    public void setHospital_id(int hospital_id) {
        this.hospital_id = hospital_id;
    }

    public int getAmbulance_id() {
        return ambulance_id;
    }

    public void setAmbulance_id(int ambulance_id) {
        this.ambulance_id = ambulance_id;
    }

    public String getAmbulance_num() {
        return ambulance_num;
    }

    public void setAmbulance_num(String ambulance_num) {
        this.ambulance_num = ambulance_num;
    }

    public VitalSign getVitalSign() {
        return vitalSign;
    }

    public void setVitalSign(VitalSign vitalSign) {
        this.vitalSign = vitalSign;
    }

    public boolean isArrived() {
        return isArrived;
    }

    public void setArrived(boolean arrived) {
        isArrived = arrived;
    }

    @Override
    public String toString() {
        return "Detail{" +
                "tagId='" + tagId + '\'' +
                ", nric='" + nric + '\'' +
                ", plv=" + plv +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", gender=" + gender +
                ", bloodGroup=" + bloodGroup +
                ", zone='" + zone + '\'' +
                ", profile='" + profile + '\'' +
                ", injuries=" + injuries +
                ", treatments=" + treatments +
                ", drugs=" + drugs +
                ", gcsScore=" + gcsScore +
                ", designated_hospital='" + designated_hospital + '\'' +
                ", hospital_id=" + hospital_id +
                ", operating_theatre='" + operating_theatre + '\'' +
                ", wardType='" + wardType + '\'' +
                ", vehicle_plate_no='" + vehicle_plate_no + '\'' +
                ", ambulance_id=" + ambulance_id +
                ", ambulance_num='" + ambulance_num + '\'' +
                ", vitalSign=" + vitalSign +
                ", isArrived=" + isArrived +
                ", isSuspect=" + isSuspect +
                ", bagId=" + bagId +
                '}';
    }
}
