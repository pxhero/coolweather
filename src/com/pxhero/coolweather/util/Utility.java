package com.pxhero.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.pxhero.coolweather.db.CoolWeatherDB;
import com.pxhero.coolweather.model.City;
import com.pxhero.coolweather.model.Weather;
import com.pxhero.coolweather.model.WeatherForecastInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class Utility {

    //解析返回的天气数据
    public synchronized static void handleWeatherResponse(Context context, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather5");

            if (jsonArray.length() > 0) {
                Weather weather = new Weather();

                JSONObject weatherObj = jsonArray.getJSONObject(0);
                //解析出更新时间
                JSONObject basicObj = weatherObj.getJSONObject("basic");
                JSONObject updateObj = basicObj.getJSONObject("update");
                String updateTime = updateObj.getString("loc");
                weather.setUpdateTime(updateTime);

                //解析出城市ID
                String cityId = basicObj.getString("id");
                weather.setCityId(cityId);

                //解析出城市名
                String cityName = basicObj.getString("city");
                weather.setCityName(cityName);
                //解析出当前天气描述
                JSONObject nowObj = weatherObj.getJSONObject("now");
                JSONObject condObj = nowObj.getJSONObject("cond");
                String desText = condObj.getString("txt");
                weather.setDescription(desText);

                //weather code
                weather.setCode(condObj.getString("code"));

                //解析出当前温度
                String temp = nowObj.getString("tmp");
                weather.setTmp(temp);

                //空气质量
                //免费api真的稳定，有些城市没有aqi数据。。。
                if(weatherObj.has("aqi")) {
                    JSONObject aqiObj = weatherObj.getJSONObject("aqi");
                    JSONObject cityAqiObj = aqiObj.getJSONObject("city");
                    weather.setAirDes(cityAqiObj.getString("qlty"));
                }
                else {
                    weather.setAirDes("暂无数据");
                    LogUtil.d("没有" + weather.getCityName() + "的api空气指数数据");
                }

                saveWeatherInfo(context, weather);

                ParseForecastInfo(weatherObj, cityId);

            }

        } catch (Exception e) {
            // TODO: handle exception
            LogUtil.d("v5:handleWeatherResponse has a exception");
            e.printStackTrace();
        }
    }

    /***************************** 解析出未来7天的天气信息（包括今天）***************/
    private static void ParseForecastInfo(JSONObject weatherObj, String cityId) throws Exception {

        List<WeatherForecastInfo> forecastList = new ArrayList<>();
        JSONArray forecastArray = weatherObj.getJSONArray("daily_forecast");
        if (forecastArray != null) {
            for (int i = 0; i < forecastArray.length(); i++) {
                JSONObject oneForecast = forecastArray.getJSONObject(i);

                WeatherForecastInfo fInfo = new WeatherForecastInfo();
                fInfo.setmCityId(cityId);
                fInfo.setmDate(oneForecast.getString("date"));

                JSONObject tmpObj = oneForecast.getJSONObject("tmp");
                StringBuilder tmpRange = new StringBuilder();
                tmpRange.append(tmpObj.getString("min") + "℃~" + tmpObj.getString("max") + "℃");
                fInfo.setmTemDes(tmpRange.toString());

                JSONObject WeaObj = oneForecast.getJSONObject("cond");
                String dayWeather = WeaObj.getString("txt_d");
                String nightWeather = WeaObj.getString("txt_n");
                if (dayWeather.equals(nightWeather)) {
                    fInfo.setmWeatherDes(dayWeather);
                } else {
                    StringBuilder weatherRange = new StringBuilder();
                    weatherRange.append(dayWeather);
                    weatherRange.append("转");
                    weatherRange.append(nightWeather);
                    fInfo.setmWeatherDes(weatherRange.toString());
                }

                fInfo.setmCodeId(WeaObj.getString("code_d"));

                JSONObject windObj = oneForecast.getJSONObject("wind");
                StringBuilder windDes = new StringBuilder();
                windDes.append(windObj.getString("dir"));
                String strSc = new String();
                strSc = windObj.getString("sc");
                boolean bNumberWind = false;
                for (int k = 0; k < 20; k++) {
                    if (strSc.contains(String.valueOf(k))) {
                        bNumberWind = true;
                        break;
                    }
                }
                if (bNumberWind) {
                    windDes.append("(");
                    windDes.append(strSc);
                    windDes.append("级");
                    windDes.append(")");
                } else {
                    windDes.append("(");
                    windDes.append(strSc);
                    windDes.append(")");
                }
                fInfo.setmWindDes(windDes.toString());

                if (i == 0) {
                    fInfo.setmDateDes("今天");
                } else if (i == 1) {
                    fInfo.setmDateDes("明天");
                } else if (i == 2) {
                    fInfo.setmDateDes("后天");
                } else {
                    fInfo.setmDateDes("第" + (i + 1) + "天");
                }

                forecastList.add(fInfo);
            }
        }
        WeatherForecastInfo.setS_WeatherInfoList(null);
        WeatherForecastInfo.setS_WeatherInfoList(forecastList);
    }


    //把weather数据存入SharedPreferences文件中（xml，key-value）
    private static void saveWeatherInfo(Context context, Weather weather) {
        if (weather == null)
            return;

        LogUtil.d("v5:saveWeatherInfo 开始保存weather信息到SharedPreferences文件,id=" + weather.getCityId()
                + ",name=" + weather.getCityName());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", weather.getCityName());
        editor.putString("city_id", weather.getCityId());
        editor.putString("current_date", simpleDateFormat.format(new Date()));
        editor.putString("update_time", weather.getUpdateTime());
        editor.putString("description", weather.getDescription());
        editor.putString("tmp", weather.getTmp());
        editor.putString("air_des", weather.getAirDes());
        editor.putString("code", weather.getCode());
        editor.commit();
    }

    //解析从服务器返回的全国城市数据
    public synchronized static boolean handleCityResponse(CoolWeatherDB db, String Response) {
        boolean bResult = false;

        if (db == null || TextUtils.isEmpty(Response)) {
            bResult = false;
            return bResult;
        }

        try {
            JSONArray dataArray = new JSONArray(Response);
            if (dataArray != null) {
                LogUtil.d("handleCityResponse:: the dataArray!=null, parse json success!");
                for (int i = 0; i < dataArray.length(); ++i) {
                    City city = new City();
                    JSONObject oneData = dataArray.getJSONObject(i);
                    city.setName(oneData.getString("cityZh"));
                    city.setCounty(oneData.getString("countryZh"));
                    city.setId(oneData.getString("id"));
                    String strLatitude = oneData.getString("lat");
                    city.setLatitude(Float.valueOf(strLatitude));
                    String strLongtitude = oneData.getString("lon");
                    city.setLongtitude(Float.valueOf(strLongtitude));
                    city.setProvince(oneData.getString("provinceZh"));
                    db.AddCityToList(city);
                }
                db.SaveAllCityToDB();
                bResult = true;
            } else {
                LogUtil.d("handleCityResponse:: the dataArray==null,parse json failed!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d("Utility.handlerCityResponse  函数发生了异常！");
            // TODO: handle exception
        }

        return bResult;
    }
}
