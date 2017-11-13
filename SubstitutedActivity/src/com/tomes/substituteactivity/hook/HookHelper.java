package com.tomes.substituteactivity.hook;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;

public class HookHelper {
	public static final String RAW_INTENT = "raw_Intent";
	/**	使用动态代理类似hook AMS hook住gDefault 和mInstance将真正要跳转的intent替换成跳转到替身activity的intent
	 * @param context
	 * @throws Exception
	 */
	public static void replaceSubstituteActivity(Context context) throws Exception {
		//通过hook ams去替换成替身activity之后，再在system_server进程校验之后，在app进程里替换回来
		//gDefault
		Class<?> ActivityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
		Field gDefaultField = ActivityManagerNativeClass.getDeclaredField("gDefault");
		gDefaultField.setAccessible(true);
		//拿到ActivityManagerNative的Singleton<IActivityManager> gDefault对象
		Object gDefault = gDefaultField.get(null);
		Class<?> SingletonClass = Class.forName("android.util.Singleton");
		Field mInstanceField = SingletonClass.getDeclaredField("mInstance");
		mInstanceField.setAccessible(true);
		//拿到Singleton的IActivityManager mInstance对象
		Object mInstance=mInstanceField.get(gDefault);
		Class<?>[] interfaces = mInstance.getClass().getInterfaces();
		Object proxy = Proxy.newProxyInstance(mInstance.getClass().getClassLoader(), interfaces, new IActivityManagerInvocationHandler(mInstance));
		//将代理和mInstance进行替换
		mInstanceField.set(gDefault, proxy);
	}

/*	//等待system_server进程执行完校验之后，在app进程里替换回真正的activity
	public static void replaceRealActivity() throws Exception{
		//尝试在performLaunchActivity()里去hook mInstrumentation.newActivity()方法，但newActivity()里面的参数我无法强转，并继续向下执行(Activity.NonConfigurationInstances)lastNonConfigurationInstance
		Class<?> ActivityThreadClass = Class.forName("android.app.ActivityThread");
		Method currentActivityThreadMethod = ActivityThreadClass.getDeclaredMethod("currentActivityThread", null);
		currentActivityThreadMethod.setAccessible(true);
		Object currentActivityThread = currentActivityThreadMethod.invoke(null);
//		mInstrumentation
		Field mInstrumentationField = ActivityThreadClass.getDeclaredField("mInstrumentation");
		mInstrumentationField.setAccessible(true);
		Object mInstrumentation = mInstrumentationField.get(currentActivityThread);
		//静态代理
		ProxyInstrumentation proxy=new ProxyInstrumentation(mInstrumentation);
		mInstrumentationField.set(currentActivityThread, proxy);
	}*/
	
	//使用动态代理mCallback，等待system_server进程执行完校验之后，在app进程里替换回真正的activity，但mCallback可能为null,会出现空指针，后面的method.invoke(proxy, args)也会出问题
/*	public static void replaceRealActivity() throws Exception{
		//在ActivityThread scheduleLaunchActivity()里会使用H mH发送消息，只有拦截handler的dispatch()替换
		Class<?> ActivityThreadClass = Class.forName("android.app.ActivityThread");
		Method currentActivityThreadMethod = ActivityThreadClass.getDeclaredMethod("currentActivityThread", null);
		currentActivityThreadMethod.setAccessible(true);
		Object currentActivityThread = currentActivityThreadMethod.invoke(null);
		//获取handlre H mH
		Field mHField = ActivityThreadClass.getDeclaredField("mH");
		mHField.setAccessible(true);
		Object mH = mHField.get(currentActivityThread);
		//获取mH中的 mCallback，反射某个类的时候，某个类一定要有该变量和方法，父类有也不算。这时要得到这个成员变量，只能反射父类的变量和方法，然后get时传入对应的子类对象
//		Class<?> HClass = Class.forName("android.app.ActivityThread$H");
		Field mCallbackField = Handler.class.getDeclaredField("mCallback");
		mCallbackField.setAccessible(true);
		//mCallback可能为null,后面的method.invoke(proxy, args)也会出问题
		Object mCallback = mCallbackField.get(mH);
		Object proxy = Proxy.newProxyInstance(currentActivityThread.getClass().getClassLoader(), new Class<?>[]{Callback.class}, new CallbackInvocationHandler(mCallback));
		mCallbackField.set(mH, proxy);
	}*/
	
	//使用静态代理mCallback，等待system_server进程执行完校验之后，在app进程里替换回真正的activity，
	public static void replaceRealActivity() throws Exception{
		//在ActivityThread scheduleLaunchActivity()里会使用H mH发送消息，只有拦截handler的dispatch()替换
		Class<?> ActivityThreadClass = Class.forName("android.app.ActivityThread");
		Method currentActivityThreadMethod = ActivityThreadClass.getDeclaredMethod("currentActivityThread", null);
		currentActivityThreadMethod.setAccessible(true);
		Object currentActivityThread = currentActivityThreadMethod.invoke(null);
		//获取handlre H mH
		Field mHField = ActivityThreadClass.getDeclaredField("mH");
		mHField.setAccessible(true);
		Handler mH = (Handler) mHField.get(currentActivityThread);
		//使用静态代理mCallback
		Field mCallbackField = Handler.class.getDeclaredField("mCallback");
		mCallbackField.setAccessible(true);
		//替换mCallback
		ProxyCallback proxy = new ProxyCallback(mH);
		mCallbackField.set(mH, proxy);
	}
}
