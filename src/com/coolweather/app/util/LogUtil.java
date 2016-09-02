package com.coolweather.app.util;

import android.util.Log;

public class LogUtil {
	
	private static final String LOG_TAG ="pxhero";
	private static final int VERBOSE = 1;
	private static final int DEBUG = 2;
	private static final int INFO = 3;
	private static final int WARN = 4;
	private static final int ERROR = 5;
//	private static final int NOTHING = 6;
	private static final int LEVEL = VERBOSE;
	
	public static void v(String msg) {
		if(LEVEL <=VERBOSE) {
			Log.v(LOG_TAG, msg);
		}
	}
	
	public static void d(String msg) {
		if(LEVEL <= DEBUG) {
			Log.d(LOG_TAG, msg);
		}
	}
	
	public static void i(String msg) {
		if(LEVEL <= INFO) {
			Log.i(LOG_TAG, msg);
		}
	}
	
	public static void w(String msg) {
		if(LEVEL <= WARN) {
			Log.w(LOG_TAG, msg);
		}
	}
	
	public static void e(String msg) {
		if(LEVEL <= ERROR) {
			Log.e(LOG_TAG, msg);
		}
	}
}
