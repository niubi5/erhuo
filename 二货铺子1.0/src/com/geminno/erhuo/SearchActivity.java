package com.geminno.erhuo;

import java.lang.reflect.Type;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.geminno.erhuo.adapter.HomePageAdapter;
import com.geminno.erhuo.entity.Goods;
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
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SearchActivity extends Activity implements OnClickListener {

	private EditText keyword;// 输入关键字
	private HomePageAdapter adapter;
	private RefreshListView refreshListView;
	private Handler handler = new Handler();
	private List<Map<Map<Goods, Users>, List<String>>> preGoods = new ArrayList<Map<Map<Goods, Users>, List<String>>>();// 记录上一次不满的记录集合
	private List<Map<Map<Goods, Users>, List<String>>> listAll = new ArrayList<Map<Map<Goods, Users>, List<String>>>();
	private int curPage = 1; // 页数
	private int pageSize = 5;// 一次加载几条
	private boolean isRefersh = false;
	private Context context;
	private String result = null;
	private String word;
	private LinearLayout linsearch;
	private ImageView delete;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search);
		// 调用setColor()方法,实现沉浸式状态栏
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
		keyword = (EditText) findViewById(R.id.search_et_input);
		linsearch = (LinearLayout) findViewById(R.id.lin_search);
		delete = (ImageView) findViewById(R.id.search_iv_delete);
		refreshListView = (RefreshListView) findViewById(R.id.refres_list_search);
		findViewById(R.id.tv_search).setOnClickListener(this);
		findViewById(R.id.ib_sousuo).setOnClickListener(this);
		delete.setOnClickListener(this);
		keyword.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				String edit = keyword.getText().toString();
				Log.i("cheshi", "赵信edit:" + edit);
				if (edit.length() != 0 && !edit.equals("null")) {
					Log.i("cheshi", "赵信");
					delete.setVisibility(delete.VISIBLE);

				}

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
				String edit = keyword.getText().toString();
				Log.i("cheshi", "洛克萨斯edit:" + edit);
				if (edit.length() == 0 && edit.equals("null")) {
					delete.setVisibility(delete.GONE);
					Log.i("cheshi", "洛克萨斯");
				}

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				String edit = keyword.getText().toString();
				Log.i("cheshi", "德玛edit:" + edit);
				if (edit.length() == 0) {
					delete.setVisibility(delete.GONE);
					Log.i("cheshi", "德玛");
				}
			}
		});
		context = this;

	}

	private void initView() {
		refreshListView.setOnRefreshCallBack(new OnRefreshCallBack() {
			@Override
			public void onRefresh() {
				// 过2s 开始执行
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						// 清空原来的+新的数据
						listAll.clear();
						curPage = 1;
						isRefersh = true;// 是刷新操作
						initData();
						// 调用刷新完成的方法
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.search_iv_delete:
			keyword.setText(null);
			break;

		case R.id.ib_sousuo:
			this.finish();
			break;
		case R.id.tv_search:
			 InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			   // 得到InputMethodManager的实例
			   if (imm.isActive()) {
			    // 如果开启
			    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
			      InputMethodManager.HIDE_NOT_ALWAYS);
			    // 关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
			   }
			refreshListView.setAdapter(null);
			word = keyword.getText().toString();
			Log.i("cheshi", "获得word值：" + word);
			if (word.length() != 0) {
				linsearch.setVisibility(linsearch.GONE);
				initData();
				initView();
			} else {
				Toast.makeText(context, "请输入关键字", Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}

	// 初始化数据源
	public void initData() {
		// 初始化数据源
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("word", word);// 输入的关键字
		params.addQueryStringParameter("curPage", curPage + "");
		params.addQueryStringParameter("pageSize", pageSize + "");
		String url = Url.getUrlHead() + "/SearchGoodsServlet";
		httpUtils.send(HttpMethod.GET, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Toast.makeText(context, "请求失败！", Toast.LENGTH_SHORT)
								.show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						result = arg0.result;
						Log.i("cheshi", "initData返回值：" + result);
						if (result != null && !result.equals("null") && !"[]".equals(result)) {
							Log.i("searchResult", "searchResult:"+result);
							Gson gson = new GsonBuilder()
									.enableComplexMapKeySerialization()
									.setDateFormat("yyyy-MM-dd HH:mm:ss")
									.create();
							Type type = new TypeToken<List<Map<Map<Goods, Users>, List<String>>>>() {
							}.getType();
							List<Map<Map<Goods, Users>, List<String>>> newGoods = gson
									.fromJson(result, type);
							listAll.addAll(newGoods);
							// if (adapter == null) {
							adapter = new HomePageAdapter(context, listAll,
									refreshListView, isRefersh);
							refreshListView.setAdapter(adapter);
							// } else {
							// adapter.notifyDataSetChanged();
							// }
						} else {
							Toast.makeText(context, "您查找的商品不存在！",
									Toast.LENGTH_SHORT).show();
//							linsearch.findViewById(id)
							linsearch.setVisibility(View.VISIBLE);
						}

					}
				});
	}

	// 加载数据
	private void addData() {
		word = keyword.getText().toString();
		if (word.length() != 0) {
			HttpUtils http = new HttpUtils();
			// 设置参数
			RequestParams params = new RequestParams();
			params.addQueryStringParameter("word", word);
			params.addQueryStringParameter("curPage", curPage + "");
			params.addQueryStringParameter("pageSize", pageSize + "");
			http.configCurrentHttpCacheExpiry(0);
			// 拼接url
			String headUrl = Url.getUrlHead();
			String url = headUrl + "/SearchGoodsServlet";
			// String
			// url="http://10.201.1.16:8080/secondHandShop/SearchGoodsServlet";
			http.send(HttpRequest.HttpMethod.GET, url, params,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							Toast.makeText(context, "网络错误！",
									Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							String result = arg0.result;
							Log.i("cheshi", "addData返回值：" + result);
							Gson gson = new GsonBuilder()
									.enableComplexMapKeySerialization()
									.setDateFormat("yyyy-MM-dd HH:mm:ss")
									.create();
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
								Toast.makeText(context, "没有更多了",
										Toast.LENGTH_SHORT).show();
								// 页数不变，之前++过，故这里要--
								curPage--;
							} else {
								// 有数据，判断是否加载满,即pageSize
								if (newGoods != null
										&& newGoods.size() < pageSize) {
									// 页数不变
									curPage--;
									// 记录在未加载满的集合中
									preGoods.addAll(newGoods);
								}
								listAll.addAll(newGoods);// 添加新查到的集合
								// 改变数据源
								if (adapter == null) {
									adapter = new HomePageAdapter(context,
											listAll, refreshListView, isRefersh);
									refreshListView.setAdapter(adapter);
								} else {
									Log.i("erhuo", "通知数据源改变");
									adapter.notifyDataSetChanged();
								}
							}
						}
					});
		}
	}

}
