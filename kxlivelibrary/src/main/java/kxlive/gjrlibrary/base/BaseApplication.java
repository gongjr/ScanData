package kxlive.gjrlibrary.base;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.android.volley.toolbox.OkHttpStack;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LargestLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.litepal.LitePalApplication;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import kxlive.gjrlibrary.http.RequestManager;
import kxlive.gjrlibrary.utils.ToolNetwork;

/**
 * 基础Application
 * 初始化Litepal,volley,universalimageloader,全局数据集,Activity栈
 * Created by gjr on 2015/11/23.
 */
public class BaseApplication extends LitePalApplication {
    /**对外提供整个应用生命周期的Context**/
    private static Context instance;
    /**整个应用全局可访问数据集合**/
    private static Map<String, Object> gloableData = new HashMap<String, Object>();
    /***寄存整个应用的Activity弱引用**/
    private final Stack<WeakReference<Activity>> activitys = new Stack<WeakReference<Activity>>();
    /**
     * 对外提供Application Context
     * @return
     */
    public static Context gainContext() {
        return instance;
    }
    public void onCreate() {
        super.onCreate();
        instance = this;
        RequestManager.init(this,new OkHttpStack());
        initImageLoader(getApplicationContext());
    }


    public static void initImageLoader(Context context) {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int memoryCacheSize = maxMemory/8;
        if(maxMemory/8 > 16 * 1024 * 1024){
            memoryCacheSize = 16 * 1024 * 1024;
        }
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.memoryCache(new LargestLimitedMemoryCache(memoryCacheSize));
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    /**
     * 获取网络是否已连接
     * @return
     */
    public static boolean isNetworkReady(){
        return ToolNetwork.getInstance().init(instance).isConnected();
    }

    /*****Application数据操作API（开始）***********************************/
    /**
     * 往Application放置数据（最大不允许超过5个）
     * @param strKey 存放属性Key
     * @param strValue 数据对象
     */
    public static void saveData(String strKey, Object strValue) {
        if (gloableData.size() > 5) {
            throw new RuntimeException("超过允许最大数");
        }
        if (gloableData.containsKey(strKey))gloableData.remove(strKey);
        //保存唯一KEY映射
        gloableData.put(strKey, strValue);
    }
    /**
     * 从Applcaiton中取数据
     * @param strKey 存放数据Key
     * @return 对应Key的数据对象
     */
    public static Object getData(String strKey) {
        return gloableData.get(strKey);
    }
    /**
     * 从Application中移除数据
     */
    public static void removeData(String key){
        if(gloableData.containsKey(key)) gloableData.remove(key);
    }
    /****************Application数据操作API（结束）*******************/
    /****************Application中存放的Activity操作（压栈/出栈）API（开始）***/
    /**
     * 将Activity压入Application栈
     * @param task 将要压入栈的Activity对象
     */
    public  void pushTask(WeakReference<Activity> task) {
        activitys.push(task);
    }
    /**
     * 将传入的Activity对象从栈中移除
     * @param task
     */
    public  void removeTask(WeakReference<Activity> task) {
        activitys.remove(task);
    }
    /**
     * 根据指定位置从栈中移除Activity
     * @param taskIndex Activity栈索引
     */
    public  void removeTask(int taskIndex) {
        if (activitys.size() > taskIndex)
            activitys.remove(taskIndex);
    }
    /**
     * 将栈中Activity移除至栈顶
     * 使栈低到栈顶,根元素以上都弹出
     */
    public  void removeToTop() {
        int end = activitys.size();
        int start = 1;
        for (int i = end - 1; i >= start; i--) {
            if (!activitys.get(i).get().isFinishing()) {
                activitys.get(i).get().finish();
            }
        }
    }
    /**
     * 移除全部（用于整个应用退出）
     */
    public  void removeAll() {
        //finish所有的Activity
        for (WeakReference<Activity> task : activitys) {
            if (!task.get().isFinishing()) {
                task.get().finish();
            }
        }
    }
    /**
     * 彻底关闭应用
     */
    public  void removeAllProgram() {
        //finish所有的Activity
        for (WeakReference<Activity> task : activitys) {
            if (!task.get().isFinishing()) {
                task.get().finish();
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }
    /********Application中存放的Activity操作（压栈/出栈）API（结束）******/

    /**
     * 重启应用
     * 需要重写intent.setClassName设置对应的启动入口
     */
    public void restartApp() {
        Intent intent = new Intent();
        // 参数1：包名，参数2：程序入口的activity
        intent.setClassName(getPackageName(), "启动LaunchActivity");
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
