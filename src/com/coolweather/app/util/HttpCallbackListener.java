package com.coolweather.app.util;

public interface HttpCallbackListener {
	public void OnFinish(String response);
	
	public void OnError(Exception e);
}
