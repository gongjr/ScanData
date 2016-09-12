package com.gjr.scandata.ui.popup;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjr.scandata.R;
import com.gjr.scandata.biz.listener.DialogDelayListener;

import kxlive.gjrlibrary.utils.KLog;

/**
 * 订单提交页面提示弹窗
 * 
 * @author gjr
 *
 * 2015年7月15日
 */
public class MakeOrderFinishDF extends DialogFragment {

	private static final String TAG = MakeOrderFinishDF.class.getSimpleName();
	private View mView;
	private RelativeLayout rl_globalView;
	private LinearLayout ll_contentView;
	private TextView mContextView;
	private String contentTxt;
	private Button btn_navToDesk;
	private ProgressBar prog_progBar;
	private FragmentActivity mActivity;
    private ImageView mImageView;
    private RelativeLayout mRelativeLayout;
    private TextView mTextView;
    private final String TYPE_INIT="INIT";
    private final String TYPE_UPDATE="UPDATE";
    private String TYPE="INIT";

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
		//setStyle(DialogFragment.STYLE_NO_FRAME, 0); //R.style.dialog_style);
		setStyle(DialogFragment.STYLE_NO_TITLE, 0);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//setStyle(R.style.dialog_style, 0); //设置无效
		//getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getDialog().setCancelable(false);
	    getDialog().setCanceledOnTouchOutside(false);
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event){
                if (keyCode == KeyEvent.KEYCODE_BACK)
                    return true; // pretend we've processed it
                else
                    return false; // pass on to be processed as normal
            }
        });
//	    getDialog().getWindow().setBackgroundDrawable(
//	    		new ColorDrawable(Color.parseColor("#00009000"))); //设置有效，但是还是有阴影
		mView = inflater.inflate(R.layout.df_confirm_order_rs, null); //在layout文件中设置也无效
		return mView;
	}
	
	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
		mActivity = getActivity();
        if (arg0!=null){
            TYPE=arg0.getString("TYPE");
            contentTxt=arg0.getString("contentTxt");
        }
		initView();
		initData();
		initListener();
	}
	
	public void initView(){
		prog_progBar = (ProgressBar)mView.findViewById(R.id.handle_prog);
		mContextView = (TextView)mView.findViewById(R.id.tv_content_txt);
		rl_globalView = (RelativeLayout)mView.findViewById(R.id.rl_global);
		ll_contentView = (LinearLayout)mView.findViewById(R.id.ll_content);
		btn_navToDesk = (Button)mView.findViewById(R.id.btn_nav_to_desk);
        mImageView = (ImageView)mView.findViewById(R.id.need_pay_code);
        mRelativeLayout = (RelativeLayout)mView.findViewById(R.id.need_pay_group);
        mTextView = (TextView)mView.findViewById(R.id.need_type_title);
		btn_navToDesk.setClickable(false);
		btn_navToDesk.setEnabled(false);
	}

    public void initData(){
		if(contentTxt!=null){
			mContextView.setText(contentTxt);
		}
        if (TYPE.equals(TYPE_UPDATE)){
            KLog.i("弹窗状态恢复");
            prog_progBar.setVisibility(View.GONE);
            mRelativeLayout.setVisibility(View.GONE);
            mTextView.setVisibility(View.GONE);
            btn_navToDesk.setClickable(true);
            btn_navToDesk.setEnabled(true);
            btn_navToDesk.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
        }
	}
	
	public void initListener(){
		btn_navToDesk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
	
	public void setNoticeText(String txt){
        TYPE=TYPE_INIT;
		contentTxt = txt;
	}
	
	public void updateNoticeText(String txt,int type){
        TYPE=TYPE_UPDATE;
		contentTxt = txt;
		mContextView.setText(txt);
		prog_progBar.setVisibility(View.GONE);
        mRelativeLayout.setVisibility(View.GONE);
        mTextView.setVisibility(View.GONE);
		btn_navToDesk.setClickable(true);
		btn_navToDesk.setEnabled(true);
        if(type==0){
            btn_navToDesk.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }else{
            btn_navToDesk.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
	}

    public void updateNoticeText(String txt){
        contentTxt = txt;
        mContextView.setText(txt);
    }

    public void showNoticeText(String txt,int type,Bitmap url){
        TYPE=TYPE_UPDATE;
        contentTxt = txt;
        mContextView.setText(txt);
        prog_progBar.setVisibility(View.GONE);
        if (url!=null){
            mRelativeLayout.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.VISIBLE);
            mImageView.setImageBitmap(url);
        }

        btn_navToDesk.setClickable(true);
        btn_navToDesk.setEnabled(true);
        if(type==0){
            btn_navToDesk.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }else{
            btn_navToDesk.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
    }


    public void updateNoticeText(String txt,final DialogDelayListener mListener){
        contentTxt = txt;
        TYPE=TYPE_UPDATE;
        KLog.i("mContextView:" + mContextView + "txt:" + txt);
        mContextView.setText(txt);
        prog_progBar.setVisibility(View.GONE);
        mRelativeLayout.setVisibility(View.GONE);
        mTextView.setVisibility(View.GONE);
        btn_navToDesk.setClickable(true);
        btn_navToDesk.setEnabled(true);
        btn_navToDesk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener!=null)
                mListener.onexecute();
                dismiss();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("TYPE", TYPE);
        outState.putString("contentTxt", contentTxt);
        super.onSaveInstanceState(outState);
    }
}
