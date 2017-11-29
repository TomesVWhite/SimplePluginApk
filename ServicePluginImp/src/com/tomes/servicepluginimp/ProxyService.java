package com.tomes.servicepluginimp;

import com.tomes.servicepluginimp.utils.LogUtils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ProxyService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		LogUtils.i("ProxyService--->onCreate()");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtils.i("ProxyService--->onStartCommand()");
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// 分发Service
		LogUtils.i("ProxyService--->onStart()");
		try {
			ProxyServiceManager.onStart(intent, startId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onStart(intent, startId);
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtils.i("ProxyService--->onDestroy()");
	}
}
