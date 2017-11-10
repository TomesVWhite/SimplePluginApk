package com.tomes.hookbinderdemo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import android.os.IBinder;

import com.tomes.hookstartactivitydemo.Utils.LogUtils;

public class IBinderInvocationHandler implements InvocationHandler {
    // 绝大部分情况下,这是一个BinderProxy对象
    // 只有当Service和我们在同一个进程的时候才是Binder本地对象
    // 这个基本不可能
	IBinder base;
	
    Class<?> stub;

    Class<?> iinterface;
	public IBinderInvocationHandler(IBinder base) {
		//注意，this很多时候是不能少的。以免出现不必要的麻烦。
		this.base=base;
		try {
            this.stub = Class.forName("android.content.IClipboard$Stub");
            this.iinterface = Class.forName("android.content.IClipboard");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		//想hook android.content.IClipboard的obj.queryLocalInterface(DESCRIPTOR)；
		//其实在这里已经hook到了剪切板服务了，但只能hook到queryLocalInterface执行，至于clipboardManager.setPrimaryClip这些更细致的这里hook不到还需要进一步操作
		if("queryLocalInterface".equals(method.getName())){
			LogUtils.i("the app have hooked queryLocalInterface()");
			//要hook到剪切板setPrimaryClip()等方法，IBinder就不能满足，需要asInterface(IBinder)后得到的IClipboard.Stub.Proxy类的对象才能操作具体的setPrimaryClip()等方法
//			return Proxy.newProxyInstance(proxy.getClass().getClassLoader(), proxy.getClass().getInterfaces(), new IClipboardInvocationHandler(base,stub));
			return Proxy.newProxyInstance(proxy.getClass().getClassLoader(), new Class[] { this.iinterface }, new IClipboardInvocationHandler(base, stub));
		}
		return method.invoke(base, args);
	}

}
