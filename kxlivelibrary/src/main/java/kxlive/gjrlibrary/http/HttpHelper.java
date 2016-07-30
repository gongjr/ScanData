package kxlive.gjrlibrary.http;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 网络连接辅助类
 *
 * @author gongjr
 *
 * */
public class HttpHelper {
    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String GET_BANG = "GET_BANG";
    private static HttpClient FoodMeikeHttpClient = null;
    private static ExecutorService executorService = null;
    private static final String CHARSET = HTTP.UTF_8;
    private static final String TAG = "HttpClientHelper";
    /*
     * 测试环境
     */
    private String urladdress = "";

    private HttpHelper() {
        /**
         * busiunion_tst测试环境
         */
        // this.urladdress = "http://115.29.35.199:19890/busiunion_tst";

        /**
         * busiunion生产环境
         */
        // this.urladdress ="http://115.29.35.199:17890/busiunion";
        /**
         * Beautiful微信测试环境
         */
		/*
		 * this.urladdress = "http://115.29.35.199:19890/Beautiful";
		 */
//		/**
//		 * Beautiful生产环境
//		 */
//		 this.urladdress = "http://115.29.35.199:38080/Beautiful";
        this.urladdress = "http://115.29.35.199:29890/Beautiful";
        /**
         * Beautiful测试环境
         */
//		this.urladdress = "http://115.29.35.199:58080/Beautiful";
//		this.urladdress = "http://192.168.1.191:8080/Beautiful";
    }

    private HttpHelper(String urladdress) {
        this.urladdress = urladdress;
    };

    public static HttpHelper getNewInstance(String urladdress) {
        if (urladdress != null) {
            return new HttpHelper(urladdress);
        } else {
            return new HttpHelper();
        }
    }

    /**
     * 设置单例模式，使用线程安全的连接管理来创建HttpClient，实现并发连接
     *
     * @return
     */
    public static synchronized HttpClient getHttpClient() {
        if (null == FoodMeikeHttpClient) {
            HttpParams params = new BasicHttpParams();
            // 设置一些基本参数
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, CHARSET);
            HttpProtocolParams.setUseExpectContinue(params, true);
            // 超时设置
			/* 从连接池中取连接的超时时间 */
            ConnManagerParams.setTimeout(params, 5000);
			/* 连接超时 */
            HttpConnectionParams.setConnectionTimeout(params, 10000);
			/* 请求超时 */
            HttpConnectionParams.setSoTimeout(params, 10000);
            // 设置我们的HttpClient支持HTTP和HTTPS两种模式
            SchemeRegistry schReg = new SchemeRegistry();
            schReg.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));
            schReg.register(new Scheme("https", SSLSocketFactory
                    .getSocketFactory(), 443));
            // 使用线程安全的连接管理来创建HttpClient
            ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
                    params, schReg);
            FoodMeikeHttpClient = new DefaultHttpClient(conMgr, params);
        }

        return FoodMeikeHttpClient;
    }

    /**
     * 单例线程池来管理网络访问的匿名线程 最大支持并发4条匿名线程，多余排队等待
     *
     * @return
     */
    public static synchronized ExecutorService getExecutor() {
        if (null == executorService) {
            executorService = Executors.newFixedThreadPool(4);// Executors的静态方法来创建，固定数量线程池
        }
        return executorService;
    }

    @SuppressWarnings("finally")
    public String Connect(String address, List<NameValuePair> params,
                          String method) throws Exception {
        String result = null;
        HttpPost httpPost = null;
        HttpGet httpGet = null;
        HttpClient client = getHttpClient();
        if (method.equals(HttpClientHelper.POST)) {
            httpPost = new HttpPost(address);
            UrlEncodedFormEntity uefEntity = null;
            uefEntity = new UrlEncodedFormEntity(params, "UTF-8");
            Log.i(TAG, "URL_POST : " + address);
            Log.i(TAG, "URL_PARAMS : " + params);
            httpPost.setEntity(uefEntity);
            httpPost.setHeader("Content-Type",
                    "application/x-www-form-urlencoded; charset=utf-8");
        } else {
            if (method.equals(HttpClientHelper.GET)) {
                String url = address;
                Log.i(TAG, "URL_GET : " + url);
                httpGet = new HttpGet(url);
            } else {
                Log.i(TAG, "URL_ADDRESS : " + address);
                httpGet = new HttpGet(address);
            }
        }
        HttpEntity entity = null;
        HttpResponse response = null;
        try {
            if (method.equals(HttpClientHelper.POST)) {
                response = client.execute(httpPost);
                if (response.getStatusLine().getStatusCode() == 200) {
                    entity = response.getEntity();
                    if (entity != null) {
                        result = new String(EntityUtils.toString(entity,
                                "UTF-8"));
                    }
                } else {
                    Log.i(TAG, "response.getStatusLine().getStatusCode() :"
                            + response.getStatusLine().getStatusCode());
                }
            } else {
                response = client.execute(httpGet);
                if (response.getStatusLine().getStatusCode() == 200) {
                    entity = response.getEntity();
                    if (entity != null) {
                        result = new String(EntityUtils.toString(entity,
                                "UTF-8"));
                    }
                } else {
                    Log.i(TAG, "response.getStatusLine().getStatusCode() :"
                            + response.getStatusLine().getStatusCode());
                }
            }
        } catch (SocketTimeoutException e) {
            // 与服务器建立连接超时
            e.printStackTrace();
        } catch (UnknownHostException e) {
            // 请求的主机地址无效
            e.printStackTrace();
        } catch (IOException e) {
            // 建立连接异常向外部接口发送数据失败
            // 在创建 Socket 实例的构造函数正确返回之前，将要进行 TCP 的三次握手协议，
            // TCP 握手协议完成后，Socket 实例对象将创建完成，否则将抛出 IOException 错误。
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpPost != null) {
                httpPost.abort();
                // 中断请求/*释放资源*/,接下来可以开始另一段请求
            }
            if (httpGet != null) {
                httpGet.abort();
                // 中断请求/*释放资源*/,接下来可以开始另一段请求
            }
            try {
                if (entity != null) {
                    if (entity.isStreaming()) {
                        InputStream instream = entity.getContent();
                        if (instream != null) {
                            instream.close();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "HTTP_result:" + result);
            return result;
        }
    }

    @SuppressWarnings("finally")
    public void ConnectWithAsyncTask(final String address,
                                     final List<NameValuePair> params, final String method,
                                     final ResponseInterface Response) throws Exception {
        class ConnectAsyncTask extends AsyncTask<Void, Integer, String> {
            @Override
            protected String doInBackground(Void... param) {
                String result = null;
                HttpPost httpPost = null;
                HttpGet httpGet = null;
                HttpClient client = getHttpClient();
                if (method.equals(HttpClientHelper.POST)) {
                    httpPost = new HttpPost(urladdress + address);
                    UrlEncodedFormEntity uefEntity = null;
                    try {
                        uefEntity = new UrlEncodedFormEntity(params, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "URL_POST : " + urladdress + address);
                    Log.i(TAG, "PARAMS : " + params);
                    httpPost.setEntity(uefEntity);
                    httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                } else {
                    if (method.equals(HttpClientHelper.GET)) {
                        String url = urladdress + address;
                        Log.i(TAG, "URL_GET : " + url);
                        httpGet = new HttpGet(url);
                    } else {
                        Log.i(TAG, "URL_ADDRESS : " + address);
                        httpGet = new HttpGet(address);
                    }
                }
                HttpEntity entity = null;
                HttpResponse response = null;
                try {
                    if (method.equals(HttpClientHelper.POST)) {
                        response = client.execute(httpPost);

                    } else {
                        response = client.execute(httpGet);
                    }
                    if (response.getStatusLine().getStatusCode() == 200) {
                        entity = response.getEntity();
                        if (entity != null) {
                            result = new String(EntityUtils.toString(entity, "UTF-8"));
                        }
                    } else {
                        Log.i(TAG, "response.getStatusLine().getStatusCode() :"
                                + response.getStatusLine().getStatusCode());
                    }
                } catch (SocketTimeoutException e) {
                    Log.i(TAG, "result-----------------" + result);
                    Log.i(TAG, "SocketTimeoutException-----------------服务器响应超时，检查网络信号或服务器");
                    // 服务器响应超时，(成功提交请求到服务器，接收服务器响应消息超时异常)
                    e.printStackTrace();
                    result = "1";
                } catch (UnknownHostException e) {
                    Log.i(TAG, "result-----------------" + result);
                    Log.i(TAG, "UnknownHostException-----------------请求主机地址无效，检查服务器");
                    // 请求的主机地址无效
                    e.printStackTrace();
                    result = "2";
                } catch (IOException e) {
                    Log.i(TAG, "result-----------------" + result);
                    Log.i(TAG, "IOException-----------------建立连接异常，检查本地网络或确认地址是否有效");
                    // 建立连接异常,向外部接口发送数据失败,提交数据到服务器失败
                    // 在创建 Socket 实例的构造函数正确返回之前，将要进行 TCP 的三次握手协议，
                    // TCP 握手协议完成后，Socket 实例对象将创建完成，否则将抛出 IOException 错误。
                    e.printStackTrace();
                    result = "3";
                } catch (Exception e) {
                    Log.i(TAG, "result-----------------" + result);
                    e.printStackTrace();
                } finally {
                    if (httpPost != null) {
                        httpPost.abort();
                        // 中断请求,接下来可以开始另一段请求
                    }
                    if (httpGet != null) {
                        httpGet.abort();
                        // 中断请求,接下来可以开始另一段请求
                    }
                    try {
                        if (entity != null) {
                            if (entity.isStreaming()) {
                                InputStream instream = entity.getContent();
                                if (instream != null) {
                                    instream.close();
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return result;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result == null) {
                    Response.onException(0);
                } else if (result.equals("1")) {
                    Response.onException(1);
                } else if (result.equals("2")) {
                    Response.onException(2);
                } else if (result.equals("3")) {
                    Response.onException(3);
                } else {
                    JSONObject json;
                    try {
                        json = new JSONObject(result);
                        if (json.getString("msg").equals("ok")
                                && json.getString("errcode").equals("0")) {
                            Response.onSuccess(result);
                        } else {
                            Response.onFailure(result);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Response.onFinish();
            }
        }
        new ConnectAsyncTask().executeOnExecutor(getExecutor());
    }

    /**
     * 当一个DefaultHttpClient实例不再需要而且要脱离范围时
     * 和它关联的连接管理器必须调用ClientConnectionManager#shutdown()方法关闭。
     */
    public void httpClientShutDown(){
        if(FoodMeikeHttpClient!=null){
            FoodMeikeHttpClient.getConnectionManager().shutdown();
            FoodMeikeHttpClient=null;
        }
        if (executorService!=null) {
            executorService.shutdownNow();
            executorService=null;
        }
    }
//	handler+线程池使用
//	threadPool.execute(runnable);
//	runnable(){
//	handler.send();}
//	handler{}
//	接口回调修改返回消息通知网络消息
//	static class ImageHandler extends Handler {
//
//		private AsyncImageLoaderListener listener;
//
//		public ImageHandler(AsyncImageLoaderListener listener) {
//			this.listener = listener;
//		}
//
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			Holder holder=new Holder();
//			holder=(Holder) msg.obj;
//			listener.onImageLoader(holder.image,holder.url);
//		}
//			
//	}


}
