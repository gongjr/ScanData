package kxlive.gjrlibrary.http;

import com.android.volley.Response;
/**
 * Created by gjr on 2016/3/21.
 */

public interface ResponseListener<T> extends Response.ErrorListener,Response.Listener<T> {

}
