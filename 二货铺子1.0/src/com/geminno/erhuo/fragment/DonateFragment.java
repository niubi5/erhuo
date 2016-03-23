package com.geminno.erhuo.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.geminno.erhuo.DonateRequestActivity;
import com.geminno.erhuo.MainActivity;
import com.geminno.erhuo.R;
import com.geminno.erhuo.StartActivity;
import com.geminno.erhuo.adapter.CommonAdapter;
import com.geminno.erhuo.entity.Donation;
import com.geminno.erhuo.utils.DonateDialog;
import com.geminno.erhuo.utils.DonationViewHolder;

public class DonateFragment extends BaseFragment {

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
				helper.setImageResource(R.id.iv_popwindow, item.getButton());
			}

		});
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
		// 添加
		ivPublish.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),DonateRequestActivity.class);
				startActivity(intent);
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
        
	}
}
