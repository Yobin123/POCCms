package com.st.cms.entity;

public class Wperson {

	//Person表数据ID
	private int id;
	private String name;
	//身份证
	private String nric;
	//护照
	private String passport;
	//TAG表数据ID
	private int tag_id;
	//手环ID
	private String taglib_id;
	//伤亡级别 （0,1,2,3）
	private int plv;
	//区域（Triage，Ambulance Point，Hospital，First Aid Point）
	private String zone;
	//区域（A,B,C,D,E）
	private String area;
	//时间戳
	private long time;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNric() {
		return nric;
	}
	public void setNric(String nric) {
		this.nric = nric;
	}
	public String getPassport() {
		return passport;
	}
	public void setPassport(String passport) {
		this.passport = passport;
	}
	public int getTag_id() {
		return tag_id;
	}
	public void setTag_id(int tag_id) {
		this.tag_id = tag_id;
	}
	public int getPlv() {
		return plv;
	}
	public void setPlv(int plv) {
		this.plv = plv;
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
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getTaglib_id() {
		return taglib_id;
	}
	public void setTaglib_id(String taglib_id) {
		this.taglib_id = taglib_id;
	}

}
