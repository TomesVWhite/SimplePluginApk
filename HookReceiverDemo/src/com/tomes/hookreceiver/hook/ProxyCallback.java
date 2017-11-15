package com.tomes.hookreceiver.hook;

import java.lang.reflect.Field;

import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.Parcelable;

public class ProxyCallback implements Callback {

	private Handler mBase;

	public ProxyCallback(Handler mH) {
		this.mBase=mH;
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		//H handler里的LAUNCH_ACTIVITY
		case 100:
			try {
				replaceLaunchActivity(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		//这句一定不能遗漏，由于重新接管了handleMessage()肯定就需要对消息进行处理，这里只替换了消息的操作，那么msg改变后，还是交由原来的Handler mBase去正常处理这个消息逻辑
		mBase.handleMessage(msg);
		return true;
	}

	/**将msg里的ActivityClientRecordClass的intent中的跳转对象从替身activity换成真正要跳转的目标activity
	 * @param msg 
	 * @throws Exception
	 */
	private void replaceLaunchActivity(Message msg) throws Exception {
		Object obj = msg.obj;
		//ActivityThread$ActivityClientRecord
		Class<? extends Object> ActivityClientRecordClass = obj.getClass();
		Field intentField = ActivityClientRecordClass.getDeclaredField("intent");
		intentField.setAccessible(true);
		//当时的跳转到替身activity的intent
		Intent fakeIntent = (Intent) intentField.get(obj);
		//原来代码里想要真正跳转的Intent，从当时保存在intent.putExtra()里的rawIntent拿出来
		Intent rawIntent = fakeIntent.getParcelableExtra(HookHelper.RAW_INTENT);
		//这里将跳转到替身activity的代码替换回来，可以真正跳转到来代码里想要跳转到的activity
		fakeIntent.setComponent(rawIntent.getComponent());
	}

}
