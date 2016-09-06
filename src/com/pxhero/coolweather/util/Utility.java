package com.pxhero.coolweather.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.pxhero.coolweather.db.CoolWeatherDB;
import com.pxhero.coolweather.model.City;
import com.pxhero.coolweather.model.Weather;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class Utility {
	
	//�������ص���������
	public synchronized static void handleWeatherResponse(Context context, String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONArray jsonArray = jsonObject.getJSONArray("HeWeather data service 3.0");
			
			if(jsonArray.length() > 0) {
				
				Weather weather = new Weather();
				
				JSONObject  weatherObj = jsonArray.getJSONObject(0);
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
				
				//��������ǰ�¶�
				String temp= nowObj.getString("tmp");
				weather.setTmp(temp);
				
				//��������ͺ�����¶�ֵ
				JSONArray dailyArray = weatherObj.getJSONArray("daily_forecast");
				JSONObject forcastObj = null;
				if(dailyArray.length() > 0) {
					forcastObj = dailyArray.getJSONObject(0);
					JSONObject tmpObj = forcastObj.getJSONObject("tmp");
					String tempMin = tmpObj.getString("min");
					weather.setTempMin(tempMin);
					//����������¶�ֵ
					String tempMax = tmpObj.getString("max");
					weather.setTempMax(tempMax);
				}
				
				saveWeatherInfo(context, weather);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	//��weather���ݴ���SharedPreferences�ļ��У�xml��key-value��
	private static void saveWeatherInfo(Context context, Weather weather) {
		if(weather == null)
			return;
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy��M��d��", Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", weather.getCityName());
		editor.putString("city_id", weather.getCityId());
		editor.putString("current_date", simpleDateFormat.format(new Date()));
		editor.putString("update_time", weather.getUpdateTime());
		editor.putString("tempMin", weather.getTempMin());
		editor.putString("tempMax", weather.getTempMax());
		editor.putString("description", weather.getDescription());
		editor.putString("tmp", weather.getTmp());
		editor.commit();
	}
	
	//�����ӷ��������ص�ȫ����������
	public synchronized static boolean handleCityResponse(CoolWeatherDB db , String Response) {
		boolean bResult = false;
		
		if(db == null || TextUtils.isEmpty(Response)) {
			bResult = false;
			return bResult;
		}
		
		try {
			JSONObject dataObject= new JSONObject(Response);
			if(dataObject != null) {
				JSONArray dataArray= dataObject.getJSONArray("city_info");
				for(int i=0; i<dataArray.length(); ++i) {
					City city = new City();
					JSONObject oneData = dataArray.getJSONObject(i);
					city.setName(oneData.getString("city"));
					city.setCounty(oneData.getString("cnty"));
					city.setId(oneData.getString("id"));
					String strLatitude = oneData.getString("lat");
					city.setLatitude(Float.valueOf(strLatitude));
					String strLongtitude = oneData.getString("lon");
					city.setLongtitude(Float.valueOf(strLongtitude));
					city.setProvince(oneData.getString("prov"));
					db.AddCityToList(city);
				}
				db.SaveAllCityToDB();
				bResult = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.d("Utility.handlerCityResponse  �����������쳣��");
			// TODO: handle exception
		}

		return bResult;
	}
}
