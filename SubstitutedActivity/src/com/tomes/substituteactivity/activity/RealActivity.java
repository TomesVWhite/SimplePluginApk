package com.tomes.substituteactivity.activity;

import com.tomes.substituteactivity.utils.LogUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class RealActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView textView=new TextView(this);
		textView.setText("this is the RealActivity!!!");
		setContentView(textView);
		LogUtils.i("RealActivity--->onCreate()");
	}
}
