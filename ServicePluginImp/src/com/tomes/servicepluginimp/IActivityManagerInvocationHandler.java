package com.tomes.servicepluginimp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.tomes.servicepluginimp.utils.LogUtils;

import android.content.Intent;

public class IActivityManagerInvocationHandler implements InvocationHandler {

	private static final String SERVICE_RAWIN_TENT = "service_rawIn_tent";
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
		return method.invoke(mBase, args);
	}

}
