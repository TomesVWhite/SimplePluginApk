package com.tomes.hookamsdemo;

import android.app.Application;
import android.content.Context;

import com.tomes.hookamsdemo.Utils.LogUtils;

/**该工程的主要目的在于hook住startActivity()
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
			HookHelper.hookAMS(base);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
