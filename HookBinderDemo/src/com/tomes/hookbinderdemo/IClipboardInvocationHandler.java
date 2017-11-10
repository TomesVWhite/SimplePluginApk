package com.tomes.hookbinderdemo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.tomes.hookstartactivitydemo.Utils.LogUtils;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

public class IClipboardInvocationHandler implements InvocationHandler {

	// 原始的Service对象 (IInterface),即IClipboard对象
	Object base;
	public IClipboardInvocationHandler(IBinder mBinder,Class<?> stubClass) {
		try {
			//通过重复调用asInterface();并传入未经替换系统原始的IBinder对象，可以正确得到与之相应的android.os.IInterface iin，让其可以向下强转成IClipboard对象，从而将这个正确的
			//对象做为hook obj.queryLocalInterface(DESCRIPTOR);方法替换后的结果，使之可以通过校验
			Method asInterfaceMethod = stubClass.getDeclaredMethod("asInterface", IBinder.class);
			asInterfaceMethod.setAccessible(true);
			base = asInterfaceMethod.invoke(null, mBinder);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		LogUtils.i("i have hooked IClipboard");
		if ("getPrimaryClip".equals(method.getName())) {
            Log.d("Tomes", "hook getPrimaryClip");
//            return ClipData.newPlainText(null, "you are hooked");
        }

        // 欺骗系统,使之认为剪切版上一直有内容
        if ("hasPrimaryClip".equals(method.getName())) {
            return true;
        }

        return method.invoke(base, args);
	}

}
