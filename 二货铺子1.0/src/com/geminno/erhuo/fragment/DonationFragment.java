package com.geminno.erhuo.fragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.geminno.erhuo.DonateActivity;
import com.geminno.erhuo.DonateDetialActivity;
import com.geminno.erhuo.HelpDetialActivity;
import com.geminno.erhuo.MyApplication;
import com.geminno.erhuo.OrderDetialActivity;
import com.geminno.erhuo.R;
import com.geminno.erhuo.SendGoodsActivity;
import com.geminno.erhuo.entity.Donates;
import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.entity.Helps;
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

public class DonationFragment extends BaseFragment implements OnClickListener {
	private Context context;
	private MyAdapter<Donates> DonatesAdapter;
	private MyAdapter<Map<Helps, List<String>>> HelpsAdapter;
	private RefreshListView rlvDonate;
	private Handler handler;
	private Button btnDonate;
	private Button btnHelp;
	private TextView tvNoDonate;
	private List<Donates> ListDonate = new ArrayList<Donates>();
	private List<Helps> listHelp = new ArrayList<Helps>();
	private Map<Integer, String> mapHelpUrl= new HashMap<Integer, String>(); 

	private List<Donates> ListDonatePhoto = new ArrayList<Donates>();
	private List<Donates> preListDonatePhoto = new ArrayList<Donates>();

	private List<Map<Helps, List<String>>> ListHelpPhoto = new ArrayList<Map<Helps, List<String>>>();
	private List<Map<Helps, List<String>>> preListHelpPhoto = new ArrayList<Map<Helps, List<String>>>();

	private int curPage = 1; // 页数
	private int pageSize = 3;// 一次加载几条
	private Users curUser;
	private String headUrl;
	private boolean isDonate = true;

	public DonationFragment(Context context) {
		super();
		this.context = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_donation, null);
		tvNoDonate = (TextView) view.findViewById(R.id.tv_no_donate);
		curUser = MyApplication.getCurrentUser();
		headUrl = Url.getUrlHead();
		return view;
	}

	@Override
	protected void initData() {
		initEvent();
		donateRequest();

	}

	@Override
	protected void initEvent() {
		btnDonate.setOnClickListener(this);
		btnHelp.setOnClickListener(this);
	}

	@Override
	protected void initView() {
		rlvDonate = (RefreshListView) getView().findViewById(R.id.rlv_donate);
		btnDonate = (Button) getView().findViewById(R.id.btn_donate_donation);
		btnHelp = (Button) getView().findViewById(R.id.btn_donate_help);
		initData();
		handler = new Handler();
		rlvDonate.setOnRefreshCallBack(new OnRefreshCallBack() {

			@Override
			public void onRefresh() {
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						curPage = 1;
						// initData();
						if (isDonate) {
							donateRequest();
						} else {
							helpRequest();
						}

						rlvDonate.completeRefresh();
					}
				}, 2000);
			}

			@Override
			public void onPull() {
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						curPage++;
						// addData();
						if (isDonate) {
							addDonateData();
						} else {
							addHelpData();
						}
						rlvDonate.completePull();
					}
				}, 2000);
			}
		});
	}

	// donate请求
	public void donateRequest() {
		// 捐赠请求
		HttpUtils httpDonate = new HttpUtils();
		httpDonate.configCurrentHttpCacheExpiry(0);
		String urlDonate = headUrl + "/GetMyDonateServlet";
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("curPage", curPage + "");
		params.addQueryStringParameter("pageSize", pageSize + "");
		params.addQueryStringParameter("userId", curUser.getId() + "");
		httpDonate.send(HttpRequest.HttpMethod.POST, urlDonate, params,
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
						Log.i("DonationFragmentURL", "onSuccess");
						String result = arg0.result;// 获得响应结果
						Log.i("DonationFragmentURL", result);
						Gson gson = new Gson();
						Type type = new TypeToken<List<Donates>>() {
						}.getType();
						if (!ListDonatePhoto.isEmpty()) {
							ListDonatePhoto.clear();
						}

						List<Donates> newGoodsPhoto = (List<Donates>) gson
								.fromJson(result, type);
						Log.i("DonationFragmentURL", newGoodsPhoto.size()+"");
						ListDonatePhoto.addAll(newGoodsPhoto);
						if(ListDonatePhoto.isEmpty()){
							tvNoDonate.setText("还没有帮助过别人哦");
							tvNoDonate.setVisibility(View.VISIBLE);
						}else{
							tvNoDonate.setVisibility(View.INVISIBLE);
						}
						if (DonatesAdapter == null) {

							DonatesAdapter = new MyAdapter<Donates>(context,
									ListDonatePhoto,
									R.layout.mygoods_donate_donation_item) {
								private Donates donates;
								@Override
								public void convert(final ViewHolder holder, Donates t) {
									
									if(ListDonate.isEmpty()){
										ListDonate.add(t);
										Log.i("holderPosition", "listSelling size:"+ListDonate.size());
									}else{
										boolean isExist = false;
										for(Donates d : ListDonate){
											if(d.getId() == t.getId()){
												isExist = true;
											}
										}
										if(!isExist){
											ListDonate.add(t);
										}
										Log.i("holderPosition", "listSelling size:"+ListDonate.size());
									}
									holder.setText(
											R.id.tv_donate_donation_name,
											t.getTitle());
									holder.setText(
											R.id.tv_donate_donation_brief,
											t.getBrief());
									holder.setText(
											R.id.tv_donate_donation_wuliu_name,
											t.getLogisticsCom());
									holder.setText(
											R.id.tv_donate_donation_wuliu_num,
											t.getLogisticsNum());
									holder.setText(
											R.id.tv_donate_donation_time,
											t.getDonTime());
									/**
									 * item点击事件监听
									 * 
									 * 
									 * 
									 * */
									Log.i("holderPosition",
											t.getTitle()+":+id:"+t.getId()+","+holder.getPosition());
									//删除
									holder.setOnClickListener(
											R.id.btn_donate_donation_delete,
											new OnClickListener() {

												@Override
												public void onClick(View v) {
													final Donates clickDonates = ListDonate.get(holder.getPosition());
													Log.i("holderPosition",
															"onClickHolderPosition :"
																	+ (holder.getPosition())+":+id:"+clickDonates.getId()+","+clickDonates.getTitle());
													AlertDialog.Builder builder = new Builder(
															context);
													builder.setTitle("删除");
													builder.setMessage("确认删除该记录吗?");
													builder.setPositiveButton(
															"确认",
															new DialogInterface.OnClickListener() {

																@Override
																public void onClick(
																		DialogInterface dialog,
																		int which) {
																	HttpUtils hu = new HttpUtils();
																	RequestParams rp = new RequestParams();
																	rp.addBodyParameter(
																			"deleteDonatesId",
																			clickDonates.getId()+"");
																	hu.configCurrentHttpCacheExpiry(0);
																	String headUrl = Url
																			.getUrlHead();
																	// 拼接url
																	String url = headUrl
																			+ "/DeleteDonateServlet";
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
																					//rlvDonate.setAdapter(null);
																					ListDonatePhoto.clear();
																					ListDonate.clear();
																					//initData();
																					donateRequest();
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
									//查看
									holder.setOnClickListener(
											R.id.btn_donate_donation_edit,
											new OnClickListener() {

												@Override
												public void onClick(View v) {
													final Donates clickDonate = ListDonate.get(holder.getPosition());
													Log.i("holderPosition",
															"onClickHolderPosition :"
																	+ holder.getPosition()+clickDonate.getTitle());
														//跳转至捐赠详情界面
														Intent intent = new Intent(context,DonateDetialActivity.class);
														intent.putExtra("donate", clickDonate);
														startActivity(intent);
												}
											});
									
								}
							};
							rlvDonate.setAdapter(DonatesAdapter);
						} else {
							DonatesAdapter.notifyDataSetChanged();
						}
						Log.i("SoldFragment", result);
					}
				});
	}

	// help请求
	public void helpRequest() {
		// 求助请求
		HttpUtils httpHelp = new HttpUtils();
		// 设置参数
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("curPage", curPage + "");
		params.addQueryStringParameter("pageSize", pageSize + "");
		params.addQueryStringParameter("userId", curUser.getId() + "");
		httpHelp.configCurrentHttpCacheExpiry(0);

		// 拼接url
		String urlHelp = headUrl + "/GetMyHelpServlet";
		Log.i("DonationFragmentURL", "urlHelp:" + urlHelp);
		httpHelp.send(HttpRequest.HttpMethod.POST, urlHelp, params,
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
						Log.i("DonationFragmentURL", "help:"+result);
						Gson gson = new Gson();
						Type type = new TypeToken<List<Map<Helps, List<String>>>>() {
						}.getType();
						if (!ListHelpPhoto.isEmpty()) {
							ListHelpPhoto.clear();
						}

						List<Map<Helps, List<String>>> newGoodsPhoto = (List<Map<Helps, List<String>>>) gson
								.fromJson(result, type);
						ListHelpPhoto.addAll(newGoodsPhoto);
						if(ListDonatePhoto.isEmpty()){
							tvNoDonate.setVisibility(View.VISIBLE);
							tvNoDonate.setText("还没有求助过哦");
						}else{
							tvNoDonate.setVisibility(View.INVISIBLE);
						}
						Log.i("DonationFragmentURL", "ListHelpPhoto:"+ListHelpPhoto.size());
						if (HelpsAdapter == null) {

							HelpsAdapter = new MyAdapter<Map<Helps, List<String>>>(
									context, ListHelpPhoto,
									R.layout.mygoods_donate_help_item) {

								@Override
								public void convert(final ViewHolder holder,
										Map<Helps, List<String>> t) {
									
									Helps help = new Helps();
									List<String> urls = new ArrayList<String>();
									Set<Entry<Helps, List<String>>> keySet = t
											.entrySet();
									for (Map.Entry<Helps, List<String>> e : keySet) {
										help = e.getKey();
										if (listHelp.isEmpty()) {
											listHelp.add(help);
										} else {
											boolean isExist = false;
											for (Helps h : listHelp) {
												if (h.getId() == help.getId()) {
													isExist = true;
												}
											}
											if (!isExist) {
												listHelp.add(help);
											}
										}
										//url
										urls = e.getValue();
										if (mapHelpUrl.isEmpty()) {
											if(urls.isEmpty()){
												mapHelpUrl.put(help.getId(), "null");	
											}else{
												mapHelpUrl.put(help.getId(), urls.get(0));
											}
										}else{
											if(!mapHelpUrl.containsKey(help.getId())){
												if(urls.isEmpty()){
													mapHelpUrl.put(help.getId(), "null");	
												}else{
													mapHelpUrl.put(help.getId(), urls.get(0));
												}
											}
										}
									}
									if (!urls.isEmpty()) {
										holder.setImageUrl(
												R.id.rciv_donate_help,
												urls.get(0));
									}
									holder.setText(R.id.tv_donate_help_name,
											help.getTitle());
									holder.setText(R.id.tv_donate_help_time,
											help.getPubTime());
									holder.setText(R.id.tv_donate_help_brief,
											help.getDetail());
									/**
									 * item点击事件监听
									 * 
									 * 
									 * 
									 * */
									Log.i("holderPosition",
											help.getTitle()+":+id:"+help.getId()+","+holder.getPosition());
									//删除
									holder.setOnClickListener(
											R.id.btn_donate_help_delete,
											new OnClickListener() {

												@Override
												public void onClick(View v) {
													final Helps clickHelp = listHelp.get(holder.getPosition());
													Log.i("holderPosition",
															"onClickHolderPosition :"
																	+ (holder.getPosition())+":+id:"+clickHelp.getId()+","+clickHelp.getTitle());
													AlertDialog.Builder builder = new Builder(
															context);
													builder.setTitle("删除");
													builder.setMessage("确认删除该信息吗?");
													builder.setPositiveButton(
															"确认",
															new DialogInterface.OnClickListener() {

																@Override
																public void onClick(
																		DialogInterface dialog,
																		int which) {
																	HttpUtils hu = new HttpUtils();
																	RequestParams rp = new RequestParams();
																	/////////////////////////////////////////////
																	rp.addBodyParameter(
																			"deleteHelpId",
																			clickHelp.getId()+"");
																	hu.configCurrentHttpCacheExpiry(0);
																	String headUrl = Url
																			.getUrlHead();
																	// 拼接url
																	String url = headUrl
																			+ "/DeleteHelpServlet";
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
											R.id.btn_donate_help_edit,
											new OnClickListener() {

												@Override
												public void onClick(View v) {
													final Helps clickHelp = listHelp.get(holder.getPosition());
													Log.i("holderPosition",
															"onClickHolderPosition :"
																	+ holder.getPosition()+clickHelp.getTitle());
														//跳转至求助详情
														Intent intent = new Intent(context,HelpDetialActivity.class);
														intent.putExtra("help", clickHelp);
														intent.putExtra("url", mapHelpUrl.get(clickHelp.getId()));
														startActivity(intent);
												}
											});
									
									
									
								}
							};
							 rlvDonate.setAdapter(HelpsAdapter);
						} else {
							HelpsAdapter.notifyDataSetChanged();
						}
						Log.i("SoldFragment", result);
					}
				});
	}

	// 添加donate数据
	private void addDonateData() {
		HttpUtils httpDonate = new HttpUtils();
		// 设置参数
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("curPage", curPage + "");
		params.addQueryStringParameter("pageSize", pageSize + "");
		params.addQueryStringParameter("userId", curUser.getId() + "");
		httpDonate.configCurrentHttpCacheExpiry(0);
		// 拼接url
		String urlDonate = headUrl + "/GetMyDonateServlet";
		// 请求捐赠
		httpDonate.send(HttpRequest.HttpMethod.POST, urlDonate, params,
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
						Type type = new TypeToken<List<Donates>>() {
						}.getType();
						// 判断preGoods是否有记录，如果有，则将其从总集合中删掉
						if (!preListDonatePhoto.isEmpty()) {
							ListDonatePhoto.removeAll(preListDonatePhoto);
							preListDonatePhoto.clear();
						}
						List<Donates> newGoodsPhoto = (List<Donates>) gson
								.fromJson(result, type);
						// 判断有没有加载到数据
						if (!newGoodsPhoto.isEmpty()
								&& newGoodsPhoto.size() != 0) {
							if (newGoodsPhoto.size() < pageSize) {
								curPage--;
								preListDonatePhoto.addAll(newGoodsPhoto);
							}
							ListDonatePhoto.addAll(newGoodsPhoto);
							if (DonatesAdapter == null) {

								DonatesAdapter = new MyAdapter<Donates>(
										context, ListDonatePhoto,
										R.layout.mygoods_donate_donation_item) {

									@Override
									public void convert(ViewHolder holder,
											Donates t) {
										holder.setText(
												R.id.tv_donate_donation_name,
												t.getTitle());
										holder.setText(
												R.id.tv_donate_donation_brief,
												t.getBrief());
										holder.setText(
												R.id.tv_donate_donation_wuliu_name,
												t.getLogisticsCom());
										holder.setText(
												R.id.tv_donate_donation_wuliu_num,
												t.getLogisticsNum());
										holder.setText(
												R.id.tv_donate_donation_time,
												t.getDonTime());
									}
								};
							} else {
								DonatesAdapter.notifyDataSetChanged();
							}
						} else {
							Toast.makeText(context, "没有更多了!",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	// 添加help数据
	private void addHelpData() {
		HttpUtils httpHelp = new HttpUtils();
		// 设置参数
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("curPage", curPage + "");
		params.addQueryStringParameter("pageSize", pageSize + "");
		params.addQueryStringParameter("userId", curUser.getId() + "");
		httpHelp.configCurrentHttpCacheExpiry(0);
		// 拼接url
		String urlHelp = headUrl + "/GetMyHelpServlet";
		// 求助请求
		httpHelp.send(HttpRequest.HttpMethod.POST, urlHelp, params,
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
						Type type = new TypeToken<List<Map<Helps, List<String>>>>() {
						}.getType();
						// 判断preGoods是否有记录，如果有，则将其从总集合中删掉
						if (!preListHelpPhoto.isEmpty()) {
							ListHelpPhoto.removeAll(preListHelpPhoto);
							preListHelpPhoto.clear();
						}
						List<Map<Helps, List<String>>> newGoodsPhoto = (List<Map<Helps, List<String>>>) gson
								.fromJson(result, type);
						// 判断有没有加载到数据
						if (!newGoodsPhoto.isEmpty()
								&& newGoodsPhoto.size() != 0) {
							if (newGoodsPhoto.size() < pageSize) {
								curPage--;
								preListHelpPhoto.addAll(newGoodsPhoto);
							}
							// else {
							ListHelpPhoto.addAll(newGoodsPhoto);
							if (HelpsAdapter == null) {

								HelpsAdapter = new MyAdapter<Map<Helps, List<String>>>(
										context, ListHelpPhoto,
										R.layout.mygoods_donate_help_item) {

									@Override
									public void convert(ViewHolder holder,
											Map<Helps, List<String>> t) {
										Helps helps = new Helps();
										List<String> urls = new ArrayList<String>();
										Set<Entry<Helps, List<String>>> keySet = t
												.entrySet();
										for (Map.Entry<Helps, List<String>> e : keySet) {
											helps = e.getKey();
											urls = e.getValue();
											// }
										}
										Log.i("SoldFragment", urls.get(0));
										holder.setImageUrl(
												R.id.rciv_donate_help,
												urls.get(0));
										holder.setText(
												R.id.tv_donate_help_name,
												helps.getTitle());
										holder.setText(
												R.id.tv_donate_help_time,
												helps.getPubTime());
										holder.setText(
												R.id.tv_donate_help_brief,
												helps.getDetail());
									}
								};
								rlvDonate.setAdapter(DonatesAdapter);
							} else {
								HelpsAdapter.notifyDataSetChanged();
							}
						} else {
							Toast.makeText(context, "没有更多了!",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_donate_donation:
			// rlvDonate.setAdapter(DonatesAdapter);
			rlvDonate.setAdapter(null);
			DonatesAdapter = null;
			isDonate = true;
			donateRequest();
			break;
		case R.id.btn_donate_help:
			// rlvDonate.setAdapter(HelpsAdapter);
			rlvDonate.setAdapter(null);
			HelpsAdapter = null;
			isDonate = false;
			helpRequest();
			break;
		default:
			break;
		}

	}

}
