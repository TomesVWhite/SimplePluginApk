package com.tomes.hookamsdemo;

import android.app.Activity;
import android.content.Intent;
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
		Intent intent=new Intent(this,SecondActivity.class);
		startActivity(intent);
	}
}
