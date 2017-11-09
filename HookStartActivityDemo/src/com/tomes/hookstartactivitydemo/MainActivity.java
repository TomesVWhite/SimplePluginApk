package com.tomes.hookstartactivitydemo;

import com.tomes.hookstartactivitydemo.Utils.LogUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtils.i("MainActivity--->onCreate()");
		setContentView(R.layout.activity_main);
	}

	public void to2(View v){
		Intent intent=new Intent(this,SecondActivity.class);
		startActivity(intent);
	}
}
