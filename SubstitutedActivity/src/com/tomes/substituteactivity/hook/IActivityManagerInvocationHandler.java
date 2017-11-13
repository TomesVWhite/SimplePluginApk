package com.tomes.substituteactivity.hook;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.tomes.substituteactivity.MyApplication;
import com.tomes.substituteactivity.activity.SubstituteActivity;
import com.tomes.substituteactivity.utils.LogUtils;

import android.content.Intent;

public class IActivityManagerInvocationHandler implements InvocationHandler {

	Object mBase;
	public IActivityManagerInvocationHandler(Object mInstance) {
		this.mBase=mInstance;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		LogUtils.i("i have hooked IActivityManager");
		if("startActivity".equals(method.getName())){
			int index=0;
			for (int i = 0; i < args.length; i++) {
				if(args[i] instanceof Intent){
					index=i;
				}
			}
			Intent rawIntent=(Intent) args[index];
			Intent newIntent=new Intent();
			newIntent.setClassName(MyApplication.getContext().getPackageName(), SubstituteActivity.class.getCanonicalName());
			newIntent.putExtra(HookHelper.RAW_INTENT, rawIntent);
			args[index]=newIntent;
			return method.invoke(mBase, args);
		}
		return method.invoke(mBase, args);
	}

}
