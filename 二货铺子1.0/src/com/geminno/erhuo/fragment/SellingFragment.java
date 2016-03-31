package com.geminno.erhuo.fragment;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.geminno.erhuo.MyApplication;
import com.geminno.erhuo.R;
import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.entity.Markets;
import com.geminno.erhuo.entity.Users;
import com.geminno.erhuo.utils.MyAdapter;
import com.geminno.erhuo.utils.MySdf;
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

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SellingFragment extends BaseFragment {
	private Context context;
	private List<Goods> listSelling = new ArrayList<Goods>();
	private List<Map<Goods, List<String>>> ListGoodsPhoto = new ArrayList<Map<Goods, List<String>>>();
	private List<Map<Goods, List<String>>> preListGoodsPhoto = new ArrayList<Map<Goods, List<String>>>();
	private MyAdapter<Map<Goods, List<String>>> adapter;
	private RefreshListView rlvSelling;
	private Handler handler;
	private int curPage = 1; // 页数
	private int pageSize = 2;// 一次加载几条
	private Users curUser;

	public SellingFragment(Context context) {
		this.context = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_selling, null);
		curUser = MyApplication.getCurrentUser();
		return view;
	}

	@Override
	protected void initData() {

		HttpUtils http = new HttpUtils();
		// 设置参数
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("curPage", curPage + "");
		params.addQueryStringParameter("pageSize", pageSize + "");
		params.addQueryStringParameter("userId", curUser.getId() + "");
		http.configCurrentHttpCacheExpiry(0);
		String headUrl = Url.getUrlHead();
		// 拼接url
		String url = headUrl + "/GetMySellingServlet";
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
									R.layout.mygoods_selling_item) {

								@Override
								public void convert(ViewHolder holder,
										Map<Goods, List<String>> t) {
									Log.i("SellingFragment", "convert");
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
									Log.i("SellingFragment", urls.get(0));
									holder.setImageUrl(R.id.rciv_selling,
											urls.get(0));
									holder.setText(
											R.id.tv_selling_time,
											"剩余展示时间"
													+ getDays(goods
															.getPubTime())
													+ "天");
									holder.setText(R.id.tv_selling_name,
											goods.getName());
									holder.setText(R.id.tv_selling_price, "¥ "
											+ goods.getSoldPrice());
									holder.setText(R.id.tv_selling_buyprice,
											"原价¥ " + goods.getBuyPrice());
									// tv.getPaint().setFlags(Paint.
									// STRIKE_THRU_TEXT_FLAG );
									holder.setText(R.id.tv_selling_brief,
											goods.getImformation());
									holder.setText(R.id.tv_selling_type,
											getTypeName(goods.getTypeId()));
									holder.setText(
											R.id.tv_selling_market,
											goods.getMarketId() == 0 ? "无集市信息"
													: getMarketName(goods
															.getMarketId()));
								}
							};
							rlvSelling.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
						Log.i("SellingFragment", result);
					}
				});
		// Log.i("SellingFragment", listSelling.toString());

	}

	@Override
	protected void initEvent() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initView() {
		rlvSelling = (RefreshListView) getView().findViewById(R.id.rlv_selling);
		initData();
		handler = new Handler();
		rlvSelling.setOnRefreshCallBack(new OnRefreshCallBack() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						curPage = 1;
						initData();
						rlvSelling.completeRefresh();
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
						rlvSelling.completePull();
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
		String url = headUrl + "/GetMySellingServlet";
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
										R.layout.mygoods_selling_item) {

									@Override
									public void convert(ViewHolder holder,
											Map<Goods, List<String>> t) {
										Log.i("SellingFragment", "convert");
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
										Log.i("SellingFragment", urls.get(0));
										holder.setImageUrl(R.id.rciv_selling,
												urls.get(0));
										holder.setText(
												R.id.tv_selling_time,
												"剩余展示时间"
														+ getDays(goods
																.getPubTime())
														+ "天");
										holder.setText(R.id.tv_selling_name,
												goods.getName());
										holder.setText(R.id.tv_selling_price,
												"¥ " + goods.getSoldPrice());
										holder.setText(
												R.id.tv_selling_buyprice,
												"原价¥ " + goods.getBuyPrice());
										// tv.getPaint().setFlags(Paint.
										// STRIKE_THRU_TEXT_FLAG );
										holder.setText(R.id.tv_selling_brief,
												goods.getImformation());
										holder.setText(R.id.tv_selling_type,
												getTypeName(goods.getTypeId()));
										holder.setText(
												R.id.tv_selling_market,
												goods.getMarketId() == 0 ? "无集市信息"
														: getMarketName(goods
																.getMarketId()));
									}
								};
								rlvSelling.setAdapter(adapter);
							} else {
								adapter.notifyDataSetChanged();
							}
							// }
						} else {
							Toast.makeText(context, "没有更多了!",
									Toast.LENGTH_SHORT).show();
						}

						Log.i("SellingFragment", result);
					}
				});
	}

	// 获取剩余天数
	public int getDays(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = sdf.parse(time);
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			long time1 = cal.getTimeInMillis();
			cal.setTime(date);
			long time2 = cal.getTimeInMillis();
			long between_days = (time2 - time1) / (1000 * 3600 * 24);
			return Integer.parseInt(String.valueOf(between_days));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 90;
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
