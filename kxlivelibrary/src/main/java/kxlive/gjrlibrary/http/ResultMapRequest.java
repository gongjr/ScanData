package kxlive.gjrlibrary.http;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author gjr
 * 直接将服务器返回内容，转换为对应实体类抛出
 * @param <T>对应接口文档建立的实体类
 */
public class ResultMapRequest<T> extends Request<T> {

    private final Listener<T> mListener;

    private Gson mGson;

    private Type modelClass;

    private Map<String, String> params;

    public ResultMapRequest(int method, String url, Type model, Listener<T> listener,
                            ErrorListener errorListener) {
        super(method, url, errorListener);
        mGson = new Gson();
        modelClass = model;
        mListener = listener;
    }

    public ResultMapRequest(int method, String url,Map<String, String> param , Type model,Listener<T> listener,
                            ErrorListener errorListener) {
        super(method, url, errorListener);
        mGson = new Gson();
        modelClass = model;
        mListener = listener;
        this.params=param;
    }

    public ResultMapRequest(String url, Type model, Listener<T> listener,
                            ErrorListener errorListener) {
        this(Method.GET, url, model, listener, errorListener);
    }

    /**
     * 原始数据解析
     */
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, HTTP.UTF_8));
            Log.d("VolleyLogTag", "result : " + jsonString);
            // 关键在此处
            // 先判断服务器的返回数据的header,获取字符集编码格式,如果服务器的返回数据的header中没有指定字符集那么就会默认使用HTTP.UTF_8
            // 按编码格式解析出String字符串，
            // 再根据对应接口文档生成的实体类的modelClass，利用Gson生成对应对象
            T result = mGson.fromJson(jsonString, modelClass);
            return Response.success(result,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    /**
     * 成功解析后,响应分发接口
     */
    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError{
        Log.i("VolleyLogTag", "params:" + params.toString());
        return params;
    }

}
