package com.geminno.erhuo.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.geminno.erhuo.DonateRequestActivity;
import com.geminno.erhuo.DonationDetailActivity;
import com.geminno.erhuo.MainActivity;
import com.geminno.erhuo.R;
import com.geminno.erhuo.StartActivity;
import com.geminno.erhuo.adapter.CommonAdapter;
import com.geminno.erhuo.entity.Donation;
import com.geminno.erhuo.utils.DonationViewHolder;

public class DonateFragment extends BaseFragment{

	private ListView mListView;
	/**
	 * 源数据
	 */
	private List<Donation> mDatas = new ArrayList<Donation>();
	/**
	 * 适配器
	 */
	private CommonAdapter<Donation> commonAdapter;
	/**
	 * 解析布局文件
	 */
	private View view;
	/**
	 * 发布图片
	 */
	private ImageView ivPublish;
	/**
	 * 回到顶部图片
	 */
	private ImageView ivToTop;
	/**
	 * 标记是否滑动
	 */
	private boolean scrollFlag = false;
	/**
	 * 记录最后一次滑动的位置
	 */
	private int lastVisiblePosition = 0;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_donate_page, null);
		initData();
		initView();
		mListView.setAdapter(commonAdapter = new CommonAdapter<Donation>(
				getContext(), mDatas, R.layout.doantion_list) {

			@Override
			public void convert(DonationViewHolder helper, Donation item) {
				helper.setImageResource(R.id.iv_head, item.getUserHeadImage());
				helper.setText(R.id.tv_donation_user_name, item.getUserName());
				helper.setText(R.id.tv_donation_time, item.getTime());
				helper.setImageResource(R.id.iv_poverty_image, item.getImage());
				helper.setText(R.id.tv_detail, item.getDetail());
				helper.setImageResource(R.id.iv_donation_address,
						item.getAddressImage());
				helper.setText(R.id.tv_donatoin_addresss, item.getAddress());
			}

		});

		// 监听mListView滚动状态
		mListView.setOnScrollListener(new OnScrollListener() {

			// mListView状态改变时回调
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				// 空闲状态 -- 停止滚动
				case OnScrollListener.SCROLL_STATE_IDLE:
					scrollFlag = false;
					// 第一条可见item == 0,到顶部,隐藏图片
					if (mListView.getFirstVisiblePosition() == 0) {
						ivToTop.setVisibility(View.GONE);
					}
					// 最后一条可见item == totalCount - 1,到底部，显示图片
					if (mListView.getLastVisiblePosition() == (mListView
							.getCount() - 1)) {
						ivToTop.setVisibility(View.VISIBLE);
					}
					break;
				// 滚动状态
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					scrollFlag = true;
					break;
				// 由于惯性产生的快速滑动
				case OnScrollListener.SCROLL_STATE_FLING:
					scrollFlag = true;
					break;
				}
			}

			// 滚动时回调
			/**
			 * firstVisibleItem --> 第一条可见itemId visibleItemCount --> 当前可见item数量
			 * totalItemCount --> item总数
			 */
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (scrollFlag) {
					// 第一条可见itemId > 0,上拉
					if (firstVisibleItem > lastVisiblePosition) {
						ivToTop.setVisibility(View.VISIBLE);
						// 第一条可见itemId < 上一次拉动位置,下拉
					} else if (firstVisibleItem < lastVisiblePosition) {
						ivToTop.setVisibility(View.GONE);
					} else {
						return;
					}
					lastVisiblePosition = firstVisibleItem;
				}
			}
		});
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(),DonationDetailActivity.class);
				startActivity(intent);
			}
		});
//		ImageView v = (ImageView) mListView.findViewById(R.id.iv_toDonate);
//		v.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(getActivity(),DonateRequestActivity.class);
//				startActivity(intent);
//			}
//		});
		initEvent();
		return view;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	protected void initData() {
		Donation donation;
		String detail = "这些地方房子年久失修，摇摇欲坠，孩子们穿的衣服破破烂烂，教室残破不堪，希望捐赠多一些生活用品，教科书等";
		String address = "四川汶川";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		donation = new Donation(R.drawable.head_image_one, "小螺丝..",
				sdf.format(new Date()), R.drawable.poverty_one, detail,
				R.drawable.icon_city, address, R.drawable.publish_donation);
		mDatas.add(donation);
		donation = new Donation(R.drawable.head_image_two, "小螺丝..",
				sdf.format(new Date()), R.drawable.poverty_two, detail,
				R.drawable.icon_city, address, R.drawable.publish_donation);
		mDatas.add(donation);
		donation = new Donation(R.drawable.head_image_three, "小螺丝..",
				sdf.format(new Date()), R.drawable.poverty_three, detail,
				R.drawable.icon_city, address, R.drawable.publish_donation);
		mDatas.add(donation);
		donation = new Donation(R.drawable.head_image_four, "小螺丝..",
				sdf.format(new Date()), R.drawable.poverty_four, detail,
				R.drawable.icon_city, address, R.drawable.publish_donation);
		mDatas.add(donation);
		donation = new Donation(R.drawable.head_image_five, "小螺丝..",
				sdf.format(new Date()), R.drawable.poverty_five, detail,
				R.drawable.icon_city, address, R.drawable.publish_donation);
		mDatas.add(donation);
		donation = new Donation(R.drawable.head_image_six, "小螺丝..",
				sdf.format(new Date()), R.drawable.poverty_six, detail,
				R.drawable.icon_city, address, R.drawable.publish_donation);
		mDatas.add(donation);
		donation = new Donation(R.drawable.head_image_seven, "小螺丝..",
				sdf.format(new Date()), R.drawable.poverty_seven, detail,
				R.drawable.icon_city, address, R.drawable.publish_donation);
		mDatas.add(donation);
		donation = new Donation(R.drawable.head_image_eight, "小螺丝..",
				sdf.format(new Date()), R.drawable.poverty_eight, detail,
				R.drawable.icon_city, address, R.drawable.publish_donation);
		mDatas.add(donation);
	}

	/**
	 * 点击事件
	 */
	@Override
	protected void initEvent() {
		// 跳转到发布捐赠页面
		ivPublish.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						DonateRequestActivity.class);
				startActivity(intent);
			}
		});
		// 滚动到第一条
		ivToTop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setPosition(0);
			}
		});
	}

	/**
	 * 初始化控件
	 */
	@Override
	protected void initView() {
		mListView = (ListView) view.findViewById(R.id.lv_donations);
		ivPublish = (ImageView) view.findViewById(R.id.home_search);
		ivToTop = (ImageView) view.findViewById(R.id.iv_to_top);
	}

	/**
	 * 滚动mListView到指定位置
	 * 
	 * @param position
	 */
	public void setPosition(int position) {
		if (android.os.Build.VERSION.SDK_INT >= 8) {
			mListView.smoothScrollToPosition(position);
		} else {
			mListView.setSelection(position);
		}
	}

	
}
