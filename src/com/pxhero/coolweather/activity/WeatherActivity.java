package com.pxhero.coolweather.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pxhero.coolweather.R;
import com.pxhero.coolweather.service.AutoUpdateService;
import com.pxhero.coolweather.util.ProcessDataCallbackListener;
import com.pxhero.coolweather.util.ProcessDataUtil;

import net.youmi.android.normal.banner.BannerManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WeatherActivity extends Activity implements OnClickListener {
	
	//UI�ؼ�
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
	
	private boolean m_showAd = true;

	private RequestQueue m_requestQueue;

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
			//�л�����
			Intent intent = new Intent(WeatherActivity.this, ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
		}
		else if(v.getId() == R.id.refresh_weather) {
			//ˢ����������
			getRemoteWeatherData();
		}
	}

	private boolean IsStartWifi(final Context context) {
		try {
			WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			return (wm!=null && WifiManager.WIFI_STATE_ENABLED == wm.getWifiState());
		} catch (Exception e) {
		}
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);


		if(IsStartWifi(this)) {
			m_showAd = true;
		}
		else {
			m_showAd = false;
		}
		 
		if(m_showAd) {
			// ʵ��������� 
			View adView = BannerManager.getInstance(this).getBanner(this);
			// ��ȡҪǶ�������Ĳ���
			LinearLayout adLayout=(LinearLayout)findViewById(R.id.adLayout);
			// ����������뵽������
			adLayout.addView(adView);
		}

		m_requestQueue = Volley.newRequestQueue(this);

		
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
		m_txtPublish.setText("����ͬ����...");
		//m_layoutWeatherInfo.setVisibility(View.INVISIBLE);  //��������δ����ɹ�֮ǰ�������ظò���


		StringBuilder remoteUrl = new StringBuilder();
		remoteUrl.append("https://api.heweather.com/x3/weather?key=61f064c29360492eb6a6d473dd1e132c&cityid=");
		remoteUrl.append(m_strCityId);

		StringRequest stringRequest = new StringRequest(remoteUrl.toString(),
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						//Log.d("TAG", response);
						ProcessDataUtil.ProcessWeatherData(WeatherActivity.this, response, new ProcessDataCallbackListener() {
							@Override
							public void OnFinish() {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										ShowWeather();
									}
								});
							}

							@Override
							public void OnError() {

							}
						});
					}
				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e("TAG", error.getMessage(), error);
			}
		});

		m_requestQueue.add(stringRequest);


/*		HttpUtil.SendHttpRequest(remoteUrl.toString(), new HttpCallbackListener() {
			
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
		});*/
	}
	
	public void ShowWeather() {
		//��SharedPreferences�ļ��ж�ȡ�������ݣ�����ʾ
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
		try {
			// ��parse���������ܻ��쳣������Ҫtry-catch
			Date date = simpleDateFormat.parse(prefs.getString("update_time", ""));
		    // ��ȡ����ʵ��
		    Calendar calendar = Calendar.getInstance();
		    // ����������Ϊָ����ʱ��
		    calendar.setTime(date); 
		    //��ȡСʱ
		    int hour = calendar.get(Calendar.HOUR_OF_DAY);  
		    //��ȡ����
		    int minute = calendar.get(Calendar.MINUTE);
		    String strPushlishTime = "����" + hour + ":" + minute + "����";
		    m_txtPublish.setText(strPushlishTime);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		m_txtCurrentDate.setText(prefs.getString("current_date", ""));
		m_txtWeatherDes.setText(prefs.getString("description", ""));
		m_txtTempMin.setText(prefs.getString("tempMin", "") + "��");
		m_txtTempMax.setText(prefs.getString("tempMax", "") + "��");
		m_txtCurrentTemp.setText("��ǰ�¶�:" + prefs.getString("tmp", "") + "��");
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
