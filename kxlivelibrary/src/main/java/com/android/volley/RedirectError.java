package com.android.volley;

/**
 * 是否重定向,301页面已经永久移动到另外一个新地址,302页面暂时移动到新地址
 * Indicates that there was a redirection.
 */
public class RedirectError extends VolleyError {

    public RedirectError() {
    }

    public RedirectError(final Throwable cause) {
        super(cause);
    }

    public RedirectError(final NetworkResponse response) {
        super(response);
    }
}
