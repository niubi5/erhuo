package com.geminno.erhuo.utils;

import com.geminno.erhuo.DonateRequestActivity;
import com.geminno.erhuo.R;
import com.geminno.erhuo.StartActivity;
import com.geminno.erhuo.fragment.DonateFragment;
import com.geminno.erhuo.utils.ImageLoader.Type;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class DonationViewHolder {
	/**
	 * SparseArray存放item中的所有控件，键只能为Integer
	 */
    private final SparseArray<View> mViews;	
    /**
     * item的positon
     */
    private int mPosition;
    private View mConvertView;
    
    
    
    /**
     * 构造方法
     * 
     * @param context  上下文
     * @param parent   parent
     * @param layoutId   布局文件id
     * @param positon    item的position
     */
    private DonationViewHolder(Context context, ViewGroup parent,int layoutId,int positon){
    	this.mPosition = positon;
    	this.mViews = new SparseArray<View>();
    	// 解析布局文件
    	mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,false);

    	// 设置标签
    	mConvertView.setTag(this);
    }
    
    /**
     * 获取一个ViewHolder对象
     * 
     * @param context
     * @param parent
     * @param convertView
     * @param layoutId
     * @param position
     * @return
     */
    public static DonationViewHolder get(Context context,ViewGroup parent,View convertView , int layoutId,int position){
    	if(convertView == null){
    		return new DonationViewHolder(context,parent,layoutId,position);
    	}
    	return (DonationViewHolder) convertView.getTag();
    }
    
    /**
     * 得到一个convertView
     * 
     * @return
     */
    public View getConvertView(){
    	return mConvertView;
    }
    
    /**
     * 通过控件的id获取对应的控件，如果没有则加入mViews
     * 
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId){
    	View view = mViews.get(viewId);
    	if(view == null){
    		view = mConvertView.findViewById(viewId);
    		mViews.put(viewId, view);
    	}
    	return (T) view;
    }
    
    /**
     * 为TextViwe设置字符串
     * 
     * @param viewId
     * @param text
     * @return
     */
    public DonationViewHolder setText(int viewId, String text){
    	TextView view = getView(viewId);
    	view.setText(text);
    	return this;
    }
    
    /**
     * 给ImageView设置图片
     * 
     * @param viewId
     * @param drawableId
     * @return
     */
    public DonationViewHolder setImageResource(int viewId, int drawableId){
    	ImageView view  = getView(viewId);
    	view.setImageResource(drawableId);
    	return this;
    }
    
    /**
     * 为ImageView设置图片
     * 
     * @param viewId
     * @param bm
     * @return
     */
    public DonationViewHolder setImageBitmap(int viewId , Bitmap bm){
    	ImageView view  = getView(viewId);
    	view.setImageBitmap(bm);
    	return this;
    }
    
    /**
     * 为imageViwe设置图片
     * 
     * @param viewId
     * @param url
     * @return
     */
    public DonationViewHolder setImageBitmap(int viewId, String url){
    	ImageLoader.getInstance(3,Type.LIFO).loadImage(url, (ImageView)getView(viewId));
    	return this;
    }
    
    /**
     * 获取positon
     * @return
     */
    public int getPosition(){
    	return mPosition;
    }
}
