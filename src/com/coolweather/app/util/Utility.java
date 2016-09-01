package com.coolweather.app.util;

import org.json.JSONArray;
import org.json.JSONObject;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;

import android.text.TextUtils;

public class Utility {
	
	//解析从服务器返回的全国城市数据
	public synchronized static boolean handlerCityResponse(CoolWeatherDB db , String Response) {
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
					db.SaveCity(city);
				}
				
				bResult = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.d("Utility.handlerCityResponse  函数发生了异常！");
			// TODO: handle exception
		}

		return bResult;
	}
}
