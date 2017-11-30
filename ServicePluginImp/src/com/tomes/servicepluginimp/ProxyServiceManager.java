package com.tomes.servicepluginimp;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.IBinder;

import com.tomes.servicepluginimp.utils.LogUtils;

public class ProxyServiceManager {
	//目前程序里活动通过service代理还在运行的service
	private static Map<String, Service> mServiceMap = new HashMap<String, Service>();
	// 存储插件的Service信息
	private static Map<ComponentName, ServiceInfo> mServiceInfoMap=new HashMap<ComponentName, ServiceInfo>();
	/**处理代理service分发过来的对应插件service的onStart()，使用这个的前提是插件apk需要被加载进classloader里，否则会找不到目标service
	 * 启动某个插件Service; 如果Service还没有启动, 那么会创建新的插件Service
	 * @param intent
	 * @param startId
	 * @throws Exception 
	 */
	public static void onStart(Intent proxyIntent, int startId) throws Exception {
		LogUtils.i("ProxyServiceManager--->onStart()");
		Intent rawIntent=proxyIntent.getParcelableExtra(IActivityManagerInvocationHandler.SERVICE_RAWIN_TENT);
		ServiceInfo serviceInfo=selectPluginService(rawIntent);
		if(serviceInfo==null){
			LogUtils.i("the apk can't find service:"+rawIntent.getComponent());
			return ;
		}
		//如果正在运行的service里面没有要启动的service
		if(!mServiceMap.containsKey(serviceInfo.name)){
			//通过系统的方法，反射先创建一个service对象
			proxyCreateService(serviceInfo);
		}
		Service service = mServiceMap.get(serviceInfo.name);
		service.onStart(rawIntent, startId);
	}
	
	//代理hook的是ActivityManagerNative.getDefault().stopService 所以动态代理返回值的时候，需要一个int值，否则会出现.ClassCastException
	/**hook到插件中执行stopService()的时候，将其分发过来，并调用插件service的onDestroy，销毁插件service对象
	 * @param rawIntent
	 * @return
	 */
	public static int stopService(Intent rawIntent) {
		ServiceInfo serviceInfo = selectPluginService(rawIntent);
		//先判断这个service有没有注册在androidManifest里
		if(serviceInfo==null){
			LogUtils.i("the apk can't find service:"+rawIntent.getComponent());
			return 0;
		}
		//根据service的名字获取通过代理孩子啊运行的service
		Service service = mServiceMap.get(serviceInfo.name);
		if(service==null){
			//证明这个service要么没存在就直接stop了，要么就已经执行过stop了
			LogUtils.i("can not runnning, are you stopped it multi-times?");
            return 0;
		}
		//调用插件中真正service的destory()；
		service.onDestroy();
		//销毁了真正的service之后，删掉他在map中的记录，证明活动的service中已经没有这个service了
		mServiceMap.remove(serviceInfo.name);
		
		//没有要代理的service后，就把代理service也一起关掉。
        if (mServiceMap.isEmpty()) {
            // 没有Service了, 这个没有必要存在了
        	LogUtils.i("service all stopped, stop proxy");
            Context appContext = MyApplication.getContext();
            Intent intent = new Intent();
            intent.setClassName(appContext.getPackageName(), ProxyService.class.getName());
            appContext.stopService(intent);
        }
		return 1;
	}
	/**
     * 通过ActivityThread的handleCreateService方法创建出Service对象
     * @param serviceInfo 插件的ServiceInfo
	 * @throws Exception 
     * @throws Exception
     */
	private static void proxyCreateService(ServiceInfo serviceInfo) throws Exception {
		LogUtils.i("ProxyServiceManager--->proxyCreateService()");
		//获取当前的ActivityThread对象 android.app.ActivityThread.handleCreateService(CreateServiceData data)
		Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
		Class<?> createServiceDataClass = Class.forName("android.app.ActivityThread$CreateServiceData");
		Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
		//获取当前线程对象
		Object currentActivityThread = currentActivityThreadMethod.invoke(null);
		
		Constructor<?> createServiceDataConstructor = createServiceDataClass.getDeclaredConstructor();
		createServiceDataConstructor.setAccessible(true);
		//获取CreateServiceData对象
		Object createServiceData = createServiceDataConstructor.newInstance();
		
		// 写入我们创建的createServiceData的token字段, ActivityThread的handleCreateService用这个作为key存储Service
		Field tokenField = createServiceDataClass.getDeclaredField("token");
		tokenField.setAccessible(true);
		IBinder token=new Binder();
		tokenField.set(createServiceData, token);
		
		// 写入CreateServiceData的info对象
		//这行代码非常重要，没有这句则会报InvocationTargetException
		//这个修改是为了loadClass的时候, LoadedApk会是主程序的ClassLoader, 我们选择Hook BaseDexClassLoader的方式加载插件
		serviceInfo.applicationInfo.packageName = MyApplication.getContext().getPackageName();
		Field infoField = createServiceDataClass.getDeclaredField("info");
		infoField.setAccessible(true);
		infoField.set(createServiceData, serviceInfo);
		
		// 写入CreateServiceData的compatInfo字段
        // 获取默认的compatibility配置
		Field compatInfoField = createServiceDataClass.getDeclaredField("compatInfo");
		compatInfoField.setAccessible(true);
		//android.content.res.CompatibilityInfo
		Class<?> compatibilityInfoClass = Class.forName("android.content.res.CompatibilityInfo");
		//获取CompatibilityInfo的DEFAULT_COMPATIBILITY_INFO
		Field DEFAULT_COMPATIBILITY_INFO_Field = compatibilityInfoClass.getDeclaredField("DEFAULT_COMPATIBILITY_INFO");
		Object DEFAULT_COMPATIBILITY_INFO = DEFAULT_COMPATIBILITY_INFO_Field.get(null);
		compatInfoField.set(createServiceData, DEFAULT_COMPATIBILITY_INFO);
		
		Method handleCreateServiceMethod = activityThreadClass.getDeclaredMethod("handleCreateService", createServiceDataClass);
		handleCreateServiceMethod.setAccessible(true);
		handleCreateServiceMethod.invoke(currentActivityThread, createServiceData);
		
		// handleCreateService创建出来的Service对象并没有返回, 而是存储在ActivityThread的mServices字段里面, 这里我们手动把它取出来
		Field mServicesField = activityThreadClass.getDeclaredField("mServices");
		mServicesField.setAccessible(true);
		Map mServices = (Map) mServicesField.get(currentActivityThread);
		Service service = (Service) mServices.get(token);
		
		// 获取到之后, 移除这个service, 我们只是借花献佛
		mServices.remove(token);
		
		//这里创建了插件的service就需要将他保存进mServiceMap来为在运行的service做个记录
		mServiceMap.put(serviceInfo.name, service);
		LogUtils.i("ProxyServiceManager--->proxyCreateService() finish!s");
	}

	/**加载了产检apk后，会将apk的配置清单信息进行解析，从中选择匹配的ServiceInfo，以防止插件apk本身的service未在其配置清单中申明
	 * @param rawIntent
	 * @return
	 */
	private static ServiceInfo selectPluginService(Intent rawIntent) {
		ComponentName component = rawIntent.getComponent();
		Set<ComponentName> keySets = mServiceInfoMap.keySet();
		for (ComponentName keySet : keySets) {
			if(keySet.equals(component)){
				return mServiceInfoMap.get(keySet);
			}
		}
		return null;
	}

    /**
     * 解析Apk文件中的 <service>, 并存储起来
     * 主要是调用PackageParser类的generateServiceInfo方法
     * @param apkFile 插件对应的apk文件
     * @throws Exception 解析出错或者反射调用出错, 均会抛出异常
     */
	public static void parsePluginApkService(File apkFile) throws Exception {

		// 首先调用parsePackage获取到apk对象对应的Package对象 android.content.pm.PackageParser.parsePackage(File, int)
		Class<?> PackageParserClass = Class.forName("android.content.pm.PackageParser");
		Object packageParser = PackageParserClass.newInstance();
		Method parsePackageMethod = PackageParserClass.getDeclaredMethod("parsePackage", File.class,int.class);
		//得到Package对象
		Object packageObj = parsePackageMethod.invoke(packageParser, apkFile,PackageManager.GET_SERVICES);
		
		// 读取Package对象里面的receivers字段,注意这是一个 List<Activity> (没错,底层把<receiver>当作<activity>处理)
	    // 接下来要做的就是根据这个List<Activity> 获取到Receiver对应的 ActivityInfo (依然是把receiver信息用activity处理了)
		Field servicesField = packageObj.getClass().getDeclaredField("services");
		List services = (List) servicesField.get(packageObj);
		
		// 需要调用 android.content.pm.PackageParser#generateServiceInfo(android.content.pm.PackageParser.Service, int, android.content.pm.PackageUserState, int)
		Class<?> PackageParser$Service = Class.forName("android.content.pm.PackageParser$Service");
		Class<?> PackageUserStateClass = Class.forName("android.content.pm.PackageUserState");
		Method generateServiceInfo = PackageParserClass.getDeclaredMethod("generateServiceInfo", PackageParser$Service,int.class,PackageUserStateClass,int.class);
		generateServiceInfo.setAccessible(true);
		
		//获取userId UserHandle.getCallingUserId android.os.UserHandle.getCallingUserId()
		Class<?> UserHandleClass = Class.forName("android.os.UserHandle");
		Method getCallingUserIdMethod = UserHandleClass.getDeclaredMethod("getCallingUserId");
		int userId = (Integer) getCallingUserIdMethod.invoke(null);
		
		// 解析出 receiver以及对应的 intentFilter
		for (Object service : services) {
			ServiceInfo info =  (ServiceInfo) generateServiceInfo.invoke(null,service,0,PackageUserStateClass.newInstance(),userId);
			mServiceInfoMap.put(new ComponentName(info.packageName, info.name), info);
		}
	
	}

}
