package com.geminno.erhuo.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.R.string;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geminno.erhuo.ClassificationActivity;
import com.geminno.erhuo.R;
import com.geminno.erhuo.StartActivity;
import com.geminno.erhuo.entity.ADInfo;
import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.entity.Markets;
import com.geminno.erhuo.entity.Users;
import com.geminno.erhuo.view.ImageCycleView;
import com.geminno.erhuo.view.ImageCycleView.ImageCycleViewListener;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.text.TextUtils;
import android.widget.ImageView.ScaleType;

/**
 * @author LuoShiHeng
 * @version 创建时间:2016-3-15下午1:48:37
 */
@SuppressLint("InflateParams")
public class HomePageAdapter extends BaseAdapter implements OnClickListener{

	private Context context;
	private final int TYPE_AD = 0;// Item的类型
	private final int TYPE_TYPES = 1;
	private final int TYPE_MARKET = 2;
	private final int TYPE_GOODS = 3;
	private ArrayList<ADInfo> infos = new ArrayList<ADInfo>();
	final int TYPECOUNT = 4;// 广告、类别、集市、商品
	// --------------------
	private List<Markets> listMarkets = new ArrayList<Markets>();// 集市集合
	private List<Goods> listgoods = new ArrayList<Goods>();
	private List<List<String>> listUrls = new ArrayList<List<String>>();
	private List<Map<Map<Goods, Users>, List<String>>> listAll;// 所有商品的所有信息
	private Map<Map<Goods, Users>, List<String>> map = new HashMap<Map<Goods, Users>, List<String>>();// 一个商品的Map集合
	private SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
	private float scale;// 屏幕密度
	private int px1;// 商品图片px
	private int px2;// 集市图片px宽
	private int px3;// 集市图片px高
	private LayoutParams params3;
	private LayoutParams imageMarket;
	// 广告图片
	private String[] imageUrls = {
			"http://img.taodiantong.cn/v55183/infoimg/2013-07/130720115322ky.jpg",
			"http://pic30.nipic.com/20130626/8174275_085522448172_2.jpg",
			"http://10.201.1.6:8080/ads/ad2.jpg",
			"http://pic15.nipic.com/20110722/2912365_092519919000_2.jpg",
			"http://10.201.1.6:8080/ads/ad1.jpg" };
	ViewHolderType viewHolderType = null;
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

	public HomePageAdapter(Context context) {
		this.context = context;
	}

	// public HomePageAdapter(Context context, List<Markets> listMarkets,
	// List<Map<Goods, List<String>>> listAll) {
	// this.context = context;
	// this.listMarkets = listMarkets;
	// this.listAll = listAll;
	// scale = context.getResources().getDisplayMetrics().density;
	// px1 = (int)(200 * scale + 0.5f);
	// px2 = (int)(180 * scale + 0.5f);
	// px3 = (int)(112.5 * scale + 0.5f);
	// params3 = new LayoutParams(px1, px1);
	// imageMarket = new LayoutParams(px2, px3);
	// }

	public HomePageAdapter(Context context, List<Markets> listMarkets,
			List<Map<Map<Goods, Users>, List<String>>> listAll) {
		this.context = context;
		this.listMarkets = listMarkets;
		this.listAll = listAll;
		scale = context.getResources().getDisplayMetrics().density;
		px1 = (int) (200 * scale + 0.5f);
		px2 = (int) (180 * scale + 0.5f);
		px3 = (int) (112.5 * scale + 0.5f);
		params3 = new LayoutParams(px1, px1);
		imageMarket = new LayoutParams(px2, px3);
	}

	// ---------------- 3.22 -----------------
	@Override
	public int getCount() {
		// return 3 + (listGoods.size() % 2 == 0 ? listGoods.size() / 2
		// : listGoods.size() / 2 + 1);
		return 3 + listAll.size();
	}

	// ---------------- 3.22 -----------------
	@Override
	public Object getItem(int position) {
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
		return TYPECOUNT;
	}

	@SuppressLint("InflateParams")
	@Override
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
	private View getTypeItem(View convertView) {
		if (convertView == null) {
			viewHolderType = new ViewHolderType();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.type_item, null);
			viewHolderType.ip = (Button) convertView
					.findViewById(R.id.type_iphone);
			viewHolderType.pad = (Button) convertView.findViewById(R.id.type_pad);
			viewHolderType.pc = (Button) convertView.findViewById(R.id.type_pc);
			viewHolderType.ixiaomi = (Button) convertView.findViewById(R.id.type_ixiaomi);
			viewHolderType.c = (Button) convertView.findViewById(R.id.type_3c);
			viewHolderType.card = (Button) convertView.findViewById(R.id.type_card);
			viewHolderType.luggage = (Button) convertView.findViewById(R.id.type_luggage);
			viewHolderType.perfume = (Button) convertView.findViewById(R.id.type_perfume);
			viewHolderType.ip.setOnClickListener(this);
			viewHolderType.pad.setOnClickListener(this);
			viewHolderType.pc.setOnClickListener(this);
			viewHolderType.ixiaomi.setOnClickListener(this);
			viewHolderType.c.setOnClickListener(this);
			viewHolderType.card.setOnClickListener(this);
			viewHolderType.luggage.setOnClickListener(this);
			viewHolderType.perfume.setOnClickListener(this);
			convertView.setTag(viewHolderType);
		} else {
			viewHolderType = (ViewHolderType) convertView.getTag();
		}
//		viewHolderType.ip.setOnClickListener(this);
//		viewHolderType.pad.setOnClickListener(this);
//		viewHolderType.pc.setOnClickListener(this);
//		viewHolderType.ixiaomi.setOnClickListener(this);
//		viewHolderType.c.setOnClickListener(this);
//		viewHolderType.card.setOnClickListener(this);
//		viewHolderType.luggage.setOnClickListener(this);
//		viewHolderType.perfume.setOnClickListener(this);
		return convertView;
	}

	// 获得商品view
	private View getGoodsView(int position, View convertView) {
		// 获得ViewHolder
		ViewHolderGoods viewHolder = null;
		// 边界判断
		if (position - 3 >= 0 && position - 3 < listAll.size()) {

			// 取得当前商品Map
			map = listAll.get(position - 3);
			// 获得EntrySet，并遍历
			Set<Map.Entry<Map<Goods, Users>, List<String>>> entry = map
					.entrySet();
			for (Map.Entry<Map<Goods, Users>, List<String>> en : entry) {
				Map<Goods, Users> goodsUsers = en.getKey();// Map中key（商品用户对象）
				List<String> urls = en.getValue();// Map的value(商品图片url集合)
				Set<Entry<Goods, Users>> entry1 = goodsUsers.entrySet();
				for (Map.Entry<Goods, Users> en1 : entry1) {
					// 取得最里面的map中的goods和users对象
					Goods goods = en1.getKey();
					Users user = en1.getValue();

					if (convertView == null) {
						viewHolder = new ViewHolderGoods();
						convertView = LayoutInflater.from(context).inflate(
								R.layout.goods_item, null);
						viewHolder.userHead = (ImageView) convertView
								.findViewById(R.id.user_head);
						viewHolder.userName = (TextView) convertView
								.findViewById(R.id.user_name);
						viewHolder.goodsName = (TextView) convertView
								.findViewById(R.id.goods_name);
						viewHolder.goodsInfo = (TextView) convertView
								.findViewById(R.id.goods_info);
						viewHolder.goodsPrice = (TextView) convertView
								.findViewById(R.id.goods_price);
						viewHolder.pubTime = (TextView) convertView
								.findViewById(R.id.goods_pubtime);
						viewHolder.userFavorite = (ImageView) convertView
								.findViewById(R.id.user_favorite);
						viewHolder.imagesContainer = (LinearLayout) convertView
								.findViewById(R.id.goods_images_container);
						// 设置值
						viewHolder.userHead
								.setImageResource(R.drawable.header_default);
						viewHolder.userName.setText(user.getName());
						viewHolder.goodsName.setText(goods.getName());
						viewHolder.goodsInfo.setText(goods.getImformation());
						viewHolder.goodsPrice.setText("￥"
								+ goods.getSoldPrice() + "");
						viewHolder.pubTime.setText(sdf.format(goods
								.getPubTime()));
						// 遍历Url集合，装载图片，放入ScrollView中
						for (String url : urls) {
							ImageView imageView = new ImageView(context);
							ImageLoader.getInstance().displayImage(url,
									imageView);
							imageView.setScaleType(ScaleType.CENTER_CROP);
							imageView.setLayoutParams(params3);
							if (position - 3 != 0) {
								imageView.setPadding(5, 0, 0, 0);
							}
							viewHolder.imagesContainer.addView(imageView);
						}
						convertView.setTag(viewHolder);
					} else {
						viewHolder = (ViewHolderGoods) convertView.getTag();
					}
					// 重新赋值，解决控件复用带来的数据重复
					viewHolder.userHead
							.setImageResource(R.drawable.header_default);
					viewHolder.userName.setText(user.getName());
					viewHolder.goodsName.setText(goods.getName());
					viewHolder.goodsInfo.setText(goods.getImformation());
					viewHolder.goodsPrice.setText("￥" + goods.getSoldPrice()
							+ "");
					viewHolder.pubTime.setText(sdf.format(goods.getPubTime()));
					// 移除之前的所有商品图片
					viewHolder.imagesContainer.removeAllViews();
					for (String url : urls) {
						ImageView imageView = new ImageView(context);
						ImageLoader.getInstance().displayImage(url, imageView);
						imageView.setScaleType(ScaleType.CENTER_CROP);
						imageView.setLayoutParams(params3);
						if (position - 3 != 0) {
							imageView.setPadding(5, 0, 0, 0);
						}
						viewHolder.imagesContainer.addView(imageView);
					}
				}

			}
		}
		return convertView;
	}
// 获得集市view
	
	private View getMarketView(View convertView) {
		ViewHolderMarket viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolderMarket();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.market_list, null);

			viewHolder.grandpa = (LinearLayout) convertView
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
					imageView.setLayoutParams(imageMarket);
					imageView.setScaleType(ImageView.ScaleType.FIT_XY);
					String imageURL = market.getUrl();
					ImageLoader.getInstance().displayImage(imageURL, imageView);
					// 集市名称
					TextView tvMarketName = new TextView(context);
					tvMarketName.setGravity(Gravity.CENTER_HORIZONTAL);
					tvMarketName.setLayoutParams(param2);
					tvMarketName.setEllipsize(TextUtils.TruncateAt.END);
					tvMarketName.setText(market.getName());
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
					tvMarketGoodsCount.setGravity(Gravity.CENTER_VERTICAL);
					// 布局间距
					View viewPadding = new View(context);
					viewPadding.setLayoutParams(new LayoutParams(8,
							LayoutParams.MATCH_PARENT));
					viewPadding.setBackgroundColor(Color
							.parseColor("#fff9f8f4"));

					son.addView(ivUserCount);
					son.addView(tvMarketUserCount);
					son.addView(ivGoodsCount);
					son.addView(tvMarketGoodsCount);
					father.addView(imageView);
					father.addView(tvMarketName);
					father.addView(son);
					viewHolder.grandpa.addView(father);
					viewHolder.grandpa.addView(viewPadding);
				}
			}
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolderMarket) convertView.getTag();
		}
		return convertView;
	}

	// 获得广告轮播
	private View getADViewPager(View convertView) {
		ViewHolderAD viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolderAD();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.adviewpager, null);
			viewHolder.mAdView = (ImageCycleView) convertView
					.findViewById(R.id.ad_view);
			// 初始化广告图片数据源
			for (int i = 0; i < imageUrls.length; i++) {
				ADInfo info = new ADInfo();
				info.setUrl(imageUrls[i]);
				info.setContent("top-->" + i);
				infos.add(info);
			}
			// 设置图片数据源
			viewHolder.mAdView.setImageResources(infos, mAdCycleViewListener);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolderAD) convertView.getTag();
		}
		return convertView;
	}

	// 获得商品图片url
	public List<String> getImagesUrl(int goodsId) {
		return null;
	}


	public class ViewHolderType {
		Button ip;
		Button pad;
		Button pc;
		Button ixiaomi;
		Button c;
		Button card;
		Button luggage;
		Button perfume;
	}

	public class ViewHolderAD {
		ImageCycleView mAdView;
	}

	public class ViewHolderMarket {
		LinearLayout grandpa;
	}

	public class ViewHolderGoods {
		TextView goodsName;
		ImageView userHead;
		TextView userName;
		TextView goodsPrice;
		TextView goodsInfo;
		TextView pubTime;
		ImageView userFavorite;
		LinearLayout imagesContainer;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		Log.i("cheshi", "老子在这里");
		switch (v.getId()) {
		case R.id.type_iphone:
		
			String iphone = viewHolderType.ip.getText().toString();
			Log.i("cheshi", iphone);
			intent.putExtra("iphone", iphone);
			intent.putExtra("tag", 1+"");
			intent.setClass(context, ClassificationActivity.class);
			context.startActivity(intent);
			break;
		case R.id.type_pad:
			String padString = viewHolderType.pad.getText().toString();
			intent.putExtra("iphone", padString);
			intent.putExtra("tag", 2+"");
			intent.setClass(context, ClassificationActivity.class);
			context.startActivity(intent);
			break;
		case R.id.type_pc:
			String pcString = viewHolderType.pc.getText().toString();
			intent.putExtra("iphone", pcString);
			intent.putExtra("tag", 3+"");
			intent.setClass(context, ClassificationActivity.class);
			context.startActivity(intent);
			break;
		case R.id.type_ixiaomi:
			String ixiaomiString = viewHolderType.ixiaomi.getText().toString();
			intent.putExtra("iphone", ixiaomiString);
			intent.putExtra("tag", 4+"");
			intent.setClass(context, ClassificationActivity.class);
			context.startActivity(intent);

			break;
		case R.id.type_3c:
			String cString = viewHolderType.c.getText().toString();
			intent.putExtra("iphone", cString);
			intent.putExtra("tag", 5+"");
			intent.setClass(context, ClassificationActivity.class);
			context.startActivity(intent);

			break;
		case R.id.type_card:
			String cardString = viewHolderType.card.getText().toString();
			intent.putExtra("iphone", cardString);
			Log.i("result", "card:" + cardString);
			intent.putExtra("tag", 6+"");
			intent.setClass(context, ClassificationActivity.class);
			context.startActivity(intent);

			break;
		case R.id.type_luggage:
			String luggageString = viewHolderType.luggage.getText().toString();
			intent.putExtra("iphone", luggageString);
			intent.putExtra("tag", 7+"");
			intent.setClass(context, ClassificationActivity.class);
			context.startActivity(intent);

			break;
		case R.id.type_perfume:
			String perfumeString = viewHolderType.perfume.getText().toString();
			intent.putExtra("iphone", perfumeString);
			intent.putExtra("tag", 8+"");
			intent.setClass(context, ClassificationActivity.class);
			context.startActivity(intent);

			break;

		default:
			break;
		}
	}
	
	
}
