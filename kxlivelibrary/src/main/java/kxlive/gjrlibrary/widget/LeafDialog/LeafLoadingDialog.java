package kxlive.gjrlibrary.widget.LeafDialog;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import java.util.Random;

import kxlive.gjrlibrary.R;
import kxlive.gjrlibrary.utils.AnimationUtils;

/**
 * 自定义loading加载进度动画（借鉴旋转飞叶动画效果）
 *
 * @author gjr
 */
public class LeafLoadingDialog extends DialogFragment implements
        LoadingActivityListener {
    private static final int REFRESH_PROGRESS = 0x10;
    private LeafLoadingView mLeafLoadingView;
    private View mFanView;
    private int mProgress = 0;
    private View loading;
    private boolean isRun = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setStyle(R.style.translucent, 0);
        loading = inflater.inflate(R.layout.leafloading, container);
        mFanView = loading.findViewById(R.id.fan_pic);
        RotateAnimation rotateAnimation = AnimationUtils.initRotateAnimation(
                false, 1500, true, Animation.INFINITE);
        mFanView.startAnimation(rotateAnimation);
        mLeafLoadingView = (LeafLoadingView) loading
                .findViewById(R.id.leaf_loading);
        mHandler.sendEmptyMessageDelayed(REFRESH_PROGRESS, 500);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.dimAmount = 0.0f;
        params.alpha = 1.0f;
        getDialog().getWindow().setAttributes(params);
        getDialog().getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        getDialog().setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent arg2) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
        return loading;
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (isRun) {
                switch (msg.what) {
                    // 进度条最大延迟随机等待21秒走完一圈，通常随机数总和11秒左右进度跑完一圈
                    case REFRESH_PROGRESS:
                        if (mProgress == 0)
                            Log.i("mProgress", "mProgress:" + mProgress);
                        if (mProgress == 100)
                            Log.i("mProgress", "mProgress:" + mProgress);
                        if (mProgress < 20) {
                            // 30时间段以内最大2秒走完
                            mProgress += 1;
                            // 随机200ms以内刷新一次
                            mHandler.sendEmptyMessageDelayed(REFRESH_PROGRESS,
                                    new Random().nextInt(100));
                            mLeafLoadingView.setProgress(mProgress);
                        } else if (mProgress >= 20 && mProgress < 70) {
                            // 30-70段最大10秒走完
                            mProgress += 1;
                            // 随机400ms以内刷新一次
                            mHandler.sendEmptyMessageDelayed(REFRESH_PROGRESS,
                                    new Random().nextInt(200));
                            mLeafLoadingView.setProgress(mProgress);
                        } else if (mProgress >= 70 && mProgress < 90) {
                            // 70-90时间段最大用6秒走完
                            mProgress += 1;
                            // 随机600ms以内刷新一次
                            mHandler.sendEmptyMessageDelayed(REFRESH_PROGRESS,
                                    new Random().nextInt(300));
                            mLeafLoadingView.setProgress(mProgress);
                        } else if (mProgress >= 90 && mProgress < 100) {
                            // 末尾最大3秒走完
                            mProgress += 1;
                            // 随机300ms以内刷新一次
                            mHandler.sendEmptyMessageDelayed(REFRESH_PROGRESS,
                                    new Random().nextInt(300));
                            mLeafLoadingView.setProgress(mProgress);
                        } else if (mProgress == 100) {
                            mProgress = 0;
                            // 随机300ms以内刷新一次
                            mHandler.sendEmptyMessageDelayed(REFRESH_PROGRESS,
                                    new Random().nextInt(300));
                            mLeafLoadingView.setProgress(mProgress);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        ;
    };

    @Override
    public void onfinish() {
        this.dismiss();
    }

    public LoadingActivityListener getLoadingActivityListener() {
        return this;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        isRun = false;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        isRun = true;
    }

    public boolean getISRun() {
        return isRun;
    }

}
