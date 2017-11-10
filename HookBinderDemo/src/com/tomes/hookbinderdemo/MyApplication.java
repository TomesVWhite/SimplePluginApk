package com.tomes.hookbinderdemo;

import com.tomes.hookstartactivitydemo.Utils.LogUtils;

import android.app.Application;
import android.content.Context;

/**该工程的主要目的在于通过hook Ibinder对象去修改控制服务，这里以剪切板服务为例，参考地址：http://weishu.me/2016/02/16/understand-plugin-framework-binder-hook/
 * @author Tomes
 *
 */
public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtils.i("MyApplication--->onCreate()");
	}
	
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		LogUtils.i("MyApplication--->attachBaseContext()");
		try {
			HookHelper.hook(base);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
