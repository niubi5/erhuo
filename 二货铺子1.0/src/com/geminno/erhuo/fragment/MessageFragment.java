package com.geminno.erhuo.fragment;

import io.rong.imkit.RongIM;
import io.rong.imkit.RongIM.UserInfoProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.geminno.erhuo.CommentActivity;
import com.geminno.erhuo.MyApplication;
import com.geminno.erhuo.R;
import com.geminno.erhuo.adapter.MessagePageAdapter;
import com.geminno.erhuo.entity.Messages;
import com.geminno.erhuo.entity.Users;
import com.geminno.erhuo.utils.Friend;
import com.geminno.erhuo.view.PullToFreshListView;
import com.geminno.erhuo.view.PullToFreshListView.OnPullTofreshCallBack;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

@SuppressLint("InflateParams")
public class MessageFragment extends BaseFragment{

	private View view;
	private Context context;
	private List<Messages> message;
	private PullToFreshListView pullToFreshListView;
//	private Handler handler = new Handler();
//	private LinearLayout llChar;
//	private LinearLayout llPinglun;
//	private List<Friend> userIdList;

	public MessageFragment(Context context) {
		this.context = context;

//		RongIM.setUserInfoProvider(this, true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_message_page, null);
		// RongIM.setUserInfoProvider(this, true);
		return view;
	}

	@Override
	protected void initView() {
		pullToFreshListView = (PullToFreshListView) view.findViewById(R.id.message_refreshListView);
//		llChar = (LinearLayout) view.findViewById(R.id.ll_message_chat);
//		llPinglun = (LinearLayout) view.findViewById(R.id.ll_message_pinglun);
//		
//		llChar.setOnClickListener(this);
//		llPinglun.setOnClickListener(this);
		initData();// 初始化数据操作
		 pullToFreshListView.setOnPullToFresh(new OnPullTofreshCallBack() {
		
		 @Override
		 public void onRefresh() {
//		 handler.postDelayed(new Runnable(){
//		
//		 @Override
//		 public void run() {
//		 // 刷新操作
//		 pullToFreshListView.completeRefresh();
//		 }
//		
//		 }, 2000);
		 }
		 });
	}

	@Override
	protected void initData() {
		// ----------------------
		 MessagePageAdapter adapter = new MessagePageAdapter(context, message,
		 pullToFreshListView);
		 pullToFreshListView.setAdapter(adapter);
	}

	@Override
	protected void initEvent() {

	}

//	// 获取token
//	// 如果已经有token就不用执行这个方法，直接调用connect（）方法
//	public void getToken(int userId, String userName, String headUrl) {
//		String url = "https://api.cn.ronghub.com/user/getToken.json";
//		HttpUtils http = new HttpUtils();
//		RequestParams params = new RequestParams();
//		// 初始化请求头参数
//		long time = Calendar.getInstance().getTimeInMillis() / 1000;
//		double nonce = Math.random() * 1000;
//		String signa = "DqpxxWb403n" + nonce + time;
//		params.addHeader("App-Key", "z3v5yqkbvttj0");// appkey
//		params.addHeader("Nonce", String.valueOf(nonce));
//		params.addHeader("Timestamp", String.valueOf(time));
//		params.addHeader("Signature", SHA1(signa));
//
//		// 请求参数
//		params.addBodyParameter("userId", userId + "");// 用户id
//		params.addBodyParameter("name", userName);// 用户名
//		params.addBodyParameter("portraitUri", headUrl);// 头像url
//		http.configCurrentHttpCacheExpiry(0);
//		http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {
//
//			@Override
//			public void onFailure(HttpException arg0, String arg1) {
//
//			}
//
//			@Override
//			public void onSuccess(ResponseInfo<String> arg0) {
//				Log.i("RongCloudDemo", "--result" + arg0.result);
//				// Toast.makeText(MainActivity.this, arg0.result, 1).show();
//				Log.i("getToken", arg0.result);
//				// 在这里解析json调用connect(token)方法
//				// connect(token);
//
//				JSONTokener jt = new JSONTokener(arg0.result);
//				try {
//					JSONObject jb = (JSONObject) jt.nextValue();
//					String token = jb.getString("token");
//					Log.i("getToken", "token:" + token);
//					MyApplication.setCurToken(token);
//					connect(token);
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//
//			}
//		});
//	}
//
//	/**
//	 * 融云聊天
//	 * 
//	 * @author Heikki 2016.03.28
//	 * */
//	// 连接融云服务器
//	private void connect(String token) {
//
//		if (context.getApplicationInfo().packageName.equals(MyApplication
//				.getCurProcessName(context.getApplicationContext()))) {
//
//			/**
//			 * IMKit SDK调用第二步,建立与服务器的连接
//			 */
//			RongIM.connect(token, new RongIMClient.ConnectCallback() {
//
//				/**
//				 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的
//				 * Token
//				 */
//				@Override
//				public void onTokenIncorrect() {
//
//					Log.d("LoginActivity", "--onTokenIncorrect");
//				}
//
//				/**
//				 * 连接融云成功
//				 * 
//				 * @param userid
//				 *            当前 token
//				 */
//				@Override
//				public void onSuccess(String userid) {
//
//					Log.d("LoginActivity", "--onSuccess" + userid);
//					// connToast.cancel();
//					Toast.makeText(context, "连接成功", 1).show();
//					if (RongIM.getInstance() != null)
//						// RongIM.getInstance().startConversationList(context);
//						RongIM.getInstance().startConversationList(context);
//				}
//
//				/**
//				 * 连接融云失败
//				 * 
//				 * @param errorCode
//				 *            错误码，可到官网 查看错误码对应的注释
//				 */
//				@Override
//				public void onError(RongIMClient.ErrorCode errorCode) {
//
//					Log.d("LoginActivity", "--onError" + errorCode);
//				}
//
//			});
//		}
//
//	}
//
//	// sha1编码
//	public static String SHA1(String decript) {
//		try {
//			MessageDigest digest = java.security.MessageDigest
//					.getInstance("SHA-1");
//			digest.update(decript.getBytes());
//			byte messageDigest[] = digest.digest();
//			// Create Hex String
//			StringBuffer hexString = new StringBuffer();
//			// 字节数组转换为 十六进制 数
//			for (int i = 0; i < messageDigest.length; i++) {
//				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
//				if (shaHex.length() < 2) {
//					hexString.append(0);
//				}
//				hexString.append(shaHex);
//			}
//			return hexString.toString();
//
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		}
//		return "";
//	}
//
//	@Override
//	public UserInfo getUserInfo(String s) {
//		for (Friend i : userIdList) {
//			if (i.getUserId().equals(s)) {
//				Log.i("getUserInfo", "adapter:" + i.getUserName());
//				return new UserInfo(i.getUserId(), i.getUserName(), Uri.parse(i
//						.getPortraitUri()));
//			}
//		}
//		return null;
//	}
//
//	@Override
//	public void onClick(View v) {
//		Users curUser = MyApplication.getCurrentUser();
//		if (curUser != null) {
//			switch (v.getId()) {
//			case R.id.ll_message_chat:
//				// 从sharepreference中获取用户及 好友信息
//				userIdList = new ArrayList<Friend>();
//				SharedPreferences sp = context.getSharedPreferences(
//						"friendInfo", Context.MODE_PRIVATE);
//				String friendList = sp.getString("friendList", null);
//				Gson gson = new Gson();
//				Type type = new TypeToken<ArrayList<Friend>>() {
//				}.getType();
//				if (friendList != null) {
//					userIdList = gson.fromJson(friendList, type);
//					Log.i("FriendList", "adapter not null:" + friendList);
//				} else {
//					Log.i("FriendList", "adapter is null:" + friendList);
//					userIdList.add(new Friend(curUser.getId() + "", curUser
//							.getName(), curUser.getPhoto() == null ? "null"
//							: curUser.getPhoto()));
//				}
//				if (MyApplication.getCurToken() != null) {
//					RongIM.getInstance().startConversationList(context);
//				} else {
//					Toast.makeText(context, "正在连接服务器...", Toast.LENGTH_SHORT)
//							.show();
//					getToken(MyApplication.getCurrentUser().getId(),
//							MyApplication.getCurrentUser().getName(),
//							MyApplication.getCurrentUser().getPhoto());
//				}
//				break;
//			case R.id.ll_message_pinglun:
//				context.startActivity(new Intent(context, CommentActivity.class));
//				break;
//			default:
//				break;
//			}
//		} else {
//			Toast.makeText(context, "请先登录！", Toast.LENGTH_SHORT).show();
//		}
//		
//	}

}
