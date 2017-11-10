package com.tomes.hookpmsdemo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.tomes.hookamsdemo.Utils.LogUtils;

/**动态代理ActivityThread的IPackageManager sPackageManager实现
 * @author Tomes
 *
 */
public class IPackageManagerInvocationHandler implements InvocationHandler {

	/**
	 * IPackageManager sPackageManager原始对象
	 */
	private Object mBase;

	public IPackageManagerInvocationHandler(Object sPackageManager) {
		this.mBase=sPackageManager;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		LogUtils.i("i have hook IPackageManager.");
		//这里已经hook到ams了，这里可以拦截IPackageManager的所有方法，如：getPackageInfo()
		if("getPackageInfo".equals(method.getName())){
			LogUtils.i("i have hook getPackageInfo().");	
		}
		return method.invoke(mBase, args);
	}

}
