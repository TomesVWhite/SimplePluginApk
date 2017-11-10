package com.tomes.hookamsdemo;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

import android.content.Context;

/**hook ams
 * @author Tomes
 *
 */
public class HookHelper {

	/**HookAMS 简单就是替换Singleton的mInstance单例完成
	 * @param base
	 * @throws Exception
	 */
	public static void hookAMS(Context base) throws Exception {

		//Instrumentation里执行execStartActivity()中ActivityManagerNative.getDefault().startActivity()时，Singleton<IActivityManager> gDefault 去get()拿到IActivityManager mInstance
		//所以这里我们需要想把mInstance给替换掉，控制了这个单例就控制了ams
		Class<?> ActivityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
		Field gDefaultField = ActivityManagerNativeClass.getDeclaredField("gDefault");
		gDefaultField.setAccessible(true);
		//拿到Singleton<IActivityManager> gDefault对象
		Object gDefault = gDefaultField.get(null);
		//拿mInstance
		Class<?> SingletonClass = Class.forName("android.util.Singleton");
		Field mInstanceField = SingletonClass.getDeclaredField("mInstance");
		mInstanceField.setAccessible(true);
		//获得IActivityManager mInstance原始对象
		Object mInstance = mInstanceField.get(gDefault);
		//动态代理替换这个IActivityManager mInstance原始对象，有接口实现，那么使用动态代理就好
		//获取mInstance这个要代理对象实现的所有接口
		Class<?>[] interfaces = mInstance.getClass().getInterfaces();
		Object proxy = Proxy.newProxyInstance(mInstance.getClass().getClassLoader(), interfaces, new IActivityManagerInvocationHandler(mInstance));
		//将代理和mInstance进行替换
		mInstanceField.set(gDefault, proxy);
	}

}
