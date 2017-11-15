package com.tomes.hookreceiver.hook;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import android.os.Handler;
import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

public class HookHelper {
	public static Map<String, Object> sLoadedApk = new HashMap<String, Object>();
	public static final String RAW_INTENT = "raw_Intent";
	/**	使用动态代理类似hook AMS hook住gDefault 和mInstance将真正要跳转的intent替换成跳转到替身activity的intent
	 * @param context
	 * @throws Exception
	 */
	public static void replaceSubstituteActivity() throws Exception {
		//通过hook ams去替换成替身activity之后，再在system_server进程校验之后，在app进程里替换回来
		//gDefault
		Class<?> ActivityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
		Field gDefaultField = ActivityManagerNativeClass.getDeclaredField("gDefault");
		gDefaultField.setAccessible(true);
		//拿到ActivityManagerNative的Singleton<IActivityManager> gDefault对象
		Object gDefault = gDefaultField.get(null);
		Class<?> SingletonClass = Class.forName("android.util.Singleton");
		Field mInstanceField = SingletonClass.getDeclaredField("mInstance");
		mInstanceField.setAccessible(true);
		//拿到Singleton的IActivityManager mInstance对象
		Object mInstance=mInstanceField.get(gDefault);
		Class<?>[] interfaces = mInstance.getClass().getInterfaces();
		Object proxy = Proxy.newProxyInstance(mInstance.getClass().getClassLoader(), interfaces, new IActivityManagerInvocationHandler(mInstance));
		//将代理和mInstance进行替换
		mInstanceField.set(gDefault, proxy);
	}

/*	//等待system_server进程执行完校验之后，在app进程里替换回真正的activity
	public static void replaceRealActivity() throws Exception{
		//尝试在performLaunchActivity()里去hook mInstrumentation.newActivity()方法，但newActivity()里面的参数我无法强转，并继续向下执行(Activity.NonConfigurationInstances)lastNonConfigurationInstance
		Class<?> ActivityThreadClass = Class.forName("android.app.ActivityThread");
		Method currentActivityThreadMethod = ActivityThreadClass.getDeclaredMethod("currentActivityThread", null);
		currentActivityThreadMethod.setAccessible(true);
		Object currentActivityThread = currentActivityThreadMethod.invoke(null);
//		mInstrumentation
		Field mInstrumentationField = ActivityThreadClass.getDeclaredField("mInstrumentation");
		mInstrumentationField.setAccessible(true);
		Object mInstrumentation = mInstrumentationField.get(currentActivityThread);
		//静态代理
		ProxyInstrumentation proxy=new ProxyInstrumentation(mInstrumentation);
		mInstrumentationField.set(currentActivityThread, proxy);
	}*/
	
	//使用动态代理mCallback，等待system_server进程执行完校验之后，在app进程里替换回真正的activity，但mCallback可能为null,会出现空指针，后面的method.invoke(proxy, args)也会出问题
/*	public static void replaceRealActivity() throws Exception{
		//在ActivityThread scheduleLaunchActivity()里会使用H mH发送消息，只有拦截handler的dispatch()替换
		Class<?> ActivityThreadClass = Class.forName("android.app.ActivityThread");
		Method currentActivityThreadMethod = ActivityThreadClass.getDeclaredMethod("currentActivityThread", null);
		currentActivityThreadMethod.setAccessible(true);
		Object currentActivityThread = currentActivityThreadMethod.invoke(null);
		//获取handlre H mH
		Field mHField = ActivityThreadClass.getDeclaredField("mH");
		mHField.setAccessible(true);
		Object mH = mHField.get(currentActivityThread);
		//获取mH中的 mCallback，反射某个类的时候，某个类一定要有该变量和方法，父类有也不算。这时要得到这个成员变量，只能反射父类的变量和方法，然后get时传入对应的子类对象
//		Class<?> HClass = Class.forName("android.app.ActivityThread$H");
		Field mCallbackField = Handler.class.getDeclaredField("mCallback");
		mCallbackField.setAccessible(true);
		//mCallback可能为null,后面的method.invoke(proxy, args)也会出问题
		Object mCallback = mCallbackField.get(mH);
		Object proxy = Proxy.newProxyInstance(currentActivityThread.getClass().getClassLoader(), new Class<?>[]{Callback.class}, new CallbackInvocationHandler(mCallback));
		mCallbackField.set(mH, proxy);
	}*/
	
	//使用静态代理mCallback，等待system_server进程执行完校验之后，在app进程里替换回真正的activity，
	public static void replaceRealActivity() throws Exception{
		//在ActivityThread scheduleLaunchActivity()里会使用H mH发送消息，只有拦截handler的dispatch()替换
		Class<?> ActivityThreadClass = Class.forName("android.app.ActivityThread");
		Method currentActivityThreadMethod = ActivityThreadClass.getDeclaredMethod("currentActivityThread", null);
		currentActivityThreadMethod.setAccessible(true);
		Object currentActivityThread = currentActivityThreadMethod.invoke(null);
		//获取hndlre H mH
		Field mHField = ActivityThreadClass.getDeclaredField("mH");
		mHField.setAccessible(true);
		Handler mH = (Handler) mHField.get(currentActivityThread);
		//使用静态代理mCallback
		Field mCallbackField = Handler.class.getDeclaredField("mCallback");
		mCallbackField.setAccessible(true);
		//替换mCallback
		ProxyCallback proxy = new ProxyCallback(mH);
		mCallbackField.set(mH, proxy);
	}

	public static void hook(ClassLoader cl, File apkFile, File optDexFile) throws Exception {
		hookClassLoader(cl,apkFile,optDexFile);
			
		replaceSubstituteActivity();
		replaceRealActivity();
		
	}

	/**激进型hook ClassLoader，即给每一个插件apk分别准备一个classLoader（该方案没实验成功）
	 * @param pluginApkFile
	 * @throws Exception 
	 */
	/*private static void hookClassLoader(File pluginApkFile) throws Exception {
		String pluginApk=pluginApkFile.getAbsolutePath();
		分析:
		 * 1、activity启动过程中，在H mH的处理消息的过程中LAUNCH_ACTIVITY时，getPackageInfoNoCheck(ApplicationInfo ai,CompatibilityInfo compatInfo)得到一个LoadApk
		 * 2、而getPackageInfoNoCheck()方法里实现的本质是WeakReference<LoadedApk> ref = mPackages.get(packageName);
		 * 	   既从ActivityThread里ArrayMap<String, WeakReference<LoadedApk>> mPackages缓存里拿取LoadedApk，我们要做的是往这个mPackages中增加我们的LoadedApk即可
		 * 自己写代码的时候可以从最重要的结果出发，逆向去写代码，代码中缺什么参数就去构造什么，直到没有办法实现的时候，才采用别的方法去解决。
		 * 
		Class<?> ActivityThreadClass = Class.forName("android.app.ActivityThread");
		Method currentActivityThreadMethod = ActivityThreadClass.getDeclaredMethod("currentActivityThread", null);
		currentActivityThreadMethod.setAccessible(true);
		Object currentActivityThread = currentActivityThreadMethod.invoke(null);
		
		Class<?> CompatibilityInfoClass = Class.forName("android.content.res.CompatibilityInfo");
		Method getPackageInfoNoCheckMethod = ActivityThreadClass.getDeclaredMethod("getPackageInfoNoCheck", ApplicationInfo.class,CompatibilityInfoClass);
		getPackageInfoNoCheckMethod.setAccessible(true);
		
		//想办法获取到第一个参数ApplicationInfo
		分析:
		 * ApplicationInfoshi 是apk信息，那么就会涉及到xml配置清单的解析，找找PackageParser里有没有获取ApplicationInfo的方法。
		 * ApplicationInfo generateApplicationInfo(Package p, int flags,PackageUserState state)；
		 * 
		Class<?> PackageParserClass = Class.forName("android.content.pm.PackageParser");
		Class<?> PackageClass = Class.forName("android.content.pm.PackageParser$Package");
		Class<?> PackageUserStateClass = Class.forName("android.content.pm.PackageUserState");
		Object PackageParser = PackageParserClass.newInstance();
		Method generateApplicationInfoMethod = PackageParserClass.getDeclaredMethod("generateApplicationInfo", PackageClass,int.class,PackageUserStateClass);
		generateApplicationInfoMethod.setAccessible(true);
		//PackageParser中的Package parsePackage(File packageFile, int flags);
		Method parsePackageMethod = PackageParserClass.getDeclaredMethod("parsePackage", File.class,int.class);
		parsePackageMethod.setAccessible(true);
		//采用0的默认解析就好得到Package
		Object packageObj = parsePackageMethod.invoke(PackageParser, pluginApkFile,0);
		
		//PackageUserState指的是包状态，使用默认的就行,反射generateApplicationInfo()得到ApplicationInfo
		ApplicationInfo applicationInfo=(ApplicationInfo) generateApplicationInfoMethod.invoke(PackageParser,packageObj,0,PackageUserStateClass.newInstance());
		
		//使用系统系统的这个方法解析得到的ApplicationInfo对象中并没有apk文件本身的信息，所以我们把解析的apk文件的路径设置一下（ClassLoader依赖dex文件以及apk的路径）：
		applicationInfo.sourceDir=pluginApk;
		applicationInfo.publicSourceDir=pluginApk;
		
		Field CompatibilityInfoField = CompatibilityInfoClass.getDeclaredField("DEFAULT_COMPATIBILITY_INFO");
		CompatibilityInfoField.setAccessible(true);
		//得到默认CompatibilityInfo对象
		Object CompatibilityInfo = CompatibilityInfoField.get(null);
		
		//反射getPackageInfoNoCheck()得到LoadApk对象
		Object LoadApk = getPackageInfoNoCheckMethod.invoke(currentActivityThread, applicationInfo,CompatibilityInfo);
		
		//---将LoadApk对象中的ClassLoader mClassLoader设置成我们自定义的ClassLoader;
		Field mClassLoaderField = LoadApk.getClass().getDeclaredField("mClassLoader");
		mClassLoaderField.setAccessible(true);
		String odexPath = Utils.getPluginOptDexDir(applicationInfo.packageName).getPath();
        String libDir = Utils.getPluginLibDir(applicationInfo.packageName).getPath();
		mClassLoaderField.set(LoadApk, new CustomClassLoader(pluginApk, odexPath, libDir, ClassLoader.getSystemClassLoader()));
		
		//--得到mPackages对象
		Field mPackagesField = ActivityThreadClass.getDeclaredField("mPackages");
		mPackagesField.setAccessible(true);
		Map mPackages = (Map) mPackagesField.get(currentActivityThread);
		
		// 由于是弱引用, 因此我们必须在某个地方存一份, 不然容易被GC; 那么就前功尽弃了.
        sLoadedApk.put(applicationInfo.packageName, LoadApk);
		//添加进我们新的WeakReference<LoadedApk>
		WeakReference value=new WeakReference(LoadApk);
		mPackages.put(applicationInfo.packageName, value);
	}*/
	
	/**保守型hook ClassLoader，主要目的 将dex信息加入到DexPathList的Element[] dexElements里，让系统自动调用findClass等等
	 * @param pluginApkFile
	 * @throws Exception 
	 */
	private static void hookClassLoader(ClassLoader cl, File apkFile, File optDexFile) throws Exception {
		//主要目的 将dex信息加入到DexPathList的Element[] dexElements里
		// 获取 BaseDexClassLoader : pathList
		Field pathListField = DexClassLoader.class.getSuperclass().getDeclaredField("pathList");
		pathListField.setAccessible(true);
		Object pathList = pathListField.get(cl);
		
		//获取DexPathList Element[] dexElements对象
		Field dexElementsField =pathList.getClass().getDeclaredField("dexElements");
		dexElementsField.setAccessible(true);
		Object[] dexElements = (Object[]) dexElementsField.get(pathList);
		
		 // Element 类型
	    Class<?> elementClass = dexElements.getClass().getComponentType();
		Object[] newDexElements = (Object[]) Array.newInstance(elementClass, dexElements.length+1);
		 // 把原始的elements复制进去
		System.arraycopy(dexElements, 0, newDexElements, 0, dexElements.length);
		
		 // 构造插件Element(File file, boolean isDirectory, File zip, DexFile dexFile) 这个构造函数,
		//从DexPathListd的makeDexElements()，elements.add(new Element(file, false, zip, dex));
		Constructor<?> constructor = elementClass.getConstructor(File.class,boolean.class,File.class,DexFile.class);
		Object element = constructor.newInstance(apkFile,false,apkFile,DexFile.loadDex(apkFile.getCanonicalPath(), optDexFile.getAbsolutePath(), 0));
		
		Object[] addElements=new Object[]{element};
		// 插件的那个element复制进去
		System.arraycopy(addElements, 0, newDexElements, dexElements.length, addElements.length);
		
		//替换
		dexElementsField.set(pathList, newDexElements);
	}
}
