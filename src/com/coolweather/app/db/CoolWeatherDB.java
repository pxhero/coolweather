package com.coolweather.app.db;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.model.City;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {
	
	class City_Table {
		private static final String CITY_NAME = "city_name";
		private static final String COUNTY_NAME ="county_name";
		private static final String CITY_ID = "city_id";
		private static final String LATITUDE ="latitude";
		private static final String LONTITUDE = "lontitude";
		private static final String PROVINCE_NAME = "province_name";
	}
	
	private final String DB_NAME ="cool_weather"; //���ݿ�����
	
	private final int VERSION = 1; //���ݿ�汾
	
	private static CoolWeatherDB coolWeatherDB;
	
	private SQLiteDatabase db;
	
	private List<City> cityList = new ArrayList<City>();
	
	//�����췽��˽�л�������ģʽ��
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper weatherOpenHelper = new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
		db = weatherOpenHelper.getWritableDatabase();  //��ȡdb�������ã�
	}
	
	//��ȡCoolWeatherDB��ʵ�� (synchronized ���̰߳�ȫ��)
	public synchronized static CoolWeatherDB getInstance(Context context) {
		if(coolWeatherDB == null) {
			coolWeatherDB =new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}
	
	
	//��city��Ϣ�������ݿ��е�City��
	public void SaveCity(City city) {
		if(city != null) {
			ContentValues contentValues = new ContentValues();
			contentValues.put(CoolWeatherDB.City_Table.CITY_NAME, city.getName());
			contentValues.put(CoolWeatherDB.City_Table.COUNTY_NAME, city.getCounty());
			contentValues.put(CoolWeatherDB.City_Table.CITY_ID, city.getId());
			contentValues.put(CoolWeatherDB.City_Table.LATITUDE, city.getLatitude());
			contentValues.put(CoolWeatherDB.City_Table.LONTITUDE, city.getLongtitude());
			contentValues.put(CoolWeatherDB.City_Table.PROVINCE_NAME, city.getProvince());
			db.insert("City", null, contentValues);
		}
	}
	
	private void ReadAllCityFromDB() {
		Cursor cursor = db.query("City", null, null, null, null, null, null);
		if(cursor.moveToFirst()) {
			do {
				//��ȡ����
				City city =new City();
				city.setName(cursor.getString(cursor.getColumnIndex(CoolWeatherDB.City_Table.CITY_NAME)));
				city.setCounty(cursor.getString(cursor.getColumnIndex(CoolWeatherDB.City_Table.COUNTY_NAME)));
				city.setId(cursor.getString(cursor.getColumnIndex(CoolWeatherDB.City_Table.CITY_ID)));
				city.setLatitude(cursor.getFloat(cursor.getColumnIndex(CoolWeatherDB.City_Table.LATITUDE)));
				city.setLongtitude(cursor.getFloat(cursor.getColumnIndex(CoolWeatherDB.City_Table.LONTITUDE)));
				city.setProvince(cursor.getString(cursor.getColumnIndex(CoolWeatherDB.City_Table.PROVINCE_NAME)));
				cityList.add(city);
			} while (cursor.moveToNext());
			cursor.close();
		}
	}
	
	public List<City> getCityList() {
		if(cityList.isEmpty()) {
			ReadAllCityFromDB();
		}
		return cityList;
	}
	
	
	
}
