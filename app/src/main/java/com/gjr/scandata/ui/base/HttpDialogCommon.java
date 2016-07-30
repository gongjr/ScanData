package com.gjr.scandata.ui.base;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjr.scandata.R;


/**
 * 基类中提示使用的弹框
 * 
 * @author gjr
 *
 * 2015年7月15日
 */
public class HttpDialogCommon extends DialogFragment {

	private View mView;
	private RelativeLayout rl_globalView;
	private LinearLayout ll_contentView;
	private TextView mContextView;
	private String contentTxt;
	
	@SuppressLint("InlinedApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
		 * DialogFragment.STYLE_NO_FRAME
	     * Style for {@link #setStyle(int, int)}: don't draw
	     * any frame at all; the view hierarchy returned by {@link #onCreateView}
	     * is entirely responsible for drawing the dialog.
	     */
		setStyle(DialogFragment.STYLE_NO_FRAME, 0); //R.style.dialog_style);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//setStyle(R.style.dialog_style, 0); //设置无效
		//getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getDialog().setCancelable(false);
	    getDialog().setCanceledOnTouchOutside(false);
	    getDialog().getWindow().setBackgroundDrawable(
	    		new ColorDrawable(Color.parseColor("#00000000"))); //设置有效，但是还是有阴影
		mView = inflater.inflate(R.layout.http_dialog_common, null); //在layout文件中设置也无效
		return mView;
	}
	
	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
		initView();
		initData();
		initListener();
	}
	
	public void initView(){
		mContextView = (TextView)mView.findViewById(R.id.tv_content_txt);
		rl_globalView = (RelativeLayout)mView.findViewById(R.id.rl_global);
		ll_contentView = (LinearLayout)mView.findViewById(R.id.ll_content);
	}
	
	public void initData(){
		if(contentTxt!=null){
			mContextView.setText(contentTxt);
		}
	}
	
	public void initListener(){
		rl_globalView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//dismiss();
			}
		});
	}
	
	public void setNoticeText(String txt){
		contentTxt = txt;
	}
}
