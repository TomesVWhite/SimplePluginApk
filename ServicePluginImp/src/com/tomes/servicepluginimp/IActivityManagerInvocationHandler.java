package com.tomes.servicepluginimp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.tomes.servicepluginimp.utils.LogUtils;

import android.content.Intent;
import android.text.TextUtils;

public class IActivityManagerInvocationHandler implements InvocationHandler {

	public static final String SERVICE_RAWIN_TENT = "service_rawIn_tent";
	private Object mBase;

	public IActivityManagerInvocationHandler(Object mInstance) {
		this.mBase=mInstance;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if("startService".equals(method.getName())){
			LogUtils.i("hook startService");
			int index=0;
			for (int i = 0; i < args.length; i++) {
				if(args[i] instanceof Intent){
					index=i;
				}
			}
			Intent rawIntent=(Intent) args[index];
			Intent proxyIntent=new Intent();
			proxyIntent.setClassName(MyApplication.getContext().getPackageName(),ProxyService.class.getName());
			proxyIntent.putExtra(SERVICE_RAWIN_TENT, rawIntent);
			args[index]=proxyIntent;
			LogUtils.i("hook startService rawIntent,proxyIntent:"+proxyIntent);
			return method.invoke(mBase, args);
		}
		if("stopService".equals(method.getName())){
			LogUtils.i("hook stopService");
			int index=0;
			for (int i = 0; i < args.length; i++) {
				if(args[i] instanceof Intent){
					index=i;
				}
			}
			Intent rawIntent=(Intent) args[index];
			//只要插件apk和外面的壳是独立的，插件apk内部操作的service的包名肯定和壳的包名不同，也只有插件里的service启动停止操作由壳代理，壳自身的还是由正常系统执行的好。
			if(!TextUtils.equals(MyApplication.getContext().getPackageName(), rawIntent.getComponent().getPackageName())){
				return ProxyServiceManager.stopService(rawIntent);
			}
		}
		return method.invoke(mBase, args);
	}

}
