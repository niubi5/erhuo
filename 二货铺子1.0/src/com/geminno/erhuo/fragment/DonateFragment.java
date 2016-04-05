package com.geminno.erhuo.fragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import com.geminno.erhuo.DonateRequestActivity;
import com.geminno.erhuo.DonationDetailActivity;
import com.geminno.erhuo.MyApplication;
import com.geminno.erhuo.R;
import com.geminno.erhuo.adapter.CommonAdapter;
import com.geminno.erhuo.entity.Donation;
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

public class DonateFragment extends BaseFragment {

	private RefreshListView mListView;
	/**
	 * 保存Donation
	 */
	private List<Donation> mDatas = new ArrayList<Donation>();
	// 存放没加满的数据
	private List<Donation> preDatas = new ArrayList<Donation>();
	
	
	// 存放每一个Donation对应的imageUrl
	private List<Map<Donation,List<String>>> donationUrls = new ArrayList<Map<Donation,List<String>>>();
	/**
	 * 閫傞厤鍣�
	 */
	private CommonAdapter<Donation> commonAdapter;
	/**
	 * 瑙ｆ瀽甯冨眬鏂囦欢
	 */
	private View view;
	/**
	 * 发布按钮
	 */
	private ImageView ivPublish;
	/**
	 * 回到顶部按钮
	 */
	private ImageView ivToTop;
	/**
	 * 滚动标志位
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
	// 用来存放所有的求助及其对应的捐赠者的名字
	List<Map<Integer,List<String>>> donatorsName = new ArrayList<Map<Integer,List<String>>>();

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_donate_page, null);
		initView();
		initData();         
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
		
		        /**
		         * 发送请求，查询要显示在捐赠列表页面上的信息
		         */
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
								// 分页查询到的记录
								List<Map<Map<Donation, Users>, List<String>>> newDonation = gson
										.fromJson(result, type);
								// 加入总集合
								listAll.addAll(newDonation);
								
								for(int i = 0; i < newDonation.size();i++){
									// 取出每一条记录
									map = newDonation.get(i);
									
								
								Set<Map.Entry<Map<Donation,Users>, List<String>>> entry = map.entrySet();
								for(Map.Entry<Map<Donation,Users>, List<String>> en:entry){
									// 一条记录的Donation,Users
									Map<Donation,Users> donationUser = en.getKey();
									// 一条记录的urls
								    List<String> singleUrls = en.getValue();
		    
								    for(int j = 0; j < singleUrls.size();j++){
								    	Log.i("imageUrls", String.valueOf(singleUrls.size()));
								    }
								 
								    // 取出Donation
								    Set<Map.Entry<Donation, Users>> dus = donationUser.entrySet();
								    for(Map.Entry<Donation, Users> du : dus){
								    	// 将要显示的数据封装到Donation对象
								    	donation = new Donation();
								    	
								    	for(int z = 0; z< singleUrls.size(); z++){
								    		Log.i("donation", donation + singleUrls.get(z));								    		
								    	}
								    	// 取出每条记录的Donation,Users
								    	user = new Users();
								    	donation = du.getKey();
								    	user = du.getValue();
								    	
								    	donation.setUserName(user.getName());
								    	
								    	donation.setHeadImage(R.drawable.header_default);
								    	if(singleUrls!=null&&singleUrls.size()!=0){
								    	// 取第一张图片显示在首页
								    	donation.setImageUrl(singleUrls.get(0));
								    	}
								    	donation.setAddressImage(R.drawable.icon_city);
								    	
								    	
								    	// 将查询到Donatoin与将其对应的url存入到donationUrls
								    	Map<Donation,List<String>> m = new HashMap<Donation,List<String>>();
								    	m.put(donation, singleUrls);
								    	donationUrls.add(m);
                                        
								    	//获得donationId对应names
								    	getName(donation.getId());
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
//	}
//};
							
			}

	/**
	 * 设置点击事件
	 */
	@Override
	protected void initEvent() {
		// 点击跳转到发布捐赠页面
		ivPublish.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				if(user != null){
					Intent intent = new Intent(getActivity(),
							DonateRequestActivity.class);
					startActivity(intent);
				}else{
					Toast.makeText(getActivity(), "请登录", Toast.LENGTH_SHORT).show();
				}
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
//		initData();
		
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
		
		// 跳转到详情页
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 得到每条记录的Donation及其对应的urls
				Donation singleDonation = null;
				ArrayList<String> ls = null;
				if(position>=1){
				Map<Donation,List<String>>  dl = donationUrls.get(position-1);
				
				Set<Map.Entry<Donation,List<String>>> d = dl.entrySet();
				for(Map.Entry<Donation, List<String>> ds: d){
					singleDonation = ds.getKey();
				//	Log.i("UISingle", singleDonation.getAddress());
				    ls = (ArrayList<String>) ds.getValue();					
				}
				}
				int helpId = 0;
				ArrayList<String> names = null;
				Map<Integer,List<String>> il = donatorsName.get(position-1);
				Set<Map.Entry<Integer, List<String>>> s = il.entrySet();
				for(Map.Entry<Integer, List<String>> ils : s){
					helpId = ils.getKey();
					names = (ArrayList<String>) ils.getValue();
				}
				
				// 给详情页传值
				Bundle bundle = new Bundle();
				bundle.putSerializable("SingleDonation", singleDonation);
				bundle.putStringArrayList("urls", ls);
				for(int i = 0;i <ls.size();i++){
					Log.i("singleImage", ls.get(i));
				}
				bundle.putInt("helpId", helpId);
				bundle.putStringArrayList("names", names);
				
				Intent intent = new Intent(getActivity(),DonationDetailActivity.class);
				intent.putExtra("Record", bundle);
				startActivity(intent);
			}
		});
		ivPublish = (ImageView) view.findViewById(R.id.home_search);
		ivToTop = (ImageView) view.findViewById(R.id.iv_to_top);
		user = MyApplication.getCurrentUser();
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
				int count = 0;
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
	            	List<String> singleUrls = en.getValue();
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
				    	if(singleUrls!=null&&singleUrls.size()!=0)
				    	{
				    	donation.setImageUrl(singleUrls.get(0));
				    	}else{
//				    		donation.setImageUrl(R.drawable.delete_white);
				    	}
				    	donation.setAddressImage(R.drawable.icon_city);
				    	
				    	// 将查询到Donatoin与将其对应的url存入到donationUrls
				    	Map<Donation,List<String>> m = new HashMap<Donation,List<String>>();
				    	m.put(donation, singleUrls);
				    	donationUrls.add(m);
                        
				    	//获得donationId对应names
				    	getName(donation.getId());
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
					Toast.makeText(getContext(), "没有更多了", Toast.LENGTH_SHORT).show();
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
	
	/**
	 * 获得对每一个求助发出捐赠的用户名的集合
	 * 
	 * @param helpId
	 */
	public void getName(final int helpId){
		// 设置请求参数
		RequestParams params = new RequestParams();
		params.addBodyParameter("helpId", String.valueOf(helpId));
		
		String url = "http://10.201.1.20:8080/secondHandShop/GetDonatorServlet";
		// 发送请求
		HttpUtils http = new HttpUtils();
		http.send(HttpRequest.HttpMethod.POST, url, params,new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Log.i("requestName", "请求失败");
				
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				Log.i("requestName", "请求成功");
				String result = arg0.result;
				Gson gson = new GsonBuilder().
						enableComplexMapKeySerialization().
						setDateFormat("yyyy-MM-dd HH:mm:ss").
						create();
				Type type = new TypeToken<List<String>>(){}.getType();
				List<String> names = gson.fromJson(result, type);
				Map<Integer,List<String>> is = new HashMap<Integer,List<String>>();
				is.put(helpId, names);
				donatorsName.add(is);
			}
			
		});
	}
	
//	/**
//	 * 判断当前网络连接是否可用
//	 */
//    public static boolean isNetWorkAvailable(Context context){
//		ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//		if(conn!= null){
//			NetworkInfo[] network = conn.getAllNetworkInfo();
//			if(network != null && network.length > 0){
//				for(int i = 0; i < network.length;i++){
//					// 判断当前是否有网络连接
//					if(network[i].getState() == NetworkInfo.State.CONNECTED){
//						return true;
//						
//					}else{
//						Toast.makeText(context, "网络连接失败，请检查您的网络设置", Toast.LENGTH_SHORT).show();
//						return false;
//					}
//				}
//			}
//		}
//		return false;
//	}

}
