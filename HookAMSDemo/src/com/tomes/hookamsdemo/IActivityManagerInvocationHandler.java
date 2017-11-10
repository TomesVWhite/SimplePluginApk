package com.tomes.hookamsdemo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.tomes.hookamsdemo.Utils.LogUtils;

/**动态代理Singleton的IActivityManager mInstance实现
 * @author Tomes
 *
 */
public class IActivityManagerInvocationHandler implements InvocationHandler {

	/**
	 * 保存IActivityManager mInstance原始对象
	 */
	Object mBase;
	public IActivityManagerInvocationHandler(Object mInstance) {
		this.mBase=mInstance;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		LogUtils.i("i have hooked the IActivityManager!");
		//这里可以拦截或者操作IActivityManager里的斯普啤方法，如startActivity
		if("startActivity".equals(method.getName())){
			LogUtils.i("i have hooked the startActivity method!");
		}
		return method.invoke(mBase, args);
	}

}
