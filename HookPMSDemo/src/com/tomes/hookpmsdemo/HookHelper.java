package com.tomes.hookpmsdemo;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

import android.content.Context;

/**Hook PMS
 * @author Tomes
 *
 */
public class HookHelper {

	/**HookPMS 简单就是替换ActivityThread的sPackageManager静态变量
	 * @param base
	 * @throws Exception
	 */
	public static void hookPMS(Context base) throws Exception {
		//替换掉ActivityThread的sPackageManager
		Class<?> ActivityThreadClass = Class.forName("android.app.ActivityThread");
		Field sPackageManagerField = ActivityThreadClass.getDeclaredField("sPackageManager");
		sPackageManagerField.setAccessible(true);
		//获取到ActivityThread的IPackageManager sPackageManager
		Object sPackageManager = sPackageManagerField.get(null);
		Class<?>[] interfaces = sPackageManager.getClass().getInterfaces();
		Object proxy = Proxy.newProxyInstance(sPackageManager.getClass().getClassLoader(), interfaces, new IPackageManagerInvocationHandler(sPackageManager));
		sPackageManagerField.set(null, proxy);
	}

}
