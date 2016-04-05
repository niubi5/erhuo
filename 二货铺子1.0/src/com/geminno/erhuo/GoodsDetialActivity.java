package com.geminno.erhuo;

import io.rong.imkit.RongIM;
import io.rong.imkit.RongIM.UserInfoProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.geminno.erhuo.GoodsDetialActivity.AsyncImageLoader.ImageCallback;
import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.utils.Friend;
import com.geminno.erhuo.utils.Url;
import com.geminno.erhuo.entity.Users;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.geminno.erhuo.adapter.RemarkAdapter;
import com.geminno.erhuo.entity.Remark;
import com.geminno.erhuo.view.PullUpToLoadListView;
import com.geminno.erhuo.view.PullUpToLoadListView.OnPullUpToLoadCallBack;
import com.google.gson.GsonBuilder;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressLint("HandlerLeak")
public class GoodsDetialActivity extends Activity implements UserInfoProvider,
		OnClickListener {
	public static Activity goodsDetialActivity;
	private ViewPager viewPager;
	private ImageView image;
	private View item;
	private MyAdapter adapter;
	private ImageView[] indicator_imgs;// 存放引到图片数组
	private LayoutInflater inflater;
	private String[] urls;
	private Users user;
	private Goods goods;
	private TextView tvGoodBrief;
	private TextView tvGoodName;
	private ArrayList<Integer> collection;
	private int position;
	private List<Friend> userIdList;//
	private Users curUser;
	private Integer goodsId;
	private PullUpToLoadListView pullUpToLoadListView;
	private Context context;
	private ScrollView scroll;
	private ImageView commentIv;
	private Users currentUser = MyApplication.getCurrentUser();
	private RemarkAdapter remarkAdapter;
	private List<Map<Remark, Users>> listRemarkUsers = new ArrayList<Map<Remark, Users>>();
	private Handler handler = new Handler();
	private float scale;// 屏幕密度

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置无标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_goods_detial);
		goodsDetialActivity = this;
		context = this;
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
		viewPager = (ViewPager) findViewById(R.id.vp_goods_images);
		commentIv = (ImageView) findViewById(R.id.comment_iv);
		commentIv.setOnClickListener(this);
		// 上拉加载的listview
		List<View> list = new ArrayList<View>();
		scroll = (ScrollView) findViewById(R.id.sc_goods_information);
		inflater = LayoutInflater.from(this);
		scale = context.getResources().getDisplayMetrics().density;
		// 获得当前商品的id
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		user = (Users) bundle.getSerializable("user");
		goods = (Goods) bundle.getSerializable("goods");
		goodsId = goods.getId();
		collection = MyApplication.getCollection();
		List<String> goodUrl = bundle.getStringArrayList("urls");
		// 用户好友列表
		curUser = MyApplication.getCurrentUser();
		userIdList = new ArrayList<Friend>();
		SharedPreferences sp = getSharedPreferences("friendInfo",
				Context.MODE_PRIVATE);
		String friendList = sp.getString("friendList", null);
		Gson gson = new Gson();
		Type type = new TypeToken<ArrayList<Friend>>() {
		}.getType();
		if (friendList != null) {
			userIdList = gson.fromJson(friendList, type);
			Log.i("FriendList", "adapter not null:" + friendList);
		} else {
			if (curUser != null) {
				Log.i("FriendList", "adapter is null:" + friendList);
				userIdList.add(new Friend(curUser.getId() + "", curUser
						.getName(), curUser.getPhoto() == null ? "null"
						: curUser.getPhoto()));
			}
		}

		urls = new String[goodUrl.size()];
		for (int i = 0; i < goodUrl.size(); i++) {
			urls[i] = goodUrl.get(i);
		}
		indicator_imgs = new ImageView[goodUrl.size()];
		/**
		 * 创建多个item （每一条viewPager都是一个item） 从服务器获取完数据（如文章标题、url地址） 后，再设置适配器
		 */
		for (int i = 0; i < urls.length; i++) {
			item = inflater.inflate(R.layout.goods_images_item, null);
			list.add(item);
		}
		// 创建适配器， 把组装完的组件传递进去
		adapter = new MyAdapter(list);
		viewPager.setAdapter(adapter);
		// 绑定动作监听器：如翻页的动画
		viewPager.setOnPageChangeListener(new MyListener());
		initData();
		initComment();// 初始化评论
		initIndicator();
		RongIM.setUserInfoProvider(this, true);
	}

	public void initData() {
		ImageView goodsFavorite = (ImageView) findViewById(R.id.goods_favorite);
		ImageView ivHead = (ImageView) findViewById(R.id.iv_user_head);
		TextView tvUserName = (TextView) findViewById(R.id.tv_user_name);
		TextView tvUserLocation = (TextView) findViewById(R.id.tv_user_location);
		TextView tvGoodPrice = (TextView) findViewById(R.id.tv_goods_price);
		TextView tvGoodOldPrice = (TextView) findViewById(R.id.tv_goods_oldprice);
		tvGoodName = (TextView) findViewById(R.id.tv_goods_name);
		TextView tvGoodTime = (TextView) findViewById(R.id.tv_goods_time);
		tvGoodBrief = (TextView) findViewById(R.id.tv_goods_brief);
		if (user.getPhoto() != null && !user.getPhoto().equals("")) {
			String headUrl = Url.getUrlHead();
			final String userHeadUrl = headUrl + user.getPhoto();
			ImageLoader.getInstance().displayImage(userHeadUrl, ivHead);
		} else {
			ivHead.setImageResource(R.drawable.header_default);
		}
		// 设置收藏的显示状态
		if (collection != null) {
			if (collection.contains(goodsId)) {
				goodsFavorite.setSelected(true);
			} else {
				goodsFavorite.setSelected(false);
			}
		}
		goodsFavorite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				collection = MyApplication.getCollection();
				if (collection.contains(goodsId)) {
					collectGoods(goods, v, false);// 调用取消收藏商品方法
				} else {
					collectGoods(goods, v, true);
				}
			}
		});
		tvUserName.setText(user.getName());
		if (MyApplication.getLocation() != null) {
			int instance = Distance(goods.getLongitude(), goods.getLatitude(),
					MyApplication.getLocation().getLongitude(), MyApplication
							.getLocation().getLatitude());
			tvUserLocation
					.setText(instance >= 100 ? ("距我:" + instance / 1000 + "km")
							: ("距我:" + instance + "m"));
		} else {
			tvUserLocation.setText("距我:");
		}
		int instance = Distance(goods.getLongitude(), goods.getLatitude(),
				MyApplication.getLocation().getLongitude(), MyApplication
						.getLocation().getLatitude());
		tvUserLocation
				.setText(instance >= 100 ? ("距我:" + instance / 1000 + "km")
						: ("距我:" + instance + "m"));
		tvGoodPrice.setText("¥" + goods.getSoldPrice());
		tvGoodOldPrice.setText("原价:" + goods.getBuyPrice());
		tvGoodName.setText(goods.getName());
		tvGoodTime.setText((goods.getPubTime().substring(2, 10)));
		tvGoodBrief.setText(goods.getImformation());
	}

	private void collectGoods(Goods goods, View v, final boolean b) {
		Users user = MyApplication.getCurrentUser();
		if (user != null && goods != null) {
			HttpUtils http = new HttpUtils();
			RequestParams params = new RequestParams();
			String urlHead = Url.getUrlHead();
			String url = null;
			if (!b) {
				// 取消收藏
				v.setSelected(false);// 设为取消状态
				// 从集合中移除
				if (collection.contains(goods.getId())) {
					collection.remove(Integer.valueOf(goods.getId()));
					MyApplication.setCollections(collection);// 更新Application里的集合
				}
				params.addBodyParameter("userId", user.getId() + "");
				params.addBodyParameter("goodsId", goods.getId() + "");
				url = urlHead + "/DeleteCollectionsServlet";
			} else {
				// 收藏
				v.setSelected(true);// 设为收藏状态
				// 并加入集合
				collection.add(goods.getId());
				MyApplication.setCollections(collection);
				params.addBodyParameter("userId", user.getId() + "");
				params.addBodyParameter("goodsId", goods.getId() + "");
				url = urlHead + "/AddCollectionsServlet";
			}
			http.send(HttpRequest.HttpMethod.POST, url, params,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							if (b) {
								Toast.makeText(context, "收藏成功",
										Toast.LENGTH_SHORT).show();
							}
						}
					});
		} else {
			Toast.makeText(GoodsDetialActivity.this, "请先登录", Toast.LENGTH_SHORT)
					.show();
		}
	}

	// 初始化评论
	private void initComment() {
		pullUpToLoadListView = (PullUpToLoadListView) findViewById(R.id.pull_up_to_load);
		initCommentData();
		scroll.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				return false;
			}
		});
		pullUpToLoadListView
				.setOnPullToLoadCallback(new OnPullUpToLoadCallBack() {

					@Override
					public void onPull() {
						handler.postDelayed(new Runnable() {

							@Override
							public void run() {
								pullUpToLoadListView.completePull();
							}
						}, 2000);

					}
				});
	}

	// 初始化评论数据
	private void initCommentData() {
		HttpUtils http = new HttpUtils();
		String urlHead = Url.getUrlHead();
		String url = urlHead + "/ListRemarkServlet";
		// 设置为不缓存，及时获取数据
		http.configCurrentHttpCacheExpiry(0);
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("goodsId", goods.getId() + "");
		http.send(HttpRequest.HttpMethod.GET, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {

					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						String result = arg0.result;
						Gson gson = new GsonBuilder()
								.enableComplexMapKeySerialization().create();
						List<Map<Remark, Users>> newComments = gson.fromJson(
								result,
								new TypeToken<List<Map<Remark, Users>>>() {
								}.getType());
						if (!listRemarkUsers.isEmpty()) {
							listRemarkUsers.clear();
						}
						listRemarkUsers.addAll(newComments);// 加到总集合中去
						if (remarkAdapter == null) {
							remarkAdapter = new RemarkAdapter(context,
									listRemarkUsers, goods,
									pullUpToLoadListView);
							pullUpToLoadListView.setAdapter(remarkAdapter);
						} else {
							remarkAdapter.notifyDataSetChanged();
							Log.i("erhuo", "通知数据源改变");
						}
						// 解决scrollview嵌套listview高度问题.
						// 获得listview对应的adapter
						ListAdapter listAdapter = pullUpToLoadListView
								.getAdapter();
						int totalHeight = 0;
						for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
							View listItem = listAdapter.getView(i, null,
									pullUpToLoadListView);
							// 计算子项的宽高
							listItem.measure(0, 0);
							// 统计所有子项总高度
							totalHeight += listItem.getMeasuredHeight();
						}
						ViewGroup.LayoutParams params = pullUpToLoadListView
								.getLayoutParams();
						// 获取子项间分隔符占用的高度 + 到总高度重去
						params.height = totalHeight
								+ (pullUpToLoadListView.getDividerHeight() * (listAdapter
										.getCount() - 1));
						// params.height最后得到整个ListView完整显示需要的高度
						pullUpToLoadListView.setLayoutParams(params);
						scroll.smoothScrollBy(0, 20);
					}
				});
	}

	private void showCommentPopUp(final Context context) {
		View contentView = LayoutInflater.from(context).inflate(
				R.layout.comment_popup, null);
		int px = (int) (60 * scale + 0.5f);
		final PopupWindow pop = new PopupWindow(contentView,
				LayoutParams.MATCH_PARENT, px, true);
		pop.setContentView(contentView);
		final EditText commentContent = (EditText) contentView
				.findViewById(R.id.pop_comment_content);
		ImageView commentSend = (ImageView) contentView
				.findViewById(R.id.pop_comment_send);
		commentContent.setHint("回复 " + user.getName() + ":");
		// 发送留言
		commentSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 当前用户已登录
				// 当评论不为空时 发送留言，存入数据库
				if (currentUser == null) {
					Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
				} else {
					// 判断输入框是否为空
					if (!TextUtils.isEmpty(commentContent.getText())) {
						HttpUtils http = new HttpUtils();
						String urlHead = Url.getUrlHead();
						String url = urlHead + "/AddCommentServlet";
						RequestParams params = new RequestParams();
						// 设置为不缓存，及时获取数据
						http.configCurrentHttpCacheExpiry(0);
						params.addBodyParameter("goodsId", goods.getId() + "");
						params.addBodyParameter("userId", currentUser.getId()
								+ "");
						params.addBodyParameter("commentContent",
								commentContent.getText().toString());
						params.addBodyParameter("fatherId", 0 + "");// 当前为一级评论
						http.send(HttpRequest.HttpMethod.POST, url, params,
								new RequestCallBack<String>() {

									@Override
									public void onFailure(HttpException arg0,
											String arg1) {
										Toast.makeText(context, "评论失败",
												Toast.LENGTH_SHORT).show();
									}

									@Override
									public void onSuccess(
											ResponseInfo<String> arg0) {
										Toast.makeText(context, "评论成功",
												Toast.LENGTH_SHORT).show();
										// 并且隐藏pop
										pop.dismiss();
										initCommentData();// 再次调用，更新数据源
										// addCommentData();
									}
								});
					} else {
						Toast.makeText(context, "请输入评论内容", Toast.LENGTH_SHORT)
								.show();
					}
				}

			}
		});
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
		pop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setTouchable(true);
		// 显示popupwindow
		View rootView = LayoutInflater.from(context).inflate(
				R.layout.activity_goods_detial, null);
		pop.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
		popupInputMethodWindow();// 自动弹出键盘
	}

	private void addCommentData() {
		HttpUtils http = new HttpUtils();
		String headUrl = Url.getUrlHead();
		String url = headUrl + "/ListRemarkServlet";
		RequestParams params = new RequestParams();
		// 设置为不缓存，及时获取数据
		http.configCurrentHttpCacheExpiry(0);
		params.addQueryStringParameter("goodsId", goods.getId() + "");
		http.send(HttpRequest.HttpMethod.GET, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						String result = arg0.result;
						Gson gson = new GsonBuilder()
								.enableComplexMapKeySerialization().create();
						List<Map<Remark, Users>> newComments = gson.fromJson(
								result,
								new TypeToken<List<Map<Remark, Users>>>() {
								}.getType());
						listRemarkUsers.addAll(newComments);
						if (remarkAdapter == null) {
							remarkAdapter = new RemarkAdapter(context,
									listRemarkUsers, goods,
									pullUpToLoadListView);
							pullUpToLoadListView.setAdapter(remarkAdapter);
						} else {
							Log.i("erhuo", "通知数据源改变");
							remarkAdapter.notifyDataSetChanged();
						}
					}
				});
	}

	//
	/**
	 * 计算地球上任意两点(经纬度)距离
	 * 
	 * @param long1
	 *            第一点经度
	 * @param lat1
	 *            第一点纬度
	 * @param long2
	 *            第二点经度
	 * @param lat2
	 *            第二点纬度
	 * @return 返回距离 单位：米
	 */
	public static int Distance(double long1, double lat1, double long2,
			double lat2) {
		double a, b, R;
		R = 6378137; // 地球半径
		lat1 = lat1 * Math.PI / 180.0;
		lat2 = lat2 * Math.PI / 180.0;
		a = lat1 - lat2;
		b = (long1 - long2) * Math.PI / 180.0;
		double d;
		double sa2, sb2;
		sa2 = Math.sin(a / 2.0);
		sb2 = Math.sin(b / 2.0);
		d = 2
				* R
				* Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)
						* Math.cos(lat2) * sb2 * sb2));
		return (int) d;
	}

	private void showShare() {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(tvGoodName.getText().toString());
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://sharesdk.cn");
		// text是分享文本，所有平台都需要这个字段
		oks.setText(tvGoodBrief.getText().toString());
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://sharesdk.cn");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://sharesdk.cn");

		// 启动分享GUI
		oks.show(this);
	}

	// 点击事件
	public void onclick(View v) {
		switch (v.getId()) {
		case R.id.iv_share_report:
			showPopupWindow(v);
			break;
		case R.id.iv_detial_return:
			finish();
			break;
		case R.id.btn_buy:
			if (MyApplication.getCurrentUser() == null) {
				Toast.makeText(this, "请先登录！", Toast.LENGTH_SHORT).show();
			} else {
				if (goods.getState() == 2) {
					Toast.makeText(GoodsDetialActivity.this, "该商品已被下单",
							Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent(this, BuyGoodsActivity.class);
					intent.putExtra("user", user);
					intent.putExtra("good", goods);
					intent.putExtra("url", urls);
					startActivity(intent);
					// }
				}
			}
			break;
		case R.id.btn_chat:
			if (curUser == null) {
				Toast.makeText(this, "请先登录！", Toast.LENGTH_SHORT).show();
			} else {
				// 获取聊天对象
				Friend fri = new Friend(user.getId() + "", user.getName(),
						user.getPhoto() == null ? "null" : user.getPhoto());
				boolean flag = true;
				// 判断该聊天对象之前是否聊过
				for (int i = 0; i < userIdList.size(); i++) {
					// Log.i("FriendList", userIdList.get(i).getUserId());
					if ((userIdList.get(i).getUserId()).equals(fri.getUserId())) {
						flag = false;
					}
				}
				Log.i("FriendList", flag + "");
				if (flag) {
					Log.i("FriendList", flag + "");
					userIdList.add(fri);
				}
				Gson gson = new Gson();
				String friendInfo = gson.toJson(userIdList);
				SharedPreferences shared = getSharedPreferences("friendInfo",
						MODE_PRIVATE);
				shared.edit().putString("friendList", friendInfo).commit();
				if (MyApplication.getCurToken() == null) {
					// connToast = new Toast(this);
					// connToast.setDuration(1000);
					// connToast.setText(new String("正在连接服务器..."));
					// connToast.show();
					Toast.makeText(this, "正在连接服务器...", Toast.LENGTH_SHORT)
							.show();
					getToken(curUser.getId(), curUser.getName(),
							curUser.getPhoto());
				} else {
					if (RongIM.getInstance() != null)
						RongIM.getInstance().startPrivateChat(
								GoodsDetialActivity.this, user.getId() + "",
								user.getName());// 26594
				}
			}
			break;
		default:
			break;
		}
	}

	// 获取token
	// 如果已经有token就不用执行这个方法，直接调用connect（）方法
	public void getToken(int userId, String userName, String headUrl) {
		String url = "https://api.cn.ronghub.com/user/getToken.json";
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		// 初始化请求头参数
		long time = Calendar.getInstance().getTimeInMillis() / 1000;
		double nonce = Math.random() * 1000;
		String signa = "DqpxxWb403n" + nonce + time;
		params.addHeader("App-Key", "z3v5yqkbvttj0");// appkey
		params.addHeader("Nonce", String.valueOf(nonce));
		params.addHeader("Timestamp", String.valueOf(time));
		params.addHeader("Signature", SHA1(signa));

		// 请求参数
		params.addBodyParameter("userId", userId + "");// 用户id
		params.addBodyParameter("name", userName);// 用户名
		params.addBodyParameter("portraitUri", headUrl);// 头像url
		http.send(HttpRequest.HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {

					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						Log.i("RongCloudDemo", "--result" + arg0.result);
						// Toast.makeText(MainActivity.this, arg0.result,
						// 1).show();
						Log.i("getToken", arg0.result);
						// 在这里解析json调用connect(token)方法
						// connect(token);

						JSONTokener jt = new JSONTokener(arg0.result);
						try {
							JSONObject jb = (JSONObject) jt.nextValue();
							String token = jb.getString("token");
							Log.i("getToken", "token:" + token);
							MyApplication.setCurToken(token);
							connect(token);
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				});
	}

	/**
	 * 融云聊天
	 * 
	 * @author Heikki 2016.03.28
	 * */
	// 连接融云服务器
	private void connect(String token) {

		if (getApplicationInfo().packageName.equals(MyApplication
				.getCurProcessName(getApplicationContext()))) {

			/**
			 * IMKit SDK调用第二步,建立与服务器的连接
			 */
			RongIM.connect(token, new RongIMClient.ConnectCallback() {

				/**
				 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的
				 * Token
				 */
				@Override
				public void onTokenIncorrect() {

					Log.d("LoginActivity", "--onTokenIncorrect");
				}

				/**
				 * 连接融云成功
				 * 
				 * @param userid
				 *            当前 token
				 */
				@Override
				public void onSuccess(String userid) {

					Log.d("LoginActivity", "--onSuccess" + userid);
					// connToast.cancel();
					Toast.makeText(GoodsDetialActivity.this, "连接成功", 1).show();
					if (RongIM.getInstance() != null)
						RongIM.getInstance().startPrivateChat(
								GoodsDetialActivity.this, user.getId() + "",
								user.getName());// 26594
				}

				/**
				 * 连接融云失败
				 * 
				 * @param errorCode
				 *            错误码，可到官网 查看错误码对应的注释
				 */
				@Override
				public void onError(RongIMClient.ErrorCode errorCode) {

					Log.d("LoginActivity", "--onError" + errorCode);
				}
			});
		}
	}

	// sha1编码
	public static String SHA1(String decript) {
		try {
			MessageDigest digest = java.security.MessageDigest
					.getInstance("SHA-1");
			digest.update(decript.getBytes());
			byte messageDigest[] = digest.digest();
			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			// 字节数组转换为 十六进制 数
			for (int i = 0; i < messageDigest.length; i++) {
				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexString.append(0);
				}
				hexString.append(shaHex);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	// 显示popupwindow
	private void showPopupWindow(View view) {

		// 一个自定义的布局，作为显示的内容
		View contentView = LayoutInflater.from(this).inflate(
				R.layout.pop_window, null);
		// 设置按钮的点击事件
		TextView tvShare = (TextView) contentView.findViewById(R.id.tv_share);
		TextView tvReport = (TextView) contentView.findViewById(R.id.tv_report);
		tvShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Toast.makeText(GoodsDetialActivity.this, "分享",
				// Toast.LENGTH_SHORT).show();
				showShare();
			}
		});
		tvReport.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Toast.makeText(GoodsDetialActivity.this, "举报",
				// Toast.LENGTH_SHORT).show();
				if (curUser != null) {
					Intent intent = new Intent(GoodsDetialActivity.this,
							ReportGoodActivity.class);
					intent.putExtra("goodId", goods.getId());
					startActivity(intent);
				}else{
					Toast.makeText(GoodsDetialActivity.this, "请登录!",
							 Toast.LENGTH_SHORT).show();
				}
			}
		});

		final PopupWindow popupWindow = new PopupWindow(contentView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);

		popupWindow.setTouchable(true);

		popupWindow.setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				return false;
				// 这里如果返回true的话，touch事件将被拦截
				// 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
			}
		});

		// 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
		popupWindow.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.round_box));

		// 设置好参数之后再show
		popupWindow.showAsDropDown(view);
		// popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

	}

	// 自动弹出键盘
	private void popupInputMethodWindow() {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) context
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}, 0);
	}

	/**
	 * 初始化引导图标 动态创建多个小圆点，然后组装到线性布局里
	 */
	private void initIndicator() {

		ImageView imgView;
		View v = findViewById(R.id.indicator);// 线性水平布局，负责动态调整导航图标

		for (int i = 0; i < urls.length; i++) {
			imgView = new ImageView(this);
			LinearLayout.LayoutParams params_linear = new LinearLayout.LayoutParams(
					10, 10);
			params_linear.setMargins(7, 10, 7, 10);
			imgView.setLayoutParams(params_linear);
			indicator_imgs[i] = imgView;
			if (i == 0) { // 初始化第一个为选中状态

				indicator_imgs[i].setBackgroundResource(R.drawable.round_point);
			} else {
				indicator_imgs[i]
						.setBackgroundResource(R.drawable.round_point_normal);
			}
			((ViewGroup) v).addView(indicator_imgs[i]);
		}

	}

	/**
	 * 适配器，负责装配 、销毁 数据 和 组件 。
	 */
	private class MyAdapter extends PagerAdapter {

		private List<View> mList;

		private AsyncImageLoader asyncImageLoader;

		public MyAdapter(List<View> list) {
			mList = list;
			asyncImageLoader = new AsyncImageLoader();
		}

		/**
		 * Return the number of views available.
		 */
		@Override
		public int getCount() {
			return mList.size();
		}

		/**
		 * Remove a page for the given position. 滑动过后就销毁 ，销毁当前页的前一个的前一个的页！
		 * instantiateItem(View container, int position) This method was
		 * deprecated in API level . Use instantiateItem(ViewGroup, int)
		 */
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mList.get(position));

		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		/**
		 * Create the page for the given position.
		 */
		@SuppressLint("NewApi")
		@Override
		public Object instantiateItem(final ViewGroup container,
				final int position) {
			Drawable cachedImage = asyncImageLoader.loadDrawable(
					urls[position], new ImageCallback() {

						@SuppressLint("NewApi")
						public void imageLoaded(Drawable imageDrawable,
								String imageUrl) {

							View view = mList.get(position);
							image = ((ImageView) view
									.findViewById(R.id.iv_goods_image));
							// image.setBackground(imageDrawable);
							image.setImageDrawable(null);
							image.setImageDrawable(imageDrawable);
							// image.setScaleType(ScaleType.CENTER_CROP);
							container.removeView(mList.get(position));
							container.addView(mList.get(position));
							// adapter.notifyDataSetChanged();

						}
					});

			View view = mList.get(position);
			image = ((ImageView) view.findViewById(R.id.iv_goods_image));
			// image.setBackground(cachedImage);
			image.setImageDrawable(cachedImage);

			container.removeView(mList.get(position));
			container.addView(mList.get(position));
			// adapter.notifyDataSetChanged();

			return mList.get(position);

		}
	}

	/**
	 * 动作监听器，可异步加载图片
	 * 
	 */
	private class MyListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == 0) {
				// new MyAdapter(null).notifyDataSetChanged();
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int position) {

			// 改变所有导航的背景图片为：未选中
			for (int i = 0; i < indicator_imgs.length; i++) {
				indicator_imgs[i]
						.setBackgroundResource(R.drawable.round_point_normal);

			}

			// 改变当前背景图片为：选中
			indicator_imgs[position]
					.setBackgroundResource(R.drawable.round_point);
		}

	}

	/**
	 * 异步加载图片
	 */
	static class AsyncImageLoader {

		// 软引用，使用内存做临时缓存 （程序退出，或内存不够则清除软引用）
		private HashMap<String, SoftReference<Drawable>> imageCache;

		public AsyncImageLoader() {
			imageCache = new HashMap<String, SoftReference<Drawable>>();
		}

		/**
		 * 定义回调接口
		 */
		public interface ImageCallback {
			public void imageLoaded(Drawable imageDrawable, String imageUrl);
		}

		/**
		 * 创建子线程加载图片 子线程加载完图片交给handler处理（子线程不能更新ui，而handler处在主线程，可以更新ui）
		 * handler又交给imageCallback，imageCallback须要自己来实现，在这里可以对回调参数进行处理
		 * 
		 * @param imageUrl
		 *            ：须要加载的图片url
		 * @param imageCallback
		 *            ：
		 * @return
		 */
		public Drawable loadDrawable(final String imageUrl,
				final ImageCallback imageCallback) {

			// 如果缓存中存在图片 ，则首先使用缓存
			if (imageCache.containsKey(imageUrl)) {
				SoftReference<Drawable> softReference = imageCache
						.get(imageUrl);
				Drawable drawable = softReference.get();
				if (drawable != null) {
					imageCallback.imageLoaded(drawable, imageUrl);// 执行回调
					return drawable;
				}
			}

			/**
			 * 在主线程里执行回调，更新视图
			 */
			final Handler handler = new Handler() {
				public void handleMessage(Message message) {
					imageCallback.imageLoaded((Drawable) message.obj, imageUrl);
				}
			};

			/**
			 * 创建子线程访问网络并加载图片 ，把结果交给handler处理
			 */
			new Thread() {
				@Override
				public void run() {
					Drawable drawable = loadImageFromUrl(imageUrl);
					// 下载完的图片放到缓存里
					imageCache.put(imageUrl, new SoftReference<Drawable>(
							drawable));
					Message message = handler.obtainMessage(0, drawable);
					handler.sendMessage(message);
				}
			}.start();

			return null;
		}

		/**
		 * 下载图片 （注意HttpClient 和httpUrlConnection的区别）
		 */
		public Drawable loadImageFromUrl(String url) {

			try {
				HttpClient client = new DefaultHttpClient();
				client.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, 1000 * 15);
				HttpGet get = new HttpGet(url);
				HttpResponse response;

				response = client.execute(get);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = response.getEntity();

					Drawable d = Drawable.createFromStream(entity.getContent(),
							"src");

					return d;
				} else {
					return null;
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		// 清除缓存
		public void clearCache() {

			if (this.imageCache.size() > 0) {

				this.imageCache.clear();
			}

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.comment_iv:
			showCommentPopUp(context);
			break;
		}
	}

	public UserInfo getUserInfo(String s) {
		Log.i("getUserInfo", userIdList.toString());
		for (Friend i : userIdList) {
			if (i.getUserId().equals(s)) {
				Log.i("getUserInfo", "activity:" + i.getUserName());
				return new UserInfo(i.getUserId(), i.getUserName(), Uri.parse(i
						.getPortraitUri()));
			}
		}
		return null;
	}

}
