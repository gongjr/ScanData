package com.gjr.scandata.biz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gjr.scandata.R;
import com.gjr.scandata.http.AddressState;


public class ChooseServerAddressAdapter extends BaseAdapter {

    private Context context;
    private int selectPosition;
    private AddressState[] lAddressState;


    public ChooseServerAddressAdapter(Context context, AddressState[] mAddressState, int selectPosition) {
        this.context = context;
        this.selectPosition = selectPosition;
        this.lAddressState = mAddressState;
    }

    private class ChooseMemberCardViewHolder {
        TextView cardName, balance;
    }

    @Override
    public int getCount() {
        return lAddressState.length;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ChooseMemberCardViewHolder holder = null;
        if (convertView == null) {
            holder = new ChooseMemberCardViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lvitem_membre_card, null);
            holder.cardName = (TextView) convertView.findViewById(R.id.mem_card_name);
            holder.balance = (TextView) convertView.findViewById(R.id.mem_card_balance);
            convertView.setTag(holder);
        } else {
            holder = (ChooseMemberCardViewHolder) convertView.getTag();
        }
        if (position == selectPosition) {
            convertView.setBackgroundResource(R.drawable.desk_item_bg_s);
        } else {
            convertView.setBackgroundResource(R.drawable.desk_item_bg_n);
        }
        holder.cardName.setText(lAddressState[position].getTitle());
        holder.balance.setVisibility(View.GONE);
        return convertView;
    }

    public void changeSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
        notifyDataSetChanged();
    }
}
