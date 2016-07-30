package com.gjr.scandata.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gjr.scandata.R;
import com.gjr.scandata.biz.bean.GoodsInfo;
import com.gjr.scandata.biz.db.GoodsSqlHelper;
import com.gjr.scandata.ui.popup.ChooseGoodsInfoDF;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import kxlive.gjrlibrary.base.BaseActivity;
import kxlive.gjrlibrary.utils.KLog;
import roboguice.inject.InjectView;

/**
 * Created by gjr on 2016/7/29 14:01.
 * mail : gjr9596@gmail.com
 */
public class DetailActivity extends BaseActivity {
    @InjectView(R.id.detail_goodsinfo_qcname)
    private TextView qcName;
    @InjectView(R.id.detail_goodsinfo_qccode)
    private TextView qcCode;
    @InjectView(R.id.detail_goodsinfo_qcStandard)
    private TextView qcStandard;
    @InjectView(R.id.goodsinfo_null)
    private TextView goodsinfo_null;
    @InjectView(R.id.GoodsInfo_container)
    private View GoodsInfo_container;
    @InjectView(R.id.datail_goodsinfo_realPicUrl)
    private ImageView realPicUrl;
    @InjectView(R.id.btn_back)
    private Button back;
    @InjectView(R.id.btn_quary)
    private Button Quary;
    @InjectView(R.id.datail_edit_content)
    private EditText Search;
    private BitmapDrawable mBitmap;
    private String initSearch="";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_detail);
        GoodsInfo mGoodsInfo=(GoodsInfo) getIntent().getSerializableExtra("GOODSINFO");
        initSearch= getIntent().getStringExtra("SEARCH");
        initData(mGoodsInfo);
        initListener();
    }

    public void initData(GoodsInfo mGoodsInfo){
        Drawable lDrawable=getResources().getDrawable(R.drawable.not_image);
        mBitmap=(BitmapDrawable)lDrawable;

        if (mGoodsInfo!=null){
            refhreshDate(mGoodsInfo);
        }else{
            disContainer("没有数据!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (initSearch!=null&&initSearch.length()>0){
            Search.setText(initSearch);
        }
    }

    public void initListener(){
        Search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId== EditorInfo.IME_ACTION_SEARCH )
                {
                    if (v.getEditableText().toString().length()>0){
                        List<GoodsInfo> lGoodsInfoList= GoodsSqlHelper.getInstances().sqlGoodsInfoByKey(v.getEditableText().toString());
                        if (lGoodsInfoList.size()>1){
                            showShortTip("查询到"+Search.getEditableText().toString()+"相关商品!");
                            clearSearch();
                            selectGoodsInfo(lGoodsInfoList);
                        }else if(lGoodsInfoList.size()==1){
                            showShortTip("查询到"+Search.getEditableText().toString()+"相关商品!");
                            clearSearch();
                            refhreshDate(lGoodsInfoList.get(0));
                        }else{
                            showShortTip("没有查询到相关商品!");
                            disContainer("没有查询到相关商品!");
                        }
                    }else{
                        showShortTip("输入信息为空!");
                        disContainer("输入信息为空!");
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
                    if (lGoodsInfoList.size()>1){
                        showShortTip("查询到"+Search.getEditableText().toString()+"相关商品!");
                        clearSearch();
                        selectGoodsInfo(lGoodsInfoList);
                    }else if(lGoodsInfoList.size()==1){
                        showShortTip("查询到"+Search.getEditableText().toString()+"相关商品!");
                        clearSearch();
                        refhreshDate(lGoodsInfoList.get(0));
                    }else{
                        showShortTip("没有查询到相关商品!");
                        disContainer("没有查询到相关商品!");
                    }
                }else{
                    showShortTip("输入信息为空!");
                    disContainer("输入信息为空!");
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void refhreshDate(GoodsInfo pGoodsInfo){
        if (pGoodsInfo!=null){
            showContainer();
            qcName.setText(pGoodsInfo.getQcName());
            qcCode.setText(pGoodsInfo.getQcCode());
            qcStandard.setText(pGoodsInfo.getQcStandard());
            Bitmap lBitmap= ImageLoader.getInstance().loadImageSync(getLocalURL(pGoodsInfo.getRealPicUrl()));
            if (lBitmap!=null){
                realPicUrl.setImageBitmap(lBitmap);
            }else{
                realPicUrl.setImageBitmap(mBitmap.getBitmap());
            }
        }
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


    public void clearSearch(){
        Search.setText("");
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void showContainer(){
        GoodsInfo_container.setVisibility(View.VISIBLE);
        goodsinfo_null.setVisibility(View.GONE);
    }

    public void disContainer(String title){
        GoodsInfo_container.setVisibility(View.GONE);
        goodsinfo_null.setVisibility(View.VISIBLE);
        goodsinfo_null.setText(title);
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
            refhreshDate(pGoodsInfo);
        }
    };
}
