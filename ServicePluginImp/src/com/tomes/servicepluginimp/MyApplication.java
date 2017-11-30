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
			Utils.extractAssets(base, "TestDemo.apk");
			File apkFile = getFileStreamPath("TestDemo.apk");
            File odexFile = getFileStreamPath("test.odex");
			//hookClassLoader
			HookHelper.hookClassLoader(getClassLoader(), apkFile, odexFile);
			//hook了classloader后，解析插件apk
			ProxyServiceManager.parsePluginApkService(apkFile);
			HookHelper.hook();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Context getContext() {
		return mContext;
	}
}
