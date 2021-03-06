package com.geminno.erhuo.fragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.geminno.erhuo.MyApplication;
import com.geminno.erhuo.R;
import com.geminno.erhuo.SearchActivity;
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

@SuppressLint("InflateParams")
public class HomeFragment extends BaseFragment implements OnClickListener {

	private View convertView;
	private RefreshListView refreshListView;
	private List<Markets> listMarkets = null;
	private Context context;
	private Handler handler = new Handler();
	private int curPage = 1; // 页数
	private int pageSize = 5;// 一次加载几条
	private HomePageAdapter adapter;
	private List<Map<Map<Goods, Users>, List<String>>> preGoods = new ArrayList<Map<Map<Goods, Users>, List<String>>>();// 记录上一次不满的记录集合
	private List<Map<Map<Goods, Users>, List<String>>> listAll = new ArrayList<Map<Map<Goods, Users>, List<String>>>();
	private boolean isRefresh = false;
	private ImageView ivhome;
	private ImageView toUP;
	private ImageView relinkIv;
	private FrameLayout relinkContainer;
	private Button relinkBtn;
	private AnimationDrawable relinkAnimation;
	private Toast toast;
	
	public HomeFragment(){}

	public HomeFragment(Context context) {
		this.context = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		convertView = inflater.inflate(R.layout.fragment_main_page, null);
		ivhome = (ImageView) convertView.findViewById(R.id.home_search);
		toUP = (ImageView) convertView.findViewById(R.id.to_up);
		relinkIv = (ImageView) convertView.findViewById(R.id.net_relink);
		relinkIv.setBackgroundResource(R.anim.net_relink);// 设置动画背景
		relinkAnimation = (AnimationDrawable) relinkIv.getBackground();// 获得动画对象
		relinkAnimation.setOneShot(false);// 是否仅启动一次
		relinkContainer = (FrameLayout) convertView.findViewById(R.id.relink_container);
		relinkBtn = (Button) convertView.findViewById(R.id.click_to_relink);
		relinkBtn.setOnClickListener(this);
		toUP.setOnClickListener(this);
		ivhome.setOnClickListener(this);
		return convertView;
	}
	
	@Override
	protected void initView() {
		// 获得listview
		refreshListView = (RefreshListView) getView().findViewById(
				R.id.refreshListView);
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
						listAll.clear();
						curPage = 1;
						isRefresh = true;
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
		HttpUtils http = new HttpUtils();
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
						relinkContainer.setVisibility(View.VISIBLE);
						refreshListView.setVisibility(View.INVISIBLE);
					}

					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						if(relinkContainer.getVisibility() == View.VISIBLE && refreshListView.getVisibility() == View.INVISIBLE){
							refreshListView.setVisibility(View.VISIBLE);// 显示listview
							relinkContainer.setVisibility(View.INVISIBLE);// 隐藏重新加载
						}
						String result = arg0.result;// 获得响应结果
						Gson gson = new Gson();
						Type type = new TypeToken<List<Markets>>() {
						}.getType();
						listMarkets = (List<Markets>) gson.fromJson(result,
								type);
						MyApplication.setMarketsList(listMarkets);
						// 获得商品集合
						String headUrl = Url.getUrlHead();
						String url = headUrl + "/ListGoodsServlet";
						// 发送第二次请求获取商品信息
						HttpUtils http2 = new HttpUtils();
						// 设置不缓存，及时获取数据
						http2.configCurrentHttpCacheExpiry(0);
						RequestParams params = new RequestParams();
						// 设置参数
						params.addQueryStringParameter("curPage", curPage + "");// 第一次加载
						params.addQueryStringParameter("pageSize", pageSize
								+ "");
						http2.send(HttpRequest.HttpMethod.GET, url, params,
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
												listMarkets, listAll,
												refreshListView, isRefresh);
										refreshListView.setAdapter(adapter);
									}

								});

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
							if(toast == null){
								toast = Toast.makeText(context, "没有更多了", Toast.LENGTH_SHORT);
							} else {
								toast.cancel();
								toast = Toast.makeText(context, "没有更多了", Toast.LENGTH_SHORT);
							}
							toast.show();
							// 页数不变，之前++过，故这里要--
							curPage--;
						} else {
							// 有数据，判断是否加载满,即pageSize
							if (newGoods != null && newGoods.size() < pageSize) {
								preGoods.addAll(newGoods);
								// 页数不变
								curPage--;
							}
							listAll.addAll(newGoods);// 添加新查到的集合
							// 改变数据源
							if (adapter == null) {
								adapter = new HomePageAdapter(context,
										listMarkets, listAll, refreshListView,
										isRefresh);
								refreshListView.setAdapter(adapter);
							} else {
								adapter.notifyDataSetChanged();
							}
						}

					}
				});
	}


	@Override
	public void onClick(final View v) {
		switch (v.getId()) {
		case R.id.home_search:
			startActivity(new Intent(context, SearchActivity.class));
			break;
		case R.id.to_up:
			// 移动到顶部
			refreshListView.smoothScrollToPosition(0);
			break;
		case R.id.click_to_relink:
			Log.i("erhuo", "进来了");
			v.setVisibility(View.INVISIBLE);// 隐藏点击重试
			relinkIv.setVisibility(View.VISIBLE); // 显示动画
			relinkAnimation.start(); // 启动动画
			Handler handler = new Handler();
			handler.postDelayed(new Runnable(){

				@Override
				public void run() {
					initView();// 重新加载数据
					if(relinkAnimation.isRunning()){
						// 停止动画
						relinkAnimation.stop();
						relinkIv.setVisibility(View.INVISIBLE);// 隐藏动画
						v.setVisibility(View.VISIBLE);// 显示按钮
					}
				}
				
			}, 3500);
			break;
		default:
			break;
		}
	}

	@Override
	protected void initEvent() {
		
	}

}
