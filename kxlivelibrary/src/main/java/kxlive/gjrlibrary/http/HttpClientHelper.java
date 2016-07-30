package kxlive.gjrlibrary.http;

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

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

/**
 * 网络连接辅助类
 * 
 * @author gongjr
 * 
 * */
public class HttpClientHelper {
	public static final String POST = "POST";
	public static final String GET = "GET";
	public static final String GET_BANG = "GET_BANG";
	private static HttpClient FoodMeikeHttpClient = null;
	private static final String CHARSET = HTTP.UTF_8;
	private static final String TAG = "HttpClientHelper";
	/**
	 * 应用连接的地址
	 */
//	public String urladdress = HttpHelper.HOST;
	public String urladdress = "";

    public HttpClientHelper() {
	}

    public HttpClientHelper(String urladdress) {
		this.urladdress = urladdress;
	};

	public static HttpClientHelper getNewInstance(String urladdress) {
		if (urladdress != null) {
			return new HttpClientHelper(urladdress);
		} else {
			return new HttpClientHelper();
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

	@SuppressWarnings("finally")
	public String Connect(String address, List<NameValuePair> params, String method) throws Exception {
		String result = null;
		HttpPost httpPost = null;
		HttpGet httpGet = null;
		HttpClient client = getHttpClient();
		if (method.equals(HttpClientHelper.POST)) {
			httpPost = new HttpPost(address);
			UrlEncodedFormEntity uefEntity = null;
			uefEntity = new UrlEncodedFormEntity(params, "UTF-8");
			Log.i(TAG, "URL_POST : " + urladdress + address);
			Log.i(TAG, "URL_PARAMS : " + params);
			httpPost.setEntity(uefEntity);
			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		} else {
			String url = address;
//			String url_ = url.replace("\"", "\\\"");
			Log.i(TAG, "URL_GET : " + url);
			httpGet = new HttpGet(url);
            httpGet.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");

        }
		HttpEntity entity = null;
		HttpResponse response = null;
		try {
			if (method.equals(HttpClientHelper.POST)) {
				response = client.execute(httpPost);
				if (response.getStatusLine().getStatusCode() == 200) {
					entity = response.getEntity();
					if (entity != null) {
						result = new String(EntityUtils.toString(entity, "UTF-8"));
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
						result = new String(EntityUtils.toString(entity, "UTF-8"));
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

	/**
	 * 当一个DefaultHttpClient实例不再需要而且要脱离范围时
	 * 和它关联的连接管理器必须调用ClientConnectionManager#shutdown()方法关闭。
	 */
	public void httpClientShutDown(){
		if(FoodMeikeHttpClient!=null){
			FoodMeikeHttpClient.getConnectionManager().shutdown();
			FoodMeikeHttpClient=null;
		}
	}
}
