package com.gjr.scandata.ui;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.gjr.scandata.R;
import com.gjr.scandata.biz.bean.GoodsInfo;
import com.gjr.scandata.biz.bean.GoodsType;
import com.gjr.scandata.biz.bean.HttpGoodsInfo;
import com.gjr.scandata.biz.bean.HttpGoodsType;
import com.gjr.scandata.biz.db.GoodsSqlHelper;
import com.gjr.scandata.biz.listener.DialogDelayListener;
import com.gjr.scandata.http.AddressState;
import com.gjr.scandata.http.HttpController;
import com.gjr.scandata.http.ImagesLoader;
import com.gjr.scandata.http.ResultMap;
import com.gjr.scandata.http.VolleyErrorHelper;
import com.gjr.scandata.ui.popup.CheckUserPwdDF;
import com.gjr.scandata.ui.popup.ChooseServerAddressDF;
import com.gjr.scandata.ui.popup.MakeOrderFinishDF;
import com.gjr.scandata.utils.Tools;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import kxlive.gjrlibrary.base.BaseActivity;
import kxlive.gjrlibrary.config.SysEnv;
import kxlive.gjrlibrary.entity.eventbus.EventBackground;
import kxlive.gjrlibrary.entity.eventbus.EventMain;
import kxlive.gjrlibrary.rx.Result;
import kxlive.gjrlibrary.utils.KLog;
import kxlive.gjrlibrary.utils.StringUtils;
import roboguice.inject.InjectView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 营销活动,编辑预览页
 * Created by gjr on 2016/6/13 17:38.
 * mail : gjr9596@gmail.com
 */
public class SettingActivity extends BaseActivity{

    @InjectView(R.id.system_versionName)
    private TextView versionName;
    @InjectView(R.id.system_versionCode)
    private TextView versionCode;
    @InjectView(R.id.system_packagename)
    private TextView packagename;
    @InjectView(R.id.system_server_address)
    private TextView server_address;
    @InjectView(R.id.system_server_toinit)
    private Button system_server_toinit;
    @InjectView(R.id.marketing_underline06)
    private View marketing_underline06;
    @InjectView(R.id.system__update_data)
    private Button update_data;
    @InjectView(R.id.system_clear_data)
    private Button clear_data;
    @InjectView(R.id.btn_back)
    private Button btn_back;

    private MakeOrderFinishDF mMakeOrderDF;
    private ImagesLoader mImagesLoader;
    public final String QcTypeKey="QCtypeUpdateTime";
    public final String QcKey="QCUpdateTime";
    public final String AppKey = "com.gjr.scandata";

    private String curQcUpdateTime="";
    private String curQcTypeUpdateTime="";
    private Subscription subscription;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.acitivity_system);
        EventBus.getDefault().register(this);
        initData();
    }

    @Override
    public boolean onEventMainThread(EventMain event) {
        boolean isRun=super.onEventMainThread(event);
        if (isRun) {
            switch (event.getType()) {
                case EventMain.TYPE_FIRST:
                    showShortTip("商品类型更新成功!");
                    saveUpdateTime(QcTypeKey,curQcTypeUpdateTime);
//                    getQueryQc();
                    getQueryQcWithAsyncResult();
                    break;
                case EventMain.TYPE_SECOND:
                    showShortTip("商品信息更新成功!");
                    List<String> needUpdateUrl = (List<String>) event.getData();
                    if (needUpdateUrl!=null&&needUpdateUrl.size()>0){
                        for (String url:needUpdateUrl){
                            mImagesLoader.loadImage(url,400,300,new ImagesLoader.AsyncImageLoaderListener() {
                                @Override
                                public void onImageLoader(Bitmap bitmap, String url,int size) {
                                    if (size==0){
                                        updateNotice("所有数据同步成功!", 1);
                                        saveUpdateTime(QcKey,curQcUpdateTime);
                                    }
                                }
                            });
                        }
                    }else{
                        updateNotice("所有数据同步成功!", 1);
                        saveUpdateTime(QcKey,curQcUpdateTime);
                    }

                    break;
                case EventMain.TYPE_THREE:
                    updateNotice(event.getDescribe(), 1);
                    break;
                case EventMain.TYPE_FOUR:
                    updateNotice(event.getDescribe(), 1);
                    break;
                default:
                    break;
            }
        }
        return isRun;
    }

    @Override
    public boolean onEventBackgroundThread(EventBackground event) {
        boolean isRun=super.onEventBackgroundThread(event);
        if (isRun) {
            switch (event.getType()) {
                case EventBackground.TYPE_FIRST:
                    try {
                        List<GoodsType> TypeInfos = (List<GoodsType>) event.getData();
                        if (TypeInfos != null && TypeInfos.size() > 0) {
                            for (GoodsType lGoodsType:TypeInfos){
                                List<GoodsType> mTypeInfos=GoodsSqlHelper.getInstances().sqlGoodsTypeByGoodsTypeId(lGoodsType.getGoodsTypeId());
                                if (mTypeInfos.size()>0){
                                    //修改
                                    for (GoodsType lGoodsType1:mTypeInfos){
                                        int size=GoodsSqlHelper.getInstances().sqlUpdateGoodsType(lGoodsType,lGoodsType1.getId());
                                        KLog.i(lGoodsType1.getGoodsTypeId()+"修改数据库行:"+size+" 变动行id:"+lGoodsType1.getId());
                                    }
                                }else{
                                    //新增
                                    lGoodsType.saveThrows();
                                }
                            }
                        }
                        Log.i("onEventBackgroundThread", "数据库更新商品类型成功");
                        EventMain eventMain = new EventMain();
                        eventMain.setName(SettingActivity.class.getName());
                        eventMain.setType(EventMain.TYPE_FIRST);
                        eventMain.setDescribe("商品类型更新成功后，通知消息发布到主线程提示更新商品详情");
                        EventBus.getDefault().post(eventMain);
                    }catch (Exception e){
                        Log.i("onEventBackgroundThread", "数据库更新商品类型,保存失败!");
                        EventMain eventMain2 = new EventMain();
                        eventMain2.setName(SettingActivity.class.getName());
                        eventMain2.setType(EventMain.TYPE_THREE);
                        eventMain2.setDescribe("数据库更新商品类型,保存失败!");
                        EventBus.getDefault().post(eventMain2);
                        e.printStackTrace();
                    }

                    break;
                case EventBackground.TYPE_SECOND:
                    try {
                        List<GoodsInfo> lGoodsInfoList = (List<GoodsInfo>) event.getData();
                        List<String> needUpdateUrl=new ArrayList<String>();
                        if (lGoodsInfoList != null && lGoodsInfoList.size() > 0) {
                            for (GoodsInfo lGoodsInfo:lGoodsInfoList){
                                List<GoodsInfo> localGoodsInfoList=GoodsSqlHelper.getInstances().sqlGoodsInfoByGoodsInfoId(lGoodsInfo.getGoodsInfoId());
                                if (localGoodsInfoList.size()>0){
                                    //修改
                                    for (GoodsInfo lGoodsInfo1:localGoodsInfoList){
                                        if (lGoodsInfo.getState().equals("1")){
                                            if (!getURLName(lGoodsInfo1.getRealPicUrl()).equals(getURLName(lGoodsInfo.getRealPicUrl()))){
                                                needUpdateUrl.add(lGoodsInfo.getRealPicUrl());
                                            }else{
                                                KLog.i(lGoodsInfo1.getGoodsInfoId()+"图片地址无变动:"+lGoodsInfo1.getRealPicUrl());
                                            }
                                            int size=GoodsSqlHelper.getInstances().sqlUpdateGoodsInfo(lGoodsInfo,lGoodsInfo1.getId());
                                            KLog.i(lGoodsInfo1.getGoodsInfoId()+"修改数据库行:"+size+" 变动行id:"+lGoodsInfo1.getId());
                                        }else{
                                            //状态为0或者2的,因该删除,放弃缓存
                                            int size=GoodsSqlHelper.getInstances().sqlDeleteGoodsInfo(lGoodsInfo1.getId());
                                            KLog.i(lGoodsInfo1.getGoodsInfoId()+"删除数据库行:"+size+" 变动行id:"+lGoodsInfo1.getId());
                                            mImagesLoader.clearCacheDir(lGoodsInfo1.getRealPicUrl());
                                        }
                                    }
                                }else{
                                    if (lGoodsInfo.getState().equals("1")){
                                        //新增,只缓存显示状态开启的,无视0禁用与2删除
                                        lGoodsInfo.saveThrows();
                                        needUpdateUrl.add(lGoodsInfo.getRealPicUrl());
                                    }
                                }
                            }
                        }

                    Log.i("onEventBackgroundThread", "setting中数据库更新商品信息成功");
                    EventMain eventMain3 = new EventMain();
                    eventMain3.setName(SettingActivity.class.getName());
                    eventMain3.setData(needUpdateUrl);
                    eventMain3.setType(EventMain.TYPE_SECOND);
                    eventMain3.setDescribe("商品信息更新成功后，通知消息发布到主线程提示更新成功");
                    EventBus.getDefault().post(eventMain3);

                    }catch (Exception e){
                        Log.i("onEventBackgroundThread", "数据库更新商品信息,保存失败!");
                        EventMain eventMain4 = new EventMain();
                        eventMain4.setName(SettingActivity.class.getName());
                        eventMain4.setType(EventMain.TYPE_FOUR);
                        eventMain4.setDescribe("数据库更新商品信息,保存失败!");
                        EventBus.getDefault().post(eventMain4);
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
        return isRun;
    }

    private void initData(){
        mImagesLoader=new ImagesLoader(this,"HjpQc");
        int curVersionCode= Tools.getVersionCode(mActivity);
        String curVersionName=Tools.getVersionName(mActivity);
        String curPackageName=Tools.getPackageName(mActivity);
        versionName.setText("版本名称: "+curVersionName);
        versionCode.setText("版本号: "+curVersionCode);
        packagename.setText("包名: " + curPackageName);
        showServerAddress();
        server_address.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showCheckUserPwdDF();
                return false;
            }
        });
        system_server_toinit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isInit = HttpController.getInstance().toInitAddress(mActivity);
                if (isInit) showShortTip("已经是默认配置了~~");
                else showShortTip("恢复默认设置成功!");
                showServerAddress();
            }
        });
        update_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getQueryQcType();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        clear_data.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                clearAllCacheData();
                return false;
            }
        });

    }



    private void showServerAddress(){
        if(HttpController.getInstance().isInit()){
            server_address.setText("服务器地址: "+ HttpController.getInstance().getAddress().getTitle());
            system_server_toinit.setVisibility(View.GONE);
            marketing_underline06.setVisibility(View.GONE);
        }else{
            server_address.setText("服务器地址: "+ HttpController.getInstance().getAddress().getTitle()+" ("+"非默认升级无法自动更新环境地址"+")");
            system_server_toinit.setVisibility(View.VISIBLE);
            marketing_underline06.setVisibility(View.VISIBLE);

        }
    }
    /**
    * 显示选择服务器环境窗口
    * */
    private void showChooseServerAddressDF() {
        try {
            ChooseServerAddressDF lChooseCardLevelDF= ChooseServerAddressDF.newInstance();
            lChooseCardLevelDF.setOnFinishBackListener(mOnFinifhBackListener);
            lChooseCardLevelDF.show(getSupportFragmentManager(), "ChooseServerAddressDF");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    ChooseServerAddressDF.OnFinifhBackListener mOnFinifhBackListener=new ChooseServerAddressDF.OnFinifhBackListener() {
        @Override
        public void onFinifhBack(AddressState pAddressState) {
           HttpController.getInstance().setAddress(pAddressState);
            HttpController.getInstance().saveAddress(mActivity,pAddressState);
            showServerAddress();
        }
    };

    /**
     * 显示系统密码验证窗口
     * */
    private void showCheckUserPwdDF() {
        CheckUserPwdDF lCheckUserPwdDF = CheckUserPwdDF.newInstance();
        lCheckUserPwdDF.setOnCheckUserPwdListener(mOnCheckUserPwdListener);
        lCheckUserPwdDF.show(getSupportFragmentManager(), "CheckSysPwdDF");
    }

    CheckUserPwdDF.OnCheckUserPwdListener mOnCheckUserPwdListener=new CheckUserPwdDF.OnCheckUserPwdListener() {
        @Override
        public void onSelectBack(String pwd) {
            if (pwd.equals(SysEnv.ChangeServerAddressPwd)){
                showShortTip("修改服务器地址通行密码正确!");
                showChooseServerAddressDF();
            }else{
                showShortTip("修改服务器地址通行密码不正确!");
            }
        }
    };

    public void getQueryQcType(){
        showLoadingDF("正在更新商品数据...");
        curQcTypeUpdateTime= StringUtils.datetime2Str(new Date());
        HttpController.getInstance().getQueryQcType(getUpdateTime(QcTypeKey),new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject data) {
                try {
                    if (data.getString("msg").equals("ok")) {
                        Gson gson = new Gson();
                        JSONObject datainfo=data.getJSONObject("data");
                        String info=datainfo.getString("info");
                        KLog.i("info:" + info);
                        List<HttpGoodsType> TypeInfos = gson.fromJson(info, new TypeToken<List<HttpGoodsType>>() {
                        }.getType());
                        Log.d(TAG, "GoodsType Count: " + TypeInfos.size());
                        List<GoodsType> lGoodsTypeList=new ArrayList<GoodsType>();
                        if (TypeInfos!=null&&TypeInfos.size()>0){
                            for (HttpGoodsType lHttpGoodsType:TypeInfos){
                                lGoodsTypeList.add(lHttpGoodsType.toGoodsType());
                            }
                        }

                        EventBackground event = new EventBackground();
                        event.setData(lGoodsTypeList);
                        event.setName(SettingActivity.class.getName());
                        event.setType(EventBackground.TYPE_FIRST);
                        event.setDescribe("商品类型数据传入后台线程存入数据库");
                        EventBus.getDefault().post(event);

                    } else {
                        showShortTip("商品类型数据" + data.getString("msg"));
                        saveUpdateTime(QcTypeKey,curQcTypeUpdateTime);
                        getQueryQcWithAsyncResult();
                    }
                } catch (JSONException e) {
                    showShortTip("商品类型数据更新失败! ");
                    dismissLoadingDF();
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoadingDF();
                Log.e("VolleyLogTag", "VolleyError:" + error.getMessage(), error);
                showShortTip(VolleyErrorHelper.getMessage(error, mActivity));
            }
        });
    }

    public void getQueryQc(){
        curQcUpdateTime= StringUtils.datetime2Str(new Date());
        HttpController.getInstance().getQueryQc(getUpdateTime(QcKey), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject data) {
                try {
                    if (data.getString("msg").equals("ok")) {
                        Gson gson = new Gson();
                        JSONObject datainfo = data.getJSONObject("data");
                        String info = datainfo.getString("info");
                        KLog.i("info:" + info);
                        List<HttpGoodsInfo> lHttpGoodsInfos = gson.fromJson(info, new TypeToken<List<HttpGoodsInfo>>() {
                        }.getType());
                        Log.d(TAG, "GoodsInfo Count: " + lHttpGoodsInfos.size());
                        List<GoodsInfo> lGoodsInfoList = new ArrayList<GoodsInfo>();
                        if (lHttpGoodsInfos != null && lHttpGoodsInfos.size() > 0) {
                            for (HttpGoodsInfo lHttpGoodsInfo : lHttpGoodsInfos) {
                                lGoodsInfoList.add(lHttpGoodsInfo.ToGoodsInfo());
                            }
                        }

                        EventBackground event = new EventBackground();
                        event.setData(lGoodsInfoList);
                        event.setName(SettingActivity.class.getName());
                        event.setType(EventBackground.TYPE_SECOND);
                        event.setDescribe("商品信息数据传入后台线程存入数据库");
                        EventBus.getDefault().post(event);

                    } else {
                        showShortTip("商品信息数据更新! " + data.getString("msg"));
                        saveUpdateTime(QcKey,curQcUpdateTime);
                        dismissLoadingDF();
                    }
                } catch (JSONException e) {
                    showShortTip("商品信息数据更新失败! ");
                    dismissLoadingDF();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoadingDF();
                Log.e("VolleyLogTag", "VolleyError:" + error.getMessage(), error);
                showShortTip(VolleyErrorHelper.getMessage(error, mActivity));
            }
        });
    }

    public void getQueryQcWithAsyncResult(){
        curQcUpdateTime= StringUtils.datetime2Str(new Date());
        Observable<Result> observable=HttpController.getInstance().getQueryQcWithAsyncResult(getUpdateTime(QcKey), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject data) {
                /*try {
                    if (data.getString("msg").equals("ok")) {
                        Gson gson = new Gson();
                        JSONObject datainfo = data.getJSONObject("data");
                        String info = datainfo.getString("info");
                        KLog.i("info:" + info);
                        List<HttpGoodsInfo> lHttpGoodsInfos = gson.fromJson(info, new TypeToken<List<HttpGoodsInfo>>() {
                        }.getType());
                        Log.d(TAG, "GoodsInfo Count: " + lHttpGoodsInfos.size());
                        List<GoodsInfo> lGoodsInfoList = new ArrayList<GoodsInfo>();
                        if (lHttpGoodsInfos != null && lHttpGoodsInfos.size() > 0) {
                            for (HttpGoodsInfo lHttpGoodsInfo : lHttpGoodsInfos) {
                                lGoodsInfoList.add(lHttpGoodsInfo.ToGoodsInfo());
                            }
                        }

                        EventBackground event = new EventBackground();
                        event.setData(lGoodsInfoList);
                        event.setName(SettingActivity.class.getName());
                        event.setType(EventBackground.TYPE_SECOND);
                        event.setDescribe("商品信息数据传入后台线程存入数据库");
                        EventBus.getDefault().post(event);

                    } else {
                        showShortTip("商品信息数据更新! " + data.getString("msg"));
                        saveUpdateTime(QcKey,curQcUpdateTime);
                        dismissLoadingDF();
                    }
                } catch (JSONException e) {
                    showShortTip("商品信息数据更新失败! ");
                    dismissLoadingDF();
                    e.printStackTrace();
                }*/
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoadingDF();
                Log.e("VolleyLogTag", "VolleyError:" + error.getMessage(), error);
                showShortTip(VolleyErrorHelper.getMessage(error, mActivity));
            }
        });

        subscription = observable
                .filter(new Func1<Result, Boolean>() {
                    @Override
                    public Boolean call(Result result) {
                        return result.data != null;
                    }
                })
                .map(new Func1<Result, ResultMap>() {
                    @Override
                    public ResultMap call(Result result) {
                        JSONObject data=null;
                        ResultMap lMap=new ResultMap();
                        try {
                            String jsonString = new String(result.data,
                                    HttpHeaderParser.parseCharset(result.header, HTTP.UTF_8));
                            data=new JSONObject(jsonString);
                            if (data==null){
                                lMap.setMsg("没有数据!");
                                lMap.setErrcode("1000");
                                return lMap;
                            }
                            lMap.setMsg(data.getString("msg"));
                            lMap.setErrcode(data.getString("errcode"));
                            if (data.getString("msg").equals("ok")) {
                                Gson gson = new Gson();
                                JSONObject datainfo = data.getJSONObject("data");
                                String info = datainfo.getString("info");
                                KLog.i("info:" + info);
                                List<HttpGoodsInfo> lHttpGoodsInfos = gson.fromJson(info, new TypeToken<List<HttpGoodsInfo>>() {
                                }.getType());
                                Log.d(TAG, "GoodsInfo Count: " + lHttpGoodsInfos.size());
                                List<GoodsInfo> lGoodsInfoList = new ArrayList<GoodsInfo>();
                                if (lHttpGoodsInfos != null && lHttpGoodsInfos.size() > 0) {
                                    for (HttpGoodsInfo lHttpGoodsInfo : lHttpGoodsInfos) {
                                        lGoodsInfoList.add(lHttpGoodsInfo.ToGoodsInfo());
                                    }
                                    List<String> needUpdateUrl=new ArrayList<String>();
                                        for (GoodsInfo lGoodsInfo:lGoodsInfoList){
                                            List<GoodsInfo> localGoodsInfoList=GoodsSqlHelper.getInstances().sqlGoodsInfoByGoodsInfoId(lGoodsInfo.getGoodsInfoId());
                                            if (localGoodsInfoList.size()>0){
                                                //修改
                                                for (GoodsInfo lGoodsInfo1:localGoodsInfoList){
                                                    if (lGoodsInfo.getState().equals("1")){
                                                        if (!getURLName(lGoodsInfo1.getRealPicUrl()).equals(getURLName(lGoodsInfo.getRealPicUrl()))){
                                                            needUpdateUrl.add(lGoodsInfo.getRealPicUrl());
                                                        }else{
                                                            KLog.i(lGoodsInfo1.getGoodsInfoId()+"图片地址无变动:"+lGoodsInfo1.getRealPicUrl());
                                                        }
                                                        int size=GoodsSqlHelper.getInstances().sqlUpdateGoodsInfo(lGoodsInfo,lGoodsInfo1.getId());
                                                        KLog.i(lGoodsInfo1.getGoodsInfoId()+"修改数据库行:"+size+" 变动行id:"+lGoodsInfo1.getId());
                                                    }else{
                                                        //状态为0或者2的,因该删除,放弃缓存
                                                        int size=GoodsSqlHelper.getInstances().sqlDeleteGoodsInfo(lGoodsInfo1.getId());
                                                        KLog.i(lGoodsInfo1.getGoodsInfoId()+"删除数据库行:"+size+" 变动行id:"+lGoodsInfo1.getId());
                                                        mImagesLoader.clearCacheDir(lGoodsInfo1.getRealPicUrl());
                                                    }
                                                }
                                            }else{
                                                if (lGoodsInfo.getState().equals("1")){
                                                    //新增,只缓存显示状态开启的,无视0禁用与2删除
                                                    lGoodsInfo.saveThrows();
                                                    needUpdateUrl.add(lGoodsInfo.getRealPicUrl());
                                                }
                                            }

                                        }
                                    lMap.setData(needUpdateUrl);
                                }else KLog.i("无数据");
                            }else {
                                KLog.i("errcode:"+data.getString("errcode")+" msg:"+data.getString("msg"));
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                            return lMap;
                        }
                    }).subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new Action1<ResultMap>() {
                                     @Override
                                     public void call(ResultMap data) {
                                         if (data.getErrcode().equals("0")) {
                                             showShortTip("商品信息更新成功!");
                                             List<String> needUpdateUrl = (List<String>) data.getData();
                                             if (needUpdateUrl != null && needUpdateUrl.size() > 0) {
                                                 for (String url : needUpdateUrl) {
                                                     mImagesLoader.loadImage(url, 400, 300, new ImagesLoader.AsyncImageLoaderListener() {
                                                         @Override
                                                         public void onImageLoader(Bitmap bitmap, String url, int size) {
                                                             if (size == 0) {
                                                                 updateNotice("所有数据同步成功!", 1);
                                                                 saveUpdateTime(QcKey, curQcUpdateTime);
                                                             }
                                                         }
                                                     });
                                                 }
                                             } else {
                                                 updateNotice("所有数据同步成功!", 1);
                                                 saveUpdateTime(QcKey, curQcUpdateTime);
                                             }
                                         } else if (data.getErrcode().equals("4")) {
                                             showShortTip("商品信息数据更新! " + data.getMsg());
                                             saveUpdateTime(QcKey, curQcUpdateTime);
                                             dismissLoadingDF();
                                         } else {
                                             showShortTip("更新失败!" + data.getMsg());
                                             dismissLoadingDF();
                                         }

                                     }
                                 }
                      );
                }

    /**
     * 提示框
     */
    private void showLoadingDF(String info) {
        try {
            mMakeOrderDF = new MakeOrderFinishDF();
            mMakeOrderDF.setNoticeText(info);
            mMakeOrderDF.show(getSupportFragmentManager(), "payMethod");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @param info
     * @param type 0无事件,1返回桌台
     */
    private void updateNotice(String info, int type) {
        if (mMakeOrderDF != null && mMakeOrderDF.isAdded()) {
            mMakeOrderDF.updateNoticeText(info, type);
        }
    }

    /**
     * @param info
     * @param type 0无事件,1返回桌台
     */
    private void showNotice(String info, int type,Bitmap url) {
        if (mMakeOrderDF != null && mMakeOrderDF.isAdded()) {
            mMakeOrderDF.showNoticeText(info, type, url);
        }
    }

    /**
     * @param info                 提示信息
     * @param pDialogDelayListener 点击相应事件
     */
    private void updateNotice(String info, DialogDelayListener pDialogDelayListener) {
        if (mMakeOrderDF != null && mMakeOrderDF.isAdded()) {
            mMakeOrderDF.updateNoticeText(info, pDialogDelayListener);
        }
    }

    private void dismissLoadingDF() {
        try {
            if (mMakeOrderDF != null && mMakeOrderDF.isAdded()) {
                mMakeOrderDF.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (subscription != null && subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        super.onDestroy();
    }


    /**
     * 根据url,获取文件名
     * @param url
     * @return
     */
    public String getURLName(String url){
        String lastName="";
        String[] names = url.split("/");
        for (int i=0;i<names.length;i++){
            if (i==names.length-1){
                lastName=names[i];
            }
        }
        return lastName;
    }

    /**
     * 保存当前更新成功时间
     */
    public void saveUpdateTime(String key,String time){
        SharedPreferences mSharedPreferences = mActivity.getSharedPreferences(AppKey, mActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, time);
        editor.apply();
    }

    /**
     * 获取当前更新成功时间
     * @return
     */
    public String getUpdateTime(String key){
        SharedPreferences mSharedPreferences = mActivity.getSharedPreferences(AppKey, mActivity.MODE_PRIVATE);
        String update = mSharedPreferences.getString(key, "2016-01-01 00:00:00");
        return update;
    }

    private void clearAllCacheData(){
        DataSupport.deleteAll(GoodsInfo.class);
        DataSupport.deleteAll(GoodsType.class);
        mImagesLoader.clearCacheDir();
        showShortTip("缓存数据已清空!");
        saveUpdateTime(QcKey, "2016-01-01 00:00:00");
        saveUpdateTime(QcTypeKey, "2016-01-01 00:00:00");
    }

}
