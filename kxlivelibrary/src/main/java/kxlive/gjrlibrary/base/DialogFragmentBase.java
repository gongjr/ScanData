package kxlive.gjrlibrary.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import kxlive.gjrlibrary.config.Constants;
import kxlive.gjrlibrary.http.RequestManager;
import roboguice.fragment.RoboDialogFragment;

/**
 * 
 * @author gjr
 *
 * 2015年6月25日
 */
public class DialogFragmentBase extends RoboDialogFragment {

	protected Activity mActivity;
    private Toast mToast;
    public Gson gson;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mActivity = getActivity();
        mToast=new Toast(mActivity);
        gson=new Gson();
	}
	
	protected void executeRequest(Request<?> request) {
		request.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, Constants.VOLLEY_MAX_RETRY_TIMES, 1.0f));
		RequestManager.addRequest(request, this);
	}

	protected Response.ErrorListener errorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(mActivity, error.getMessage(), Toast.LENGTH_LONG).show();
			}
		};
	}
	
	protected void showShortToast(String txt){
		Toast.makeText(mActivity, txt, Toast.LENGTH_SHORT).show();
	}

    protected void showShortToast(String txt,int time){
        mToast.makeText(mActivity, txt, time).show();
    }
	
	protected void showLongToast(String txt){
		Toast.makeText(mActivity, txt, Toast.LENGTH_SHORT).show();
	}
}
