package com.pxhero.coolweather.model;

import java.util.List;

/**
 * Created by pxhero on 2016/11/20.
 */

public class WeatherForecastInfo {

    public static List<WeatherForecastInfo> s_WeatherInfoList=null;

    public static List<WeatherForecastInfo> getS_WeatherInfoList() {
        return s_WeatherInfoList;
    }

    public static void setS_WeatherInfoList(List<WeatherForecastInfo> s_WeatherInfoList) {
        WeatherForecastInfo.s_WeatherInfoList = s_WeatherInfoList;
    }

    private String mCityId;//城市id
    private String mDate;//日期
    private String mDateDes;//日期描述，如今天
    private String mCodeId; //天气code，用于下载天气图片
    private String mWeatherDes;//天气描述
    private String mTemDes;//气温描述
    private String mWindDes; //风力描述

    public String getmCityId() {
        return mCityId;
    }

    public void setmCityId(String mCityId) {
        this.mCityId = mCityId;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public String getmDateDes() {
        return mDateDes;
    }

    public void setmDateDes(String mDateDes) {
        this.mDateDes = mDateDes;
    }

    public String getmCodeId() {
        return mCodeId;
    }

    public void setmCodeId(String mCodeId) {
        this.mCodeId = mCodeId;
    }

    public String getmWeatherDes() {
        return mWeatherDes;
    }

    public void setmWeatherDes(String mWeatherDes) {
        this.mWeatherDes = mWeatherDes;
    }

    public String getmTemDes() {
        return mTemDes;
    }

    public void setmTemDes(String mTemDes) {
        this.mTemDes = mTemDes;
    }

    public String getmWindDes() {
        return mWindDes;
    }

    public void setmWindDes(String mWindDes) {
        this.mWindDes = mWindDes;
    }
}
