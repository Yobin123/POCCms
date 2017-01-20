package com.st.cms.entity;

public class Wgcs {

	private int id;
	private String taglibId;
	// 0 = eye, 1 = mouse , 2 = move
	private int type;
	// 1 表示 1，2表示2，以此类推
	private int status;
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
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
}
