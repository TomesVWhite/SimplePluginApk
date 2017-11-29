package com.tomes.servicepluginimp;

import android.content.Intent;

import com.tomes.servicepluginimp.utils.LogUtils;

public class ProxyServiceManager {

	/**处理代理service分发过来的对应插件service的onStart()，使用这个的前提是插件apk需要被加载进classloader里，否则会找不到目标service
	 * 启动某个插件Service; 如果Service还没有启动, 那么会创建新的插件Service
	 * @param intent
	 * @param startId
	 * @throws Exception 
	 */
	public static void onStart(Intent proxyIntent, int startId) throws Exception {
//		Intent rawIntent=proxyIntent.getParcelableExtra(IActivityManagerInvocationHandler.SERVICE_RAWIN_TENT);
//		rawIntent
		Class<?> TestServiceClass = Class.forName("com.ytx.testdemo.TestService");
		LogUtils.i("ProxyServiceManager:"+TestServiceClass);
	}

}
