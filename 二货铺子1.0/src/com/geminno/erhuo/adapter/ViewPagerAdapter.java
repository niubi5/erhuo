package com.geminno.erhuo.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

//	private List<View> views;
//
//	public ViewPagerAdapter(List<View> views) {
//		this.views = views;
//	}
//
//	@Override
//	public int getCount() {
//		// TODO Auto-generated method stub
//		return views.size();
//	}
//
//	@Override
//	public boolean isViewFromObject(View arg0, Object arg1) {
//		// TODO Auto-generated method stub
//		return arg0 == arg1;
//	}
//
//	// 销毁position位置的界面
//	@Override
//	public void destroyItem(ViewGroup container, int position, Object object) {
//		// TODO Auto-generated method stub
//		container.removeView(views.get(position));
//	}
//
//	// 初始化position位置的界面
//	@Override
//	public Object instantiateItem(ViewGroup container, int position) {
//		// TODO Auto-generated method stub
//		container.addView(views.get(position));
//		return views.get(position);
//	}

	 private List<Fragment> fragmentList;
	
	 public ViewPagerAdapter(FragmentManager fm) {
	 super(fm);
	 }
	
	 public ViewPagerAdapter(FragmentManager fm, List<Fragment> fragmentList){
	 super(fm);
	 this.fragmentList = fragmentList;
	 }
	
	
	 @Override
	 public Fragment getItem(int arg0) {
	 return fragmentList.get(arg0);
	 }
	
	 @Override
	 public int getCount() {
	 return fragmentList.size();
	 }
}
