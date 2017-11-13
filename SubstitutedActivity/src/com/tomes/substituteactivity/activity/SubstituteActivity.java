package com.tomes.substituteactivity.activity;

import com.tomes.substituteactivity.utils.LogUtils;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SubstituteActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView textView=new TextView(this);
		textView.setText("this is the SubstituteActivity@@@@!!!");
		setContentView(textView);
		LogUtils.i("SubstituteActivity--->onCreate()");
	}
}
