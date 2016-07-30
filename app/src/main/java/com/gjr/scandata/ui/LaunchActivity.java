package com.gjr.scandata.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.gjr.scandata.R;

import kxlive.gjrlibrary.base.BaseActivity;
import kxlive.gjrlibrary.utils.KLog;

/**
 * 启动页面，不设置内容视图，避免应用启动时出现空白区域
 * 2015年6月18日
 */
public class LaunchActivity extends BaseActivity{
    private static final String LOG_MSG = "KLog is a so cool Log Tool!";
    private static final String JSON = "{\"menu\":[\"泰式柠檬肉片\",\"鸡柳汉堡\",\"蒸桂鱼卷 \"],\"tag\":\"其他\"}";
    private static final String TAG = "KLog";
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mContextView = LayoutInflater.from(this).inflate(R.layout.activity_launcher, null);
        setContentView(mContextView);
        initView(mContextView);
    }
    public void initView(View view) {
        //添加动画效果
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(1300);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //跳转界面
                getOperation().forward(HomeActivity.class);
                finish();
                //右往左推出效果
                overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
                //转动淡出效果1
//                overridePendingTransition(R.anim.scale_rotate_in,R.anim.alpha_out);
                //下往上推出效果
//				overridePendingTransition(R.anim.push_up_in,R.anim.push_up_out);
            }
        });
        view.setAnimation(animation);
    }



    public void log() {
        KLog.v();
        KLog.d();
        KLog.i();
        KLog.w();
        KLog.e();
        KLog.a();
        logWithMsg();
    }

    public void logWithMsg() {
        KLog.v(LOG_MSG);
        KLog.d(LOG_MSG);
        KLog.i(LOG_MSG);
        KLog.w(LOG_MSG);
        KLog.e(LOG_MSG);
        KLog.a(LOG_MSG);
        logWithTag();
    }

    public void logWithTag() {
        KLog.v(TAG, LOG_MSG);
        KLog.d(TAG, LOG_MSG);
        KLog.i(TAG, LOG_MSG);
        KLog.w(TAG, LOG_MSG);
        KLog.e(TAG, LOG_MSG);
        KLog.a(TAG, LOG_MSG);
        logWithNull();
    }

    public void logWithNull(){
        KLog.v(null);
        KLog.d(null);
        KLog.i(null);
        KLog.w(null);
        KLog.e(null);
        KLog.a(null);
        logWithJson();
    }

    public void logWithJson() {
        KLog.json(JSON);
        logWithJsonTag();
    }

    public void logWithJsonTag() {
        KLog.json(TAG, JSON);
    }


}
