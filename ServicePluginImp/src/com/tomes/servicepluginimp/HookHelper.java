package com.tomes.servicepluginimp;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

import android.R.bool;
import android.renderscript.Element;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexFile;

public class HookHelper {

	public static void hook() throws Exception {
		//把插件apk中真正要启动的Service全部重定向为启动ProxyService
		//android.app.ActivityManagerNative.gDefault
		Class<?> ActivityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
		Field gDefaultField = ActivityManagerNativeClass.getDeclaredField("gDefault");
		gDefaultField.setAccessible(true);
		Object gDefault = gDefaultField.get(null);
		//hook替换android.util.Singleton.mInstance
		Class<?> SingletonClass = Class.forName("android.util.Singleton");
		Field mInstanceField = SingletonClass.getDeclaredField("mInstance");
		mInstanceField.setAccessible(true);
		Object mInstance = mInstanceField.get(gDefault);
		Class<?>[] interfaces = mInstance.getClass().getInterfaces();
		Object proxy = Proxy.newProxyInstance(mInstance.getClass().getClassLoader(), interfaces, new IActivityManagerInvocationHandler(mInstance));
		mInstanceField.set(gDefault, proxy);
		
	}
	
	/**保守法hook ClassLoader
	 * @throws Exception
	 */
	public static void hookClassLoader(ClassLoader cl, File apkFile, File optDexFile) throws Exception{
		//dalvik.system.BaseDexClassLoader DexPathList pathList;
		Field pathListField = BaseDexClassLoader.class.getDeclaredField("pathList");
		pathListField.setAccessible(true);
		Object pathList = pathListField.get(cl);
		
		//dalvik.system.DexPathList Element[] dexElements;
		Class<?> DexPathListClass = Class.forName("dalvik.system.DexPathList");
		Field dexElementsField = DexPathListClass.getDeclaredField("dexElements");
		dexElementsField.setAccessible(true);
		Object[] dexElements = (Object[]) dexElementsField.get(pathList);
		//返回dexElements数组组件类型的 Class
		Class<?> componentType = dexElements.getClass().getComponentType();
		
		// 创建一个数组, 用来替换原始的数组
		Object[] newInstance = (Object[]) Array.newInstance(componentType, dexElements.length+1);
		System.arraycopy(dexElements, 0, newInstance, 0, dexElements.length);
		
		// 构造插件Element(File file, boolean isDirectory, File zip, DexFile dexFile) 这个构造函数 elements.add(new Element(file, false, zip, dex));
		Constructor<?> constructor = componentType.getConstructor(File.class,boolean.class,File.class,DexFile.class);
		Object addElement = constructor.newInstance(apkFile,false,apkFile,DexFile.loadDex(apkFile.getCanonicalPath(), optDexFile.getAbsolutePath(),0));
		newInstance[dexElements.length]=addElement;
		
		dexElementsField.set(pathList, newInstance);
	}

}
