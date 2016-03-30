package com.geminno.erhuo;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.geminno.erhuo.adapter.HomePageAdapter;
import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.entity.Users;
import com.geminno.erhuo.utils.Url;
import com.geminno.erhuo.view.RefreshListView;
import com.geminno.erhuo.view.RefreshListView.OnRefreshCallBack;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class ClassificationActivity extends Activity implements OnClickListener {

	private Context context;
	private Spinner classificationSpinner = null; // 分类
	private Spinner sortSpinner = null; // 排序
	ArrayAdapter<String> classificationAdapter = null; // 分类适配器
	ArrayAdapter<String> sortAdapter = null; // 排序适配器
	ArrayAdapter<String> screenAdapter = null; // 筛选适配器
	private RefreshListView refreshListView;
	private SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
	// spinner中的分类
	private String[] classif;
	private String type = null;
	private String tag = null;
	private HomePageAdapter adapter;
	private String result = null;
	private Handler handler = new Handler();
	private int curPage = 1; // 页数
	private int pageSize = 5;// 一次加载几条
	private boolean isRefersh = false;
	private List<Map<Map<Goods, Users>, List<String>>> preGoods = new ArrayList<Map<Map<Goods, Users>, List<String>>>();// 记录上一次不满的记录集合
	private List<Map<Map<Goods, Users>, List<String>>> listAll = new ArrayList<Map<Map<Goods, Users>, List<String>>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_classification);
		// 调用setColor()方法,实现沉浸式状态栏
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
		Intent intent = getIntent();
		// 获得点击分类按钮传过来的tag值
		type = intent.getStringExtra("type");
		tag = intent.getStringExtra("tag");
		context = this;
		// 返回按钮
		findViewById(R.id.ib_fenlei).setOnClickListener(this);
		initView();
	}

	private void initView() {
		setSpinner();
		refreshListView = (RefreshListView) findViewById(R.id.listView_class);
		initData();
		refreshListView.setOnRefreshCallBack(new OnRefreshCallBack() {

			@Override
			public void onRefresh() {
				// 过2s 开始执行
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// 清空原来的+新的数据
						listAll.clear();
						curPage = 1;
						isRefersh = true;// 是刷新操作
						initData();
						// 调用刷新完成的方法
						refreshListView.completeRefresh();
					}
				}, 2000);

			}

			@Override
			public void onPull() {
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						curPage++;
						addData();
						refreshListView.completePull();
					}

				}, 2000);

			}
		});
	}

	private void setSpinner() {
		classif = new String[] { type, "苹果手机", "平板电脑", "笔记本", "小米", "数码3c",
				"书籍文体", "美容美体", "服装箱包", "其他", };
		String[] sort = new String[] { "排序", "默认排序", "最新发布", "离我最近", "价格最低",
				"价格最高" };
		classificationSpinner = (Spinner) findViewById(R.id.spinner1);
		sortSpinner = (Spinner) findViewById(R.id.spinner2);
		classificationSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						// 点第一个不做处理
						Log.i("erhuo", "item点击事件");
						if (position != 0) {
							tag = position + "";
							listAll.clear();
							isRefersh = true;
							curPage = 1;
							initData();
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}
				});
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
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_fenlei:
			// 返回
			finish();
			break;
		default:
			break;
		}
	}

	// 初始化数据源
	public void initData() {
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("tag", tag);// 分类标记
		params.addQueryStringParameter("curPage", curPage + "");
		params.addQueryStringParameter("pageSize", pageSize + "");
		String url = Url.getUrlHead() + "/ClassificationServlet";
		httpUtils.send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {

					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						result = arg0.result;
						Gson gson = new GsonBuilder()
								.enableComplexMapKeySerialization()
								.setDateFormat("yyyy-MM-dd HH:mm:ss").create();
						Type type = new TypeToken<List<Map<Map<Goods, Users>, List<String>>>>() {
						}.getType();
						List<Map<Map<Goods, Users>, List<String>>> newGoods = gson
								.fromJson(result, type);
						listAll.addAll(newGoods);
						adapter = new HomePageAdapter(context, listAll,
								refreshListView, isRefersh);
						refreshListView.setAdapter(adapter);

					}
				});

	}

	// 加载数据
	private void addData() {
		HttpUtils http = new HttpUtils();
		// 设置参数
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("tag", tag);
		params.addQueryStringParameter("curPage", curPage + "");
		params.addQueryStringParameter("pageSize", pageSize + "");
		http.configCurrentHttpCacheExpiry(0);
		String headUrl = Url.getUrlHead();
		// 拼接url
		String url = headUrl + "/ClassificationServlet";
		http.send(HttpRequest.HttpMethod.GET, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {

					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						String result = arg0.result;
						Gson gson = new GsonBuilder()
								.enableComplexMapKeySerialization()
								.setDateFormat("yyyy-MM-dd HH:mm:ss").create();
						Type type = new TypeToken<List<Map<Map<Goods, Users>, List<String>>>>() {
						}.getType();
						List<Map<Map<Goods, Users>, List<String>>> newGoods = gson
								.fromJson(result, type);
						// 判断preGoods是否有记录，如果有，则将其从总集合中删掉
						if (!preGoods.isEmpty()) {
							listAll.removeAll(preGoods);
							// 清空preGoods
							preGoods.clear();
						}
						// 判断有没有加载到数据
						if (newGoods == null || newGoods.isEmpty()) {
							// 没有加载到数据，则弹出提示
							Toast.makeText(context, "没有更多了", Toast.LENGTH_SHORT)
									.show();
							// 页数不变，之前++过，故这里要--
							curPage--;
						} else {
							// 有数据，判断是否加载满,即pageSize
							if (newGoods != null && newGoods.size() < pageSize) {
								// 页数不变
								curPage--;
								// 记录在未加载满的集合中
								preGoods.addAll(newGoods);
							}
							listAll.addAll(newGoods);// 添加新查到的集合
							// 改变数据源
							if (adapter == null) {
								adapter = new HomePageAdapter(context, listAll,
										refreshListView, isRefersh);
								refreshListView.setAdapter(adapter);
							} else {
								Log.i("erhuo", "通知数据源改变");
								adapter.notifyDataSetChanged();
							}
						}
					}
				});
	}

}
