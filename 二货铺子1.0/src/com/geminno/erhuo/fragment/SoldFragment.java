package com.geminno.erhuo.fragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.geminno.erhuo.MyApplication;
import com.geminno.erhuo.R;
import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.entity.Markets;
import com.geminno.erhuo.entity.Users;
import com.geminno.erhuo.utils.MyAdapter;
import com.geminno.erhuo.utils.Url;
import com.geminno.erhuo.utils.ViewHolder;
import com.geminno.erhuo.view.RefreshListView;
import com.geminno.erhuo.view.RefreshListView.OnRefreshCallBack;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class SoldFragment extends BaseFragment {

	private Context context;
	private List<Goods> listSold = new ArrayList<Goods>();
	private RefreshListView rlvSold;
	
	private List<Map<Goods, List<String>>> ListGoodsPhoto = new ArrayList<Map<Goods, List<String>>>();
	private List<Map<Goods, List<String>>> preListGoodsPhoto = new ArrayList<Map<Goods, List<String>>>();
	private MyAdapter<Map<Goods, List<String>>> adapter;
	private Handler handler;
	private int curPage = 1; // 页数
	private int pageSize = 2;// 一次加载几条
	private Users curUser;

	public SoldFragment(Context context) {
		this.context = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sold, null);
		curUser = MyApplication.getCurrentUser();
		return view;
	}

	@Override
	protected void initData() {
		// state:1在售中，2:未发货，3:已发货，4:已完成
//		Goods g1 = new Goods();
//		g1.setName("笔记本1");
//		g1.setSoldPrice(100);
//		g1.setBuyPrice(100);
//		g1.setImformation("惠普(HP)WASD 暗影精灵 15.6英寸游戏笔记本(i5-6300HQ 4G 1TB+128G SSD GTX950M 4G独显 FHD IPS屏 Win10"
//				+ "【手机端抢4999】搭载第六代skylake处理器！更快更强！开机飞快，让你一开始你就赢了");
//		g1.setTypeId(1);
//		g1.setMarketId(1);
//		g1.setState(2);
//
//		Goods g2 = new Goods();
//		g2.setName("笔记本2");
//		g2.setSoldPrice(200);
//		g2.setBuyPrice(200);
//		g2.setImformation("惠普(HP)WASD 暗影精灵 15.6英寸游戏笔记本(i5-6300HQ 4G 1TB+128G SSD GTX950M 4G独显 FHD IPS屏 Win10"
//				+ "【手机端抢4999】搭载第六代skylake处理器！更快更强！开机飞快，让你一开始你就赢了");
//		g2.setTypeId(1);
//		g2.setMarketId(1);
//		g2.setState(3);
//
//		Goods g3 = new Goods();
//		g3.setName("笔记本3");
//		g3.setSoldPrice(300);
//		g3.setBuyPrice(300);
//		g3.setImformation("惠普(HP)WASD 暗影精灵 15.6英寸游戏笔记本(i5-6300HQ 4G 1TB+128G SSD GTX950M 4G独显 FHD IPS屏 Win10"
//				+ "【手机端抢4999】搭载第六代skylake处理器！更快更强！开机飞快，让你一开始你就赢了");
//		g3.setTypeId(1);
//		g3.setMarketId(1);
//		g3.setState(4);
//
//		listSold.add(g1);
//		listSold.add(g2);
//		listSold.add(g3);
		HttpUtils http = new HttpUtils();
		// 设置参数
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("curPage", curPage + "");
		params.addQueryStringParameter("pageSize", pageSize + "");
		params.addQueryStringParameter("userId", curUser.getId() + "");
		http.configCurrentHttpCacheExpiry(0);
		String headUrl = Url.getHeikkiUrlHead();
		// 拼接url
		String url = headUrl + "/GetMySoldServlet";
		http.send(HttpRequest.HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT)
								.show();
					}

					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						String result = arg0.result;// 获得响应结果
						Gson gson = new Gson();
						Type type = new TypeToken<List<Map<Goods, List<String>>>>() {
						}.getType();
						if (!ListGoodsPhoto.isEmpty()) {
							ListGoodsPhoto.clear();
						}

						List<Map<Goods, List<String>>> newGoodsPhoto = (List<Map<Goods, List<String>>>) gson
								.fromJson(result, type);
						ListGoodsPhoto.addAll(newGoodsPhoto);
						if (adapter == null) {

							adapter = new MyAdapter<Map<Goods, List<String>>>(
									context, ListGoodsPhoto,
									R.layout.mygoods_sold_item) {

								@Override
								public void convert(ViewHolder holder,
										Map<Goods, List<String>> t) {
									Log.i("SoldFragment", "convert");
									Goods goods = new Goods();
									List<String> urls = new ArrayList<String>();
									// for(Map<Goods, List<String>> map :
									// ListGoodsPhoto){
									Set<Entry<Goods, List<String>>> keySet = t
											.entrySet();
									for (Map.Entry<Goods, List<String>> e : keySet) {
										goods = e.getKey();
										urls = e.getValue();
										// }
									}
									Log.i("SoldFragment", urls.get(0));
									holder.setImageUrl(R.id.rciv_sold,
											urls.get(0));
									holder.setText(R.id.tv_sold_name,
											goods.getName());
									holder.setText(R.id.tv_sold_price, "¥ "
											+ goods.getSoldPrice());
									holder.setText(R.id.tv_sold_buyprice,
											"原价¥ " + goods.getBuyPrice());
									// tv.getPaint().setFlags(Paint.
									// STRIKE_THRU_TEXT_FLAG );
									holder.setText(R.id.tv_sold_brief,
											goods.getImformation());
									holder.setText(R.id.tv_sold_type,
											getTypeName(goods.getTypeId()));
									holder.setText(
											R.id.tv_mysold_market,
											goods.getMarketId() == 0 ? "无集市信息"
													: getMarketName(goods
															.getMarketId()));
								}
							};
							rlvSold.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
						Log.i("SoldFragment", result);
					}
				});
	}

	@Override
	protected void initEvent() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initView() {
		rlvSold = (RefreshListView) getView().findViewById(R.id.rlv_sold);
		initData();
		handler = new Handler();
		rlvSold.setOnRefreshCallBack(new OnRefreshCallBack() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						curPage = 1;
						initData();
						rlvSold.completeRefresh();
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
						addData();
						rlvSold.completePull();
					}
				}, 2000);
			}
		});

	}
	
	private void addData() {
		HttpUtils http = new HttpUtils();
		// 设置参数
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("curPage", curPage + "");
		params.addQueryStringParameter("pageSize", pageSize + "");
		params.addQueryStringParameter("userId", curUser.getId() + "");
		http.configCurrentHttpCacheExpiry(0);
		String headUrl = Url.getUrlHead();
		// 拼接url
		String url = headUrl + "/GetMySoldServlet";
		http.send(HttpRequest.HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT)
								.show();
					}

					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						String result = arg0.result;// 获得响应结果
						Gson gson = new Gson();
						Type type = new TypeToken<List<Map<Goods, List<String>>>>() {
						}.getType();
						// 判断preGoods是否有记录，如果有，则将其从总集合中删掉
						if (!preListGoodsPhoto.isEmpty()) {
							ListGoodsPhoto.removeAll(preListGoodsPhoto);
							preListGoodsPhoto.clear();
						}
						List<Map<Goods, List<String>>> newGoodsPhoto = (List<Map<Goods, List<String>>>) gson
								.fromJson(result, type);
						// 判断有没有加载到数据
						if (!newGoodsPhoto.isEmpty()
								&& newGoodsPhoto.size() != 0) {
							if (newGoodsPhoto.size() < pageSize) {
								curPage--;
								preListGoodsPhoto.addAll(newGoodsPhoto);
							}
							// else {
							ListGoodsPhoto.addAll(newGoodsPhoto);
							if (adapter == null) {

								adapter = new MyAdapter<Map<Goods, List<String>>>(
										context, ListGoodsPhoto,
										R.layout.mygoods_sold_item) {

									@Override
									public void convert(ViewHolder holder,
											Map<Goods, List<String>> t) {
										Log.i("SoldFragment", "convert");
										Goods goods = new Goods();
										List<String> urls = new ArrayList<String>();
										// for(Map<Goods, List<String>> map
										// :
										// ListGoodsPhoto){
										Set<Entry<Goods, List<String>>> keySet = t
												.entrySet();
										for (Map.Entry<Goods, List<String>> e : keySet) {
											goods = e.getKey();
											urls = e.getValue();
											// }
										}
										Log.i("SoldFragment", urls.get(0));
										holder.setImageUrl(R.id.rciv_sold,
												urls.get(0));	
										holder.setText(R.id.tv_sold_name,
												goods.getName());
										holder.setText(R.id.tv_sold_price,
												"¥ " + goods.getSoldPrice());
										holder.setText(
												R.id.tv_sold_buyprice,
												"原价¥ " + goods.getBuyPrice());
										// tv.getPaint().setFlags(Paint.
										// STRIKE_THRU_TEXT_FLAG );
										holder.setText(R.id.tv_sold_brief,
												goods.getImformation());
										holder.setText(R.id.tv_sold_type,
												getTypeName(goods.getTypeId()));
										holder.setText(
												R.id.tv_mysold_market,
												goods.getMarketId() == 0 ? "无集市信息"
														: getMarketName(goods
																.getMarketId()));
									}
								};
								rlvSold.setAdapter(adapter);
							} else {
								adapter.notifyDataSetChanged();
							}
							// }
						} else {
							Toast.makeText(context, "没有更多了!",
									Toast.LENGTH_SHORT).show();
						}

						Log.i("SoldFragment", result);
					}
				});
	}
	
	// 获取分类名称
		public String getTypeName(int typeId) {
			String typeName = "其他闲置";
			switch (typeId) {
			case 1:
				typeName = "其他闲置";
				break;
			case 2:
				typeName = "手机电脑";
				break;
			case 3:
				typeName = "相机数码";
				break;
			case 4:
				typeName = "书籍文体";
				break;
			case 5:
				typeName = "服装鞋包";
				break;
			case 6:
				typeName = "美容美体";
				break;
			default:
				break;
			}
			return typeName;
		}

		// 获取集市名称
		public String getMarketName(int marketId) {
			String marketName = "";
			for (Markets m : MyApplication.getMarketsList()) {
				if (m.getId() == marketId) {
					marketName = m.getName();
				}
			}
			return marketName;
		}

}
