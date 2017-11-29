package com.tomes.servicepluginimp;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

public class HookHelper {

	public static void hook() throws Exception {
		//把插件apk中真正要启动的Service全部重定向为启动ProxyService
		//android.app.ActivityManagerNative.gDefault
		Class<?> ActivityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
		Field gDefaultField = ActivityManagerNativeClass.getDeclaredField("gDefault");
		gDefaultField.setAccessible(true);
		Object gDefault = gDefaultField.get(null);
		//hook替换android.util.Singleton.mInstance
		Class<?> SingletonClass = Class.forName("android.util.Singleton");
		Field mInstanceField = SingletonClass.getDeclaredField("mInstance");
		mInstanceField.setAccessible(true);
		Object mInstance = mInstanceField.get(gDefault);
		Class<?>[] interfaces = mInstance.getClass().getInterfaces();
		Object proxy = Proxy.newProxyInstance(mInstance.getClass().getClassLoader(), interfaces, new IActivityManagerInvocationHandler(mInstance));
		mInstanceField.set(gDefault, proxy);
		
	}

}
