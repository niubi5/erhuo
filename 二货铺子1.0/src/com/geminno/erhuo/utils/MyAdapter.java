package com.geminno.erhuo.utils;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Toast;

/**
 * @author LuoShiHeng
 * @version 创建时间:2016-3-9下午6:48:08
 */
public abstract class MyAdapter<T> extends BaseAdapter{

	protected Context mContext;
	protected List<T> mDatas;
	protected LayoutInflater mInflater;
	private int layoutId;

	public MyAdapter(Context context, List<T> datas) {
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
		this.mDatas = datas;
	}

	public MyAdapter(Context context, List<T> datas, int layoutId) {
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
		this.mDatas = datas;
		this.layoutId = layoutId;
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
		ViewHolder holder = ViewHolder.get(mContext, convertView, parent,
				layoutId, position);
		//holder.setOnClickListener(new MyAdapterListener(position));
		convert(holder, getItem(position));
		return holder.getConvertView();
	}

	public abstract void convert(ViewHolder holder, T t);
	
	class MyAdapterListener implements OnClickListener {
		 
        private int position;
 
        public MyAdapterListener(int pos) {
            position = pos;
        }
 
        @Override
        public void onClick(View v) {
            Toast.makeText(mContext, "您点击了-" + mDatas.get(position), Toast.LENGTH_LONG).show();
        }
    }
}
