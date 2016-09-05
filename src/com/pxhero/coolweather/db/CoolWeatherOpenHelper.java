package com.pxhero.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {

	private static final String CREATE_CITY_TABLE ="create table City("
			+ "id integer primary key autoincrement,"
			+ "city_name text,"
			+ "county_name text,"
			+ "city_id text,"
			+ "latitude real,"
			+ "lontitude real,"
			+ "province_name text"
			+ ")";

	public CoolWeatherOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_CITY_TABLE); //´´½¨City±í
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("drop table if exists City");
		onCreate(db);
	}

}
