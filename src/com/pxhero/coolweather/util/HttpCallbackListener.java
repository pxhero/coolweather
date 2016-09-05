package com.pxhero.coolweather.util;

public interface HttpCallbackListener {
	public void OnFinish(String response);
	
	public void OnException(Exception e);
	
	public void OnError(String msg);
}
