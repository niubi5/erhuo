package com.geminno.erhuo.fragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import android.R.integer;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.geminno.erhuo.MyApplication;
import com.geminno.erhuo.R;
import com.geminno.erhuo.adapter.HomePageAdapter;
import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.entity.Users;
import com.geminno.erhuo.utils.MyAdapter;
import com.geminno.erhuo.utils.Url;
import com.geminno.erhuo.utils.ViewHolder;
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

public class CollectionFragment extends BaseFragment {
	private Context context;
	private List<Goods> listCollec = new ArrayList<Goods>();
	private RefreshListView rlvCollec;
	private Handler handler;
	private List<Integer> listColGoodsId = new ArrayList<Integer>();
	private int curPage = 1; // 页数
	private int pageSize = 2;// 一次加载几条
	private List<Map<Map<Goods, Users>, List<String>>> listAll = new ArrayList<Map<Map<Goods, Users>, List<String>>>();
	private List<Map<Map<Goods, Users>, List<String>>> preGoods = new ArrayList<Map<Map<Goods, Users>, List<String>>>();// 记录上一次不满的记录集合
	private HomePageAdapter adapter;
	private boolean isRefresh = false;
	private String listIdJson;

	public CollectionFragment(Context context) {
		super();
		this.context = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_collec, null);

		return view;
	}

	@Override
	protected void initData() {
		// // state:1在售中，2:未发货，3:已发货，4:已完成
		// Goods g1 = new Goods();
		// g1.setName("笔记本1");
		// g1.setSoldPrice(100);
		// g1.setBuyPrice(100);
		// g1.setImformation("惠普(HP)WASD 暗影精灵 15.6英寸游戏笔记本(i5-6300HQ 4G 1TB+128G SSD GTX950M 4G独显 FHD IPS屏 Win10"
		// + "【手机端抢4999】搭载第六代skylake处理器！更快更强！开机飞快，让你一开始你就赢了");
		// g1.setTypeId(1);
		// g1.setMarketId(1);
		// g1.setState(2);
		//
		// Goods g2 = new Goods();
		// g2.setName("笔记本2");
		// g2.setSoldPrice(200);
		// g2.setBuyPrice(200);
		// g2.setImformation("惠普(HP)WASD 暗影精灵 15.6英寸游戏笔记本(i5-6300HQ 4G 1TB+128G SSD GTX950M 4G独显 FHD IPS屏 Win10"
		// + "【手机端抢4999】搭载第六代skylake处理器！更快更强！开机飞快，让你一开始你就赢了");
		// g2.setTypeId(1);
		// g2.setMarketId(1);
		// g2.setState(3);
		//
		// Goods g3 = new Goods();
		// g3.setName("笔记本3");
		// g3.setSoldPrice(300);
		// g3.setBuyPrice(300);
		// g3.setImformation("惠普(HP)WASD 暗影精灵 15.6英寸游戏笔记本(i5-6300HQ 4G 1TB+128G SSD GTX950M 4G独显 FHD IPS屏 Win10"
		// + "【手机端抢4999】搭载第六代skylake处理器！更快更强！开机飞快，让你一开始你就赢了");
		// g3.setTypeId(1);
		// g3.setMarketId(1);
		// g3.setState(4);
		//
		// listCollec.add(g1);
		// listCollec.add(g2);
		// listCollec.add(g3);
		listColGoodsId = MyApplication.getCollection();
		Gson gson = new Gson();
		listIdJson = gson.toJson(listColGoodsId);
		HttpUtils http = new HttpUtils();
		// 设置参数
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("curPage", curPage + "");
		params.addQueryStringParameter("pageSize", pageSize + "");
		params.addQueryStringParameter("collecGoodsId", listIdJson);
		http.configCurrentHttpCacheExpiry(0);
		String headUrl = Url.getUrlHead();
		// 拼接url
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
						listAll.addAll(newGoods);
						adapter = new HomePageAdapter(context, listAll,
								rlvCollec, false);
						rlvCollec.setAdapter(adapter);
					}

				});

		rlvCollec.setAdapter(adapter);

	}

	@Override
	protected void initEvent() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initView() {
		rlvCollec = (RefreshListView) getView().findViewById(R.id.rlv_collec);
		handler = new Handler();
		initData();
		rlvCollec.setOnRefreshCallBack(new OnRefreshCallBack() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// 清空原来的+新的数据
						listAll.clear();
						curPage = 1;
						isRefresh = true;
						initData();
						// 调用刷新完成的方法
						rlvCollec.completeRefresh();
					}
				}, 2000);
			}

			@Override
			public void onPull() {
				// TODO Auto-generated method stub
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						curPage++;
						// 第一次加载获取第二页数据，第二次加载获取第三页数据
						// 获取下一页数据，没有更多，则弹出提示
						addData(); // 调用加载数据方法
						// 调用完成加载
						rlvCollec.completePull();
					}
				}, 2000);
			}
		});
	}
	
	// 加载数据
		private void addData() {
			HttpUtils http = new HttpUtils();
			// 设置参数
			RequestParams params = new RequestParams();
			params.addQueryStringParameter("curPage", curPage + "");
			params.addQueryStringParameter("pageSize", pageSize + "");
			params.addQueryStringParameter("collecGoodsId",listIdJson);
			http.configCurrentHttpCacheExpiry(0);
			String headUrl = Url.getUrlHead();
			// 拼接url
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
									//  页数不变
									curPage--;
									Log.i("erhuo", "curPage ：" + curPage);
								}
								listAll.addAll(newGoods);// 添加新查到的集合
								// 改变数据源
								if (adapter == null) {
									adapter = new HomePageAdapter(context, listAll, rlvCollec, isRefresh);
									rlvCollec.setAdapter(adapter);
								} else {
									adapter.notifyDataSetChanged();
								}
							}

						}
					});
		}

}
