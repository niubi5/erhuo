package com.geminno.erhuo.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.geminno.erhuo.ClassificationActivity;
import com.geminno.erhuo.GoodsDetialActivity;
import com.geminno.erhuo.MyApplication;
import com.geminno.erhuo.R;
import com.geminno.erhuo.entity.ADInfo;
import com.geminno.erhuo.entity.Collections;
import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.entity.Markets;
import com.geminno.erhuo.entity.Url;
import com.geminno.erhuo.entity.Users;
import com.geminno.erhuo.view.ImageCycleView;
import com.geminno.erhuo.view.ImageCycleView.ImageCycleViewListener;
import com.geminno.erhuo.view.RefreshListView;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author LuoShiHeng
 * @version 创建时间:2016-3-15下午1:48:37
 */
public class HomePageAdapter extends BaseAdapter implements OnClickListener,
		OnItemClickListener {

	private Context context;
	private final int TYPE_AD = 0;// Item的类型
	private final int TYPE_TYPES = 1;
	private final int TYPE_MARKET = 2;
	private final int TYPE_GOODS = 3;
	private ArrayList<ADInfo> infos = new ArrayList<ADInfo>();
	private int typeCount;// item的布局类型的个数
	private List<Markets> listMarkets = new ArrayList<Markets>();// 集市集合
	private List<Map<Map<Goods, Users>, List<String>>> listAll;// 所有商品的所有信息
	private Map<Map<Goods, Users>, List<String>> map = new HashMap<Map<Goods, Users>, List<String>>();// 一个商品的Map集合
	private SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
	private float scale;// 屏幕密度
	private int px1;// 商品图片px
	private int px2;// 集市图片px宽
	private int px3;// 集市图片px高
	private LayoutParams params3;
	private LayoutParams imageMarket;
	private Goods goods;
	private Users user;
	private List<String> urls;
	private ViewHolderType viewHolderType = null;
	private RefreshListView refreshListView;
	private List<Object> userGoodsUrls = new ArrayList<Object>();
	private boolean first = true;
	private boolean second = false;
	private boolean third = false;
	private boolean isRefresh;
	// -------------------
	private ArrayList<Integer> collection = new ArrayList<Integer>();// 收藏按钮的position集合
	private float startX;
	private float stopX;
	private boolean isFavorite = false;// 是否收藏
	private ImageLoader imageLoader = ImageLoader.getInstance();
	// 广告图片
	private String[] imageUrls = {
			"http://img.taodiantong.cn/v55183/infoimg/2013-07/130720115322ky.jpg",
			"http://pic30.nipic.com/20130626/8174275_085522448172_2.jpg",
			"http://10.201.1.6:8080/ads/ad2.jpg",
			"http://pic15.nipic.com/20110722/2912365_092519919000_2.jpg",
			"http://10.201.1.6:8080/ads/ad1.jpg" };
	// 实现接口
	private ImageCycleViewListener mAdCycleViewListener = new ImageCycleViewListener() {

		@Override
		// 广告栏点击事件
		public void onImageClick(ADInfo info, int position, View imageView) {

		}

		@Override
		public void displayImage(String imageURL, ImageView imageView) {
			// 使用ImageLoader对图片进行加装
			imageLoader.displayImage(imageURL, imageView);
		}
	};

	public HomePageAdapter(Context context) {
		this.context = context;
	}

	/**
	 * 
	 * @param context
	 * @param listAll
	 *            所有商品信息
	 * @param refreshListView
	 *            listview
	 * @param isRefresh
	 *            是否是刷新操作
	 */
	public HomePageAdapter(Context context,
			List<Map<Map<Goods, Users>, List<String>>> listAll,
			RefreshListView refreshListView, boolean isRefresh) {
		this.context = context;
		this.listAll = listAll;
		this.refreshListView = refreshListView;
		this.isRefresh = isRefresh;
		typeCount = 1;
		scale = context.getResources().getDisplayMetrics().density;
		px1 = (int) (200 * scale + 0.5f);
		params3 = new LayoutParams(px1, px1);
		refreshListView.setOnItemClickListener(this);
	}

	/**
	 * 
	 * @param context
	 * @param listMarkets
	 *            集市集合
	 * @param listAll
	 *            所有商品信息
	 * @param refreshListView
	 *            listview
	 * @param isRefresh
	 *            是否是刷新操作
	 */
	public HomePageAdapter(final Context context, List<Markets> listMarkets,
			List<Map<Map<Goods, Users>, List<String>>> listAll,
			RefreshListView refreshListView, boolean isRefresh) {
		this.context = context;
		this.listMarkets = listMarkets;
		this.listAll = listAll;
		this.refreshListView = refreshListView;
		this.isRefresh = isRefresh;
		typeCount = 4;
		scale = context.getResources().getDisplayMetrics().density;
		px1 = (int) (200 * scale + 0.5f);
		px2 = (int) (180 * scale + 0.5f);
		px3 = (int) (112.5 * scale + 0.5f);
		params3 = new LayoutParams(px1, px1);
		imageMarket = new LayoutParams(px2, px3);
		refreshListView.setOnItemClickListener(this);
	}

	@Override
	public int getCount() {
		if (typeCount == 4) {
			return 3 + listAll.size();
		} else
			return listAll.size();
	}

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

	// 获得不同item类型的总数
	@Override
	public int getViewTypeCount() {
		return typeCount;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (typeCount == 4) {
			if (position == 0) {
				return getADViewPager(convertView);
			} else if (position == 1) {
				return getTypeItem(convertView);
			} else if (position == 2) {
				return getMarketView(convertView);
			} else
				return getGoodsView(position, convertView, parent);
		} else
			return getGoodsView(position, convertView, parent);

	}

	// 获得类别Item
	private View getTypeItem(View convertView) {
		if (convertView == null) {
			viewHolderType = new ViewHolderType();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.type_item, null);
			viewHolderType.ip = (Button) convertView
					.findViewById(R.id.type_iphone);
			viewHolderType.pad = (Button) convertView
					.findViewById(R.id.type_pad);
			viewHolderType.pc = (Button) convertView.findViewById(R.id.type_pc);
			viewHolderType.ixiaomi = (Button) convertView
					.findViewById(R.id.type_ixiaomi);
			viewHolderType.c = (Button) convertView.findViewById(R.id.type_3c);
			viewHolderType.card = (Button) convertView
					.findViewById(R.id.type_card);
			viewHolderType.luggage = (Button) convertView
					.findViewById(R.id.type_luggage);
			viewHolderType.perfume = (Button) convertView
					.findViewById(R.id.type_perfume);
			convertView.setTag(viewHolderType);
		} else {
			viewHolderType = (ViewHolderType) convertView.getTag();
		}
		viewHolderType.ip.setOnClickListener(this);
		viewHolderType.pad.setOnClickListener(this);
		viewHolderType.pc.setOnClickListener(this);
		viewHolderType.ixiaomi.setOnClickListener(this);
		viewHolderType.c.setOnClickListener(this);
		viewHolderType.card.setOnClickListener(this);
		viewHolderType.luggage.setOnClickListener(this);
		viewHolderType.perfume.setOnClickListener(this);
		return convertView;
	}

	// 获得商品view
	private View getGoodsView(final int position, View convertView,
			ViewGroup parent) {
		if (typeCount == 4) {
			map = listAll.get(position - 3);
		} else {
			map = listAll.get(position);
		}
		// 取得当前商品Map
		// 获得EntrySet，并遍历
		Set<Map.Entry<Map<Goods, Users>, List<String>>> entry = map.entrySet();
		for (Map.Entry<Map<Goods, Users>, List<String>> en : entry) {
			Map<Goods, Users> goodsUsers = en.getKey();// Map中key（商品用户对象）
			urls = en.getValue();
			Set<Entry<Goods, Users>> entry1 = goodsUsers.entrySet();
			for (Map.Entry<Goods, Users> en1 : entry1) {
				// 取得最里面的map中的goods和users对象
				goods = en1.getKey();
				user = en1.getValue();
				if (isRefresh) {
					Log.i("erhuo", "确实清空了");
					userGoodsUrls.clear();// 如果是刷新操作，清空集合
					isRefresh = false;
				}
				// 将数据放入集合，以便商品详情页使用
				if (!userGoodsUrls.contains(user)
						&& !userGoodsUrls.contains(goods)
						&& !userGoodsUrls.contains(urls)) {
					userGoodsUrls.add(user);
					userGoodsUrls.add(goods);
					userGoodsUrls.add(urls);
				}
				ViewHolderGoods viewHolder = null;
				if (convertView == null) {
					viewHolder = new ViewHolderGoods();
					convertView = LayoutInflater.from(context).inflate(
							R.layout.goods_item, parent, false);
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
					viewHolder.homeScrollView = (HorizontalScrollView) convertView
							.findViewById(R.id.home_scrollview);
					viewHolder.imagesContainer = (LinearLayout) convertView
							.findViewById(R.id.goods_images_container);
					convertView.setTag(viewHolder);
				} else {
					viewHolder = (ViewHolderGoods) convertView.getTag();
				}
				viewHolder.userHead.setImageResource(R.drawable.header_default);
				viewHolder.userName.setText(user.getName());
				viewHolder.goodsName.setText(goods.getName());
				viewHolder.goodsInfo.setText(goods.getImformation());
				viewHolder.goodsPrice.setText("￥" + goods.getSoldPrice() + "");
				viewHolder.pubTime.setText(goods.getPubTime().substring(2, 10));
				// --------------------
				viewHolder.imagesContainer
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								onItemClick(refreshListView, v, position + 1,
										position + 1);
							}
						});
				viewHolder.userFavorite.setTag(position);
				if (collection.contains(position)) {
					// 如果集合中有，代表收藏过,显示为收藏状态
					viewHolder.userFavorite.setSelected(true);
				} else {
					viewHolder.userFavorite.setSelected(false);
				}
				viewHolder.userFavorite
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// 如果在集合里面，说明点过，再次点击则取消收藏，并从集合移除
								if (collection.contains(v.getTag())) {
									collectGoods(goods, v, false);// 调用收藏商品方法
								} else {
									// 否则设为选中状态，并加入集合
									collectGoods(goods, v, true);
								}
							}
						});
				// 移除之前的所有商品图片
				viewHolder.imagesContainer.removeAllViews();
				for (int i = 0; i < urls.size(); i++) {
					ImageView imageView = new ImageView(context);
					imageLoader.displayImage(urls.get(i), imageView);
					imageView.setScaleType(ScaleType.CENTER_CROP);
					imageView.setLayoutParams(params3);
					if (i != 0) {
						imageView.setPadding(5, 0, 0, 0);
					}
					viewHolder.imagesContainer.addView(imageView);
				}
			}
		}
		return convertView;
	}

	// 获得集市view
	private View getMarketView(View convertView) {
		ViewHolderMarket viewHolder = null;
		if (convertView == null) {
			// 集市ID
			int ids[] = new int[] { R.id.market_book, R.id.market_iphone,
					R.id.market_baby, R.id.market_bao, R.id.market_nb,
					R.id.market_other };
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
				for (int i = 0; i < listMarkets.size(); i++) {
					// 线性布局
					LinearLayout father = new LinearLayout(context);
					father.setId(ids[i]);
					father.setOnClickListener(this);
					father.setLayoutParams(param1);
					father.setOrientation(LinearLayout.VERTICAL);
					father.setBackgroundColor(Color.WHITE);
					// 集市图片
					ImageView imageView = new ImageView(context);
					imageView.setLayoutParams(imageMarket);
					imageView.setScaleType(ImageView.ScaleType.FIT_XY);
					String imageURL = listMarkets.get(i).getUrl();
					imageLoader.displayImage(imageURL, imageView);
					// 集市名称
					TextView tvMarketName = new TextView(context);
					tvMarketName.setGravity(Gravity.CENTER_HORIZONTAL);
					tvMarketName.setLayoutParams(param2);
					tvMarketName.setEllipsize(TextUtils.TruncateAt.END);
					tvMarketName.setText(listMarkets.get(i).getName());
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
					tvMarketUserCount.setText(listMarkets.get(i).getUserCount()
							+ "");
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
					tvMarketGoodsCount.setText(listMarkets.get(i)
							.getGoodsCount() + "");
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
		HorizontalScrollView homeScrollView;
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
		intent.setClass(context, ClassificationActivity.class);
		switch (v.getId()) {
		case R.id.type_iphone:
			String iphone = viewHolderType.ip.getText().toString();
			intent.putExtra("type", iphone);
			intent.putExtra("tag", 1 + "");
			context.startActivity(intent);
			break;
		case R.id.type_pad:
			String padString = viewHolderType.pad.getText().toString();
			intent.putExtra("type", padString);
			intent.putExtra("tag", 2 + "");
			context.startActivity(intent);
			break;
		case R.id.type_pc:
			String pcString = viewHolderType.pc.getText().toString();
			intent.putExtra("type", pcString);
			intent.putExtra("tag", 3 + "");
			context.startActivity(intent);
			break;
		case R.id.type_ixiaomi:
			String ixiaomiString = viewHolderType.ixiaomi.getText().toString();
			intent.putExtra("type", ixiaomiString);
			intent.putExtra("tag", 4 + "");
			context.startActivity(intent);
			break;
		case R.id.type_3c:
			String cString = viewHolderType.c.getText().toString();
			intent.putExtra("type", cString);
			intent.putExtra("tag", 5 + "");
			context.startActivity(intent);
			break;
		case R.id.type_card:
			String cardString = viewHolderType.card.getText().toString();
			intent.putExtra("type", cardString);
			intent.putExtra("tag", 6 + "");
			context.startActivity(intent);
			break;
		case R.id.type_luggage:
			String luggageString = viewHolderType.luggage.getText().toString();
			intent.putExtra("type", luggageString);
			intent.putExtra("tag", 7 + "");
			context.startActivity(intent);
			break;
		case R.id.type_perfume:
			String perfumeString = viewHolderType.perfume.getText().toString();
			intent.putExtra("type", perfumeString);
			intent.putExtra("tag", 8 + "");
			context.startActivity(intent);
			break;
		// 集市按钮
		case R.id.market_book:
			Toast.makeText(context, "敬请期待", Toast.LENGTH_SHORT).show();
			break;
		case R.id.market_iphone:
			Toast.makeText(context, "敬请期待", Toast.LENGTH_SHORT).show();
			break;
		case R.id.market_baby:
			Toast.makeText(context, "敬请期待", Toast.LENGTH_SHORT).show();
			break;
		case R.id.market_bao:
			Toast.makeText(context, "敬请期待", Toast.LENGTH_SHORT).show();
			break;
		case R.id.market_nb:
			Toast.makeText(context, "敬请期待", Toast.LENGTH_SHORT).show();
			break;
		case R.id.market_other:
			Toast.makeText(context, "敬请期待", Toast.LENGTH_SHORT).show();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// 商品点击事件
		if (!userGoodsUrls.isEmpty()) {
			Intent intent = new Intent(context, GoodsDetialActivity.class);
			Bundle bundle = new Bundle();
			int i = 0;
			if (typeCount == 4) {
				i = (position - 4);
			} else {
				i = position - 1;
			}
			Log.i("erhuo", "当前position：" + position);
			for (int j = i * 3; j < i * 3 + 3; j++) {
				if (first) {
					Users user = (Users) userGoodsUrls.get(j);
					bundle.putSerializable("user", user);
					first = false;
					second = true;
				} else if (second) {
					Goods goods = (Goods) userGoodsUrls.get(j);
					bundle.putSerializable("goods", goods);
					second = false;
					third = true;
				} else if (third) {
					ArrayList<String> urls = (ArrayList<String>) userGoodsUrls
							.get(j);
					bundle.putStringArrayList("urls", urls);
					third = false;
					first = true;
				}
			}
			// 传当前item的position和position集合。判断是否点过赞
			bundle.putInt("position", position);
			bundle.putIntegerArrayList("collection", collection);
			intent.putExtras(bundle);
			context.startActivity(intent);
		}

	}

	/**
	 * 
	 * @param goods
	 *            当前商品对象
	 * @param v
	 *            当前点击的图片控件
	 * @param isFavorite
	 *            是否收藏
	 */
	private void collectGoods(Goods goods, View v, boolean isFavorite) {
		// 当前用户登录过则发请求，否则弹框提示
		Users user = MyApplication.getCurrentUser();
		if (user != null && goods != null) {
			HttpUtils http = new HttpUtils();
			RequestParams params = new RequestParams();
			String urlHead = Url.getUrlHead();
			String url = null;
			if (!isFavorite) {
				// 取消收藏
				v.setSelected(false);// 设为取消状态
				collection.remove(v.getTag());// 从集合中移除
				params.addBodyParameter("userId", user.getId() + "");
				params.addBodyParameter("goodsId", goods.getId() + "");
				url = urlHead + "/DeleteCollectionsServlet";
			} else {
				// 收藏
				v.setSelected(true);// 设为取消状态
				collection.add((Integer) v.getTag());// 并加入集合
				params.addBodyParameter("userId", user.getId() + "");
				params.addBodyParameter("goodsId", goods.getId() + "");
				url = urlHead + "/AddCollectionsServlet";
			}
			http.send(HttpRequest.HttpMethod.POST, url, params,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {

						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {

						}
					});
		} else {
			Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
		}
	}

}
