package com.pxhero.coolweather.activity;

import android.app.Activity;
import android.app.WallpaperInfo;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pxhero.coolweather.R;
import com.pxhero.coolweather.model.WeatherForecastInfo;
import com.pxhero.coolweather.service.AutoUpdateService;
import com.pxhero.coolweather.util.BitmapCache;
import com.pxhero.coolweather.util.LogUtil;
import com.pxhero.coolweather.util.ProcessDataCallbackListener;
import com.pxhero.coolweather.util.ProcessDataUtil;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeatherActivity_v2 extends Activity implements OnClickListener {
	
	//UI�ؼ�
	private Button m_btnSwitchCity; //�л����а�ť
	private Button m_btnRefresh;//ˢ�°�ť
	private TextView m_txtCity;//������
	private TextView m_txtPublish;//����ʱ��
	private TextView m_txtWeatherDes;//��ǰ��������
	private TextView m_txtCurrentTemp;//��ǰ�¶�
	private TextView m_txtCurrentAirDes; //��ǰ������������
	private NetworkImageView m_imgCurrentWeather;//��ǰ����ͼƬ����

	private LinearLayout mForecastScrollLayout;

	private static  boolean s_bHasStartService = false;
	private static  long  s_LastShowTime = 0;
	
	private RequestQueue m_requestQueue;
	private ImageLoader m_imageLoader;

	public static long getS_LastShowTime() {
		return s_LastShowTime;
	}


	public static void setS_LastShowTime(long s_LastShowTime) {
		WeatherActivity_v2.s_LastShowTime = s_LastShowTime;
	}

	private String m_strCityId;
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.switch_city) {
			//�л�����
			Intent intent = new Intent(WeatherActivity_v2.this, ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
		}
		else if(v.getId() == R.id.refresh_weather) {
			//ˢ����������
			getRemoteWeatherData();
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_v2_layout);

		m_requestQueue = Volley.newRequestQueue(this); //��ʼ��volley
		m_imageLoader = new ImageLoader(m_requestQueue, new BitmapCache());
		
		m_btnSwitchCity = (Button)findViewById(R.id.switch_city);
		m_btnRefresh = (Button)findViewById(R.id.refresh_weather);
		m_txtCity = (TextView)findViewById(R.id.city_name);
		m_txtPublish = (TextView)findViewById(R.id.publish_time);
		m_txtWeatherDes = (TextView)findViewById(R.id.weather_desp);
		m_txtCurrentTemp = (TextView)findViewById(R.id.currentTmp);
		m_txtCurrentAirDes = (TextView) findViewById(R.id.cur_air_des);
		m_imgCurrentWeather = (NetworkImageView) findViewById(R.id.current_weather_img);

		mForecastScrollLayout = (LinearLayout)findViewById(R.id.forecastScroll);

		m_btnSwitchCity.setOnClickListener(this);
		m_btnRefresh.setOnClickListener(this);
		
		m_strCityId = new String();
		m_strCityId = getIntent().getStringExtra("city_id");
		String cityName = getIntent().getStringExtra("city_name");
		m_txtCity.setText(cityName);
		
		getRemoteWeatherData();
		
		AutoUpdateService.setWeatherActivityV2(this);
	}
	

	/**
	 * 
	 */
	private void getRemoteWeatherData() {
		m_txtPublish.setText("����ͬ����...");
		//m_layoutWeatherInfo.setVisibility(View.INVISIBLE);  //��������δ����ɹ�֮ǰ�������ظò���

		StringBuilder remoteUrl = new StringBuilder();
		remoteUrl.append("https://free-api.heweather.com/v5/weather?key=61f064c29360492eb6a6d473dd1e132c&city=");
		remoteUrl.append(m_strCityId);


		StringRequest stringRequest = new StringRequest(remoteUrl.toString(),
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						//Log.d("TAG", response);
						LogUtil.d("v5:getRemoteWeatherData:url=" + "����ɹ�����" + response);
						ProcessDataUtil.ProcessWeatherData(WeatherActivity_v2.this, response, new ProcessDataCallbackListener() {
							@Override
							public void OnFinish() {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										LogUtil.d("v5:getRemoteWeatherData:url=" + "�л������߳�׼��ִ��ShowWeather");
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
		LogUtil.d("v5:getRemoteWeatherData:url=" + remoteUrl.toString());
	}
	
	public void ShowWeather() {
		//��SharedPreferences�ļ��ж�ȡ�������ݣ�����ʾ
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		String cityId =new String();
		cityId = prefs.getString("city_id","");
		if(!cityId.equals(m_strCityId)) {
			LogUtil.d("v5:ShowWeather:cityid not equal");
			return;
		}

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
		
		m_txtWeatherDes.setText(prefs.getString("description", ""));
		m_txtCurrentTemp.setText(prefs.getString("tmp", "") + "��");
		m_txtCurrentAirDes.setText(prefs.getString("air_des",""));

		//��ȡ����ͼƬ
		String code = prefs.getString("code","");
		StringBuilder imageUrl = new StringBuilder();
		imageUrl.append("http://files.heweather.com/cond_icon/");
		imageUrl.append(code);
		imageUrl.append(".png");



//		m_imgCurrentWeather.setDefaultImageResId(R.drawable.weather_default);
//		m_imgCurrentWeather.setErrorImageResId(R.drawable.weather_default);
		m_imgCurrentWeather.setImageUrl(imageUrl.toString(), m_imageLoader);

		s_LastShowTime = SystemClock.elapsedRealtime();
		
		if(!s_bHasStartService) {
			Intent intent = new Intent(this, AutoUpdateService.class);
			startService(intent);
			s_bHasStartService = true;
		}

		//��ʾ�����������ϢHorizontalScrollView
		View itemView = null;
		TextView dateDesText = null;
		NetworkImageView forecastImageView = null;
		TextView dateText = null;
		TextView weatherText = null;
		TextView tempText = null;
		TextView windText = null;
		boolean isFirstItem = true;
		List<WeatherForecastInfo> WeatherForecastList = WeatherForecastInfo.getS_WeatherInfoList();
		if(WeatherForecastList != null && WeatherForecastList.size()>0) {

			mForecastScrollLayout.removeAllViews();

			for(WeatherForecastInfo wInfo : WeatherForecastList) {

				if(!m_strCityId.equals(wInfo.getmCityId()))
					continue;

				itemView = getLayoutInflater().inflate(R.layout.daily_forecast_layout,null);
				dateDesText = (TextView)itemView.findViewById(R.id.forecast_dateDes);
				dateDesText.setText(wInfo.getmDateDes());

				forecastImageView = (NetworkImageView)itemView.findViewById(R.id.forecast_weather_img);
				StringBuilder imageForcastUrl = new StringBuilder();
				imageForcastUrl.append("http://files.heweather.com/cond_icon/");
				imageForcastUrl.append(wInfo.getmCodeId());
				imageForcastUrl.append(".png");
				forecastImageView.setImageUrl(imageForcastUrl.toString(),m_imageLoader);

				dateText = (TextView)itemView.findViewById(R.id.forecast_date);
				dateText.setText(wInfo.getmDate());

				weatherText = (TextView)itemView.findViewById(R.id.forcast_weather_des);
				weatherText.setText(wInfo.getmWeatherDes());

				tempText = (TextView)itemView.findViewById(R.id.forecast_tmp_range);
				tempText.setText(wInfo.getmTemDes());

				windText = (TextView)itemView.findViewById(R.id.forecast_wind);
				windText.setText(wInfo.getmWindDes());

				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				if(isFirstItem) {
					lp.setMargins(0, 0, 0, 0);
					isFirstItem = false;
				}
				else {
					int dpValue = 30; // margin in dips
					float d = this.getResources().getDisplayMetrics().density;
					int marginLeftInPixel = (int)(dpValue * d); // margin in pixels
					lp.setMargins(marginLeftInPixel, 0, 0, 0);
				}
				itemView.setLayoutParams(lp);

				mForecastScrollLayout.addView(itemView);
			}
		}

	}
		
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//m_requestQueue.stop();
		AutoUpdateService.setWeatherActivityV2(null);
	}

}
