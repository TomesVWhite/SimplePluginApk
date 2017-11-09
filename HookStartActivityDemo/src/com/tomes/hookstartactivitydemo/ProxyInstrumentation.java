package com.tomes.hookstartactivitydemo;

import java.lang.reflect.Method;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.tomes.hookstartactivitydemo.Utils.LogUtils;

public class ProxyInstrumentation extends Instrumentation {
	// ActivityThread中原始的对象mInstrumentation, 保存起来
	Instrumentation mBase;

	public ProxyInstrumentation(Instrumentation mInstrumentation) {
		mBase = mInstrumentation;
	}

	/**当有谁执行StartActivity()时，就会hook到，然后执行这里
	 * @param who
	 * @param contextThread
	 * @param token
	 * @param target
	 * @param intent
	 * @param requestCode
	 * @param options
	 * @return
	 */
	public ActivityResult execStartActivity(Context who, IBinder contextThread,
			IBinder token, Activity target, Intent intent, int requestCode,
			Bundle options) {
		LogUtils.i("ProxyInstrumentation--->execStartActivity()");
		LogUtils.i("\n执行了startActivity, 参数如下: \n" + "who = [" + who + "], "
				+ "\ncontextThread = [" + contextThread + "], \ntoken = ["
				+ token + "], " + "\ntarget = [" + target + "], \nintent = ["
				+ intent + "], \nrequestCode = [" + requestCode
				+ "], \noptions = [" + options + "]");
		try {
			// 开始调用原始的方法, 不调用的话, 所有的startActivity都失效了.
			// 由于这个方法是隐藏的,因此需要使用反射调用;首先找到这个方法
			Method execStartActivityMethod = Instrumentation.class
					.getDeclaredMethod("execStartActivity", Context.class,
							IBinder.class, IBinder.class, Activity.class,
							Intent.class, int.class, Bundle.class);
			execStartActivityMethod.setAccessible(true);
			return (ActivityResult) execStartActivityMethod.invoke(mBase, who,
					contextThread, token, target, intent, requestCode, options);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
