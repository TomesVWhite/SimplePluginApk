package com.tomes.hookstartactivitydemo;

import com.tomes.hookstartactivitydemo.Utils.LogUtils;

import android.app.Application;
import android.content.Context;

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
			HookHelper.hook(base);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
