package com.tomes.hookpmsdemo;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;

import com.tomes.hookamsdemo.Utils.LogUtils;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtils.i("MainActivity--->onCreate()");
		setContentView(R.layout.activity_main);
	}

	public void to2(View v){
		try {
			//使用getPackageInfo去激活pms关于getPackageInfo()的拦截
			PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			LogUtils.i("packageInfo:"+packageInfo);
			LogUtils.i("packageName:"+packageInfo.packageName);
			LogUtils.i("versionName:"+packageInfo.versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
