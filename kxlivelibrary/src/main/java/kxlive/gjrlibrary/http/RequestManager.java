/*
 * Created by Storm Zhang, Feb 11, 2014.
 */

package kxlive.gjrlibrary.http;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.os.Build;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.OkHttpStack;
import com.android.volley.toolbox.Volley;

import kxlive.gjrlibrary.rx.Result;
import kxlive.gjrlibrary.rx.RxBus;
import rx.Observable;
import rx.functions.Func1;

public class RequestManager {
	private static RequestQueue mRequestQueue;

	private RequestManager() {
	}

    /**
     * 默认初始化,volley默认实现
     * @param context
     */
	public static void init(Context context) {
		mRequestQueue = Volley.newRequestQueue(context);
	}

    /**
     * api>9,2.3以下是httpClient,以后用okhttp实现传输层
     * okhttp不支持2.3以下版本
     * @param context
     * @param okHttpStack
     */
    public static void init(Context context,OkHttpStack okHttpStack) {
        String userAgent = "com/android/volley/0";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        if (Build.VERSION.SDK_INT >= 9) {
            mRequestQueue = Volley.newRequestQueue(context,okHttpStack);
        } else {
            mRequestQueue = Volley.newRequestQueue(context,new HttpClientStack(AndroidHttpClient.newInstance(userAgent)));
        }

    }

	public static RequestQueue getRequestQueue() {
		if (mRequestQueue != null) {
			return mRequestQueue;
		} else {
			throw new IllegalStateException("RequestQueue not initialized");
		}
	}
	
	public static void addRequest(Request<?> request, Object tag) {
        if (tag != null) {
            request.setTag(tag);
        }
        mRequestQueue.add(request);
    }

    /**
     * 执行请求任务,并返回一个RxJava的Observable类型
     */
    public static Observable<Result> getResult(final Request<?> request, Object tag) {
        addRequest(request,tag);
        return RxBus.getDefault().take(Result.class)
                .filter(new Func1<Result, Boolean>() {
                    @Override
                    public Boolean call(Result result) {
                        return request.getUrl().equals(result.url);
                    }
                })
                .take(1);
    }
	
	public static void cancelAll(Object tag) {
        mRequestQueue.cancelAll(tag);
    }
}
