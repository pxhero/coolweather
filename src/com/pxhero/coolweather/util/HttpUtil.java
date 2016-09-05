package com.pxhero.coolweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class HttpUtil {
	
	public static boolean isNetAvailable() {
		boolean isAvailable = false;
		
		ConnectivityManager connectivityManager = (ConnectivityManager)CoolWeatherApplication.getContext().
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = connectivityManager.getActiveNetworkInfo();
		if(network != null && network.isAvailable())
			isAvailable = true;
		
		return isAvailable;
	}
	
	public static void SendHttpRequest(final String address, final HttpCallbackListener listener) {
		
		if(!isNetAvailable()) {
			String msg = "当前无网络连接，请检查~";
			if(listener !=null) {
				listener.OnError(msg);
			}
			return;
		}
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				HttpURLConnection httpURLConnection = null;
				
				try {
					URL url = new URL(address);
					httpURLConnection = (HttpURLConnection)url.openConnection();
					httpURLConnection.setRequestMethod("GET");
					httpURLConnection.setConnectTimeout(8000);
					httpURLConnection.setReadTimeout(8000);
					httpURLConnection.setDoInput(true);
					httpURLConnection.setDoOutput(true);
					httpURLConnection.addRequestProperty("Accept-Language", "zh-CN");
					
					InputStream inputStream = httpURLConnection.getInputStream();
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
					StringBuilder response = new StringBuilder();
					String line = bufferedReader.readLine();
					while(line != null) {
						response.append(line);
						response.append("\n");
						line = bufferedReader.readLine();
					}
					
					if(listener != null) {
						listener.OnFinish(response.toString());
					}
					
					
				} catch (Exception e) {
					// TODO: handle exception
					if(listener != null) {
						listener.OnException(e);
					}
				}
				finally {
					if(httpURLConnection != null) {
						httpURLConnection.disconnect();
					}
				}
			}
		}).start();;


	}
}
