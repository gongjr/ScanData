package com.gjr.scandata.ui.popup;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gjr.scandata.R;
import com.gjr.scandata.biz.adapter.ChooseServerAddressAdapter;
import com.gjr.scandata.http.AddressState;
import com.gjr.scandata.http.HttpController;

import kxlive.gjrlibrary.base.DialogFragmentBase;

public class ChooseServerAddressDF extends DialogFragmentBase implements View.OnClickListener {


    private View view;
    private TextView title;
    private ImageView closeBtn;
    private ListView listView;
    private Button submitBtn;
    private ChooseServerAddressAdapter adapter;
    private OnFinifhBackListener onFinifhBackListener;
    private AddressState curAddressState= HttpController.getInstance().getAddress();
    private AddressState[] lAddressState=AddressState.values();


    public  static ChooseServerAddressDF chooseServerAddressDF;

    public static ChooseServerAddressDF newInstance() {
        if (chooseServerAddressDF==null)
            chooseServerAddressDF = new ChooseServerAddressDF();
        return chooseServerAddressDF;
    }

    public interface OnFinifhBackListener {
        public void onFinifhBack(AddressState pAddressState);
    }

    @SuppressLint("InlinedApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        int initSelected=0;
        for (int i=0;i<lAddressState.length;i++){
            if (curAddressState.getValue().equals(lAddressState[i].getValue())){
                initSelected=i;break;
            }
        }
        adapter = new ChooseServerAddressAdapter(view.getContext(),lAddressState, initSelected);
        listView.setAdapter(adapter);
        title.setText("选择服务器环境");
    }

    /*
    * 设置相关点击事件
    * */
    private void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.changeSelectPosition(position);
                curAddressState=lAddressState[position];
            }
        });
        closeBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }

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
                onFinifhBackListener.onFinifhBack(curAddressState);
                dismiss();

                break;
        }
    }


}
