package com.geminno.erhuo.market;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.geminno.erhuo.MainActivity;
import com.geminno.erhuo.MyApplication;
import com.geminno.erhuo.R;
import com.geminno.erhuo.adapter.HomePageAdapter;
import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.entity.Markets;
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

public class MarketBaseActivity extends Activity {

	private RefreshListView refreshListView;
	private int curPage = 1;
	private int pageSize = 5;
	private Handler handler;
	private boolean isRefersh = false;
	private List<Markets> listMarkets = MyApplication.getMarketsList();
	private List<Map<Map<Goods, Users>, List<String>>> preGoods = new ArrayList<Map<Map<Goods, Users>, List<String>>>();// 记录上一次不满的记录集合
	private List<Map<Map<Goods, Users>, List<String>>> listAll = new ArrayList<Map<Map<Goods, Users>, List<String>>>();
	private HomePageAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_base_market);
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
	}

	public void back(View v) {
		finish();
	}

	protected void initView(final int marketId, final int marketsIndex,
			final Context context) {
		refreshListView = (RefreshListView) findViewById(R.id.market_refresh_listview);
		initData(marketId, marketsIndex, context);
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
						initData(marketId, marketsIndex, context);
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
						addData(marketId, marketsIndex, context);
						refreshListView.completePull();
					}

				}, 2000);
			}
		});
	}

	private void initData(final int marketId, final int marketsIndex,
			final Context context) {
		// 从服务器获取集市，商品集合
		HttpUtils http = new HttpUtils();
		// String head = null;// http: 头部
		String headUrl = Url.getUrlHead();
		// 拼接url
		String url = headUrl + "/ListMarketsServlet";
		// 设置为不缓存，及时获取数据
		http.configCurrentHttpCacheExpiry(0);
		// 获得集市集合
		http.send(HttpRequest.HttpMethod.POST, url,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT)
								.show();

					}
					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						String result = arg0.result;// 获得响应结果
						Gson gson = new Gson();
						Type type = new TypeToken<List<Markets>>() {
						}.getType();
						listMarkets = (List<Markets>) gson.fromJson(result,
								type);
						MyApplication.setMarketsList(listMarkets);

						HttpUtils http = new HttpUtils();
						String url = Url.getUrlHead() + "/ListGoodsServlet";
						RequestParams params = new RequestParams();
						http.configCurrentHttpCacheExpiry(0);
						params.addQueryStringParameter("marketId", marketId
								+ "");
						params.addQueryStringParameter("curPage", curPage + "");// 第一次加载
						params.addQueryStringParameter("pageSize", pageSize
								+ "");
						http.send(HttpMethod.GET, url, params,
								new RequestCallBack<String>() {

									@Override
									public void onFailure(HttpException arg0,
											String arg1) {

									}

									@Override
									public void onSuccess(
											ResponseInfo<String> arg0) {
										String result = arg0.result;
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
										adapter = new HomePageAdapter(context,
												listAll, refreshListView,
												listMarkets.get(marketsIndex),
												isRefersh);
										refreshListView.setAdapter(adapter);
									}
								});
					}
				});
	}

	private void addData(int marketId, final int marketsIndex,
			final Context context) {
		HttpUtils http = new HttpUtils();
		// 设置参数
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("marketId", marketId + "");
		params.addQueryStringParameter("curPage", curPage + "");
		params.addQueryStringParameter("pageSize", pageSize + "");
		http.configCurrentHttpCacheExpiry(0);
		String headUrl = Url.getUrlHead();
		String url = headUrl + "/ListGoodsServlet";
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
								Log.i("erhuo", "有数据但没加满");
								preGoods.addAll(newGoods);
								// 页数不变
								curPage--;
							}
							listAll.addAll(newGoods);// 添加新查到的集合
							// 改变数据源
							if (adapter == null) {
								adapter = new HomePageAdapter(context, listAll,
										refreshListView, listMarkets
												.get(marketsIndex), isRefersh);
								refreshListView.setAdapter(adapter);
							} else {
								adapter.notifyDataSetChanged();
							}
						}

					}
				});
	}

}
