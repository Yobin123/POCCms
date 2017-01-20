package com.st.cms.entity;

import java.util.List;

public class Geometry {

	private String type;
	
	private List<Double> coordinates;//共2个值，第一个是经度lon，第二个是纬度lat

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Double> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<Double> coordinates) {
		this.coordinates = coordinates;
	}
	
	
}
