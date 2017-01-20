package com.st.cms.entity;

public class WVitalSigns {

	private int id;
	private String taglibId;
	private int spo2;
	private int bpm;
	private long timestamp;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTaglibId() {
		return taglibId;
	}
	public void setTaglibId(String taglibId) {
		this.taglibId = taglibId;
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
