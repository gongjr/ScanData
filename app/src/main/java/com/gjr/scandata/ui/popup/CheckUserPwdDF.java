package com.gjr.scandata.ui.popup;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gjr.scandata.R;

import kxlive.gjrlibrary.base.DialogFragmentBase;

/**
 * @author gjr
 * @creatTime 16/7/6 上午10:19
 */
public class CheckUserPwdDF extends DialogFragmentBase implements View.OnClickListener {

    private View view;
    private OnCheckUserPwdListener mOnCheckUserPwdListener;
    private Button sureBtn;
    private EditText mEditText;
    public static CheckUserPwdDF sCheckUserPwdDF;

    public static CheckUserPwdDF newInstance(){
        if(sCheckUserPwdDF==null){
            sCheckUserPwdDF=new CheckUserPwdDF();
        }
        return sCheckUserPwdDF;
    }

    @SuppressLint("InlinedApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.checkuserpwd_df, null);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initListener();
    }

    private void initView() {
        sureBtn = (Button) view.findViewById(R.id.checkuserpwd_surebtn);
        mEditText = (EditText) view.findViewById(R.id.checkuserpwd_pwd);
        mEditText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        mEditText.setText("");
        Editable editable = mEditText.getText();
        Selection.setSelection(editable, editable.length());//光标置尾
    }

    private void initListener() {
        sureBtn.setOnClickListener(this);
    }

    public void setOnCheckUserPwdListener(OnCheckUserPwdListener onCheckUserPwdListener) {
        this.mOnCheckUserPwdListener = onCheckUserPwdListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkuserpwd_surebtn:
                String pwd=mEditText.getText().toString();
                if (pwd.length()>0){
                    dismiss();
                    mOnCheckUserPwdListener.onSelectBack(pwd);
                }else{
                    Toast.makeText(this.mActivity,"消费密码不能为空!",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public interface OnCheckUserPwdListener {
        public void  onSelectBack(String pwd);
    }
}
