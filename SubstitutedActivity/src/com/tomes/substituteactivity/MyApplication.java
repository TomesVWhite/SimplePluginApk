package com.tomes.substituteactivity;

import com.tomes.substituteactivity.hook.HookHelper;
import com.tomes.substituteactivity.utils.LogUtils;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

	private static Context mContext;
	public static Context getContext() {
		return mContext;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		LogUtils.i("MyApplication--->onCreate()");
	}
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		LogUtils.i("MyApplication--->attachBaseContext()");
		MyApplication.mContext=base;
		try {
			//第一步：用替身activity替换真正要跳转的activity
			HookHelper.replaceSubstituteActivity(base);
			//第一步：把替身activity替换回真正要跳转的activity
			HookHelper.replaceRealActivity();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
