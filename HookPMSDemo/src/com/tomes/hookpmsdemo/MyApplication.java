package com.tomes.hookpmsdemo;

import android.app.Application;
import android.content.Context;

import com.tomes.hookamsdemo.Utils.LogUtils;

/**该工程的主要目的在于hook住pms
 * @author Tomes
 *
 */
public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtils.i("MyApplication--->onCreate()");
	}
	
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		LogUtils.i("MyApplication--->attachBaseContext()");
		try {
			HookHelper.hookPMS(base);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
