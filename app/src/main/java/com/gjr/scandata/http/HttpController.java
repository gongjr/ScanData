package com.gjr.scandata.http;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gjr.scandata.config.Constants;

import kxlive.gjrlibrary.http.RequestManager;
import kxlive.gjrlibrary.rx.Result;
import rx.Observable;

/**
 * 统一管理公共接口地址,参数传入,接口回调响应交互
 * post和get仅代表本次调用的http方法
 * 2015年6月17日
 */
public class HttpController {
    /**
     * 默认服务器配置,程序初始化确认的地址,恢复的初始状态
     */
    public static final AddressState init=AddressState.release_jinjiamen;
    /**
     * 当前服务器环境值,如果配置,保有最新选择
     */
    public static AddressState Address=init;

    /**
     * 使用地址内容
     */
    public static  String HOST = Address.getKey();

    /**
     * AppKey 服务器约定app更新key字段
     */
    public static final String AppKey = "com.gjr.scandata";

    public final String sharedPreferenceAddressKey="Address";

    public static HttpController httpController;

    public static HttpController getInstance() {
        if (httpController == null) httpController = new HttpController();
        return httpController;
    }

    public boolean toInitAddress(Context mContext) {
        Boolean isInit=false;
        if (Address.getValue().equals(init.getValue()))isInit=true;
        setAddress(init);
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(AppKey, mContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear().apply();
        return isInit;
    }

    public Boolean isInit(){
        if (Address.getValue().equals(init.getValue()))return true;
        return false;
    }

    public AddressState getAddress() {
        return Address;
    }

    public  void setAddress(AddressState pAddress) {
        Address = pAddress;
        HOST = Address.getKey();
    }

    /**
     * 保存当前服务器环境到配置文件
     */
    public void saveAddress(Context mContext,AddressState pAddress){
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(AppKey, mContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(sharedPreferenceAddressKey, pAddress.getValue());
        editor.apply();
    }

    /**
     * 初始化获取,没有配置的情况下,默认配置环境
     * 静态参数保存服务器地址,下次升级后,还是读取本地配置的环境,而不是服务器默认设置的连接环境
     * @param mContext
     * @return
     */
    public void initAddress(Context mContext){
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(AppKey, mContext.MODE_PRIVATE);
        String addressKey = mSharedPreferences.getString(sharedPreferenceAddressKey, Address.getValue());
        for(AddressState lAddressState:AddressState.values()){
            if (addressKey.equals(lAddressState.getValue())){
                setAddress(lAddressState);
                break;
            }
        }
    }

    /**
     * 执行网络请求，加入执行队列
     * 30秒超时,连接异常默认不自动重新连接
     *
     * @param request
     */
    protected void executeRequest(Request<?> request) {
        request.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, Constants.VOLLEY_MAX_RETRY_TIMES, 1.0f));
        RequestManager.addRequest(request, this);
    }

    /**
     * 执行网络请求，加入执行队列,返回可观察者对象,在外层进行订阅,基于rxjava支持异步响应事件
     * 30秒超时,连接异常默认不自动重新连接
     *
     * @param request
     */
    protected Observable<Result> executeRequestWithAsyncResult(Request<?> request) {
        request.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, Constants.VOLLEY_MAX_RETRY_TIMES, 1.0f));
        return RequestManager.getResult(request, this);
    }

    /**
     * 执行网络请求，加入执行队列
     *
     * @param request
     * @param maxNumRetries
     */
    protected void executeRequest(Request<?> request, int maxNumRetries) {
        request.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, maxNumRetries, 1.0f));
        RequestManager.addRequest(request, this);
    }

    /**
     * 查询类型接口
     *
     * @param listener      响应监听器
     * @param errorListener 异常监听器
     */
    public void getQueryQcType(String time,  Response.Listener listener,
                               Response.ErrorListener errorListener) {
        String param = "/HjpQc/app/queryQcType.do?synTime="+time;
        JsonObjectRequest GoodsType = new JsonObjectRequest(HOST + param, null, listener, errorListener);
        executeRequest(GoodsType);
    }

    /**
     * 查询类型接口
     *
     * @param listener      响应监听器
     * @param errorListener 异常监听器
     */
    public void getQueryQc(String time,  Response.Listener listener,
                               Response.ErrorListener errorListener) {
        String param = "/HjpQc/app/queryQc.do?synTime="+time;
        JsonObjectRequest GoodsType = new JsonObjectRequest(HOST + param, null, listener, errorListener);
        executeRequest(GoodsType);
    }

    /**
     * 查询信息接口,异步结果处理
     *
     * @param listener      响应监听器
     * @param errorListener 异常监听器
     */
    public Observable<Result> getQueryQcWithAsyncResult(String time,  Response.Listener listener,
                           Response.ErrorListener errorListener) {
        String param = "/HjpQc/app/queryQc.do?synTime="+time;
        JsonObjectRequest GoodsType = new JsonObjectRequest(HOST + param, null, listener, errorListener);
        return executeRequestWithAsyncResult(GoodsType);
    }
}
