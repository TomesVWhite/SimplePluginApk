package com.tomes.servicepluginimp;

import java.io.File;

import android.app.Application;
import android.content.Context;

import com.tomes.servicepluginimp.utils.LogUtils;
import com.tomes.servicepluginimp.utils.Utils;

/**Service插件化的实现
 * @author Tomes
 *
 */
public class MyApplication extends Application {
	private static Context mContext;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		mContext = base;
		try {
			HookHelper.hook();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Context getContext() {
		return mContext;
	}
}
