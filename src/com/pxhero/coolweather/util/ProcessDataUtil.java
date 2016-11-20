package com.pxhero.coolweather.util;

import android.content.Context;

import com.pxhero.coolweather.db.CoolWeatherDB;

/**
 * Created by pengxianheng on 2016/9/27.
 */


public class ProcessDataUtil {
    public static void ProcessCityData(final CoolWeatherDB db , final String data, final ProcessDataCallbackListener listener) {
        if(db ==null || data.isEmpty()) {
            LogUtil.d("ProcessCityData: param is invalid");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean result = Utility.handleCityResponse(db, data); // 解析返回的数据，并存入数据库中
                if(result && listener!=null) {
                    listener.OnFinish();
                }
                else if(listener!=null){
                    listener.OnError();
                }
            }
        }).start();

    }

    public static void ProcessWeatherData(final Context context, final String data,final ProcessDataCallbackListener listener) {
        if(context ==null || data.isEmpty()) {
            LogUtil.d("ProcessWeatherData: param is invalid");
            return;
        }
        LogUtil.d("ProcessWeatherData: Start a thread to parse Weather response");
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.d("v5:ProcessWeatherData run" + data);
                Utility.handleWeatherResponse(context, data);
                if(listener!=null) {
                    listener.OnFinish();
                }
            }
        }).start();
    }
}
