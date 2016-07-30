package kxlive.gjrlibrary.utils;

import android.content.Context;
import android.util.Log;

import java.util.Set;
import java.util.TreeSet;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class JPushUtils {

	private static final String TAG = JPushUtils.class.getSimpleName();
	private Context mContext;

	public JPushUtils(Context mContext) {
		this.mContext = mContext;
	}

	// 初始化 JPush。如果已经初始化，但没有登录成功，则执行重新登录。
	public void initJPush() {
		JPushInterface.init(mContext);
	}

	/**
	 * 设置极光推送的自定义通知
	 */
	public void setJPushCustomNotification() {
		/*CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(
				mContext, R.layout.jpush_customer_notitfication_layout,
				R.id.icon, R.id.title, R.id.text);
		// 指定定制的 Notification Layout
		builder.statusBarDrawable = R.drawable.logo_icon;
		// 指定最顶层状态栏小图标
		builder.layoutIconDrawable = R.drawable.logo_icon;
		builder.notificationFlags = Notification.FLAG_AUTO_CANCEL; // 设置为自动消失
		builder.notificationDefaults = Notification.DEFAULT_SOUND
				| Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS; // 设置为铃声与震动都要
		// 指定下拉状态栏时显示的通知图标
		JPushInterface.setDefaultPushNotificationBuilder(builder);*/
	}

	/**
	 * 停止极光推送服务
	 */
	public void stopJPush() {
		JPushInterface.stopPush(mContext);
	}

	/**
	 * 恢复极光推送服务
	 */
	public void resumeJPush() {
		JPushInterface.resumePush(mContext);
	}

	/**
	 * 获取注册ID
	 * @return
	 */
	public String getRegistrationId() {
		String regId = JPushInterface.getRegistrationID(mContext);
		return regId;
	}
	
	/**
	 * 设置标签
	 */
	public void setJPushTags(Set<String> tagSet){
		JPushInterface.setTags(mContext, tagSet, new TagAliasCallback() {
			@Override
			public void gotResult(int arg0, String arg1, Set<String> arg2) {
				Logger.d(TAG, "setJPushTags arg0: " + arg0);
				Logger.d(TAG, "setJPushTags arg1: " + arg1);
				Logger.d(TAG, "setJPushTags arg2: " + arg2);
			}
		});
	}
	
	public void setJPushTag(String tag){
		Set<String> tagSet = new TreeSet<String>();
        Log.i("Tag","tag:"+tag);
        Log.i("Tag","tagSet:"+tagSet);
        tagSet.add(tag);
		JPushInterface.setTags(mContext, tagSet, new TagAliasCallback() {
			@Override
			public void gotResult(int arg0, String arg1, Set<String> arg2) {
				Logger.d(TAG, "setJPushTag arg0: " + arg0);
				Logger.d(TAG, "setJPushTag arg1: " + arg1);
				Logger.d(TAG, "setJPushTag arg2: " + arg2);
			}
		});
	}
}
