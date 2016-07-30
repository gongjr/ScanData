package com.gjr.scandata;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import kxlive.gjrlibrary.base.BaseApplication;
import kxlive.gjrlibrary.utils.CrashHandler;
import kxlive.gjrlibrary.utils.KLog;

public class AppApplication extends BaseApplication {

    /*
    * 判断是否显示餐牌号
    * */
    public static boolean IS_NEED_MEAL_NUMBER = false;

	public void onCreate() {
		super.onCreate();
        /**捕获系统崩溃信息*/
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext(),this);
        KLog.init(BuildConfig.LOG_DEBUG);

    }

    /********Application中存放的Activity操作（压栈/出栈）API（结束）******/
    /**
     *重启应用
     */
    @Override
    public void restartApp() {
        Intent intent = new Intent();
        // 参数1：包名，参数2：程序入口的activity
        intent.setClassName(getPackageName(), "com.gjr.scandata.ui.LaunchActivity");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent restartIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
                restartIntent); // 1秒钟后重启应用
        removeAllProgram();
    }

}
