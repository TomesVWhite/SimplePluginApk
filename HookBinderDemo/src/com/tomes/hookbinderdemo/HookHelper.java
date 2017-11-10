package com.tomes.hookbinderdemo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

import android.content.Context;
import android.os.IBinder;

/**hook binder，主要是hook 2个要点
IBinder b = ServiceManager.getService("service_name"); // 获取原始的IBinder对象
IXXInterface in = IXXInterface.Stub.asInterface(b); // 转换为Service接口
最后替换这个in
 * @author Tomes
 *
 */
public class HookHelper {

	/**这里hook一个剪切板服务
	 * android.content.IClipboard的asInterface()里hook android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
	 * @param base
	 * @throws Exception 
	 */
	public static void hook(Context base) throws Exception {
		//---获取ServiceManager.getService的IBinder b对象
		Class<?> ServiceManagerClass = Class.forName("android.os.ServiceManager");
		Method getServiceMethod = ServiceManagerClass.getDeclaredMethod("getService", String.class);
		getServiceMethod.setAccessible(true);
		//其实是通过ServiceManager的Map sCache通过key CLIPBOARD_SERVICE拿到值的，实际上是一个裸Binder代理对象，它只有与驱动打交道的能力，但是它并不能独立工作。
		IBinder rawBinder = (IBinder) getServiceMethod.invoke(null, Context.CLIPBOARD_SERVICE);
		//用于代理替换rawBinder的proxy
		IBinder proxy = (IBinder) Proxy.newProxyInstance(rawBinder.getClass().getClassLoader(), rawBinder.getClass().getInterfaces(), new IBinderInvocationHandler(rawBinder));
		//将sCache中原来的rawBinder和代理proxy进行替换
		Field sCacheField = ServiceManagerClass.getDeclaredField("sCache");
		sCacheField.setAccessible(true);
		HashMap<String, IBinder> sCache = (HashMap<String, IBinder>) sCacheField.get(null);
		sCache.put(Context.CLIPBOARD_SERVICE, proxy);
	}

}
