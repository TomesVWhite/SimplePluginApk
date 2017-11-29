package com.tomes.servicepluginimp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}

	public void startProxyservice(View v) {
		// 启动服务
//		Intent intent = new Intent(this, RealService.class);
		Intent intent = new Intent();
		//启动插件中的TestService服务
		intent.setClassName("com.ytx.testdemo", "com.ytx.testdemo.TestService");
		startService(intent);
	}
}
