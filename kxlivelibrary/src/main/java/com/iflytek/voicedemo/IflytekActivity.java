package com.iflytek.voicedemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import kxlive.gjrlibrary.R;

public class IflytekActivity extends Activity implements OnClickListener{

	private Toast mToast;
	@SuppressLint("ShowToast")
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		// 设置标题栏（无标题）
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_iflytek);
		mToast = Toast.makeText(this,"",Toast.LENGTH_SHORT);
		SimpleAdapter listitemAdapter = new SimpleAdapter();		
		((ListView)findViewById(R.id.listview_main)).setAdapter(listitemAdapter);
	}
	@Override
	public void onClick(View view) {
		int tag = Integer.parseInt(view.getTag().toString());
		Intent intent = null;
		switch (tag) {
		case 0:
			// 语音转写
			intent = new Intent(this, IatDemo.class);
			break;
		case 1:
			// 合成
			intent = new Intent(this, TtsDemo.class);
			break;
		}
		if (intent != null) {
			System.out.println("1111111:"+intent);
			startActivity(intent);		
		}else{
			System.out.println("22222:"+intent);
		}
	}


	//Menu 列表
	String items[] = {"立刻体验语音听写","立刻体验语音合成"};
	private class SimpleAdapter extends BaseAdapter{
		public View getView(int position, View convertView, ViewGroup parent) 
		{	   		  
			if(null == convertView){
				LayoutInflater factory = LayoutInflater.from(IflytekActivity.this);
				View mView = factory.inflate(R.layout.list_items, null);
				convertView = mView;
			}
			Button btn = (Button)convertView.findViewById(R.id.btn);
			btn.setOnClickListener(IflytekActivity.this);
			btn.setTag(position);
			btn.setText(items[position]);
			return convertView;
		}
		@Override
		public int getCount() {
			return items.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
	}	
	private void showTip(final String str)
	{
		mToast.setText(str);
		mToast.show();
	}
}
