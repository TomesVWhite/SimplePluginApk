package com.tomes.hookreceiver.hook;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import android.content.Intent;

import com.tomes.hookreceiver.MyApplication;
import com.tomes.hookreceiver.activity.SubstituteActivity;
import com.tomes.hookreceiver.utils.LogUtils;

/**将真正要跳转的intent替换成跳转到替身activity的intent
 * @author Tomes
 *
 */
public class IActivityManagerInvocationHandler implements InvocationHandler {

	Object mBase;
	public IActivityManagerInvocationHandler(Object mInstance) {
		this.mBase=mInstance;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		LogUtils.i("i have hooked IActivityManager");
		if("startActivity".equals(method.getName())){
			int index=0;
			for (int i = 0; i < args.length; i++) {
				if(args[i] instanceof Intent){
					index=i;
				}
			}
			Intent rawIntent=(Intent) args[index];
			Intent newIntent=new Intent();
			newIntent.setClassName(MyApplication.getContext().getPackageName(), SubstituteActivity.class.getCanonicalName());
			newIntent.putExtra(HookHelper.RAW_INTENT, rawIntent);
			args[index]=newIntent;
			return method.invoke(mBase, args);
		}
		return method.invoke(mBase, args);
	}

}
