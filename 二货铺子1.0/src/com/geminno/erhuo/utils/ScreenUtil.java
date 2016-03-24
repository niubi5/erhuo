package com.geminno.erhuo.utils;

import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

/**
 * 获取手机屏幕信息
 * 
 * @author Administrator
 *
 */
public class ScreenUtil {
	
	/**
	 * 获取屏幕宽度
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context){
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getWidth();
	}

	/**
	 * 获得屏幕高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context){
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getHeight();
	}
	
	/**
	 * 获取屏幕中控件顶部位置的高度，即控件顶部的Y点
	 * 
	 * @param view
	 * @return
	 */
	public static int getScreenViewTopHeight(View view){
		return view.getTop();
	}
	
	/**
	 * 获取屏幕中控件底部位置的高度，即控件底部的Y点
	 * 
	 * @param view
	 * @return
	 */
	public static int getScreenViewBottonHeight(View view){
		return view.getBottom();
	}
	
	/**
	 * 获取屏幕中控件左侧的位置，即控件左侧的X点
	 * 
	 * @param view
	 * @return
	 */
	public static int getScreenViewLeftHeight(View view){
		return view.getLeft();
	}
	
	/**
	 * 获取屏幕中控件右侧的位置，即控件右侧的X点
	 * 
	 * @param view
	 * @return
	 */
	public static int getScreenViewRightHight(View view){
		return view.getRight();
	}
}
