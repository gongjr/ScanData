package com.android.volley;

/**
 * 与服务器建立Socket连接后,socket从服务器读数据的超时时间，
 * 即从服务器获取响应数据需要等待的时间
 * 捕捉SocketTimeoutException异常后抛出
 * Created by gjr on 2016/4/1.
 */
public class SocketTimeoutError extends TimeoutError{
}
