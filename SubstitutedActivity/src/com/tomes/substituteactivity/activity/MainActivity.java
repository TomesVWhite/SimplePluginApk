package com.tomes.substituteactivity.activity;

import com.tomes.substituteactivity.R;
import com.tomes.substituteactivity.R.layout;
import com.tomes.substituteactivity.utils.LogUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		LogUtils.i("MainActivity--->onCreate()");
	}

	public void toReal(View v){
		Intent intent=new Intent(this,RealActivity.class);
		startActivity(intent);
	}
}
