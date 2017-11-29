package com.tomes.servicepluginimp;

import com.tomes.servicepluginimp.utils.LogUtils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RealService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		LogUtils.i("RealService--->onCreate()");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtils.i("RealService--->onStartCommand()");
		return super.onStartCommand(intent, flags, startId);
	}
}
