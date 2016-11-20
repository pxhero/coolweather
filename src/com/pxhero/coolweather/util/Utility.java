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

    //�������ص���������
    public synchronized static void handleWeatherResponse(Context context, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather5");

            if (jsonArray.length() > 0) {
                Weather weather = new Weather();

                JSONObject weatherObj = jsonArray.getJSONObject(0);
                //����������ʱ��
                JSONObject basicObj = weatherObj.getJSONObject("basic");
                JSONObject updateObj = basicObj.getJSONObject("update");
                String updateTime = updateObj.getString("loc");
                weather.setUpdateTime(updateTime);

                //����������ID
                String cityId = basicObj.getString("id");
                weather.setCityId(cityId);

                //������������
                String cityName = basicObj.getString("city");
                weather.setCityName(cityName);
                //��������ǰ��������
                JSONObject nowObj = weatherObj.getJSONObject("now");
                JSONObject condObj = nowObj.getJSONObject("cond");
                String desText = condObj.getString("txt");
                weather.setDescription(desText);

                //weather code
                weather.setCode(condObj.getString("code"));

                //��������ǰ�¶�
                String temp = nowObj.getString("tmp");
                weather.setTmp(temp);

                //��������
                //���api����ȶ�����Щ����û��aqi���ݡ�����
                if(weatherObj.has("aqi")) {
                    JSONObject aqiObj = weatherObj.getJSONObject("aqi");
                    JSONObject cityAqiObj = aqiObj.getJSONObject("city");
                    weather.setAirDes(cityAqiObj.getString("qlty"));
                }
                else {
                    weather.setAirDes("��������");
                    LogUtil.d("û��" + weather.getCityName() + "��api����ָ������");
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

    /***************************** ������δ��7���������Ϣ���������죩***************/
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
                tmpRange.append(tmpObj.getString("min") + "��~" + tmpObj.getString("max") + "��");
                fInfo.setmTemDes(tmpRange.toString());

                JSONObject WeaObj = oneForecast.getJSONObject("cond");
                String dayWeather = WeaObj.getString("txt_d");
                String nightWeather = WeaObj.getString("txt_n");
                if (dayWeather.equals(nightWeather)) {
                    fInfo.setmWeatherDes(dayWeather);
                } else {
                    StringBuilder weatherRange = new StringBuilder();
                    weatherRange.append(dayWeather);
                    weatherRange.append("ת");
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
                    windDes.append("��");
                    windDes.append(")");
                } else {
                    windDes.append("(");
                    windDes.append(strSc);
                    windDes.append(")");
                }
                fInfo.setmWindDes(windDes.toString());

                if (i == 0) {
                    fInfo.setmDateDes("����");
                } else if (i == 1) {
                    fInfo.setmDateDes("����");
                } else if (i == 2) {
                    fInfo.setmDateDes("����");
                } else {
                    fInfo.setmDateDes("��" + (i + 1) + "��");
                }

                forecastList.add(fInfo);
            }
        }
        WeatherForecastInfo.setS_WeatherInfoList(null);
        WeatherForecastInfo.setS_WeatherInfoList(forecastList);
    }


    //��weather���ݴ���SharedPreferences�ļ��У�xml��key-value��
    private static void saveWeatherInfo(Context context, Weather weather) {
        if (weather == null)
            return;

        LogUtil.d("v5:saveWeatherInfo ��ʼ����weather��Ϣ��SharedPreferences�ļ�,id=" + weather.getCityId()
                + ",name=" + weather.getCityName());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy��M��d��", Locale.CHINA);
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

    //�����ӷ��������ص�ȫ����������
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
            LogUtil.d("Utility.handlerCityResponse  �����������쳣��");
            // TODO: handle exception
        }

        return bResult;
    }
}
