package com.geminno.erhuo.fragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import android.R.integer;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.geminno.erhuo.MyApplication;
import com.geminno.erhuo.OrderDetialActivity;
import com.geminno.erhuo.R;
import com.geminno.erhuo.SendGoodsActivity;
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
	private Map<Integer,String> mapGoodUrl = new HashMap<Integer,String>();
	private RefreshListView rlvSold;

	private List<Map<Goods, List<String>>> ListGoodsPhoto = new ArrayList<Map<Goods, List<String>>>();
	private List<Map<Goods, List<String>>> preListGoodsPhoto = new ArrayList<Map<Goods, List<String>>>();
	private MyAdapter<Map<Goods, List<String>>> adapter;
	private Handler handler;
	private int curPage = 1; // 页数
	private int pageSize = 3;// 一次加载几条
	private Users curUser;
	private TextView tvNoSold;

	public SoldFragment(Context context) {
		this.context = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sold, null);
		tvNoSold = (TextView) view.findViewById(R.id.tv_no_sold);
		curUser = MyApplication.getCurrentUser();
		return view;
	}

	@Override
	protected void initData() {
		// state:1在售中，2:未发货，3:已发货，4:已完成
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
						if (!ListGoodsPhoto.isEmpty()) {
							ListGoodsPhoto.clear();
						}

						List<Map<Goods, List<String>>> newGoodsPhoto = (List<Map<Goods, List<String>>>) gson
								.fromJson(result, type);
						ListGoodsPhoto.addAll(newGoodsPhoto);
						if(ListGoodsPhoto.isEmpty()){
							tvNoSold.setVisibility(View.VISIBLE);
						}else{
							tvNoSold.setVisibility(View.INVISIBLE);
						}
						if (adapter == null) {

							adapter = new MyAdapter<Map<Goods, List<String>>>(
									context, ListGoodsPhoto,
									R.layout.mygoods_sold_item) {
								private Goods goods;
								@Override
								public void convert(final ViewHolder holder,
										Map<Goods, List<String>> t) {
									//Log.i("SoldFragment", "convert");
									List<String> urls = new ArrayList<String>();
									Set<Entry<Goods, List<String>>> keySet = t
											.entrySet();
									for (Map.Entry<Goods, List<String>> e : keySet) {
										//商品
										goods = e.getKey();
										if (listSold.isEmpty()) {
											listSold.add(goods);
										} else {
											boolean isExist = false;
											for (Goods g : listSold) {
												if (g.getId() == goods.getId()) {
													isExist = true;
												}
											}
											if (!isExist) {
												listSold.add(goods);
											}
										}
										//url
										urls = e.getValue();
										if (mapGoodUrl.isEmpty()) {
											mapGoodUrl.put(goods.getId(), urls.get(0));
										}else{
											if(!mapGoodUrl.containsKey(goods.getId())){
												mapGoodUrl.put(goods.getId(), urls.get(0));
											}
										}
									}
									//Log.i("SoldFragment", urls.get(0));
									holder.setVisibility(R.id.iv_sold, 0);
									holder.setImageUrl(R.id.rciv_sold,
											urls.get(0));
									holder.setText(R.id.tv_sold_name,
											goods.getName());
									holder.setText(R.id.tv_sold_price, "¥ "
											+ goods.getSoldPrice());
									holder.setText(R.id.tv_sold_buyprice,
											"原价¥ " + goods.getBuyPrice());
									holder.setText(R.id.tv_sold_brief,
											goods.getImformation());
									holder.setText(R.id.tv_sold_type,
											getTypeName(goods.getTypeId()));
									holder.setText(
											R.id.tv_mysold_market,
											goods.getMarketId() == 0 ? "无集市信息"
													: getMarketName(goods
															.getMarketId()));
									//根据商品的状态设置按钮显示不同的文字
									Log.i("SoldFragment", goods.getName()+":"+goods.getState());
									if (goods.getState() == 4) {
										holder.setVisibility(R.id.iv_sold, 1);
										holder.setButtonText(R.id.btn_sold_edit,
												"已完成");
										holder.setDrawableLeft(
												R.id.btn_sold_edit,
												getResources()
														.getDrawable(
																R.drawable.iconfont_gougou_blue));
									} else if (goods.getState() == 2) {
										holder.setVisibility(R.id.iv_sold, 0);
										holder.setButtonText(R.id.btn_sold_edit,
												"去发货");
										holder.setDrawableLeft(
												R.id.btn_sold_edit,
												getResources()
														.getDrawable(
																R.drawable.iconfont_fahuo));
									} else if (goods.getState() == 3) {
										holder.setVisibility(R.id.iv_sold, 0);
										holder.setButtonText(R.id.btn_sold_edit,
												"运输中");
										holder.setDrawableLeft(
												R.id.btn_sold_edit,
												getResources()
														.getDrawable(
																R.drawable.iconfont_wuliu));
									}
									/**
									 * item点击事件监听
									 * 
									 * 
									 * 
									 * */
									Log.i("holderPosition",
											goods.getName()+":+id:"+goods.getId()+","+holder.getPosition());
									//删除
									holder.setOnClickListener(
											R.id.btn_sold_delete,
											new OnClickListener() {

												@Override
												public void onClick(View v) {
													final Goods clickGood = listSold.get(holder.getPosition());
													Log.i("holderPosition",
															"onClickHolderPosition :"
																	+ (holder.getPosition())+":+id:"+clickGood.getId()+","+clickGood.getName());
													AlertDialog.Builder builder = new Builder(
															context);
													builder.setTitle("删除");
													builder.setMessage("确认删除该商品吗?");
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
									//发货
									holder.setOnClickListener(
											R.id.btn_sold_edit,
											new OnClickListener() {

												@Override
												public void onClick(View v) {
													final Goods clickGood = listSold.get(holder.getPosition());
													Log.i("holderPosition",
															"onClickHolderPosition :"
																	+ holder.getPosition()+clickGood.getName());
													if(clickGood.getState() == 2){
														//跳转至发货界面
														Intent intent = new Intent(context,SendGoodsActivity.class);
														intent.putExtra("good", clickGood);
														intent.putExtra("url", mapGoodUrl.get(clickGood.getId()));
														startActivity(intent);
														
														//Toast.makeText(context, clickGood.getId()+","+clickGood.getName()+"去发货", Toast.LENGTH_SHORT).show();
													}else if (clickGood.getState() == 3){
														//跳转至商品订单详情
														Intent intent = new Intent(context,OrderDetialActivity.class);
														intent.putExtra("good", clickGood);
														intent.putExtra("url", mapGoodUrl.get(clickGood.getId()));
														intent.putExtra("jumpFragment", "soldFragment");
														startActivity(intent);
														
														//Toast.makeText(context, clickGood.getName()+"运输中", Toast.LENGTH_SHORT).show();
													}else if (clickGood.getState() == 4){
														//Toast.makeText(context, clickGood.getName()+"已完成", Toast.LENGTH_SHORT).show();
														Intent intent = new Intent(context,OrderDetialActivity.class);
														intent.putExtra("good", clickGood);
														intent.putExtra("url", mapGoodUrl.get(clickGood.getId()));
														startActivity(intent);
													}
												}
											});
									
									
									
									// ////////////
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
						listSold.clear();
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
									private Goods goods;
									@Override
									public void convert(final ViewHolder holder,
											Map<Goods, List<String>> t) {
										Log.i("SoldFragment", "convert");
//										Goods goods = new Goods();
										List<String> urls = new ArrayList<String>();
										Set<Entry<Goods, List<String>>> keySet = t
												.entrySet();
										for (Map.Entry<Goods, List<String>> e : keySet) {
											goods = e.getKey();
											if (listSold.isEmpty()) {
												listSold.add(goods);
											} else {
												boolean isExist = false;
												for (Goods g : listSold) {
													if (g.getId() == goods.getId()) {
														isExist = true;
													}
												}
												if (!isExist) {
													listSold.add(goods);
												}
											}
											//url
											urls = e.getValue();
											if (mapGoodUrl.isEmpty()) {
												mapGoodUrl.put(goods.getId(), urls.get(0));
											}else{
												if(!mapGoodUrl.containsKey(goods.getId())){
													mapGoodUrl.put(goods.getId(), urls.get(0));
												}
											}
										}
										Log.i("SoldFragment", urls.get(0));
										holder.setVisibility(R.id.iv_sold, 0);
										holder.setImageUrl(R.id.rciv_sold,
												urls.get(0));
										holder.setText(R.id.tv_sold_name,
												goods.getName());
										holder.setText(R.id.tv_sold_price, "¥ "
												+ goods.getSoldPrice());
										holder.setText(R.id.tv_sold_buyprice,
												"原价¥ " + goods.getBuyPrice());
										holder.setText(R.id.tv_sold_brief,
												goods.getImformation());
										holder.setText(R.id.tv_sold_type,
												getTypeName(goods.getTypeId()));
										holder.setText(
												R.id.tv_mysold_market,
												goods.getMarketId() == 0 ? "无集市信息"
														: getMarketName(goods
																.getMarketId()));
										//根据商品的状态设置按钮显示不同的文字
										Log.i("SoldFragment", goods.getName()+":"+goods.getState());
										if (goods.getState() == 4) {
											holder.setVisibility(R.id.iv_sold, 1);
											holder.setButtonText(R.id.btn_sold_edit,
													"已完成");
											holder.setDrawableLeft(
													R.id.btn_sold_edit,
													getResources()
															.getDrawable(
																	R.drawable.iconfont_gougou_blue));
										} else if (goods.getState() == 2) {
											holder.setVisibility(R.id.iv_sold, 0);
											holder.setButtonText(R.id.btn_sold_edit,
													"去发货");
											holder.setDrawableLeft(
													R.id.btn_sold_edit,
													getResources()
															.getDrawable(
																	R.drawable.iconfont_fahuo));
										} else if (goods.getState() == 3) {
											holder.setVisibility(R.id.iv_sold, 0);
											holder.setButtonText(R.id.btn_sold_edit,
													"运输中");
											holder.setDrawableLeft(
													R.id.btn_sold_edit,
													getResources()
															.getDrawable(
																	R.drawable.iconfont_wuliu));
										}
										
										/**
										 * item点击事件监听
										 * 
										 * 
										 * 
										 * */
										Log.i("holderPosition",
												goods.getName()+":+id:"+goods.getId()+","+holder.getPosition());
										//删除
										holder.setOnClickListener(
												R.id.btn_sold_delete,
												new OnClickListener() {

													@Override
													public void onClick(View v) {
														final Goods clickGood = listSold.get(holder.getPosition());
														Log.i("holderPosition",
																"onClickHolderPosition :"
																		+ (holder.getPosition())+":+id:"+clickGood.getId()+","+clickGood.getName());
														AlertDialog.Builder builder = new Builder(
																context);
														builder.setTitle("删除");
														builder.setMessage("确认删除该商品吗?");
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
												R.id.btn_sold_edit,
												new OnClickListener() {

													@Override
													public void onClick(View v) {
														final Goods clickGood = listSold.get(holder.getPosition());
														Log.i("holderPosition",
																"onClickHolderPosition :"
																		+ holder.getPosition()+clickGood.getName());
														if(clickGood.getState() == 2){
															//跳转至发货界面
															Intent intent = new Intent(context,SendGoodsActivity.class);
															intent.putExtra("sendGoodsId", clickGood.getId());
															startActivity(intent);
															
															//Toast.makeText(context, clickGood.getId()+","+clickGood.getName()+"去发货", Toast.LENGTH_SHORT).show();
														}else if (clickGood.getState() == 3){
															//跳转至商品订单详情
															Intent intent = new Intent(context,OrderDetialActivity.class);
															intent.putExtra("good", clickGood);
															intent.putExtra("url", mapGoodUrl.get(clickGood.getId()));
															startActivity(intent);
															
															//Toast.makeText(context, clickGood.getName()+"运输中", Toast.LENGTH_SHORT).show();
														}else if (clickGood.getState() == 4){
															//Toast.makeText(context, clickGood.getName()+"已完成", Toast.LENGTH_SHORT).show();
															Intent intent = new Intent(context,OrderDetialActivity.class);
															intent.putExtra("good", clickGood);
															intent.putExtra("url", mapGoodUrl.get(clickGood.getId()));
															startActivity(intent);
														}
													}
												});
										
										
										
										// ////////////
										
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

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//rlvSold.setAdapter(null);
		initData();
	}
	
	

}
