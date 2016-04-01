package com.geminno.erhuo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.geminno.erhuo.adapter.CommentListAdapter;
import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.entity.Remark;
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
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class CommentActivity extends Activity {

	private RefreshListView refreshListView;
	private Handler handler;
	private Users currentUser = MyApplication.getCurrentUser();
	private LinearLayout noCommentContainer;
	private Context context;
	private List<Map<Remark, Users>> listRemarkUsers = new ArrayList<Map<Remark, Users>>();
	private List<Map<Remark, Users>> preRemarks = new ArrayList<Map<Remark, Users>>();
	private List<Map<Map<Goods, Users>, List<String>>> listAll = new ArrayList<Map<Map<Goods, Users>, List<String>>>();
	private int curPage = 1;// 第一页
	private int pageSize = 5;// 每页几条
	private CommentListAdapter adapter = null;
	private boolean isRefresh = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_comment);
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
		context = this;
		initView();

	}

	private void initView() {
		refreshListView = (RefreshListView) findViewById(R.id.comment_refresh_listview);
		noCommentContainer = (LinearLayout) findViewById(R.id.no_comment_container);
		initData();
		handler = new Handler();
		refreshListView.setOnRefreshCallBack(new OnRefreshCallBack() {

			@Override
			public void onRefresh() {
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						isRefresh = true;
						listRemarkUsers.clear();
						listAll.clear();
						initData();
						refreshListView.completeRefresh();
					}
				}, 2000);

			}

			@Override
			public void onPull() {
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						curPage++;
						addData();
						refreshListView.completePull();
					}

				}, 2000);
			}
		});
	}

	// 初始化评论数据
	private void initData() {
		final HttpUtils http = new HttpUtils();
		String url = Url.getUrlHead() + "/ListMsgRemarkServlet";
		RequestParams params = new RequestParams();
		http.configCurrentHttpCacheExpiry(0);
		params.addQueryStringParameter("userId", currentUser.getId() + "");
		params.addQueryStringParameter("curPage", curPage + "");
		params.addQueryStringParameter("pageSize", pageSize + "");
		http.send(HttpMethod.GET, url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// 显示无消息界面
				noCommentContainer.setVisibility(View.VISIBLE);
				Toast.makeText(context, "评论信息获取失败", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				String result = arg0.result;
				Gson gson = new GsonBuilder()
						.enableComplexMapKeySerialization()
						.setDateFormat("yyyy-MM-dd HH:mm:ss").create();
				List<Map<Remark, Users>> newRemarkUsers = gson.fromJson(result,
						new TypeToken<List<Map<Remark, Users>>>() {
						}.getType());
				if (newRemarkUsers.size() == 0 || newRemarkUsers == null) {
					noCommentContainer.setVisibility(View.VISIBLE);
					return;
				}
				listRemarkUsers.addAll(newRemarkUsers);
				List<Remark> listRemarks = new ArrayList<Remark>();
				for (Map<Remark, Users> map : newRemarkUsers) {
					Set<Entry<Remark, Users>> entry = map.entrySet();
					for (Map.Entry<Remark, Users> en : entry) {
						Remark remark = en.getKey();
						listRemarks.add(remark);
					}
				}
				Gson gson1 = new Gson();
				String str = gson1.toJson(listRemarks);
				String url = Url.getUrlHead() + "/ListCommentGoodsServlet";
				RequestParams params = new RequestParams();
				params.addBodyParameter("remarkList", str);
				HttpUtils http2 = new HttpUtils();
				http2.configCurrentHttpCacheExpiry(0);
				http2.send(HttpMethod.POST, url, params,
						new RequestCallBack<String>() {

							@Override
							public void onFailure(HttpException arg0,
									String arg1) {

							}

							@Override
							public void onSuccess(ResponseInfo<String> arg0) {
								String result = arg0.result;
								Type type = new TypeToken<List<Map<Map<Goods, Users>, List<String>>>>() {
								}.getType();
								Gson gson = new GsonBuilder()
										.enableComplexMapKeySerialization()
										.setDateFormat("yyyy-MM-dd HH:mm:ss")
										.create();
								List<Map<Map<Goods, Users>, List<String>>> newlist = gson
										.fromJson(result, type);
								listAll.addAll(newlist);
								adapter = new CommentListAdapter(context,
										listRemarkUsers, listAll,
										refreshListView, isRefresh);
								refreshListView.setAdapter(adapter);
							}

						});
			}
		});
	}

	private void addData() {
		final HttpUtils http = new HttpUtils();
		String url = Url.getUrlHead() + "/ListMsgRemarkServlet";
		RequestParams params = new RequestParams();
		http.configCurrentHttpCacheExpiry(0);
		params.addQueryStringParameter("userId", currentUser.getId() + "");
		params.addQueryStringParameter("curPage", curPage + "");
		params.addQueryStringParameter("pageSize", pageSize + "");
		http.send(HttpMethod.GET, url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// 显示无消息界面
				noCommentContainer.setVisibility(View.VISIBLE);
				Toast.makeText(context, "评论信息获取失败", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				String result = arg0.result;
				Gson gson = new GsonBuilder()
						.enableComplexMapKeySerialization()
						.setDateFormat("yyyy-MM-dd HH:mm:ss").create();
				List<Map<Remark, Users>> newRemarkUsers = gson.fromJson(result,
						new TypeToken<List<Map<Remark, Users>>>() {
						}.getType());
				// 判断为加载满的集合有没有数据，有则清空
				if (!preRemarks.isEmpty()) {
					listRemarkUsers.removeAll(preRemarks);
					preRemarks.clear();
				}
				// 判断有没有数据
				if (newRemarkUsers.isEmpty() || newRemarkUsers == null) {
					Toast.makeText(context, "没有更多了", Toast.LENGTH_SHORT).show();
					curPage--;
					return;
				} else {
					// 有数据，判断是否加载满,即pageSize
					if (newRemarkUsers != null
							&& newRemarkUsers.size() < pageSize) {
						preRemarks.addAll(newRemarkUsers);
						// 页数不变
						curPage--;
					}
					listRemarkUsers.addAll(newRemarkUsers);
					List<Remark> listRemarks = new ArrayList<Remark>();
					for (Map<Remark, Users> map : newRemarkUsers) {
						Set<Entry<Remark, Users>> entry = map.entrySet();
						for (Map.Entry<Remark, Users> en : entry) {
							Remark remark = en.getKey();
							listRemarks.add(remark);
						}
					}
					Gson gson1 = new Gson();
					String str = gson1.toJson(listRemarks);
					String url = Url.getUrlHead() + "/ListCommentGoodsServlet";
					RequestParams params = new RequestParams();
					params.addBodyParameter("remarkList", str);
					HttpUtils http2 = new HttpUtils();
					http2.configCurrentHttpCacheExpiry(0);
					http2.send(HttpMethod.POST, url, params,
							new RequestCallBack<String>() {

								@Override
								public void onFailure(HttpException arg0,
										String arg1) {

								}

								@Override
								public void onSuccess(ResponseInfo<String> arg0) {
									String result = arg0.result;
									Type type = new TypeToken<List<Map<Map<Goods, Users>, List<String>>>>() {
									}.getType();
									Gson gson = new GsonBuilder()
											.enableComplexMapKeySerialization()
											.setDateFormat(
													"yyyy-MM-dd HH:mm:ss")
											.create();
									List<Map<Map<Goods, Users>, List<String>>> newlist = gson
											.fromJson(result, type);
									listAll.addAll(newlist);
									if (adapter == null) {
										adapter = new CommentListAdapter(
												context, listRemarkUsers,
												listAll, refreshListView, false);
										refreshListView.setAdapter(adapter);
									} else {
										adapter.notifyDataSetChanged();
									}
								}
							});
				}
			}
		});
	}

	public void msgBack(View v) {
		switch (v.getId()) {
		case R.id.message_back:
			finish();
			break;
		}
	}

}
