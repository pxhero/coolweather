package com.pxhero.coolweather.model;


public class City {
	private String name;
	private String county;
	private String id;
	private float latitude;
	private float longtitude;
	private String province;
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "name=" + name + ", country=" + county + ", id" + id + ",latitude=" + latitude + ",longtitude="
				+longtitude+",province" + province;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public float getLatitude() {
		return latitude;
	}
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}
	public float getLongtitude() {
		return longtitude;
	}
	public void setLongtitude(float longtitude) {
		this.longtitude = longtitude;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	
}
