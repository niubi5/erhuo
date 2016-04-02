package com.geminno.erhuo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;

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
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class MarketBookActivity extends Activity {

	private RefreshListView refreshListView;
	private int curPage = 1;
	private int pageSize = 5;
	private Handler handler;
	private boolean isRefersh = false;
	private List<Map<Map<Goods, Users>, List<String>>> preGoods = new ArrayList<Map<Map<Goods, Users>, List<String>>>();// 记录上一次不满的记录集合
	private List<Map<Map<Goods, Users>, List<String>>> listAll = new ArrayList<Map<Map<Goods, Users>, List<String>>>();
	private Context context;
	private HomePageAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_market_book);
		context = this;
		initView();
	}

	private void initView() {
		refreshListView = (RefreshListView) findViewById(R.id.market_refresh_listview);
		initData();
		handler = new Handler();
		refreshListView.setOnRefreshCallBack(new OnRefreshCallBack() {

			@Override
			public void onRefresh() {
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// 清空原来的+新的数据
						listAll.clear();
						curPage = 1;
						isRefersh = true;
						initData();
						refreshListView.completeRefresh();
					}
				}, 2000);
			}

			@Override
			public void onPull() {

			}
		});
	}

	private void initData() {
		HttpUtils http = new HttpUtils();
		String url = Url.getUrlHead() + "/listMarketGoods";
		RequestParams params = new RequestParams();
		http.configCurrentHttpCacheExpiry(0);
		params.addQueryStringParameter("marketId", 1 + "");
		params.addQueryStringParameter("curPage", curPage + "");// 第一次加载
		params.addQueryStringParameter("pageSize", pageSize + "");
		http.send(HttpMethod.GET, url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {

			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				String result = arg0.result;
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
				adapter = new HomePageAdapter(context, listAll,
						refreshListView, isRefersh);
				refreshListView.setAdapter(adapter);
			}
		});
	}

}
