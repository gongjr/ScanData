package com.gjr.scandata.biz.listener;

import android.view.View;

/**
 * 列表子元素点击事件监听器
 * 
 * @author gjr
 * 
 */
public interface OnItemClickListener<T> {
	public void onItemClick(View view, T data);
}
