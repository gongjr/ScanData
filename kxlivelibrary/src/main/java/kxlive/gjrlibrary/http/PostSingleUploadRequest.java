package kxlive.gjrlibrary.http;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;


/**
 * 自定义上传图片或者其他文件的请求(支持添加参数)
 * 此处已图片上传为主
 * 实现研究借鉴网页表单提交时的Post请求的格式，然后自己构造一个合法的Post请求
 * 需要重写getBody()方法，逐行仿照Post格式写入我们自己的图片和参数，就可实现参数+图片的上传。多张图片的上传原理相同。
 * 另外，图片要注意在上传之前进行压缩处理。另外，为了方便服务器解析多张图片，将图片参数设置为uploadedfile[index]格式
 * 后台扒的http://www.kxlive.com/tacos/uploadMainFile.do
 * Created by gjr on 2016/3/21.
 */
public class PostSingleUploadRequest<T> extends Request<T> {

    /**
     * 正确数据的时候回掉用
     */
    private ResponseListener mListener ;
    private ImageInfo imageInfo;

    private static final String BOUNDARY = "-----ZZQ----MZZ-----"; //数据分隔线
    private static final String MULTIPART_FORM_DATA = "multipart/form-data";//使用表单数据方法提交
    private static final String TAG = "hehe_upload_request";
    private static final String PARAM = "uploadedfile";//图片的参数，<span style="font-family: Arial, Helvetica, sans-serif;">为了上传多张图片，在下面的封装中使用uploadedfile[index]格式作为图片参数</span>


    private Map<String, String> params;
    private Gson gson;
    private Type clazz;

    public PostSingleUploadRequest(String url, ImageInfo mimageInfo,
                                   Map<String, String> params,
                                   Type type, ResponseListener listener) {
        super(Method.POST, url, listener);
        this.mListener = listener ;
        this.params = params;
        this.gson = new Gson();
        this.clazz = type;
        setShouldCache(false);
        imageInfo = mimageInfo ;
        //设置请求的响应事件，因为文件上传需要较长的时间，所以在这里加大了，设为5秒
        setRetryPolicy(new DefaultRetryPolicy(5000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    /**
     * 这里开始解析数据
     * @param response Response from the network
     * @return
     */
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String mString =
                    new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Log.v(TAG, "mString = " + mString);
//            T result = gson.fromJson(StringUtils.FixJsonString(mString), clazz);
            T result = gson.fromJson(mString, clazz);
            return Response.success(result,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    /**
     * 回调正确的数据
     * @param response The parsed response returned by
     */
    @Override
    protected void deliverResponse(T response) {

        mListener.onResponse(response);
    }

    //重写getBody()方法，封装Post包
    @Override
    public byte[] getBody() throws AuthFailureError {
        if (imageInfo == null||imageInfo.getmBitmap() == null){
            return super.getBody() ;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream() ;

        if(params!=null&&!params.isEmpty()) {
            StringBuilder sbParams = new StringBuilder();

            for (Map.Entry<String, String> o : params.entrySet()) {
                Map.Entry entry = (Map.Entry) o;

                /*第一行*/
                //`"--" + BOUNDARY + "\r\n"`
                sbParams.append("--" + BOUNDARY);
                sbParams.append("\r\n");
                /*第二行*/
                //Content-Disposition: form-data; name="参数的名称"; + 参数 + "\r\n"
                sbParams.append("Content-Disposition: form-data;");
                sbParams.append(" name=\"");
                sbParams.append((String) entry.getKey());
                sbParams.append("\"");
                sbParams.append("\r\n");
                sbParams.append("\r\n");
                sbParams.append((String) entry.getValue());
                sbParams.append("\r\n");
            }
            try {
                bos.write(sbParams.toString().getBytes("utf-8"));
//                bos.write("\r\n".getBytes("utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        StringBuilder sb= new StringBuilder() ;
            /*第一行*/
        //`"--" + BOUNDARY + "\r\n"`
        sb.append("--"+BOUNDARY);
        sb.append("\r\n") ;
            /*第二行*/
        //Content-Disposition: form-data; name="参数的名称"; filename="上传的文件名" + "\r\n"
        sb.append("Content-Disposition: form-data;");
        sb.append(" name=\"");
        sb.append(imageInfo.getmName()); //后台测试接口mainFile作为图片参数
        sb.append("\"") ;
        sb.append("; filename=\"") ;
        sb.append(imageInfo.getFileName()) ;
        sb.append("\"");
        sb.append("\r\n") ;
            /*第三行*/
        //Content-Type: 文件的 mime 类型 + "\r\n"
        sb.append("Content-Type: ");
        sb.append(imageInfo.getMime()) ;
        sb.append("\r\n") ;
            /*第四行*/
        //"\r\n"
        sb.append("\r\n") ;
        try {
            bos.write(sb.toString().getBytes("utf-8"));
                /*第五行*/
            //文件的二进制数据 + "\r\n"
            bos.write(imageInfo.getValue());
            bos.write("\r\n".getBytes("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*结尾行*/
        //`"--" + BOUNDARY + "--" + "\r\n"`
        String endLine = "--" + BOUNDARY + "--" + "\r\n" ;
        try {
            bos.write(endLine.getBytes("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Log.v(TAG,"imageInfo =\n"+bos.toString()) ;
        return bos.toByteArray();
    }
    //Content-Type: multipart/form-data; boundary=----------8888888888888
    @Override
    public String getBodyContentType() {
        return MULTIPART_FORM_DATA+"; boundary="+BOUNDARY;
    }

//    @Override //注意一旦重写getBody()方法，则使用此方法添加参数无效。
//    protected Map<String, String> getParams() throws AuthFailureError{
//        return params;
//    }
}
