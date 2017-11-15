package com.tomes.hookreceiver.hook;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;

public class HookBroadcastReceiver {
	public static Map<ActivityInfo, List<? extends IntentFilter>> sCache=null;
	/**解析插件apk配置清单中的receiver
	 * @param apkFile
	 * @throws Exception 
	 */
	public static void parserReceivers(File apkFile) throws Exception{
		// 首先调用parsePackage获取到apk对象对应的Package对象 android.content.pm.PackageParser.parsePackage(File, int)
		Class<?> PackageParserClass = Class.forName("android.content.pm.PackageParser");
		Object packageParser = PackageParserClass.newInstance();
		Method parsePackageMethod = PackageParserClass.getDeclaredMethod("parsePackage", File.class,int.class);
		//得到Package对象
		Object packageObj = parsePackageMethod.invoke(packageParser, apkFile,PackageManager.GET_RECEIVERS);
		
		// 读取Package对象里面的receivers字段,注意这是一个 List<Activity> (没错,底层把<receiver>当作<activity>处理)
	    // 接下来要做的就是根据这个List<Activity> 获取到Receiver对应的 ActivityInfo (依然是把receiver信息用activity处理了)
		Field receiversField = packageObj.getClass().getDeclaredField("receivers");
		List receivers = (List) receiversField.get(packageObj);
		
		// 需要调用 android.content.pm.PackageParser#generateActivityInfo(android.content.pm.ActivityInfo, int, android.content.pm.PackageUserState, int)
		Class<?> PackageParser$Activity = Class.forName("android.content.pm.PackageParser$Activity");
		Class<?> PackageUserStateClass = Class.forName("android.content.pm.PackageUserState");
		Method generateActivityInfo = PackageParserClass.getDeclaredMethod("generateActivityInfo", PackageParser$Activity,int.class,PackageUserStateClass,int.class);
		generateActivityInfo.setAccessible(true);
		
		//获取userId UserHandle.getCallingUserId android.os.UserHandle.getCallingUserId()
		Class<?> UserHandleClass = Class.forName("android.os.UserHandle");
		Method getCallingUserIdMethod = UserHandleClass.getDeclaredMethod("getCallingUserId");
		int userId = (Integer) getCallingUserIdMethod.invoke(null);
		
        Class<?> componentClass = Class.forName("android.content.pm.PackageParser$Component");
        Field intentsField = componentClass.getDeclaredField("intents");
		// 解析出 receiver以及对应的 intentFilter
		for (Object receiver : receivers) {
			ActivityInfo info = (android.content.pm.ActivityInfo) generateActivityInfo.invoke(null,receiver,0,PackageUserStateClass.newInstance(),userId);
			List<? extends IntentFilter> filters = (List<? extends IntentFilter>) intentsField.get(receiver);
			sCache.put(info, filters);
		}
	}
	
	public static void hookReceiver(Context context, File apk) throws Exception{
		sCache=new HashMap<ActivityInfo, List<? extends IntentFilter>>();
		parserReceivers(apk);

		ClassLoader cl = null;
        for (ActivityInfo activityInfo : HookBroadcastReceiver.sCache.keySet()) {
            List<? extends IntentFilter> intentFilters = HookBroadcastReceiver.sCache.get(activityInfo);
            if (cl == null) {
                cl = CustomClassLoader.getPluginClassLoader(apk, activityInfo.packageName);
            }
            // 把解析出来的每一个静态Receiver都注册为动态的
            for (IntentFilter intentFilter : intentFilters) {
                BroadcastReceiver receiver = (BroadcastReceiver) cl.loadClass(activityInfo.name).newInstance();
                context.registerReceiver(receiver, intentFilter);
            }
        }
	}
}
