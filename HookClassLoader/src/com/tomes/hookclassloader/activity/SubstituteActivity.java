package com.tomes.hookclassloader.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.tomes.hookclassloader.utils.LogUtils;

/**替身activity
 * @author Tomes
 *
 */
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
