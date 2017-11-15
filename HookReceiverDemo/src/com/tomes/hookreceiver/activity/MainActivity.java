package com.tomes.hookreceiver.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.tomes.hookreceiver.R;

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
	
	public void to2(View v){
		Intent intent=new Intent();
		//跳转到插件中的一个activity中，这里的插件apk是用java写的布局，因为资源加载没做处理，不能加载插件中的资源。
		intent.setComponent(new ComponentName("com.ytx.testdemo",
                "com.ytx.testdemo.MainActivity"));
		startActivity(intent);
		Handler handler=new Handler();
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Intent a=new Intent();
				a.setAction("com.tomes.ijm");
				sendBroadcast(a);
			}
		}, 1000);
	}
}
