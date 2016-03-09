package com.geminno.erhuo;
import java.util.ArrayList;
import java.util.List;

import com.geminno.erhuo.adapter.ViewPagerAdapter;
import com.geminno.erhuo.fragment.GuideFragment1;
import com.geminno.erhuo.fragment.GuideFragment2;
import com.geminno.erhuo.fragment.GuideFragment3;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GuideActivity extends FragmentActivity implements
		OnClickListener, OnPageChangeListener {

	private ImageView[] points; // 底部小点
	private int currentIndex;
	private ViewPager viewPager;
	private List<Fragment> fragmentList;
	private ViewPagerAdapter vpAdapter;
	private GuideFragment1 fragment1;
	private GuideFragment2 fragment2;
	private GuideFragment3 fragment3;
	//private ActionBar mActionBar;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 隐藏标题栏
		setContentView(R.layout.activity_guide);
		initView();
		initPoint();
	}

	// 初始化
	@SuppressWarnings("deprecation")
	public void initView() {
		fragmentList = new ArrayList<Fragment>();
		fragment1 = new GuideFragment1();
		fragment2 = new GuideFragment2();
		fragment3 = new GuideFragment3();
		fragmentList.add(fragment1);
		fragmentList.add(fragment2);
		fragmentList.add(fragment3);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		vpAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList);
		viewPager.setAdapter(vpAdapter);
		viewPager.setOnPageChangeListener(this);
	}
	

	private void initPoint() {
		LinearLayout linearlayout = (LinearLayout) findViewById(R.id.point_layout);
		points = new ImageView[fragmentList.size()];
		for (int i = 0; i < fragmentList.size(); i++) {
			// 获得子组件
			points[i] = (ImageView) linearlayout.getChildAt(i);
			points[i].setImageResource(R.drawable.round_point_normal); // 设置默认状态显示图片
			points[i].setOnClickListener(this);
			points[i].setTag(i);
		}
		currentIndex = 0; // 当前位置
		points[currentIndex].setImageResource(R.drawable.round_point); // 设置为选中状态
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		setCurDot(arg0);
	}

	@Override
	public void onClick(View v) {
			// 点击时跳转到另一个页面
			int position = (Integer) v.getTag(); // 取出之前为小圆点设置的Tag标志
			setCurView(position);
			setCurDot(position);
	}

	// 设置页面当前位置
	private void setCurView(int position) {
		if (position < 0 || position >= fragmentList.size()) {
			return;
		}
		viewPager.setCurrentItem(position);
	}

	// 设置当前小点位置
	private void setCurDot(int position) {
		if (position < 0 || position > fragmentList.size() - 1
				|| currentIndex == position) {
			return;
		}
		points[position].setImageResource(R.drawable.round_point);
		points[currentIndex].setImageResource(R.drawable.round_point_normal);
		currentIndex = position;
	}


}
