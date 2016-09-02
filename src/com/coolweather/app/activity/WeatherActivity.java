package com.coolweather.app.activity;

import com.coolweather.app.R;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
	private LinearLayout m_layoutWeatherInfo;
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.switch_city) {
			//�л�����
			
		}
		else if(v.getId() == R.id.refresh_weather) {
			//ˢ����������
			
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		m_btnSwitchCity = (Button)findViewById(R.id.switch_city);
		m_btnRefresh = (Button)findViewById(R.id.refresh_weather);
		m_txtCity = (TextView)findViewById(R.id.city_name);
		m_txtPublish = (TextView)findViewById(R.id.publish_time);
		m_txtPublish.setText("����ͬ����...");
		m_txtCurrentDate = (TextView)findViewById(R.id.current_date);
		m_txtWeatherDes = (TextView)findViewById(R.id.weather_desp);
		m_txtTempMin = (TextView)findViewById(R.id.temp1);
		m_txtTempMax = (TextView)findViewById(R.id.temp2);
		m_layoutWeatherInfo = (LinearLayout)findViewById(R.id.weather_info_layout);
		m_layoutWeatherInfo.setVisibility(View.INVISIBLE);  //��������δ����ɹ�֮ǰ�������ظò���
		
		m_btnSwitchCity.setOnClickListener(this);
		m_btnRefresh.setOnClickListener(this);
		
		String cityId = getIntent().getStringExtra("city_id");
		String cityName = getIntent().getStringExtra("city_name");
		m_txtCity.setText(cityName);
		
		StringBuilder remoteUrl = new StringBuilder();
		remoteUrl.append("https://api.heweather.com/x3/weather?key=61f064c29360492eb6a6d473dd1e132c&cityid=");
		remoteUrl.append(cityId);
		
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
	
	private void ShowWeather() {
		//��SharedPreferences�ļ��ж�ȡ�������ݣ�����ʾ
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		m_txtPublish.setText("����ʱ��:" + prefs.getString("update_time", ""));
		m_txtCurrentDate.setText(prefs.getString("current_date", ""));
		m_txtWeatherDes.setText(prefs.getString("description", ""));
		m_txtTempMin.setText(prefs.getString("tempMin", "") + "��");
		m_txtTempMax.setText(prefs.getString("tempMax", "") + "��");
		m_layoutWeatherInfo.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
