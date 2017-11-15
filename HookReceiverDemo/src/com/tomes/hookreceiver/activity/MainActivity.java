package com.tomes.hookreceiver.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tomes.hookreceiver.R;
import com.tomes.hookreceiver.R.layout;
import com.tomes.hookreceiver.utils.LogUtils;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void send2(View v){
		Intent intent=new Intent();
		intent.setAction("com.tomes.testdemo");
		sendBroadcast(intent);
	}
}
