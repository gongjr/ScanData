package com.gjr.scandata.biz.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gjr.scandata.R;
import com.gjr.scandata.biz.bean.GoodsInfo;
import com.gjr.scandata.biz.bean.GoodsType;
import com.gjr.scandata.biz.listener.OnItemClickListener;

import java.util.List;


public class GoodsTypeAdapter<T> extends BaseAdapter {

    public static final int TYPE_GoodsType=0;
    public static final int TYPE_GoodsInfo=1;
    private Context context;
    private List<T> mGoodsTypeList;
    private int selectPosition=-1;
    private int sBgColor, nBgColor;
    private Drawable sBg, nBg;
    private OnItemClickListener mOnItemClickListener;
    private int TYPE;

    public GoodsTypeAdapter(Context context, List<T> stringList,int type) {
        this.context = context;
        this.mGoodsTypeList = stringList;
        this.TYPE=type;
        this.sBgColor=context.getResources().getColor(R.color.lvitem_goodstype_bg_s);
        this.nBgColor=context.getResources().getColor(R.color.white);
        this.sBg=context.getResources().getDrawable(R.drawable.lvitem_group_bg_s);
        this.nBg=context.getResources().getDrawable(R.drawable.lvitem_group_bg_n);
        if (TYPE==TYPE_GoodsInfo)selectPosition=-1;
        else selectPosition=0;
    }

    private class GoodsTypeViewHolder {
        TextView goodsTypeName;
    }
    @Override
    public int getCount() {
        return mGoodsTypeList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        GoodsTypeViewHolder holder = null;
        if (convertView == null) {
            holder = new GoodsTypeViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lvitem_goodstype, null);
            holder.goodsTypeName = (TextView) convertView.findViewById(R.id.goodstype_name);
            convertView.setTag(holder);
        } else {
            holder = (GoodsTypeViewHolder) convertView.getTag();
        }
        if (selectPosition==position){
            convertView.setBackground(sBg);
        }else{
            convertView.setBackground(nBg);
        }
        if (TYPE==TYPE_GoodsType){
            GoodsType lGoodsType=(GoodsType)mGoodsTypeList.get(position);
            holder.goodsTypeName.setText(lGoodsType.getQcTypeName());
        }else if (TYPE==TYPE_GoodsInfo){
            GoodsInfo lGoodsInfo=(GoodsInfo)mGoodsTypeList.get(position);
            holder.goodsTypeName.setText(lGoodsInfo.getQcName()+" ("+lGoodsInfo.getQcCode()+")");
        }


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    SelectPosition(position);
                    if (mOnItemClickListener!=null)
                        mOnItemClickListener.onItemClick(v,mGoodsTypeList.get(position));

            }
        });
        return convertView;
    }

    public int getSelectPosition() {
        return this.selectPosition;
    }

    public void refreshDate(List<T> pGoodsTypeList){
        this.mGoodsTypeList=pGoodsTypeList;
        if (TYPE==TYPE_GoodsInfo)selectPosition=-1;
        else selectPosition=0;
        notifyDataSetChanged();
    }

    public void SelectPosition(int position){
        this.selectPosition=position;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener pOnItemClickListener){
        this.mOnItemClickListener=pOnItemClickListener;
    }
}
