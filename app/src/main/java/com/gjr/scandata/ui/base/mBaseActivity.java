package com.gjr.scandata.ui.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.gjr.scandata.AppApplication;
import com.gjr.scandata.R;
import com.gjr.scandata.config.Constants;
import com.google.gson.Gson;

import kxlive.gjrlibrary.base.BaseActivity;
import kxlive.gjrlibrary.widget.LeafDialog.DialogDelayListener;
import kxlive.gjrlibrary.widget.LeafDialog.LeafLoadingDialog;

/**
 * Created by gjr on 2015/11/24.
 */
public class mBaseActivity  extends BaseActivity {

    public AppApplication mApp;
    public Gson gson;
    public Toast mToast;
    public LeafLoadingDialog leafDialog;
    private DialogDelayListener leafdelay,delay;
    protected HttpDialogCommon mHttpDialogCommon;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setActivityModel();
        getBaseApplication();
        gson= new Gson();
    }

    private AppApplication getBaseApplication() {
        if (mApp == null)
            mApp = (AppApplication) getApplication();
        return mApp;
    }
    private void setActivityModel() {
        // 无标题
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //保持常亮
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // 不默认弹出输入框
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initMyToast();
    }

    private void initMyToast() {
        View view = getLayoutInflater().inflate(R.layout.toast_content_view, null, false);
        mToast = new Toast(getApplicationContext());
        mToast.setView(view);
    }

    @Override
    protected void showLongTip(String txt) {
        View v = mToast.getView();
        ((TextView) v.findViewById(R.id.toast_view)).setText(txt);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.show();
    }

    @Override
    protected void showShortTip(String txt) {
        View v = mToast.getView();
        ((TextView) v.findViewById(R.id.toast_view)).setText(txt);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }


    public void showLoadingDialog(DialogDelayListener delay, int delaytime) {
        try {
            if (leafDialog == null)
                leafDialog = new LeafLoadingDialog();
            if (!leafDialog.isAdded()) {
                if (delay != null) {
                    this.leafdelay = delay;
                    mHandler.sendEmptyMessageDelayed(Constants.Handler_Dialog_Delay,
                            delaytime);
                }
                leafDialog.show(getFragmentManager(), "leafDialog");
            } else {
                Log.i("monkey", "leafDialog dialogFragment already add");
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void dismissLoadingDialog() {
        try {
            if (leafDialog != null) {
                if (leafDialog.getISRun()) {
                    leafDialog.dismiss();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void showDelay(DialogDelayListener delay, int delaytime) {
        this.delay = delay;
        mHandler.sendEmptyMessageDelayed(Constants.Handler_elay,
                delaytime);
    }

    /**
     * 延迟处理
     */
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.Handler_Dialog_Delay:
                    leafdelay.onexecute();
                    break;
                case Constants.Handler_elay:
                    delay.onexecute();
                    break;
            }
        }

        ;
    };


    protected void showCommonDialog(){
        try {
            if(mHttpDialogCommon==null)
                mHttpDialogCommon = new HttpDialogCommon();
            if(mHttpDialogCommon!=null&&!mHttpDialogCommon.isAdded()) {
                mHttpDialogCommon.show(getSupportFragmentManager(), "dialog_fragment_http_common");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }}

    protected void showCommonDialog(String txt){
        try {
            if(mHttpDialogCommon==null)
                mHttpDialogCommon = new HttpDialogCommon();
            mHttpDialogCommon.setNoticeText(txt);
            if(mHttpDialogCommon!=null&&!mHttpDialogCommon.isAdded()) {
                mHttpDialogCommon.show(getSupportFragmentManager(), "dialog_fragment_http_common");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void dismissCommonDialog(){
        try {
            if(mHttpDialogCommon!=null&&mHttpDialogCommon.isAdded()){
                mHttpDialogCommon.dismiss();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected Boolean isComoDialogShowing(){
        if(mHttpDialogCommon!=null && mHttpDialogCommon.isVisible()){
            return true;
        }
        return false;
    }
}
