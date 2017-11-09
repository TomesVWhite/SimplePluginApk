package com.tomes.hookstartactivitydemo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import android.app.Instrumentation;
import android.content.Context;

public class HookHelper {

	/**hook住startActivity() 主要就是替换ActivityThread的mInstrumentation
	 * @param base
	 * @throws Exception 
	 */
	public static void hook(Context base) throws Exception {
		//获取当前线程对象
		Class<?> ActivityThreadClass = Class.forName("android.app.ActivityThread");
		Method currentActivityThreadMethod = ActivityThreadClass.getDeclaredMethod("currentActivityThread");
		currentActivityThreadMethod.setAccessible(true);
		Object currentActivityThread = currentActivityThreadMethod.invoke(null);
		
		//获取当前线程下的mInstrumentation对象
		Field mInstrumentationField = ActivityThreadClass.getDeclaredField("mInstrumentation");
		mInstrumentationField.setAccessible(true);
		Instrumentation mInstrumentation = (Instrumentation) mInstrumentationField.get(currentActivityThread);
		//非常重要：！！！！！
		//因为mInstrumentation没有实现接口，所以不能使用动态代理，只能使用静态代理。
//		Object proxy = Proxy.newProxyInstance(currentActivityThread.getClass().getClassLoader(), mInstrumentation.getClass().getInterfaces(), new MyInvocationHandler(mInstrumentation));
		//创建静态代理对象
		ProxyInstrumentation proxy=new ProxyInstrumentation(mInstrumentation);
		//将代理对象和原来的mInstrumentation进行替换
		mInstrumentationField.set(currentActivityThread, proxy);
	}

}
