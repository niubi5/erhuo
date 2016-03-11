package com.geminno.erhuo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author LuoShiHeng
 * @version 创建时间:2016-3-10下午8:33:20
 */
public class RefreshListView extends ListView {
	
	View headView;
	ImageView imageView;
	ProgressBar progressBar;
	TextView tvRefreshTime;
	private OnRefreshCallBack refreshCallBack;

	public RefreshListView(Context context) {
		super(context);
	}

	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setOnRefreshCallBack(OnRefreshCallBack refreshCallBack){
		this.refreshCallBack=refreshCallBack;
		
	}
	
	//接口：封装下拉刷新，上拉加载的方法
	public interface OnRefreshCallBack{
		void onRefresh();//刷新
		void onPull();//加载
	}
	
	

}
