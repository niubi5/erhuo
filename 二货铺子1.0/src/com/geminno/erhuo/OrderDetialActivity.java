package com.geminno.erhuo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.entity.Markets;
import com.geminno.erhuo.entity.Orders;
import com.geminno.erhuo.utils.GetExpressageCom;
import com.geminno.erhuo.utils.MyAdapter;
import com.geminno.erhuo.utils.MySdf;
import com.geminno.erhuo.utils.Url;
import com.geminno.erhuo.utils.ViewHolder;
import com.geminno.erhuo.view.RoundCornerImageView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.JsonToken;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class OrderDetialActivity extends Activity {
	private Goods good;
	private String goodUrl;
	private Orders order;
	private final List<Map<String, String>> listLogis = new ArrayList<Map<String, String>>();
	private MyAdapter<Map<String, String>> myAdapter;

	// 界面控件
	private RoundCornerImageView rciv;
	private TextView tvGoodName;
	private TextView tvGoodPrice;
	private TextView tvGoodOldPrice;
	private TextView tvGoodBrief;
	private TextView tvGoodType;
	private TextView tvGoodMarket;
	private TextView tvLogisCom;
	private TextView tvLogisNum;
	private TextView tvLogisName;
	private TextView tvlogisHao;
	private ListView lvlogisBrief;
	private TextView tvNoLogis;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_order_detial);

		MainActivity.setColor(this, getResources().getColor(R.color.main_red));

		initData();
		initView();
	}

	// 初始化界面参数
	public void initView() {
		// 初始化控件
		rciv = (RoundCornerImageView) findViewById(R.id.rciv_order_detial);
		tvGoodName = (TextView) findViewById(R.id.tv_order_detial_name);
		tvGoodPrice = (TextView) findViewById(R.id.tv_order_detial_price);
		tvGoodOldPrice = (TextView) findViewById(R.id.tv_order_detial_buyprice);
		tvGoodBrief = (TextView) findViewById(R.id.tv_order_detial_brief);
		tvGoodType = (TextView) findViewById(R.id.tv_order_detial_type);
		tvGoodMarket = (TextView) findViewById(R.id.tv_myorder_detial_market);
		tvLogisCom = (TextView) findViewById(R.id.tv_logis_com);
		tvLogisNum = (TextView) findViewById(R.id.tv_logis_num);
		tvLogisName = (TextView) findViewById(R.id.tv_logis_name);
		tvlogisHao = (TextView) findViewById(R.id.tv_logis_hao);
		tvNoLogis = (TextView) findViewById(R.id.tv_no_logis);
		
		//17.41
		
		lvlogisBrief = (ListView) findViewById(R.id.lv_logis_imformatin);

		ImageLoader.getInstance().displayImage(goodUrl, rciv);
		tvGoodName.setText(good.getName());
		tvGoodPrice.setText("¥"+good.getSoldPrice());
		tvGoodOldPrice.setText("原价:"+good.getBuyPrice());
		tvGoodBrief.setText(good.getImformation());
		tvGoodType.setText(getTypeName(good.getTypeId()));
		tvGoodMarket.setText(good.getMarketId() == 0 ? "无集市信息"
				: getMarketName(good.getMarketId()));
	}

	// 初始化数据
	public void initData() {
		good = (Goods) getIntent().getSerializableExtra("good");
		goodUrl = getIntent().getStringExtra("url");

		// 发送请求获取订单详情
		HttpUtils hu = new HttpUtils();
		RequestParams rp = new RequestParams();
		rp.addBodyParameter("goodId", good.getId() + "");
		String headUrl = Url.getUrlHead();
		String url = headUrl + "/GetGoodOrderServlet";
		/**
		 * @heikki 04.01 22:34
		 * */
		hu.send(HttpRequest.HttpMethod.POST, url, rp,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Toast.makeText(OrderDetialActivity.this,
								"网络异常，获取订单信息失败!", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						if (arg0 != null && !"null".equals(arg0.result)) {
							Gson gson = new Gson();
							order = gson.fromJson(arg0.result, Orders.class);
							//
							if (order.getLogisticsCom() != null
									&& order.getLogisticsNum() != null) {
								Log.i("logisDetial", order.getLogisticsCom()
										+ "+" + order.getLogisticsNum());
								tvLogisCom.setText(GetExpressageCom
										.getExpressageName(order
												.getLogisticsCom()));
								tvLogisNum.setText(order.getLogisticsNum());
								getLogisticsDetial(order.getLogisticsCom(),
										order.getLogisticsNum());
								// Collections.reverse(listLogis);
								Log.i("LogisAdapter", listLogis.isEmpty()+"");
							}
						}else{
							tvLogisCom.setVisibility(View.INVISIBLE);
							tvLogisNum.setVisibility(View.INVISIBLE);
							tvlogisHao.setVisibility(View.INVISIBLE);
							tvLogisName.setText("该商品为线下交易，无订单信息!");
						}

					}
				});
	}

	// 聚合数据-常用快递api接口获取物流信息
	public void getLogisticsDetial(String logisCom, String logisNum) {
		HttpUtils hu = new HttpUtils();
		String apiUrl = "http://v.juhe.cn/exp/index";// api接口地址
		String dtype = "json";// 返回数据的格式,json
		String key = "35c780dd2fb7ac07df4f6cf1cf892ba0";// api所需的key
		// com 需要查询的快递公司编号
		// mo 需要查询的订单号
		RequestParams rp = new RequestParams();
		rp.addQueryStringParameter("com", logisCom);
		rp.addQueryStringParameter("no", logisNum);
		rp.addQueryStringParameter("dtype", "json");
		rp.addQueryStringParameter("key", key);
		// String url =
		// apiUrl+"?com="+logisCom+"&no="+logisNum+"&dtype="+dtype+"&key="+key;
		// Log.i("logisDetial",url);
		// 发送请求
		hu.send(HttpRequest.HttpMethod.GET, apiUrl, rp,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Toast.makeText(OrderDetialActivity.this,
								"网络异常，无法查询物流信息！", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						if (arg0 != null && !"null".equals(arg0)) {
							Log.i("logisDetial", "onSuccess:" + arg0.result);
							// 解析获得的json数据
							try {
								// 解析外层数据
								JSONTokener jtOut = new JSONTokener(arg0.result);
								JSONObject jbOut = (JSONObject) jtOut
										.nextValue();
								String result = jbOut.getString("result");
								Log.i("JSONresult", "result:" + result);
								if(!"null".equals(result)){
									
								tvNoLogis.setVisibility(View.INVISIBLE);
								// 解析内层数据
								JSONTokener jtIn = new JSONTokener(result);
								JSONObject jbIn = (JSONObject) jtIn.nextValue();
								String brief = jbIn.getString("list");
								Log.i("JSONresult", "list:" + result);
								// 得到内层物流信息数组
								JSONArray jsonArray = new JSONArray(brief);
								int iSize = jsonArray.length();
								Log.i("JSONresult", "Size:" + iSize);
//								Toast.makeText(OrderDetialActivity.this,
//										iSize + "", Toast.LENGTH_SHORT).show();
								for (int i = 0; i < iSize; i++) {
									JSONObject jsonObj = jsonArray
											.getJSONObject(i);
									Map<String, String> mapBrief = new HashMap<String, String>();
									mapBrief.put("remark",
											jsonObj.getString("remark"));
									mapBrief.put("datetime",
											jsonObj.getString("datetime"));
									mapBrief.put("zone",
											jsonObj.getString("zone"));
									listLogis.add(mapBrief);
									Log.i("LogisAdapter", listLogis.isEmpty()+"加数据");
									Log.i("JSONresult",
											"remark"
													+ jsonObj
															.getString("remark"));
									Log.i("JSONresult",
											"datetime"
													+ jsonObj
															.getString("datetime"));
									Log.i("JSONresult",
											"zone" + jsonObj.getString("zone"));
								}
								
								Collections.reverse(listLogis);
								// listView 设置数据源
								myAdapter = new MyAdapter<Map<String, String>>(
										OrderDetialActivity.this, listLogis,
										R.layout.logis_imformation_item) {
									@Override
									public void convert(ViewHolder holder,
											Map<String, String> t) {
										if (holder.getPosition() == 0) {
											holder.setTextColor(R.id.tv_logis_detail,getResources().getColor(R.color.logis_green));
											holder.setTextColor(R.id.tv_logis_time, getResources().getColor(R.color.logis_green));
											holder.setImageDrawable(R.id.iv_logis_state, getResources().getDrawable(R.drawable.iconfont_yuan_green));
										}else{
											holder.setTextColor(R.id.tv_logis_detail,getResources().getColor(R.color.dimgray));
											holder.setTextColor(R.id.tv_logis_time, getResources().getColor(R.color.dimgray));
											holder.setImageDrawable(R.id.iv_logis_state, getResources().getDrawable(R.drawable.iconfont_yuan_32));
										}
										
										Set<Entry<String, String>> keySet = t
												.entrySet();
										for (Map.Entry<String, String> e : keySet) {
											Log.i("LogisAdapter", e.getKey()+":"+e.getValue());
											if ("remark".equals(e.getKey())) {
												holder.setText(
														R.id.tv_logis_detail,
														e.getValue());
												
											} else if ("datetime".equals(e
													.getKey())) {
												holder.setText(
														R.id.tv_logis_time,
														e.getValue());
											} else {
											}
										}
									}
								};
								lvlogisBrief.setAdapter(myAdapter);
								}else{
									tvNoLogis.setVisibility(View.VISIBLE);
								}	
							} catch (JSONException e) {
								e.printStackTrace();
//								Toast.makeText(OrderDetialActivity.this, "异常",
//										Toast.LENGTH_SHORT).show();
							}
						
						}
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

	// 点击事件
	public void onclick(View v) {
		switch (v.getId()) {
		case R.id.iv_order_detial_return:
			finish();
			break;
		case R.id.btn_order_complete:
			AlertDialog.Builder builder = new Builder(
					OrderDetialActivity.this);
			builder.setTitle("确认收货");
			builder.setMessage("确认已收到宝贝吗?");
			builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					HttpUtils hu = new HttpUtils();
					RequestParams rp = new RequestParams();
					rp.addBodyParameter("goodId",good.getId()+"");
					rp.addBodyParameter("completeTime",MySdf.getDateToString(new Date(System.currentTimeMillis())));
					String headUrl = Url.getUrlHead();
					String url = headUrl + "/UpdateOrderServlet";
					
					hu.send(HttpRequest.HttpMethod.POST, url, rp, new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							Toast.makeText(OrderDetialActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							Toast.makeText(OrderDetialActivity.this, "收货成功!", Toast.LENGTH_SHORT).show();
							finish();
						}
					});
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
			break;
		default:
			break;
		}
	}
}
