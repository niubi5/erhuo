package com.geminno.erhuo.adapter;

import java.util.List;

import com.geminno.erhuo.utils.DonationViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class CommonAdapter<T> extends BaseAdapter {
	protected LayoutInflater mInflater;
	// 上下文
	protected Context mContext;
	// 数据源
	protected List<T> mDatas;
	// 布局文件
	protected final int mItemLayoutId;
	
	public CommonAdapter(Context context,List<T> mDatas, int itemLayoutId){
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		this.mDatas = mDatas;
		this.mItemLayoutId = itemLayoutId;
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public T getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final DonationViewHolder viewHolder = getViewHolder(position, convertView, parent);
	    convert(viewHolder ,getItem(position));
	    // 返回convertView
		return viewHolder.getConvertView();
	}

	public abstract void convert(DonationViewHolder helper, T item);
	
	/**
	 * 得到一个ViewHolder
	 * 
	 * @param position
	 * @param convertView
	 * @param parent
	 * @return
	 */
	private DonationViewHolder getViewHolder(int position,View convertView,ViewGroup parent){
		return DonationViewHolder.get(mContext, parent, convertView, mItemLayoutId, position);	
	}
	
	
}
