package com.pxhero.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pxhero.coolweather.R;
import com.pxhero.coolweather.db.CoolWeatherDB;
import com.pxhero.coolweather.model.City;
import com.pxhero.coolweather.util.LogUtil;
import com.pxhero.coolweather.util.ProcessDataCallbackListener;
import com.pxhero.coolweather.util.ProcessDataUtil;


import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends Activity implements OnClickListener {

	private EditText m_editProvince;
	private Button m_btnSearch;
	private ListView m_listViewCity;
	private ProgressDialog m_ProgressDialog;

	private CoolWeatherDB m_coolWeatherDB;

	private List<String> m_listCityData = new ArrayList<String>();
	private ArrayAdapter<String> m_adapterCity;

	private List<City> m_listCity = new ArrayList<City>(); // 当前省下面的城市
	private City m_selectedCity; // 当前选择的城市
	private String m_CityOrProvinceName; // 当前输入的省的名称

	private RequestQueue m_requestQueue;

	// 显示加载进度框
	private void showProgressDialog() {
		if (m_ProgressDialog == null) {
			m_ProgressDialog = new ProgressDialog(this);
			m_ProgressDialog.setMessage("正在加载...");
			m_ProgressDialog.setCanceledOnTouchOutside(false);
		}
		m_ProgressDialog.show();
	}

	// 隐藏进度框
	private void CloseProgressDialog() {
		if (m_ProgressDialog != null) {
			m_ProgressDialog.dismiss();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.SearchBtn) {

			m_CityOrProvinceName = m_editProvince.getText().toString();
			if (TextUtils.isEmpty(m_CityOrProvinceName)) {
				LogUtil.d("please input province name first");
				Toast.makeText(this, "未输入省份名或城市名，请输入~", Toast.LENGTH_SHORT).show();
				return;
			}

			showProgressDialog();

			searchCity();
		}
	}

	private void searchCity() {
		List<City> allCityList = m_coolWeatherDB.getCityList();
		if (!allCityList.isEmpty()) {

			// 清空数据和界面
			m_listCity.clear();
			m_listCityData.clear();
			m_adapterCity.notifyDataSetChanged();

			StringBuilder itemContentBuilder = new StringBuilder();
			for (City city : allCityList) {
				String province = city.getProvince();
				String cityName = city.getName();
				if (province.contains(m_CityOrProvinceName) || m_CityOrProvinceName.contains(province)
						|| cityName.contains(m_CityOrProvinceName) || m_CityOrProvinceName.contains(cityName)) {
					m_listCity.add(city);
					itemContentBuilder.setLength(0);

					itemContentBuilder.append(city.getCounty());
					itemContentBuilder.append("," + city.getProvince());
					itemContentBuilder.append("," + city.getName());
					m_listCityData.add(itemContentBuilder.toString());
				}
			}

			if (m_listCity.isEmpty()) {
				Toast.makeText(this, "未搜索到相关省份或城市数据，请检查您输入的名称是否正确", Toast.LENGTH_LONG).show();
			}

			m_adapterCity.notifyDataSetChanged();
			m_listViewCity.setSelection(0);

			CloseProgressDialog();
		} else {
			getCityListFromRemote();
		}
	}

	// 从服务器上获取全国城市l数据，并存入db中
	private void getCityListFromRemote() {
		//old
		//String remoteAddress = "https://api.heweather.com/x3/citylist?search=allchina&key=61f064c29360492eb6a6d473dd1e132c";

		String remoteAddress = "http://files.heweather.com/china-city-list.json";


        StringRequest stringRequest = new StringRequest(remoteAddress,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //当前是主线程

                        ProcessDataUtil.ProcessCityData(m_coolWeatherDB, response, new ProcessDataCallbackListener() {
                            @Override
                            public void OnFinish() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        searchCity();
                                    }
                                });
                            }

                            @Override
                            public void OnError() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ChooseAreaActivity.this, "解析城市数据失败。。。", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CloseProgressDialog();
                Toast.makeText(ChooseAreaActivity.this, "获取城市数据失败。。。", Toast.LENGTH_SHORT).show();
            }
        });
        m_requestQueue.add(stringRequest);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);

        //LogUtil.d("当前线程id=" +String.valueOf(Thread.currentThread().getId()));

		boolean bFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String cityId = sharedPreferences.getString("city_id", "");
		String cityName = sharedPreferences.getString("city_name", "");
		boolean bSelected = sharedPreferences.getBoolean("city_selected", false);
		if (!TextUtils.isEmpty(cityId) && !TextUtils.isEmpty(cityName) && bSelected && !bFromWeatherActivity) {
			Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity_v2.class);
			intent.putExtra("city_id", cityId);
			intent.putExtra("city_name", cityName);
			startActivity(intent);
			finish();
		}

        m_requestQueue = Volley.newRequestQueue(this);

		setContentView(R.layout.choose_area);
		m_editProvince = (EditText) findViewById(R.id.provinceEdit);
		m_btnSearch = (Button) findViewById(R.id.SearchBtn);
		m_btnSearch.setOnClickListener(this);

		m_coolWeatherDB = CoolWeatherDB.getInstance(this);

		m_listViewCity = (ListView) findViewById(R.id.cityListView);
		m_adapterCity = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, m_listCityData);
		m_listViewCity.setAdapter(m_adapterCity);

		m_listViewCity.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {

				m_selectedCity = m_listCity.get(index);
				LogUtil.d(m_selectedCity.toString());
				Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity_v2.class);
				intent.putExtra("city_id", m_selectedCity.getId());
				intent.putExtra("city_name", m_selectedCity.getName());
				startActivity(intent);
				finish();
			}
		});

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
