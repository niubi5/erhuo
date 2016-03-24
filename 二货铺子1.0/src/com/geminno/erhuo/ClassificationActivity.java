package com.geminno.erhuo;


import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.geminno.erhuo.R.id;
import com.geminno.erhuo.adapter.HomePageAdapter;
import com.geminno.erhuo.adapter.HomePageAdapter.ViewHolderGoods;
import com.geminno.erhuo.adapter.HomePageAdapter.ViewHolderMarket;
import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.entity.Markets;
import com.geminno.erhuo.entity.Url;
import com.geminno.erhuo.entity.Users;
import com.geminno.erhuo.utils.MyAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Telephony.Sms.Conversations;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class ClassificationActivity extends Activity implements OnClickListener {

	private Context context;
	private Spinner classificationSpinner = null; // 分类
	private Spinner sortSpinner = null; // 排序
	private Spinner screenSpinner = null; // 筛选
	ArrayAdapter<String> classificationAdapter = null; // 分类适配器
	ArrayAdapter<String> sortAdapter = null; // 排序适配器
	ArrayAdapter<String> screenAdapter = null; // 筛选适配器
	private ListView listView;
	private List<Map<Map<Goods, Users>, List<String>>> listAll;// 所有商品的所有信息
	private Map<Map<Goods, Users>, List<String>> map = new HashMap<Map<Goods, Users>, List<String>>();// 一个商品的Map集合
	private SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
	private LayoutParams params3;
	
	
	//spinner中的分类
	private String[] classif = new String[] { "分类", "全部分类", "手机", "数码", "交通工具",
			"生活文体", "二手书苑", "美妆美饰", "鞋服箱包", "其他", };
	private ClassificationActivity(Context context){
		this.context=context;
	}
	

	private String iphone = null;
	private String tag = null;
	private MyListAdapter adapter;
	private String result=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_classification);
		// 调用setColor()方法,实现沉浸式状态栏
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));

		Intent intent = getIntent();
		iphone = intent.getStringExtra("iphone");
		tag=intent.getStringExtra("tag");
		Log.i("result", "iphone:" + iphone);
		sendRequest();
		setSpinner();
		findViewById(R.id.ib_fenlei).setOnClickListener(this);
		listView = (ListView) findViewById(R.id.listView_class);
		listView.setAdapter(new MyListAdapter(this));
	}


	private void setSpinner() {

		if (iphone.equals("苹果手机")) {
			classif = new String[] { iphone, "全部分类", "平板电脑", "笔记本", "小米",
					"数码3c", "卡劵", "美容美体", "箱包", "其他", };
		} else if (iphone.equals("平板电脑")) {
			classif = new String[] { iphone, "全部分类", "苹果手机", "笔记本", "小米",
					"数码3c", "卡劵", "美容美体", "箱包", "其他", };
		} else if (iphone.equals("笔记本")) {
			classif = new String[] { iphone, "全部分类", "苹果手机", "平板电脑", "小米",
					"数码3c", "卡劵", "美容美体", "箱包", "其他", };
		} else if (iphone.equals("小米")) {
			classif = new String[] { iphone, "全部分类", "苹果手机", "平板电脑", "笔记本",
					"数码3c", "卡劵", "美容美体", "箱包", "其他", };
		} else if (iphone.equals("数码3c")) {
			classif = new String[] { iphone, "全部分类", "苹果手机", "平板电脑", "笔记本",
					"小米", "卡劵", "美容美体", "箱包", "其他", };
		} else if (iphone.equals("卡券")) {
			classif = new String[] { iphone, "全部分类", "苹果手机", "平板电脑", "笔记本",
					"小米", "数码3c", "美容美体", "箱包", "其他", };
		} else if (iphone.equals("箱包")) {
			classif = new String[] { iphone, "全部分类", "苹果手机", "平板电脑", "笔记本",
					"小米", "数码3c", "美容美体", "卡劵", "其他", };
		} else if (iphone.equals("美容美体")) {
			classif = new String[] { iphone, "全部分类", "苹果手机", "平板电脑", "笔记本",
					"小米", "数码3c", "卡劵", "箱包", "其他", };
		}

		String[] sort = new String[] { "排序", "默认排序", "最新发布", "离我最近", "价格最低",
				"价格最高" };
		String[] screen = new String[] { "选择价格", "0~500元", "500~1000元",
				"1000~1500元", "1500~2000元", "2000~2500元", "2500以上" };
		classificationSpinner = (Spinner) findViewById(R.id.spinner1);
		sortSpinner = (Spinner) findViewById(R.id.spinner2);
		screenSpinner = (Spinner) findViewById(R.id.spinner3);

		// 绑定适配器
		classificationAdapter = new ArrayAdapter<String>(this,
				R.layout.spinner_search, classif);
		classificationSpinner.setAdapter(classificationAdapter);
		classificationAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 默认第一个
		classificationSpinner.setSelection(0, true);

		// 绑定适配器
		sortAdapter = new ArrayAdapter<String>(this, R.layout.spinner_search,
				sort);
		sortSpinner.setAdapter(sortAdapter);
		sortAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// 默认第一个
		sortSpinner.setSelection(0, true);

		// 绑定适配器
		screenAdapter = new ArrayAdapter<String>(this, R.layout.spinner_search,
				screen);
		screenSpinner.setAdapter(screenAdapter);
		screenAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 默认第一个
		screenSpinner.setSelection(0, true);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ib_fenlei:
			this.finish();
			break;

		default:
			break;
		}
	}

	// 自定义适配器
	class MyListAdapter extends BaseAdapter {
		List<View> view;

		public MyListAdapter(Context context) {

		}
		public MyListAdapter(Context context,List<Markets> listMarkets,
				List<Map<Map<Goods, Users>, List<String>>> listAll){
			
		}

		public MyListAdapter(List<View> view) {
			this.view = view;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 10;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return view.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View covertView, ViewGroup arg2) {
			ViewHolder viewHolder = null;
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
						if (covertView == null) {
							// 解析布局，只解析一次
							covertView = LayoutInflater.from(
									ClassificationActivity.this).inflate(
									R.layout.classifgtion_1, null);
							// viewHolder.goodsName
							// 找控件
							viewHolder.userName = (TextView) covertView.findViewById(R.id.class1_user_name);
							viewHolder.userFavorite = (ImageView) covertView.findViewById(R.id.class1_user_favorite);
							viewHolder.goodsName = (TextView) covertView.findViewById(R.id.class1_goods_name);
							viewHolder.goodsPrice = (TextView) findViewById(R.id.class1_goods_price);
							viewHolder.goodsInfo = (TextView) findViewById(R.id.class1_goods_info);
							viewHolder.goodsjieshao = (TextView) findViewById(R.id.class1_goods_jieshao);
							viewHolder.pubTime = (TextView) findViewById(R.id.class1_goods_pubtime);
                            viewHolder.userHead=(ImageView) findViewById(R.id.class1_user_head);
							// 控件赋值
							viewHolder.userName.setText(user.getName());
							viewHolder.userFavorite.setImageResource(R.drawable.ic_launcher);
							viewHolder.goodsName.setText(goods.getName());
							viewHolder.goodsPrice.setText("￥"+ goods.getSoldPrice() + "");
							viewHolder.goodsInfo.setText(goods.getImformation());
							viewHolder.pubTime.setText(sdf.format(goods.getPubTime()));
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
							covertView.setTag(viewHolder);
						} else {
							viewHolder = (ViewHolder) covertView.getTag();
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
			return covertView;
		}

		// view的个数

		class ViewHolder {
			TextView goodsName;
			ImageView userHead;
			TextView userName;
			TextView goodsjieshao;
			TextView goodsPrice;
			TextView goodsInfo;
			TextView pubTime;
			ImageView userFavorite;
			LinearLayout imagesContainer;
		}
	}
	
	//发送请求时用到
		public void sendRequest(){
			HttpUtils httpUtils=new HttpUtils();
			RequestParams params=new RequestParams();
			params.addQueryStringParameter("tag",tag);
			String url=Url.getUrlHead()+"ClassificationServlet";
			httpUtils.send(HttpMethod.POST, url, params,new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					result=arg0.result;
					Log.i("erhuo", result);
					Gson gson = new GsonBuilder()
							.enableComplexMapKeySerialization()
							.setDateFormat(
									"yyyy-MM-dd HH:mm:ss")
							.create();
					Type type = new TypeToken<List<Map<Map<Goods, Users>, List<String>>>>() {
					}.getType();
					List<Map<Map<Goods, Users>, List<String>>> newGoods = gson
							.fromJson(result, type);
					listAll.addAll(newGoods);
					if (adapter == null) {
						adapter = new MyListAdapter(context
								);
					} else {
						adapter.notifyDataSetChanged();
					}
					
				}
			});
			
		}


}
