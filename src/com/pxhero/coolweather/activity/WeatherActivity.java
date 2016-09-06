package com.pxhero.coolweather.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.pxhero.coolweather.R;
import com.pxhero.coolweather.service.AutoUpdateService;
import com.pxhero.coolweather.util.HttpCallbackListener;
import com.pxhero.coolweather.util.HttpUtil;
import com.pxhero.coolweather.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import net.youmi.android.normal.banner.BannerManager;

public class WeatherActivity extends Activity implements OnClickListener {
	
	//UI控件
	private Button m_btnSwitchCity;
	private Button m_btnRefresh;
	private TextView m_txtCity;
	private TextView m_txtPublish;
	private TextView m_txtCurrentDate;
	private TextView m_txtWeatherDes;
	private TextView m_txtTempMin;
	private TextView m_txtTempMax;
	private TextView m_txtCurrentTemp;
	private TextView m_txtWave;
	
	private static  boolean s_bHasStartService = false;
	private static  long  s_LastShowTime = 0;
	
	private static boolean s_ShowAd = true;

	public static long getS_LastShowTime() {
		return s_LastShowTime;
	}


	public static void setS_LastShowTime(long s_LastShowTime) {
		WeatherActivity.s_LastShowTime = s_LastShowTime;
	}

	private String m_strCityId;
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.switch_city) {
			//切换城市
			Intent intent = new Intent(WeatherActivity.this, ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
		}
		else if(v.getId() == R.id.refresh_weather) {
			//刷新天气数据
			getRemoteWeatherData();
		}
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		 
		if(s_ShowAd) {
			// 实例化广告条 
			View adView = BannerManager.getInstance(this).getBanner(this);
			// 获取要嵌入广告条的布局
			LinearLayout adLayout=(LinearLayout)findViewById(R.id.adLayout);
			// 将广告条加入到布局中
			adLayout.addView(adView);
		}

		
		m_btnSwitchCity = (Button)findViewById(R.id.switch_city);
		m_btnRefresh = (Button)findViewById(R.id.refresh_weather);
		m_txtCity = (TextView)findViewById(R.id.city_name);
		m_txtPublish = (TextView)findViewById(R.id.publish_time);
		m_txtCurrentDate = (TextView)findViewById(R.id.current_date);
		m_txtWeatherDes = (TextView)findViewById(R.id.weather_desp);
		m_txtTempMin = (TextView)findViewById(R.id.temp1);
		m_txtTempMax = (TextView)findViewById(R.id.temp2);
		m_txtCurrentTemp = (TextView)findViewById(R.id.currentTmp);
		m_txtWave = (TextView)findViewById(R.id.waveText);
		m_txtWave.setVisibility(View.INVISIBLE);
		
		m_btnSwitchCity.setOnClickListener(this);
		m_btnRefresh.setOnClickListener(this);
		
		m_strCityId = new String();
		
		m_strCityId = getIntent().getStringExtra("city_id");
		String cityName = getIntent().getStringExtra("city_name");
		m_txtCity.setText(cityName);
		
		getRemoteWeatherData();
		
		AutoUpdateService.setWeatherActivity(this);
	}
	
	

	/**
	 * 
	 */
	private void getRemoteWeatherData() {
		m_txtPublish.setText("正在同步中...");
		//m_layoutWeatherInfo.setVisibility(View.INVISIBLE);  //天气数据未请求成功之前，先隐藏该布局

		
		StringBuilder remoteUrl = new StringBuilder();
		remoteUrl.append("https://api.heweather.com/x3/weather?key=61f064c29360492eb6a6d473dd1e132c&cityid=");
		remoteUrl.append(m_strCityId);
		
		HttpUtil.SendHttpRequest(remoteUrl.toString(), new HttpCallbackListener() {
			
			@Override
			public void OnFinish(String response) {
				// TODO Auto-generated method stub
				Utility.handleWeatherResponse(WeatherActivity.this, response);
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						ShowWeather();
					}
				});
			}
			
			@Override
			public void OnException(Exception e) {
				// TODO Auto-generated method stub
				e.printStackTrace();
			}
			
			@Override
			public void OnError(String msg) {
				// TODO Auto-generated method stub
				Toast.makeText(WeatherActivity.this, msg, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	public void ShowWeather() {
		//从SharedPreferences文件中读取天气数据，并显示
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
		try {
			// 用parse方法，可能会异常，所以要try-catch
			Date date = simpleDateFormat.parse(prefs.getString("update_time", ""));
		    // 获取日期实例
		    Calendar calendar = Calendar.getInstance();
		    // 将日历设置为指定的时间
		    calendar.setTime(date); 
		    //获取小时
		    int hour = calendar.get(Calendar.HOUR_OF_DAY);  
		    //获取分钟
		    int minute = calendar.get(Calendar.MINUTE);
		    String strPushlishTime = "今天" + hour + ":" + minute + "发布";
		    m_txtPublish.setText(strPushlishTime);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		m_txtCurrentDate.setText(prefs.getString("current_date", ""));
		m_txtWeatherDes.setText(prefs.getString("description", ""));
		m_txtTempMin.setText(prefs.getString("tempMin", "") + "℃");
		m_txtTempMax.setText(prefs.getString("tempMax", "") + "℃");
		m_txtCurrentTemp.setText("当前温度:" + prefs.getString("tmp", "") + "℃");
		m_txtWave.setVisibility(View.VISIBLE);
		
		s_LastShowTime = SystemClock.elapsedRealtime();
		
		if(!s_bHasStartService) {
			Intent intent = new Intent(this, AutoUpdateService.class);
			startService(intent);
			s_bHasStartService = true;
		}
	}
		
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		AutoUpdateService.setWeatherActivity(null);
	}

}
