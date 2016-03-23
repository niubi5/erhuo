package com.geminno.erhuo.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.geminno.erhuo.R;
import com.geminno.erhuo.entity.Logistics;
/**
 * 显示请求捐赠页面的物流信息下拉列表
 * 
 * @author Administrator
 *
 */

public class LogisticsAdapter extends BaseAdapter {
	
	private List<Logistics> logistics;
	private Context conetxt;
	private ImageView imageView;
	private TextView textView;

	public LogisticsAdapter(List<Logistics> logistics, Context conetxt) {
		super();
		this.logistics = logistics;
		this.conetxt = conetxt;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return logistics.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return logistics.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("ViewHolder") @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(conetxt).inflate(R.layout.logistic_spinner, null);
		// 判断convertView 是否为空
		if(convertView != null){
		   imageView = (ImageView) convertView.findViewById(R.id.iv_logistics_image);
		   imageView.setImageResource(logistics.get(position).getImageId());
		   textView = (TextView) convertView.findViewById(R.id.tv_logistic_verify);
		   textView.setText(logistics.get(position).getVerify());
		}
		return convertView;
	}

}
