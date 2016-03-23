package com.geminno.erhuo.fragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.geminno.erhuo.MainActivity;
import com.geminno.erhuo.MyApplication;
import com.geminno.erhuo.R;
import com.geminno.erhuo.adapter.HomePageAdapter;
import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.entity.Markets;
import com.geminno.erhuo.view.ImageCycleView;
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

@SuppressLint("InflateParams")
public class HomeFragment extends BaseFragment {

	private ImageCycleView mAdView;
	private View convertView;
	private RefreshListView refreshListView;
	// ------------------------
	private List<Markets> listMarkets = null;
	private List<Goods> listGoods = new ArrayList<Goods>();
	private Context context;
	private Handler handler = new Handler();
	private int curPage = 1; // 页数
	private int pageSize = 2;// 一次加载几条
	private String url;
	private HomePageAdapter adapter;
	private List<Goods> preGoods = new ArrayList<Goods>();// 记录上一次不满的记录集合

	public HomeFragment(Context context) {
		this.context = context;
	}

	public ImageCycleView getmAdView() {
		return mAdView;
	}

	public RefreshListView getRefreshListView() {
		return refreshListView;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		convertView = inflater.inflate(R.layout.fragment_main_page, null);
		
		return convertView;
	}

	@Override
	protected void initView() {
		// 获得listview
		refreshListView = (RefreshListView) getView().findViewById(
				R.id.refreshListView);
		Log.i("erhuo", refreshListView.toString());
		initData();
		refreshListView.setOnRefreshCallBack(new OnRefreshCallBack() {

			@Override
			// 刷新
			public void onRefresh() {
				// 过2s 开始执行
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// 清空原来的+新的数据
						listGoods.clear();
						initData();
						// 调用刷新完成的方法
						refreshListView.completeRefresh();
					}
				}, 2000);
			}

			@Override
			// 加载
			public void onPull() {
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						curPage++;
						// 第一次加载获取第二页数据，第二次加载获取第三页数据
						// 获取下一页数据，没有更多，则弹出提示
						addData(); // 调用加载数据方法
						// 调用完成加载
						refreshListView.completePull();
					}
				}, 2000);
			}
		});

	}

	@Override
	protected void initData() {
		// 从服务器获取集市，商品集合
		new Thread() {

			@Override
			public void run() {

				HttpUtils http = new HttpUtils();
				Properties prop = new Properties();
				String head = null;// http: 头部
				try {
					// 从属性文件读取url
					prop.load(MainActivity.class
							.getResourceAsStream("/com/geminno/erhuo/utils/url.properties"));
					head = prop.getProperty("url");
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 拼接url
				url = head + "/ListMarketsServlet";
				// 设置为不缓存，及时获取数据
				http.configCurrentHttpCacheExpiry(0);
				// 获得集市集合
				http.send(HttpRequest.HttpMethod.POST, url,
						new RequestCallBack<String>() {

							@Override
							public void onFailure(HttpException arg0,
									String arg1) {
								Log.i("erhuo", "失败");
								Toast.makeText(context, "网络异常",
										Toast.LENGTH_SHORT).show();

							}

							@SuppressWarnings("unchecked")
							@Override
							public void onSuccess(ResponseInfo<String> arg0) {
								String result = arg0.result;// 获得响应结果
								Gson gson = new Gson();
								Type type = new TypeToken<List<Markets>>() {
								}.getType();
								listMarkets = (List<Markets>) gson.fromJson(
										result, type);
								MyApplication.setMarketsList(listMarkets);
							}

						});

				// 获得商品集合
				url = head + "/ListGoodsServlet";
				HttpUtils http2 = new HttpUtils();
				// 设置不缓存，及时获取数据
				http2.configCurrentHttpCacheExpiry(0);
				RequestParams params = new RequestParams();
				// 设置参数
				params.addQueryStringParameter("curPage", curPage + "");// 第一次加载
				params.addQueryStringParameter("pageSize", pageSize + "");
				http2.send(HttpRequest.HttpMethod.GET, url, params,
						new RequestCallBack<String>() {

							@Override
							public void onFailure(HttpException arg0,
									String arg1) {

							}

							@Override
							public void onSuccess(ResponseInfo<String> arg0) {
								String result = arg0.result;
								Gson gson = new GsonBuilder().setDateFormat(
										"yyyy-MM-dd HH:mm:ss").create();
								Type type = new TypeToken<List<Goods>>() {
								}.getType();
								// 获得刷新后的新数据
								List<Goods> newGoods = gson.fromJson(result,
										type);
								// 添加到集合中
								listGoods.addAll(newGoods);
								// 设置数据源
								if (adapter == null) {
									adapter = new HomePageAdapter(context,
											listMarkets, newGoods);
									refreshListView.setAdapter(adapter);
								} else {
									// 通知改变数据源
									adapter.notifyDataSetChanged();
								}

							}

						});
				super.run();
			}

		}.start();

	}

	// 加载数据
	private void addData() {
		HttpUtils http = new HttpUtils();
		// 设置参数
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("curPage", curPage + "");
		params.addQueryStringParameter("pageSize", pageSize + "");
		http.configCurrentHttpCacheExpiry(0);
		http.send(HttpRequest.HttpMethod.GET, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {

					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						String result = arg0.result;
						Gson gson = new GsonBuilder().setDateFormat(
								"yyyy-MM-dd HH:mm:ss").create();
						Type type = new TypeToken<List<Goods>>() {
						}.getType();
						List<Goods> newGoods = gson.fromJson(result, type);
						// 判断preGoods是否有记录，如果有，则将其从总集合中删掉
						if (!preGoods.isEmpty()) {
							listGoods.removeAll(preGoods);
							// 清空preGoods
							preGoods.clear();
						}
						// 判断有没有加载到数据
						if (newGoods == null || newGoods.isEmpty()) {
							// 没有加载到数据，则弹出提示
							Toast.makeText(context, "没有更多了", Toast.LENGTH_LONG)
									.show();
							// 页数不变，之前++过，故这里要--
							curPage--;
						} else {
							// 有数据，判断是否加载满,即小于pageSize
							if (newGoods != null && newGoods.size() < pageSize) {
								// 页数不变
								curPage--;
								// 记录在未加载满的集合中
								preGoods.addAll(newGoods);
							}
						}
						listGoods.addAll(newGoods);
						// 改变数据源
						if (adapter == null) {
							adapter = new HomePageAdapter(context, listMarkets,
									newGoods);
							refreshListView.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
					}
				});
	}

	@Override
	protected void initEvent() {

	}

}
