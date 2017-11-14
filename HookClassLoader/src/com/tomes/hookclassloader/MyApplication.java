package com.tomes.hookclassloader;

import java.io.File;

import android.app.Application;
import android.content.Context;

import com.tomes.hookclassloader.hook.HookHelper;
import com.tomes.hookclassloader.utils.LogUtils;
import com.tomes.hookclassloader.utils.Utils;

public class MyApplication extends Application {

	private static Context mContext;

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtils.i("MyApplication--->onCreate()");
	}
	
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		LogUtils.i("MyApplication--->attachBaseContext()");
		mContext=base;
		File pluginApkFile = getFileStreamPath("TestDemo.apk");
		//解压这个apk到/data/data/files 目录下
		Utils.extractAssets(base, "TestDemo.apk");
		try {
			if(pluginApkFile.exists()){
                File dexFile = getFileStreamPath("TestDemo.apk");
                File optDexFile = getFileStreamPath("test.dex");
				//加载插件apk进去
				HookHelper.hook(getClassLoader(),dexFile,optDexFile);
			}else {
				LogUtils.i(pluginApkFile+"该插件apk不存在");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Context getContext() {
		return mContext;
	}
}
