package com.coolweather.app.util;

public interface HttpCallbackListener {
	public void OnFinish(String response);
	
	public void OnException(Exception e);
	
	public void OnError(String msg);
}
