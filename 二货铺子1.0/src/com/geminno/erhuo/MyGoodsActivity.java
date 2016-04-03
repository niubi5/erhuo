package com.geminno.erhuo;

import java.util.ArrayList;
import java.util.List;

import com.geminno.erhuo.fragment.BaseFragment;
import com.geminno.erhuo.fragment.BoughtFragment;
import com.geminno.erhuo.fragment.CollectionFragment;
import com.geminno.erhuo.fragment.DonateFragment;
import com.geminno.erhuo.fragment.DonationFragment;
import com.geminno.erhuo.fragment.HomeFragment;
import com.geminno.erhuo.fragment.MessageFragment;
import com.geminno.erhuo.fragment.SellingFragment;
import com.geminno.erhuo.fragment.SoldFragment;
import com.geminno.erhuo.fragment.UserInfoFragment;

import android.R.fraction;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MyGoodsActivity extends FragmentActivity implements
		OnClickListener {
	private Context context;
	private SellingFragment sellingFragment;
	private SoldFragment soldFragment;
	private BoughtFragment boughtFragment;
	private DonationFragment donationFragment;
	private CollectionFragment collecFragment;
	private List<BaseFragment> fragments;
	private ViewPager vpFragment;
	private List<Button> btns;
	private Button btnSelling;
	private Button btnSold;
	private Button btnBought;
	private Button btnDonation;
	private Button btnFavorite;
	private ImageView ivreturn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_my_goods);
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
		context = this;
		int curPage = getIntent().getIntExtra("btnId", 0);
		initView();
		vpFragment.setCurrentItem(curPage);
		btns.get(curPage).setSelected(true);
	}

	@SuppressWarnings({ "deprecation", "deprecation" })
	private void initView() {
		context = this;
		sellingFragment = new SellingFragment(context);
		soldFragment = new SoldFragment(context);
		boughtFragment = new BoughtFragment(context);
		donationFragment = new DonationFragment(context);
		collecFragment = new CollectionFragment(context);
		vpFragment=(ViewPager) findViewById(R.id.vp_fragment);
		ivreturn=(ImageView) findViewById(R.id.iv_mine_return);
		btnSelling = (Button) findViewById(R.id.btn_selling);
		btnSold = (Button) findViewById(R.id.btn_sold);
		btnBought = (Button) findViewById(R.id.btn_bought);
		btnDonation = (Button) findViewById(R.id.btn_donate);
		btnFavorite = (Button) findViewById(R.id.btn_collec);
		fragments = new ArrayList<BaseFragment>();
		btns = new ArrayList<Button>();

		fragments.add(sellingFragment);
		fragments.add(soldFragment);
		fragments.add(boughtFragment);
		fragments.add(donationFragment);
		fragments.add(collecFragment);
		btns.add(btnSelling);
		btns.add(btnSold);
		btns.add(btnBought);
		btns.add(btnDonation);
		btns.add(btnFavorite);

		btnSelling.setOnClickListener(this);
		btnSold.setOnClickListener(this);
		btnBought.setOnClickListener(this);
		btnDonation.setOnClickListener(this);
		btnFavorite.setOnClickListener(this);
		ivreturn.setOnClickListener(this);
		// // 默认显示第一个fragment
		// getSupportFragmentManager().beginTransaction()
		// .add(R.id.fragment_container, fragments.get(0)).commit();
		// // 默认选中首页按钮
		// btns.get(0).setSelected(true);
		vpFragment.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager()));
		vpFragment.setOffscreenPageLimit(4);
		vpFragment.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				for(Button btn : btns){
					btn.setSelected(false);
				}
				btns.get(arg0).setSelected(true);
//				fragments.get(arg0).
				//sellingFragment.
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	@Override
	public void onClick(View v) {
		int item=-1;//记录当前选中的item
		switch (v.getId()) {
		case R.id.iv_mine_return:
			this.finish();
			break;
		// 主页按钮
		case R.id.btn_selling:
			item = 0;
			break;
		// 捐赠按钮
		case R.id.btn_sold:
			item = 1;
			break;
		// 消息按钮
		case R.id.btn_bought:
			item = 2;
			break;
		// 个人中心按钮
		case R.id.btn_donate:
			item = 3;
			break;
		// 发布按钮
		case R.id.btn_collec:
			item = 4;
			break;
		}
		// 取消当前按钮选中状态
//		btns.get(currentIndex).setSelected(false);
//		// 更换fragment
//		//changeFragment(nextIndex);
//		// 选中nextIndex按钮
//		btns.get(nextIndex).setSelected(true);
		vpFragment.setCurrentItem(item);
	}

	//viewPager适配器
	class MyFragmentPagerAdapter extends FragmentPagerAdapter {

		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public android.support.v4.app.Fragment getItem(int pos) {
			// TODO Auto-generated method stub
			return fragments.get(pos);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return fragments.size();
		}

	}
	
	public void onclick(View v){
		switch (v.getId()) {
		case R.id.iv_mine_return:
			finish();
			break;

		default:
			break;
		}
	}
}
