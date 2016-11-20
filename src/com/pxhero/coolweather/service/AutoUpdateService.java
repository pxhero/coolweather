package com.pxhero.coolweather.service;

import com.pxhero.coolweather.activity.WeatherActivity_v2;
import com.pxhero.coolweather.receiver.AutoUpdateReceiver;
import com.pxhero.coolweather.util.HttpCallbackListener;
import com.pxhero.coolweather.util.HttpUtil;
import com.pxhero.coolweather.util.LogUtil;
import com.pxhero.coolweather.util.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class AutoUpdateService extends Service {
	
	private static WeatherActivity_v2 weatherActivityV2;
	

	public static WeatherActivity_v2 getWeatherActivityV2() {
		return weatherActivityV2;
	}

	public static void setWeatherActivityV2(WeatherActivity_v2 weatherActivityV2) {
		AutoUpdateService.weatherActivityV2 = weatherActivityV2;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		int oneMinute = 60 * 1000;  //1分钟的毫秒数
		if(SystemClock.elapsedRealtime() - WeatherActivity_v2.getS_LastShowTime() >= oneMinute) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					updateWeather();
				}
			}).start();
		}

		AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		int eightHours = 8 * 60 * 60 * 1000;  //8小时的毫秒数
		long triggerAtTime = SystemClock.elapsedRealtime() + eightHours;
		Intent intent2 = new Intent(this, AutoUpdateReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent2, 0);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
		return super.onStartCommand(intent, flags, startId);
	}

	//更新天气，写入SharedPreferences文件中
	private void updateWeather() {
		LogUtil.d("开始到后台更新天气数据");
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String cityId = prefs.getString("city_id", "");
		if(!TextUtils.isEmpty(cityId)) {
			StringBuilder remoteUrl = new StringBuilder();
			remoteUrl.append("https://api.heweather.com/x3/weather?key=61f064c29360492eb6a6d473dd1e132c&cityid=");
			remoteUrl.append(cityId);
			HttpUtil.SendHttpRequest(remoteUrl.toString(), new HttpCallbackListener() {
				
				@Override
				public void OnFinish(String response) {
					// TODO Auto-generated method stub
					Utility.handleWeatherResponse(AutoUpdateService.this, response);
				   if(weatherActivityV2 !=null && !weatherActivityV2.isFinishing()) {
					   weatherActivityV2.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							LogUtil.d("后台服务以获取到新数据，开始更新界面");
							// TODO Auto-generated method stub
							weatherActivityV2.ShowWeather();
						}
					});
				   }
				}
				
				@Override
				public void OnException(Exception e) {
					// TODO Auto-generated method stub
					e.printStackTrace();
				}
				
				@Override
				public void OnError(String msg) {
					// TODO Auto-generated method stub
					LogUtil.d("自动更新天气数据失败!");
				}
			});
		}
	}
	
}
