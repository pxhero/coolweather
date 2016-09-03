package com.coolweather.app.service;

import com.coolweather.app.receiver.AutoUpdateReceiver;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.LogUtil;
import com.coolweather.app.util.Utility;

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

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				updateWeather();
			}
		}).start();
		
		AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		int anHour = 8 * 60 * 60 * 1000;  //8Сʱ�ĺ�����
		long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
		Intent intent2 = new Intent(this, AutoUpdateReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent2, 0);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
		return super.onStartCommand(intent, flags, startId);
	}

	//����������д��SharedPreferences�ļ���
	private void updateWeather() {
		LogUtil.d("��ʼ����̨������������");
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
				}
				
				@Override
				public void OnException(Exception e) {
					// TODO Auto-generated method stub
					e.printStackTrace();
				}
				
				@Override
				public void OnError(String msg) {
					// TODO Auto-generated method stub
					LogUtil.d("�Զ�������������ʧ��!");
				}
			});
		}
	}
	
}