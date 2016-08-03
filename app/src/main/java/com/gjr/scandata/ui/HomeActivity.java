package com.gjr.scandata.ui;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.gjr.scandata.R;
import com.gjr.scandata.biz.adapter.GoodsTypeAdapter;
import com.gjr.scandata.biz.bean.GoodsInfo;
import com.gjr.scandata.biz.bean.GoodsType;
import com.gjr.scandata.biz.db.GoodsSqlHelper;
import com.gjr.scandata.biz.listener.OnItemClickListener;
import com.gjr.scandata.biz.presenter.HomePresenter;
import com.gjr.scandata.ui.popup.ChooseGoodsInfoDF;

import java.util.ArrayList;
import java.util.List;

import kxlive.gjrlibrary.base.BaseActivity;
import kxlive.gjrlibrary.utils.KLog;
import roboguice.inject.InjectView;


public class HomeActivity extends BaseActivity {

    @InjectView(R.id.home_edit_content)
    private EditText Search;
    @InjectView(R.id.btn_quary)
    private Button Quary;
    @InjectView(R.id.btn_setting)
    private Button setting;
    @InjectView(R.id.GoodType_level_one)
    private ListView GoodType_level_one;
    @InjectView(R.id.GoodType_level_two)
    private ListView GoodType_level_two;
    @InjectView(R.id.GoodType_level_three)
    private ListView GoodType_level_three;


    private HomePresenter mHomePresenter;
    private List<GoodsType> oneGoodsTypeList=new ArrayList<GoodsType>();
    private List<GoodsType> twoGoodsTypeList=new ArrayList<GoodsType>();
    private List<GoodsInfo> threeGoodsTypeList=new ArrayList<GoodsInfo>();
    private GoodsTypeAdapter oneGoodsTypeAdapter;
    private GoodsTypeAdapter twoGoodsTypeAdapter;
    private GoodsTypeAdapter threeGoodsTypeAdapter;
    private BitmapDrawable mBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initListener();
    }

    public void initData(){
        Drawable lDrawable=getResources().getDrawable(R.drawable.not_image);
        mBitmap=(BitmapDrawable)lDrawable;
        oneGoodsTypeAdapter=new GoodsTypeAdapter(mActivity,oneGoodsTypeList,GoodsTypeAdapter.TYPE_GoodsType);
        oneGoodsTypeAdapter.setOnItemClickListener(mTypeOneOnItemClickListener);
        GoodType_level_one.setAdapter(oneGoodsTypeAdapter);

        twoGoodsTypeAdapter=new GoodsTypeAdapter(mActivity,twoGoodsTypeList,GoodsTypeAdapter.TYPE_GoodsType);
        twoGoodsTypeAdapter.setOnItemClickListener(mTypetwoOnItemClickListener);
        GoodType_level_two.setAdapter(twoGoodsTypeAdapter);

        threeGoodsTypeAdapter=new GoodsTypeAdapter(mActivity,threeGoodsTypeList,GoodsTypeAdapter.TYPE_GoodsInfo);
        threeGoodsTypeAdapter.setOnItemClickListener(mInfoOnItemClickListener);
        GoodType_level_three.setAdapter(threeGoodsTypeAdapter);



    }

    @Override
    protected void onResume() {
        super.onResume();
        oneGoodsTypeList.clear();
        oneGoodsTypeList=GoodsSqlHelper.getInstances().sqlGoodsTypeByQcTypeLevel_1();
        oneGoodsTypeAdapter.refreshDate(oneGoodsTypeList);

        if (oneGoodsTypeList.size()>0){
            twoGoodsTypeList=GoodsSqlHelper.getInstances().sqlGoodsTypeByPreQcTypeCode(oneGoodsTypeList.get(0).getGoodsTypeId());
            twoGoodsTypeAdapter.refreshDate(twoGoodsTypeList);
            if (twoGoodsTypeList.size()>0){
                threeGoodsTypeList=GoodsSqlHelper.getInstances().sqlGoodsInfoByQcTypeCode(twoGoodsTypeList.get(0).getGoodsTypeId());
                threeGoodsTypeAdapter.refreshDate(threeGoodsTypeList);
            }
        }else{
            twoGoodsTypeList.clear();
            twoGoodsTypeAdapter.refreshDate(twoGoodsTypeList);
            threeGoodsTypeList.clear();
            threeGoodsTypeAdapter.refreshDate(threeGoodsTypeList);
        }
    }

    public void initListener(){
        Search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId== EditorInfo.IME_ACTION_SEARCH )
                {
                    if (v.getEditableText().toString().length()>0){
                        List<GoodsInfo> lGoodsInfoList=GoodsSqlHelper.getInstances().sqlGoodsInfoByKey(v.getEditableText().toString());
                        if (lGoodsInfoList.size()>1){
                            showShortTip("查询到" + Search.getEditableText().toString() + "相关商品!");
                            clearSearch();
                            selectGoodsInfo(lGoodsInfoList);
                        }else if (lGoodsInfoList.size()==1){
                            showShortTip("查询到" + Search.getEditableText().toString() + "相关商品!");
                            clearSearch();
                            refresh(lGoodsInfoList.get(0),Search.getEditableText().toString());
                        }else{
                            showShortTip("没有查询到相关商品!");
                        }
                    }else{
                        showShortTip("输入信息为空!");
                    }
                }
                return false;
            }
        });
        Quary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Search.getEditableText().toString().length()>0){
                    List<GoodsInfo> lGoodsInfoList=GoodsSqlHelper.getInstances().sqlGoodsInfoByKey(Search.getEditableText().toString());
                    if (lGoodsInfoList.size()>0){
                        showShortTip("查询到" + Search.getEditableText().toString() + "相关商品!");
                        clearSearch();
                        selectGoodsInfo(lGoodsInfoList);
                    }else if(lGoodsInfoList.size()==1){
                        showShortTip("查询到"+Search.getEditableText().toString()+"相关商品!");
                        clearSearch();
                        refresh(lGoodsInfoList.get(0),Search.getEditableText().toString());
                    }else{
                        showShortTip("没有查询到相关商品!");
                    }
                }else{
                    showShortTip("输入信息为空!");
                }
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOperation().forward(SettingActivity.class);
            }
        });
    }

    OnItemClickListener mTypeOneOnItemClickListener=new OnItemClickListener() {
        @Override
        public void onItemClick(View view, Object data) {
            GoodsType lGoodsType=(GoodsType)data;
            twoGoodsTypeList.clear();
            twoGoodsTypeList=GoodsSqlHelper.getInstances().sqlGoodsTypeByPreQcTypeCode(lGoodsType.getGoodsTypeId());
            twoGoodsTypeAdapter.refreshDate(twoGoodsTypeList);

            threeGoodsTypeList.clear();
            if (twoGoodsTypeList.size()>0)
                threeGoodsTypeList=GoodsSqlHelper.getInstances().sqlGoodsInfoByQcTypeCode(twoGoodsTypeList.get(0).getGoodsTypeId());
            threeGoodsTypeAdapter.refreshDate(threeGoodsTypeList);
        }
    };

    OnItemClickListener mTypetwoOnItemClickListener=new OnItemClickListener() {
        @Override
        public void onItemClick(View view, Object data) {
            GoodsType lGoodsType=(GoodsType)data;
            threeGoodsTypeList.clear();
            threeGoodsTypeList=GoodsSqlHelper.getInstances().sqlGoodsInfoByQcTypeCode(lGoodsType.getGoodsTypeId());
            threeGoodsTypeAdapter.refreshDate(threeGoodsTypeList);
        }
    };

    OnItemClickListener mInfoOnItemClickListener=new OnItemClickListener() {
        @Override
        public void onItemClick(View view, Object data) {
            GoodsInfo lGoodsInfo=(GoodsInfo)data;
            refresh(lGoodsInfo,"");

        }
    };

    public void refresh(GoodsInfo pGoodsInfo,String initSearch){

        getOperation().addParameter("GOODSINFO",pGoodsInfo);
        getOperation().addParameter("SEARCH",initSearch);
        getOperation().forward(DetailActivity.class);
    }

    public void clearSearch(){
        Search.setText("");
        threeGoodsTypeList.clear();
        threeGoodsTypeAdapter.refreshDate(threeGoodsTypeList);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 根据url,解析本地硬盘双重缓存路径
     * @param url
     * @return
     */
    public String getLocalURL(String url){
        String lastName="file:///mnt/sdcard";
        String[] names = url.split("/");
        for (int i=0;i<names.length;i++){
            KLog.i(names[i]);
            if (i==names.length-2||i==names.length-1){
                lastName+="/"+names[i];
            }
        }
        return lastName;
    }

    /**
    * 选取会信息
    * */
    public void selectGoodsInfo(List<GoodsInfo> pGoodsInfos) {
        ChooseGoodsInfoDF chooseMemberCardDF = ChooseGoodsInfoDF.newInstance(pGoodsInfos);
        chooseMemberCardDF.setOnFinishBackListener(onFinifhBackListener);
        chooseMemberCardDF.show(getSupportFragmentManager(), "selectGoodsInfo");
    }

    ChooseGoodsInfoDF.OnFinifhBackListener onFinifhBackListener= new ChooseGoodsInfoDF.OnFinifhBackListener() {
        @Override
        public void onFinifhBack(GoodsInfo pGoodsInfo) {
            refresh(pGoodsInfo,"");
        }
    };

}
