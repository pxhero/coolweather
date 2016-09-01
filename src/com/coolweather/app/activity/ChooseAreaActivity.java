package com.coolweather.app.activity;

import java.util.ArrayList;

import com.coolweather.app.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.util.LogUtil;

import android.R.menu;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ChooseAreaActivity extends Activity implements OnClickListener  {
	
	private EditText m_editProvince;
	private Button  m_btnSearch;
	private ListView m_listViewCity;
	private ProgressDialog m_ProgressDialog;
	
	private CoolWeatherDB  m_coolWeatherDB;
	
	private ArrayList<String> m_listCityData = new ArrayList<String>();
	private ArrayAdapter<String> m_adapterCity;
	
	private static final int SHOW_CITY = 1;
	
	private Handler m_handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		}
		
	};
	


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.SearchBtn) {
			String province = m_editProvince.getText().toString();
			if(TextUtils.isEmpty(province)) {
				LogUtil.d("please input province name first");
				return;
			}
			
			
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		m_editProvince = (EditText)findViewById(R.id.provinceEdit);
		m_btnSearch = (Button)findViewById(R.id.SearchBtn);
		m_btnSearch.setOnClickListener(this);
		
		m_listViewCity = (ListView)findViewById(R.id.cityListView);
		
		m_adapterCity = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, m_listCityData);
		m_listViewCity.setAdapter(m_adapterCity);
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	
}
