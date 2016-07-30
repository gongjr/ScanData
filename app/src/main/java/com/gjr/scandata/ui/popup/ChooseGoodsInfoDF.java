package com.gjr.scandata.ui.popup;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gjr.scandata.R;
import com.gjr.scandata.biz.adapter.GoodsTypeAdapter;
import com.gjr.scandata.biz.bean.GoodsInfo;
import com.gjr.scandata.biz.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import kxlive.gjrlibrary.base.DialogFragmentBase;

public class ChooseGoodsInfoDF extends DialogFragmentBase implements View.OnClickListener {


    private View view;
    private TextView title;
    private ImageView closeBtn;
    private ListView listView;
    private Button submitBtn;
    private GoodsTypeAdapter adapter;
    private OnFinifhBackListener onFinifhBackListener;
    private GoodsInfo mGoodsInfo;
    private List<GoodsInfo> goodsInfoList;


    public  static ChooseGoodsInfoDF sChooseGoodsInfoDF;

    public static ChooseGoodsInfoDF newInstance(List<GoodsInfo> pGoodsInfos) {
        if (sChooseGoodsInfoDF==null)
            sChooseGoodsInfoDF = new ChooseGoodsInfoDF();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("GoodsInfoList", (ArrayList<? extends Parcelable>) pGoodsInfos);
        if (sChooseGoodsInfoDF.getArguments() != null) {
            sChooseGoodsInfoDF.getArguments().clear();
        }
        sChooseGoodsInfoDF.setArguments(bundle);
        return sChooseGoodsInfoDF;
    }

    public interface OnFinifhBackListener {
        public void onFinifhBack(GoodsInfo pGoodsInfo);
    }

    @SuppressLint("InlinedApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goodsInfoList = getArguments().getParcelableArrayList("GoodsInfoList");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.df_choose_desk_order, null);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    /*
    * 初始化控件
    * */
    private void initView() {
        title = (TextView) view.findViewById(R.id.tv_desk_info);
        closeBtn = (ImageView) view.findViewById(R.id.img_close);
        listView = (ListView) view.findViewById(R.id.lv_desk_orders);
        submitBtn = (Button) view.findViewById(R.id.btn_ensure);
    }

    /*
    * 相关数据操作
    * */
    private void initData() {
        adapter = new GoodsTypeAdapter(mActivity,goodsInfoList,GoodsTypeAdapter.TYPE_GoodsInfo);
        adapter.setOnItemClickListener(mInfoOnItemClickListener);
        listView.setAdapter(adapter);
        title.setText("请选择查看项");
    }

    /*
    * 设置相关点击事件
    * */
    private void initListener() {
        closeBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }

    OnItemClickListener mInfoOnItemClickListener=new OnItemClickListener() {
        @Override
        public void onItemClick(View view, Object data) {
            mGoodsInfo=(GoodsInfo)data;
        }
    };

    public void setOnFinishBackListener(OnFinifhBackListener onFinishBackListener) {
        this.onFinifhBackListener = onFinishBackListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_close:
                dismiss();
                break;
            case R.id.btn_ensure:
                if (mGoodsInfo!=null){
                    onFinifhBackListener.onFinifhBack(mGoodsInfo);
                    dismiss();
                }else{
                    showShortToast("没有选择查看项!");
                }
                break;
        }
    }


}
