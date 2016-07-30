package kxlive.gjrlibrary.base;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;

import java.lang.ref.WeakReference;

import kxlive.gjrlibrary.BuildConfig;
import kxlive.gjrlibrary.R;
import kxlive.gjrlibrary.config.Constants;
import kxlive.gjrlibrary.entity.eventbus.EventAsync;
import kxlive.gjrlibrary.entity.eventbus.EventBackground;
import kxlive.gjrlibrary.entity.eventbus.EventMain;
import kxlive.gjrlibrary.http.RequestManager;
import kxlive.gjrlibrary.utils.SystemBarTintManager;
import roboguice.activity.RoboFragmentActivity;

/**
 * 基类公共资源服务
 * @author gjr
 */
public class BaseActivity extends RoboFragmentActivity {
    /**当前Activity的弱引用，防止内存泄露**/
    private WeakReference<Activity> context = null;
    /**共通操作**/
    public Operation mBaseOperation = null;
    /**当前Activity渲染的视图View**/
    public View mContextView = null;
    public static final boolean DEBUG = BuildConfig.DEBUG;
	public String TAG ;
    public BaseApplication bApp;
    public DisplayMetrics gMetrice;
    public Resources mRes;
    public FragmentActivity mActivity;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
        TAG=getClass().getSimpleName();
		Log.d(TAG, "onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setTranslucentStatus();
		initData();
	}

    public boolean onEventMainThread(EventMain event) {
        if (event.getName().equals(getClass().getName())) {
            Log.i(TAG,"onEventMainThread Describe:"+event.getDescribe());
            return true;
        }
        return false;
    }

    public boolean onEventAsync(EventAsync event) {
        if (event.getName().equals(getClass().getName())) {
            Log.i(TAG,"onEventAsync Describe:"+event.getDescribe());
            return true;
        }
        return false;
    }

    public boolean onEventBackgroundThread(EventBackground event) {
        if (event.getName().equals(getClass().getName())) {
            Log.i(TAG,"onEventBackgroundThread Describe:"+event.getDescribe());
            return true;
        }
        return false;
    }

    /**
     *设置状态栏背景状态
     */
    public void setTranslucentStatus() {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.statusbar_bg);
	}

	private void initData() {
        mActivity = this;
        context = new WeakReference<Activity>(this);
		mRes = getResources();
        //实例化共通操作
        mBaseOperation = new Operation(this);
        getBaseApplication();
        getDisplayMetrics();
        //将当前Activity压入栈
        bApp.pushTask(context);
	}

    private DisplayMetrics getDisplayMetrics() {
        if (gMetrice == null) {
            gMetrice = new DisplayMetrics();
            Display defaultDisplay = getWindowManager().getDefaultDisplay();
            defaultDisplay.getMetrics(gMetrice);
        }
        return gMetrice;
    }

    private BaseApplication getBaseApplication() {
        if (bApp == null)
            bApp = (BaseApplication) getApplication();
        return bApp;
    }
    /**
     * 获取当前Activity
     * @return
     */
    protected Activity getContext(){
        if(null != context)
            return context.get();
        else
            return null;
    }

    /**
     * 获取共通操作机能
     */
    public Operation getOperation(){
        return this.mBaseOperation;
    }

    /**
     * 执行网络请求，加入执行队列
     * @param request
     */
	protected void executeRequest(Request<?> request) {
			request.setRetryPolicy(new DefaultRetryPolicy(15 * 1000, Constants.VOLLEY_MAX_RETRY_TIMES, 1.0f));
			RequestManager.addRequest(request, this);
	}

    /**
     * 执行网络请求，加入执行队列
     * @param request
     * @param maxNumRetries
     */
    protected void executeRequest(Request<?> request,int maxNumRetries) {
            request.setRetryPolicy(new DefaultRetryPolicy(15 * 1000, maxNumRetries, 1.0f));
            RequestManager.addRequest(request, this);
    }

    /**
     * 执行网络请求，加入执行队列
     */
    protected void cancelAllRequest() {
        RequestManager.cancelAll(this);
    }

    @Override
    protected void onDestroy() {
        bApp.removeTask(context);
        super.onDestroy();
    }

    /**
     * 显示短提示
     * @param txt
     */
    protected void showShortTip(String txt){
        Toast.makeText(mActivity, txt, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示长提示
     * @param txt
     */
    protected void showLongTip(String txt){
        Toast.makeText(mActivity, txt, Toast.LENGTH_LONG).show();
    }


}