package kxlive.gjrlibrary.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.io.Serializable;

import kxlive.gjrlibrary.R;
import kxlive.gjrlibrary.utils.ToolAlert;

/**
 * 基本的操作共通抽取
 * @version 1.0
 */
public class Operation {

	/**激活Activity组件意图**/
	private Intent mIntent = new Intent();
	/***上下文**/
	private Activity mContext = null;
	/***整个应用Applicaiton**/
	private BaseApplication mApplication = null;
	
	public Operation(Activity mContext) {
		this.mContext = mContext;
		mApplication = (BaseApplication) this.mContext.getApplicationContext();
	}

    /**
     * 动画结束
     */
    public void finish() {
        mContext.finish();
        mContext.overridePendingTransition(0, R.anim.base_slide_right_out);
    }

    /**
     * 动画结束
     */
    public void finishByResultCode(int ResultCode) {
        mContext.setResult(ResultCode);
        mContext.finish();
        mContext.overridePendingTransition(0, R.anim.base_slide_right_out);
    }

	/**
	 * 跳转Activity
	 * @param activity 需要跳转至的Activity
	 */
	public void forward(Class<? extends Activity> activity) {
		mIntent.setClass(mContext, activity);
		mContext.startActivity(mIntent);
		mContext.overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
	}

    /**
     * 跳转Activity
     * @param activity 需要跳转至的Activity,ForResult
     */
    public void forwardForResult(Class<? extends Activity> activity,int requestCode) {
        mIntent.setClass(mContext, activity);
        mContext.startActivityForResult(mIntent,requestCode);
        mContext.overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
    }



	/**
	 * 设置传递参数
	 * @param key 参数key
	 * @param value 数据传输对象
	 */
	public void addParameter(String key,Bundle value) {
		mIntent.putExtra(key, value);
	}
	
	/**
	 * 设置传递参数
	 * @param key 参数key
	 * @param value 数据传输对象
	 */
	public void addParameter(String key,Serializable value) {
		mIntent.putExtra(key, value);
	}
	
	/**
	 * 设置传递参数
	 * @param key 参数key
	 * @param value 数据传输对象
	 */
	public void addParameter(String key,String value) {
		mIntent.putExtra(key, value);
	}

	
	/**
	 * 设置全局Application传递参数
	 * @param strKey 参数key
	 * @param value 数据传输对象
	 */
	public void addGloableAttribute(String strKey,Object value) {
		mApplication.saveData(strKey, value);
	}
	
	/**
	 * 获取跳转时设置的参数
	 * @param strKey
	 * @return
	 */
	public Object getGloableAttribute(String strKey) {
		return mApplication.getData(strKey);
	}
	
	/**
	 * 弹出等待对话框
	 * @param message 提示信息
	 */
	public void showLoading(String message){
		ToolAlert.loading(mContext, message);
	}
	
	/**
	 * 弹出等待对话框
	 * @param message 提示信息
	 * @param listener 按键监听器
	 */
	public void showLoading(String message,ToolAlert.ILoadingOnKeyListener listener){
		ToolAlert.loading(mContext, message, listener);
	}
	
	/**
	 * 更新等待对话框显示文本
	 * @param message 需要更新的文本内容
	 */
	public void updateLoadingText(String message){
		ToolAlert.updateProgressText(message);
	}
	
	/**
	 * 关闭等待对话框
	 */
	public void closeLoading(){
		ToolAlert.closeLoading();
	}
	
}
