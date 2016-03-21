package com.geminno.erhuo.utils;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geminno.erhuo.R;
import com.geminno.erhuo.entity.ADInfo;
import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.entity.Markets;
import com.geminno.erhuo.view.ImageCycleView;
import com.geminno.erhuo.view.ImageCycleView.ImageCycleViewListener;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author LuoShiHeng
 * @version 创建时间:2016-3-15下午1:48:37
 */
@SuppressLint("InflateParams") public class HomePageAdapter extends BaseAdapter {

	// private List<Goods> list = new ArrayList<Goods>();
	private Context context;
	private final int TYPE_AD = 0;// Item的类型
	private final int TYPE_TYPES = 1;
	private final int TYPE_MARKET = 2;
	private final int TYPE_GOODS = 3;
	private View view;
	private ImageCycleView mAdView;
	private ArrayList<ADInfo> infos = new ArrayList<ADInfo>();
	final int OTHERTYPE = 3;// 广告、类别、集市
	// --------------------
	private List<Goods> listGoods = new ArrayList<Goods>();// 商品集合
	private List<Markets> listMarkets = new ArrayList<Markets>();// 集市集合
	private int currentIndex;
	// 广告图片
	private String[] imageUrls = {
			"http://img.taodiantong.cn/v55183/infoimg/2013-07/130720115322ky.jpg",
			"http://pic30.nipic.com/20130626/8174275_085522448172_2.jpg",
			"http://10.201.1.6:8080/ads/ad2.jpg",
			"http://pic15.nipic.com/20110722/2912365_092519919000_2.jpg",
			"http://10.201.1.6:8080/ads/ad1.jpg" };

	public HomePageAdapter(Context context) {
		this.context = context;
	}

	public HomePageAdapter(Context context, List<Markets> listMarkets,
			List<Goods> listGoods) {
		this.context = context;
		this.listMarkets = listMarkets;
		this.listGoods = listGoods;
	}

	@Override
	public int getCount() {
		return 3 + (listGoods.size() % 2 == 0 ? listGoods.size() / 2
				: listGoods.size() / 2 + 1);
	}

	@Override
	public Object getItem(int position) {
		// return list.get(position);
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	// 每个convertView都会调用此方法，获得当前所需要的view样式
	@Override
	public int getItemViewType(int position) {
		switch (position) {
		case 0:
			return TYPE_AD;
		case 1:
			return TYPE_TYPES;
		case 2:
			return TYPE_MARKET;
		case 3:
			return TYPE_GOODS;
		default:
			return TYPE_GOODS;
		}
	}

	@Override
	public int getViewTypeCount() {
		// return list.size();
		return 4;
	}

	@SuppressLint("InflateParams") @Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (position == 0) {
			return getADViewPager(convertView);
		} else if (position == 1) {
			return getTypeItem(convertView);
		} else if (position == 2) {
			return getMarketView(convertView);
		} else
			return getGoodsView(position, convertView);
	}
	
	// 获得类别Item
	private View getTypeItem(View convertView){
		View view = LayoutInflater.from(context).inflate(R.layout.type_item,
				null);
		
		return view;
	}

	// 获得商品view
	private View getGoodsView(int position, View convertView) {
		View view = LayoutInflater.from(context).inflate(R.layout.product_item,
				null);
		ImageView imageLeft = (ImageView) view
				.findViewById(R.id.product_image_left);
		ImageView imageRight = (ImageView) view
				.findViewById(R.id.product_image_right);
		TextView infoLeft = (TextView) view
				.findViewById(R.id.product_info_left);
		TextView infoRight = (TextView) view
				.findViewById(R.id.product_info_right);
		TextView priceLeft = (TextView) view
				.findViewById(R.id.product_price_left);
		TextView priceRight = (TextView) view
				.findViewById(R.id.product_price_right);
		if (currentIndex < listGoods.size()) {
			imageLeft.setImageResource(R.drawable.withdraw_default);
			infoLeft.setText(listGoods.get(currentIndex).getName());
			priceLeft.setText(listGoods.get(currentIndex).getSoldPrice() + "");
			if (currentIndex + 1 < listGoods.size()) {
				imageRight.setImageResource(R.drawable.withdraw_default);
				infoRight.setText(listGoods.get(currentIndex + 1).getName());
				priceRight.setText(listGoods.get(currentIndex + 1)
						.getSoldPrice() + "");
			}
			if (currentIndex == (listGoods.size() - 1)) {
				imageRight.setVisibility(View.GONE);
				infoRight.setVisibility(View.GONE);
				priceRight.setVisibility(View.GONE);
			}
			currentIndex = currentIndex + 2;
		}
		return view;
	}

	// 获得集市view
	private View getMarketView(View convertView) {
		Log.i("erhuo", "getMarketView()");
		View view = LayoutInflater.from(context).inflate(R.layout.market_list,
				null);
		LinearLayout grandpa = (LinearLayout) view
				.findViewById(R.id.market_container);
		// 包裹内容
		LayoutParams param1 = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		// 布局参数(match_parent，wrap_content)
		LayoutParams param2 = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		LayoutParams param3 = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.MATCH_PARENT);
		if (listMarkets != null) {
			for (Markets market : listMarkets) {
				// 线性布局
				LinearLayout father = new LinearLayout(context);
				father.setLayoutParams(param1);
				father.setOrientation(LinearLayout.VERTICAL);
				father.setBackgroundColor(Color.WHITE);
				// 集市图片
				ImageView imageView = new ImageView(context);
				imageView.setLayoutParams(param1);
				imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				String imageURL = market.getUrl();
				ImageLoader.getInstance().displayImage(imageURL, imageView);
				// 集市名称
				TextView tvMarketName = new TextView(context);
				tvMarketName.setGravity(Gravity.CENTER_HORIZONTAL);
				tvMarketName.setLayoutParams(param2);
				tvMarketName.setText(market.getName());
				tvMarketName.setTextSize(11);
				// 装填集市人数，商品数
				LinearLayout son = new LinearLayout(context);
				son.setLayoutParams(param2);
				son.setOrientation(LinearLayout.HORIZONTAL);
				son.setGravity(Gravity.CENTER_HORIZONTAL);
				// 用户数图片
				ImageView ivUserCount = new ImageView(context);// 用户图标
				ivUserCount.setLayoutParams(param1);
				ivUserCount.setImageResource(R.drawable.ic_member);

				// 关注集市的人数
				TextView tvMarketUserCount = new TextView(context);
				tvMarketUserCount.setLayoutParams(param3);
				tvMarketUserCount.setPadding(5, 0, 0, 0);
				tvMarketUserCount.setText(market.getUserCount() + "");
				tvMarketUserCount.setTextSize(11);
				tvMarketUserCount.setGravity(Gravity.CENTER_VERTICAL);

				// 商品数图片
				ImageView ivGoodsCount = new ImageView(context);// 商品图标
				ivGoodsCount.setLayoutParams(param1);
				ivGoodsCount.setImageResource(R.drawable.ic_comment);
				ivGoodsCount.setPadding(50, 0, 0, 0);
				// 商品数量textview
				TextView tvMarketGoodsCount = new TextView(context);
				tvMarketGoodsCount.setLayoutParams(param3);
				tvMarketGoodsCount.setPadding(5, 0, 0, 0);
				tvMarketGoodsCount.setText(market.getGoodsCount() + "");
				tvMarketGoodsCount.setTextSize(11);
				tvMarketGoodsCount.setGravity(Gravity.CENTER_VERTICAL);
				// 布局间距
				View viewPadding = new View(context);
				viewPadding.setLayoutParams(new LayoutParams(8,
						LayoutParams.MATCH_PARENT));
				viewPadding.setBackgroundColor(Color.parseColor("#fff9f8f4"));

				son.addView(ivUserCount);
				son.addView(tvMarketUserCount);
				son.addView(ivGoodsCount);
				son.addView(tvMarketGoodsCount);
				father.addView(imageView);
				father.addView(tvMarketName);
				father.addView(son);
				grandpa.addView(father);
				grandpa.addView(viewPadding);
			}
		}
		return view;
	}

	// 获得广告轮播
	private View getADViewPager(View convertView) {
		Log.i("erhuo", "getADViewPager()");
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.adviewpager,
					null);
			mAdView = (ImageCycleView) view.findViewById(R.id.ad_view);
			// 初始化广告图片数据源
			for (int i = 0; i < imageUrls.length; i++) {
				ADInfo info = new ADInfo();
				info.setUrl(imageUrls[i]);
				info.setContent("top-->" + i);
				infos.add(info);
			}
			// 设置数据源
			mAdView.setImageResources(infos, mAdCycleViewListener);
		}
		return view;
	}

	public class ViewHolder1 {

	}

	public class ViewHolder2 {

	}

	public class ViewHolder3 {

	}

	// 实现接口
	private ImageCycleViewListener mAdCycleViewListener = new ImageCycleViewListener() {

		@Override
		// 广告栏点击事件
		public void onImageClick(ADInfo info, int position, View imageView) {

		}

		@Override
		public void displayImage(String imageURL, ImageView imageView) {
			// 使用ImageLoader对图片进行加装
			ImageLoader.getInstance().displayImage(imageURL, imageView);
		}
	};

}
