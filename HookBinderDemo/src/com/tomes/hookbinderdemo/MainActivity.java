package com.tomes.hookbinderdemo;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tomes.hookstartactivitydemo.Utils.LogUtils;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtils.i("MainActivity--->onCreate()");
		setContentView(R.layout.activity_main);
	}

	public void to2(View v){
		//获取剪切板服务
		ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//		clipboardManager.setPrimaryClip(ClipData.newPlainText(null, "我就是想粘贴这个"));
		//激活IClipboardInvocationHandler类里动态代理的getPrimaryClip();
		clipboardManager.getPrimaryClip();
	}
}
