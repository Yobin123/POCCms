package com.st.cms.entity;

/**
 * Created by jt on 2016/8/24.
 */
public class Prioritise {
    private int id;
    private String tagId;
    private String zone;
    private String area;
    private long timestamp;
    private Victim victim;
    private int pLv;
    private int status;
    private int hospitalId;
    private int ambulanceId;
    private int isArrived;
    //lat,lon
    private String coordinate;
    private String profile;

    public Prioritise(String tagId, String zone) {
        this.tagId = tagId;
        this.zone = zone;
    }

    public Prioritise(Victim victim, String tagId) {
        this.victim = victim;
        this.tagId = tagId;
    }

    public Prioritise(String tagId, String zone, int plv) {
        this.tagId = tagId;
        this.zone = zone;
        this.pLv = plv;
    }

    public Prioritise(int id, String tagId, Victim victim) {
        this.id = id;
        this.tagId = tagId;
        this.victim = victim;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Victim getVictim() {
        return victim;
    }

    public void setVictim(Victim victim) {
        this.victim = victim;
    }

    public int getpLv() {
        return pLv;
    }

    public void setpLv(int pLv) {
        this.pLv = pLv;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public int getAmbulanceId() {
        return ambulanceId;
    }

    public void setAmbulanceId(int ambulanceId) {
        this.ambulanceId = ambulanceId;
    }

    public int getIsArrived() {
        return isArrived;
    }

    public void setIsArrived(int isArrived) {
        this.isArrived = isArrived;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
