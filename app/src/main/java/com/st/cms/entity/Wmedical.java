package com.st.cms.entity;

public class Wmedical {

	private String medical_name;
	private String medical_id;
	private int used;
	private int qty;
	private String medical_type;
	private String picRoute;

	public String getMedical_id() {
		return medical_id;
	}
	public void setMedical_id(String medical_id) {
		this.medical_id = medical_id;
	}
	public String getMedical_name() {
		return medical_name;
	}
	public void setMedical_name(String medical_name) {
		this.medical_name = medical_name;
	}
	public int getUsed() {
		return used;
	}
	public void setUsed(int used) {
		this.used = used;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	public String getMedical_type() {
		return medical_type;
	}
	public void setMedical_type(String medical_type) {
		this.medical_type = medical_type;
	}
	public String getPicRoute() {
		return picRoute;
	}
	public void setPicRoute(String picRoute) {
		this.picRoute = picRoute;
	}

}
