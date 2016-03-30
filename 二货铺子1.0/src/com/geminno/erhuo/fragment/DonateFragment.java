package com.geminno.erhuo.fragment;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.baidu.wallet.core.restframework.http.HttpMethod;
import com.geminno.erhuo.DonateRequestActivity;
import com.geminno.erhuo.DonationDetailActivity;
import com.geminno.erhuo.MainActivity;
import com.geminno.erhuo.R;
import com.geminno.erhuo.StartActivity;
import com.geminno.erhuo.adapter.CommonAdapter;
import com.geminno.erhuo.entity.Donation;
import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.entity.Users;
import com.geminno.erhuo.utils.DonationViewHolder;
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
import com.lidroid.xutils.view.annotation.event.OnTabChange;

public class DonateFragment extends BaseFragment {

	private RefreshListView mListView;
	/**
	 * 保存Donation
	 */
	private List<Donation> mDatas = new ArrayList<Donation>();
	private List<Donation> preDatas = new ArrayList<Donation>();
	/**
	 * 閫傞厤鍣�
	 */
	private CommonAdapter<Donation> commonAdapter;
	/**
	 * 瑙ｆ瀽甯冨眬鏂囦欢
	 */
	private View view;
	/**
	 * 鍙戝竷鍥剧墖
	 */
	private ImageView ivPublish;
	/**
	 * 鍥炲埌椤堕儴鍥剧墖
	 */
	private ImageView ivToTop;
	/**
	 * 鏍囪鏄惁婊戝姩
	 */
	private boolean scrollFlag = false;
	/**
	 * 璁板綍鏈�鍚庝竴娆℃粦鍔ㄧ殑浣嶇疆
	 */
	private int lastVisiblePosition = 0;

	private Context context;
	private Handler handler = new Handler();
	// 页数
	private int curPage = 1;
	// 每页显示的记录
	private int pageSize = 5;
	// 图片的url
	private List<String> urls;
	
	// 记录上一次不满的记录
	private List<Map<Map<Donation, Users>, List<String>>> preHelps = new ArrayList<Map<Map<Donation, Users>, List<String>>>();
	private List<Map<Map<Donation, Users>, List<String>>> listAll = new ArrayList<Map<Map<Donation, Users>, List<String>>>();
	private Map<Map<Donation, Users>, List<String>> map = new HashMap<Map<Donation, Users>, List<String>>();
	private boolean isRefresh = false;
	private Donation donation;
	private Users user;
	private String imageUrl;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_donate_page, null);
		Log.i("createView", "createView");
		initData();
		initView();
//		mListView.setAdapter();
//		commonAdapter.notifyDataSetChanged();
		initEvent();
		return view;
	}

	/**
	 * 初始化数据源
	 */
	@SuppressLint("SimpleDateFormat")
	@Override
	protected void initData() {
		// 开启线程初始化数据
//		new Thread() {
//
//			@Override
//			public void run() {
				HttpUtils http = new HttpUtils();
				String url = "http://10.201.1.20:8080/secondHandShop/ListHelpsServlet";
				// 设置为不缓存
				http.configCurrentHttpCacheExpiry(0);
				RequestParams params = new RequestParams();
				// 设置参数
				params.addQueryStringParameter("curPage", curPage + "");
				params.addQueryStringParameter("pageSize", pageSize + "");
				http.send(HttpRequest.HttpMethod.GET, url, params,
						new RequestCallBack<String>() {

							@Override
	                        public void onFailure(HttpException arg0,
									String arg1) {

					}

							@Override
	                        public void onSuccess(ResponseInfo<String> arg0) {
								String result = arg0.result;
								Log.i("result", result);
								Gson gson = new GsonBuilder()
										.enableComplexMapKeySerialization()
										.setDateFormat("yyyy-MM-dd HH:mm:ss")
										.create();
								Type type = new TypeToken<List<Map<Map<Donation, Users>, List<String>>>>() {
								}.getType();
								List<Map<Map<Donation, Users>, List<String>>> newDonation = gson
										.fromJson(result, type);
								listAll.addAll(newDonation);
								
								for(int i = 0; i < newDonation.size();i++){
									map = newDonation.get(i);
									
								
								Set<Map.Entry<Map<Donation,Users>, List<String>>> entry = map.entrySet();
								for(Map.Entry<Map<Donation,Users>, List<String>> en:entry){
									Map<Donation,Users> donationUser = en.getKey();
								    urls = en.getValue();
								 
								    Set<Map.Entry<Donation, Users>> dus = donationUser.entrySet();
								    for(Map.Entry<Donation, Users> du : dus){
								    	// 将要显示的数据封装到Donation对象
								    	donation = new Donation();
								    	user = new Users();
								    	donation = du.getKey();
								    	user = du.getValue();
								    	donation.setUserName(user.getName());
								    	donation.setHeadImage(R.drawable.header_default);
								    	donation.setImageUrl(urls.get(0));
								    	donation.setAddressImage(R.drawable.icon_city);
								    	Log.i("donation", donation.toString());
								    	mDatas.add(donation);				    	
								    }
								}
								}
								 
								if(commonAdapter == null){
									commonAdapter = new CommonAdapter<Donation>(getContext(),mDatas,R.layout.doantion_list) {

										@Override
										public void convert(
												DonationViewHolder viewHolder,
												Donation item) {
						                   viewHolder.setImageResource(R.id.iv_head, item.getHeadImage());
						                   viewHolder.setText(R.id.tv_donation_user_name, item.getUserName());
						                   viewHolder.setText(R.id.tv_donation_time, item.getPubTime());
										   viewHolder.setImageBitmap(R.id.iv_poverty_image, item.getImageUrl());
										   viewHolder.setText(R.id.tv_detail, item.getDetail());
										   viewHolder.setImageResource(R.id.iv_donation_address, item.getAddressImage());
										   viewHolder.setText(R.id.tv_donatoin_addresss, item.getAddress());
										}
									};
									mListView.setAdapter(commonAdapter);
								}else{
									commonAdapter.notifyDataSetChanged();
								}
//				
							}
						});
			}

	/**
	 * 设置点击事件
	 */
	@Override
	protected void initEvent() {
		// 点击跳转到发布捐赠页面
		ivPublish.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						DonateRequestActivity.class);
				startActivity(intent);
			}
		});
		// 点击回到捐赠列表顶部
		ivToTop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setPosition(0);
			}
		});
	}

	/**
	 * 初始化控件
	 */
	@Override
	protected void initView() {
		mListView = (RefreshListView) view.findViewById(R.id.lv_donations);
		initData();
		mListView.setOnScrollListener(new OnScrollListener() {

					@Override
			    public void onScrollStateChanged(AbsListView view, int scrollState) {
						switch (scrollState) {
						case OnScrollListener.SCROLL_STATE_IDLE:
							scrollFlag = false;
							if (mListView.getFirstVisiblePosition() == 0) {
								ivToTop.setVisibility(View.GONE);
							}
							if (mListView.getLastVisiblePosition() == (mListView
									.getCount() - 1)) {
								ivToTop.setVisibility(View.VISIBLE);
							}
							break;
						case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
							scrollFlag = true;
							break;
						case OnScrollListener.SCROLL_STATE_FLING:
							scrollFlag = true;
							break;
						}
					}

					/**
					 * firstVisibleItem --> 第一条可见itemId visibleItemCount --> 可见item数量
					 * totalItemCount --> item总数
					 */
					@Override
					public void onScroll(AbsListView view, int firstVisibleItem,
							int visibleItemCount, int totalItemCount) {
						if (scrollFlag) {
							if (firstVisibleItem > lastVisiblePosition) {
								ivToTop.setVisibility(View.VISIBLE);
							} else if (firstVisibleItem < lastVisiblePosition) {
								ivToTop.setVisibility(View.GONE);
							} else {
								return;
							}
							lastVisiblePosition = firstVisibleItem;
						}
					}
				});

			
		
		mListView.setOnRefreshCallBack(new OnRefreshCallBack() {

			// 下拉刷新
			@Override
			public void onRefresh() {
				// 过两秒，开始执行
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// 清空原来的数据，添加新的数据
						mDatas.clear();
						curPage = 1;
						isRefresh = true;
						initData();
						// 完成刷新
						mListView.completeRefresh();
					}
				}, 2000);

			}

			// 加载
			@Override
			public void onPull() {
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						curPage++;
						// 加载当前页的下一页数据
						// 获取下一页的数据
						addData(); 
						// 加载完成
						mListView.completePull();
					}
				}, 2000);

			}

		});
		ivPublish = (ImageView) view.findViewById(R.id.home_search);
		ivToTop = (ImageView) view.findViewById(R.id.iv_to_top);
	}
	
	// 加载
	public void addData(){
		
		HttpUtils http = new HttpUtils();
		String url = "http://10.201.1.20:8080/secondHandShop/ListHelpsServlet";
        // 设置不缓存
		http.configCurrentHttpCacheExpiry(0);
		// 设置参数
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("curPage", curPage + "");
		params.addQueryStringParameter("pageSize", pageSize + "");
		
		http.send(HttpRequest.HttpMethod.GET, url, params,new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				Log.i("request", "请求成功");
				String result = arg0.result;
				Gson gson = new GsonBuilder()
				.enableComplexMapKeySerialization()
				.setDateFormat("yyyy-MM-dd hh-mm-ss")
				.create();
				Log.i("gson", gson.toString());
				Type type = new TypeToken<List<Map<Map<Donation, Users>, List<String>>>>() {
				}.getType();
				// 获取的新数据
				List<Map<Map<Donation, Users>, List<String>>> newDonation = gson.fromJson(result, type);
	            listAll.addAll(newDonation);
	            
	            for(int i = 0; i < newDonation.size() ; i++){
	            	map = newDonation.get(i);
	            
	            
	            Set<Map.Entry<Map<Donation,Users>, List<String>>> entry = map.entrySet();
	            for(Map.Entry<Map<Donation,Users>, List<String>> en : entry){
	            	Map<Donation,Users> donationUser = en.getKey();
	            	urls = en.getValue();
//	            	for(int i = 0 ; i < urls.size();i++){
//	            		Log.i("imageUrl", urls.get(i));
//	            	}
	            	
	            	Set<Map.Entry<Donation, Users>> dus = donationUser.entrySet();
	            	for(Map.Entry<Donation, Users> du: dus){
	            		donation = new Donation();
				    	user = new Users();
	            		donation = du.getKey();
	            		user = du.getValue();
	            		// 将取到的数据封装到Donation对象
	            		donation.setUserName(user.getName());
				    	donation.setHeadImage(R.drawable.header_default);
				    	donation.setImageUrl(urls.get(0));
				    	donation.setAddressImage(R.drawable.icon_city);
				    	mDatas.add(donation);
	            	}
	            }
	            }
				// 判断preHelps是否为空，如果不为空，移除记录
				if(!preDatas.isEmpty()){
					listAll.remove(preDatas);
					preDatas.clear();
				}
				
				// 判断是否加载到了数据
				if(newDonation == null || newDonation.isEmpty()){
					// 页数不变，之前++过
					curPage --;
				}else{
					// 如果有数据但没有加满，页数仍然不变
					if(newDonation != null && newDonation.size() < pageSize){
						preDatas.addAll(mDatas);
						curPage--;
					}
					// 加入新取到的数据
					listAll.addAll(newDonation);
					
				}
				
				if(commonAdapter == null){
					commonAdapter = new CommonAdapter<Donation>(getContext(),mDatas,R.layout.doantion_list) {

						@Override
						public void convert(
								DonationViewHolder viewHolder,
								Donation item) {
		                   viewHolder.setImageResource(R.id.iv_head, item.getHeadImage());
		                   viewHolder.setText(R.id.tv_donation_user_name, item.getUserName());
		                   viewHolder.setText(R.id.tv_donation_time, item.getPubTime());
						   viewHolder.setImageBitmap(R.id.iv_poverty_image, item.getImageUrl());
						   viewHolder.setText(R.id.tv_detail, item.getDetail());
						   viewHolder.setImageResource(R.id.iv_donation_address, item.getAddressImage());
						   viewHolder.setText(R.id.tv_donatoin_addresss, item.getAddress());
						}
					};
					mListView.setAdapter(commonAdapter);
				}else{
					commonAdapter.notifyDataSetChanged();
				}
				
			}
		});
		
	}

	/**
	 * 滚动到指定位置
	 * 
	 * @param position
	 */
	public void setPosition(int position) {
		if (android.os.Build.VERSION.SDK_INT >= 8) {
			mListView.smoothScrollToPosition(position);
		} else {
			mListView.setSelection(position);
		}
	}

}
