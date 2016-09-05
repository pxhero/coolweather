package com.pxhero.coolweather.receiver;

import com.pxhero.coolweather.service.AutoUpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoUpdateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Intent intent2  = new Intent(context, AutoUpdateService.class);
		context.startService(intent2);
	}

}
