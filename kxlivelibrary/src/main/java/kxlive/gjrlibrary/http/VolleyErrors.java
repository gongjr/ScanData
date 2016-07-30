package kxlive.gjrlibrary.http;

/**
 * Volley的网络响应异常，消息类
 * Created by gjr on 2015/8/12.
 */
public class VolleyErrors {

    int errorType;
    String errorMsg;

    public int getErrorType() {
        return errorType;
    }

    public void setErrorType(int errorType) {
        this.errorType = errorType;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
