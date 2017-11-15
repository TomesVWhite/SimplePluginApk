package com.tomes.hookreceiver;

import java.io.File;

import android.app.Application;
import android.content.Context;

import com.tomes.hookreceiver.hook.HookBroadcastReceiver;
import com.tomes.hookreceiver.hook.HookHelper;
import com.tomes.hookreceiver.utils.LogUtils;
import com.tomes.hookreceiver.utils.Utils;

/**该hook BroadcastReceiver只针对5.x及以上生效，4.x需要适配
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
		File pluginApkFile = getFileStreamPath("TestDemo.apk");
		// 解压这个apk到/data/data/files 目录下
		Utils.extractAssets(base, "TestDemo.apk");
		try {
			if (pluginApkFile.exists()) {
				// 加载插件apk进去
				File dexFile = getFileStreamPath("TestDemo.apk");
                File optDexFile = getFileStreamPath("test.dex");
				//加载插件apk进去
				HookHelper.hook(getClassLoader(),dexFile,optDexFile);
				HookBroadcastReceiver.hookReceiver(base, pluginApkFile);
			} else {
				LogUtils.i(pluginApkFile + "该插件apk不存在");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Context getContext() {
		// TODO Auto-generated method stub
		return mContext;
	}
}
