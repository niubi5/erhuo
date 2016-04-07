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

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
	private int pageSize = 3;// 一次加载几条
	private Users curUser;
	private TextView tvNoSell;

	public SellingFragment(Context context) {
		this.context = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_selling, null);
		tvNoSell = (TextView) view.findViewById(R.id.tv_no_sell);
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
						if(ListGoodsPhoto.isEmpty()){
							tvNoSell.setVisibility(View.VISIBLE);
						}else{
							tvNoSell.setVisibility(View.INVISIBLE);
						}
						if (adapter == null) {

							adapter = new MyAdapter<Map<Goods, List<String>>>(
									context, ListGoodsPhoto,
									R.layout.mygoods_selling_item) {

								private Goods goods;

								@Override
								public void convert(final ViewHolder holder,
										Map<Goods, List<String>> t) {
									Gson gs = new Gson();
									Log.i("SellingFragment", "convert");
									// goods = new Goods();
									List<String> urls = new ArrayList<String>();
									// for(Map<Goods, List<String>> map :
									// ListGoodsPhoto){
									Set<Entry<Goods, List<String>>> keySet = t
											.entrySet();
									for (Map.Entry<Goods, List<String>> e : keySet) {
										goods = e.getKey();
										Log.i("holderPosition", "listSelling size:"+listSelling.size());
										if(listSelling.isEmpty()){
											listSelling.add(goods);
											Log.i("holderPosition", "listSelling size:"+listSelling.size());
										}else{
											boolean isExist = false;
											for(Goods g : listSelling){
												if(g.getId() == goods.getId()){
													isExist = true;
												}
											}
											if(!isExist){
												listSelling.add(goods);
											}
											Log.i("holderPosition", "listSelling size:"+listSelling.size());
										}
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
									holder.setText(R.id.tv_selling_brief,
											goods.getImformation());
									holder.setText(R.id.tv_selling_type,
											getTypeName(goods.getTypeId()));
									holder.setText(
											R.id.tv_selling_market,
											goods.getMarketId() == 0 ? "无集市信息"
													: getMarketName(goods
															.getMarketId()));
									Log.i("holderPosition",
											goods.getName()+":+id:"+goods.getId()+","+holder.getPosition());
									//删除
									holder.setOnClickListener(
											R.id.btn_selling_delete,
											new OnClickListener() {

												@Override
												public void onClick(View v) {
													final Goods clickGood = listSelling.get(holder.getPosition());
													Log.i("holderPosition",
															"onClickHolderPosition :"
																	+ (holder.getPosition())+":+id:"+goods.getId()+","+clickGood.getName());
													AlertDialog.Builder builder = new Builder(
															context);
													builder.setTitle("删除");
													builder.setMessage("确认删除吗?");
													builder.setPositiveButton(
															"确认",
															new DialogInterface.OnClickListener() {

																@Override
																public void onClick(
																		DialogInterface dialog,
																		int which) {
																	HttpUtils hu = new HttpUtils();
																	RequestParams rp = new RequestParams();
																	Gson gs = new Gson();

																	clickGood.setState(0);
																	String goodJson = gs
																			.toJson(clickGood);
																	rp.addBodyParameter(
																			"newGoods",
																			goodJson);
																	hu.configCurrentHttpCacheExpiry(0);
																	String headUrl = Url
																			.getUrlHead();
																	// 拼接url
																	String url = headUrl
																			+ "/UpdateGoodServlet";
																	hu.send(HttpRequest.HttpMethod.POST,
																			url,
																			rp,
																			new RequestCallBack<String>() {

																				@Override
																				public void onFailure(
																						HttpException arg0,
																						String arg1) {
																					Toast.makeText(
																							context,
																							"删除失败!",
																							Toast.LENGTH_SHORT)
																							.show();
																				}

																				@Override
																				public void onSuccess(
																						ResponseInfo<String> arg0) {
																					initData();
																					Toast.makeText(
																							context,
																							"删除成功!",
																							Toast.LENGTH_SHORT)
																							.show();
																				}
																			});
																	dialog.dismiss();
																}
															});
													builder.setNegativeButton(
															"取消",
															new DialogInterface.OnClickListener() {

																@Override
																public void onClick(
																		DialogInterface dialog,
																		int which) {
																	dialog.dismiss();
																}
															});
													builder.create().show();
												}
											});
									//标记售出
									holder.setOnClickListener(
											R.id.btn_selling_marksold,
											new OnClickListener() {

												@Override
												public void onClick(View v) {
													final Goods clickGood = listSelling.get(holder.getPosition());
													Log.i("holderPosition",
															"onClickHolderPosition -"
																	+ holder.getPosition()+clickGood.getName());
													AlertDialog.Builder builder = new Builder(
															context);
													builder.setTitle("标记售出");
													builder.setMessage("确认将该商品标记为已售出吗?");
													builder.setPositiveButton(
															"确认",
															new DialogInterface.OnClickListener() {

																@Override
																public void onClick(
																		DialogInterface dialog,
																		int which) {
																	HttpUtils hu = new HttpUtils();
																	RequestParams rp = new RequestParams();
																	Gson gs = new Gson();

																	clickGood.setState(4);
																	String goodJson = gs
																			.toJson(clickGood);
																	rp.addBodyParameter(
																			"newGoods",
																			goodJson);
																	hu.configCurrentHttpCacheExpiry(0);
																	String headUrl = Url
																			.getUrlHead();
																	// 拼接url
																	String url = headUrl
																			+ "/UpdateGoodServlet";
																	hu.send(HttpRequest.HttpMethod.POST,
																			url,
																			rp,
																			new RequestCallBack<String>() {

																				@Override
																				public void onFailure(
																						HttpException arg0,
																						String arg1) {
																					Toast.makeText(
																							context,
																							"网络错误!",
																							Toast.LENGTH_SHORT)
																							.show();
																				}

																				@Override
																				public void onSuccess(
																						ResponseInfo<String> arg0) {
																					initData();
																					Toast.makeText(
																							context,
																							"标记成功!",
																							Toast.LENGTH_SHORT)
																							.show();
																				}
																			});
																	dialog.dismiss();
																}
															});
													builder.setNegativeButton(
															"取消",
															new DialogInterface.OnClickListener() {

																@Override
																public void onClick(
																		DialogInterface dialog,
																		int which) {
																	dialog.dismiss();
																}
															});
													builder.create().show();
												}
											});
									//

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
									public void convert(final ViewHolder holder,
											Map<Goods, List<String>> t) {
										Log.i("SellingFragment", "convert");
										Goods goods = new Goods();
										List<String> urls = new ArrayList<String>();
										Set<Entry<Goods, List<String>>> keySet = t
												.entrySet();
										for (Map.Entry<Goods, List<String>> e : keySet) {
											goods = e.getKey();
											if(listSelling.isEmpty()){
												listSelling.add(goods);											
											}else{
												boolean isExist = false;
												for(Goods g : listSelling){
													if(g.getId() == goods.getId()){
														isExist = true;
													}
												}
												if(!isExist){
													listSelling.add(goods);
												}
											}
											urls = e.getValue();
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
										Log.i("holderPosition",
												goods.getName()+holder.getPosition());
										//删除
										holder.setOnClickListener(
												R.id.btn_selling_delete,
												new OnClickListener() {

													@Override
													public void onClick(View v) {
														final Goods clickGood = listSelling.get(holder.getPosition());
														Log.i("holderPosition",
																"onClickHolderPosition -"
																		+ (holder.getPosition())+clickGood.getName());
														AlertDialog.Builder builder = new Builder(
																context);
														builder.setTitle("删除");
														builder.setMessage("确认删除吗?");
														builder.setPositiveButton(
																"确认",
																new DialogInterface.OnClickListener() {

																	@Override
																	public void onClick(
																			DialogInterface dialog,
																			int which) {
																		HttpUtils hu = new HttpUtils();
																		RequestParams rp = new RequestParams();
																		Gson gs = new Gson();

																		clickGood.setState(0);
																		String goodJson = gs
																				.toJson(clickGood);
																		rp.addBodyParameter(
																				"newGoods",
																				goodJson);
																		hu.configCurrentHttpCacheExpiry(0);
																		String headUrl = Url
																				.getUrlHead();
																		// 拼接url
																		String url = headUrl
																				+ "/UpdateGoodServlet";
																		hu.send(HttpRequest.HttpMethod.POST,
																				url,
																				rp,
																				new RequestCallBack<String>() {

																					@Override
																					public void onFailure(
																							HttpException arg0,
																							String arg1) {
																						Toast.makeText(
																								context,
																								"删除失败!",
																								Toast.LENGTH_SHORT)
																								.show();
																					}

																					@Override
																					public void onSuccess(
																							ResponseInfo<String> arg0) {
																						initData();
																						Toast.makeText(
																								context,
																								"删除成功!",
																								Toast.LENGTH_SHORT)
																								.show();
																					}
																				});
																		dialog.dismiss();
																	}
																});
														builder.setNegativeButton(
																"取消",
																new DialogInterface.OnClickListener() {

																	@Override
																	public void onClick(
																			DialogInterface dialog,
																			int which) {
																		dialog.dismiss();
																	}
																});
														builder.create().show();
													}
												});
										//标记售出
										holder.setOnClickListener(
												R.id.btn_selling_marksold,
												new OnClickListener() {

													@Override
													public void onClick(View v) {
														final Goods clickGood = listSelling.get(holder.getPosition());
														Log.i("holderPosition",
																"onClickHolderPosition -"
																		+ holder.getPosition()+clickGood.getName());
														AlertDialog.Builder builder = new Builder(
																context);
														builder.setTitle("标记售出");
														builder.setMessage("确认将该商品标记为已售出吗?");
														builder.setPositiveButton(
																"确认",
																new DialogInterface.OnClickListener() {

																	@Override
																	public void onClick(
																			DialogInterface dialog,
																			int which) {
																		HttpUtils hu = new HttpUtils();
																		RequestParams rp = new RequestParams();
																		Gson gs = new Gson();

																		clickGood.setState(4);
																		String goodJson = gs
																				.toJson(clickGood);
																		rp.addBodyParameter(
																				"newGoods",
																				goodJson);
																		hu.configCurrentHttpCacheExpiry(0);
																		String headUrl = Url
																				.getUrlHead();
																		// 拼接url
																		String url = headUrl
																				+ "/UpdateGoodServlet";
																		hu.send(HttpRequest.HttpMethod.POST,
																				url,
																				rp,
																				new RequestCallBack<String>() {

																					@Override
																					public void onFailure(
																							HttpException arg0,
																							String arg1) {
																						Toast.makeText(
																								context,
																								"网络错误!",
																								Toast.LENGTH_SHORT)
																								.show();
																					}

																					@Override
																					public void onSuccess(
																							ResponseInfo<String> arg0) {
																						Toast.makeText(
																								context,
																								"标记成功!",
																								Toast.LENGTH_SHORT)
																								.show();
																						initData();
																					}
																				});
																		dialog.dismiss();
																	}
																});
														builder.setNegativeButton(
																"取消",
																new DialogInterface.OnClickListener() {

																	@Override
																	public void onClick(
																			DialogInterface dialog,
																			int which) {
																		dialog.dismiss();
																	}
																});
														builder.create().show();
													}
												});
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
