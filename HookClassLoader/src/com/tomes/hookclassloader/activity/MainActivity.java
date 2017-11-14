package com.tomes.hookclassloader.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tomes.hookclassloader.R;
import com.tomes.hookclassloader.utils.LogUtils;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		LogUtils.i("context classloader: " + getApplicationContext().getClassLoader());
		try {
			Class<?> MainActivityClass = Class.forName("com.ytx.testdemo.MainActivity");
			LogUtils.i("MainActivityClass:"+MainActivityClass);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	public void to2(View v){
		Intent intent=new Intent();
		//跳转到插件中的一个activity中，这里的插件apk是用java写的布局，因为资源加载没做处理，不能加载插件中的资源。
		intent.setComponent(new ComponentName("com.ytx.testdemo",
                "com.ytx.testdemo.MainActivity"));
		startActivity(intent);
	} 
}
