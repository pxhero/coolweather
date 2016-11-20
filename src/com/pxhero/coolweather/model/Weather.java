package com.pxhero.coolweather.model;

public class Weather {

	private String cityName;//城市名称
	private String cityId;//城市id
	private String  updateTime;//发布时间
	private String description;//天气描述
	private String tmp; //当前温度
	private String code;//天气代码
	private String airDes; //天气空气质量描述

	public String getAirDes() {
		return airDes;
	}

	public void setAirDes(String airDes) {
		this.airDes = airDes;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTmp() {

		return tmp;
	}
	public void setTmp(String tmp) {
		this.tmp = tmp;
	}
	public String getCityId() {
		return cityId;
	}
	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}



}
