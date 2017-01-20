package com.st.cms.entity;

/**
 * Created by jt on 2016/10/10.
 */
public class GcsScore {
    private int eye_opening;
    private int verbal_response;
    private int motor_response;
    private long timestamp;

    public GcsScore()
    {}
    public GcsScore(int eye_opening, int verbal_response, int motor_response, long timestamp) {
        this.eye_opening = eye_opening;
        this.verbal_response = verbal_response;
        this.motor_response = motor_response;
        this.timestamp = timestamp;
    }

    public int getEye_opening() {
        return eye_opening;
    }

    public void setEye_opening(int eye_opening) {
        this.eye_opening = eye_opening;
    }

    public int getVerbal_response() {
        return verbal_response;
    }

    public void setVerbal_response(int verbal_response) {
        this.verbal_response = verbal_response;
    }

    public int getMotor_response() {
        return motor_response;
    }

    public void setMotor_response(int motor_response) {
        this.motor_response = motor_response;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
