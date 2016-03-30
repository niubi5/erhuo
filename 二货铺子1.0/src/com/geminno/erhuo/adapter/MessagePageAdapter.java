package com.geminno.erhuo.adapter;

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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.geminno.erhuo.CommentActivity;
import com.geminno.erhuo.ConversationListActivity;
import com.geminno.erhuo.GoodsDetialActivity;
import com.geminno.erhuo.MyApplication;
import com.geminno.erhuo.R;
import com.geminno.erhuo.SystemMsgActivity;
import com.geminno.erhuo.entity.Messages;
import com.geminno.erhuo.entity.Users;
import com.geminno.erhuo.utils.Friend;
import com.geminno.erhuo.view.PullToFreshListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * @author LuoShiHeng
 * @version 创建时间:2016-3-27下午5:12:49
 */
public class MessagePageAdapter extends BaseAdapter implements
		OnItemClickListener,UserInfoProvider{

	private Context context;
	// 消息图标
	private int[] messageImages = new int[] { R.drawable.message_chat,
			R.drawable.message_pinglun, R.drawable.message_system };
	// 消息标题
	private String[] messageTitles = new String[] { "私聊", "收到的评论", "系统消息" };
	private String[] messageState = new String[] { "还没有收到私聊哦", "还没有收到评论哦",
			"暂无消息" };
	private List<Messages> message;
	private PullToFreshListView pullToFreshListView;
	private List<Friend> userIdList;//

	public MessagePageAdapter(Context context) {
		this.context = context;
	}

	public MessagePageAdapter(Context context, List<Messages> message,
			PullToFreshListView pullToFreshListView) {
		this.context = context;
		this.message = message;
		this.pullToFreshListView = pullToFreshListView;
		pullToFreshListView.setOnItemClickListener(this);
		RongIM.setUserInfoProvider(this, true);
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.message_item, null);
			viewHolder.messageImage = (ImageView) convertView
					.findViewById(R.id.message_image);
			viewHolder.messageTitle = (TextView) convertView
					.findViewById(R.id.message_title);
			viewHolder.messageState = (TextView) convertView
					.findViewById(R.id.message_state);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.messageImage.setImageResource(messageImages[position]);
		viewHolder.messageTitle.setText(messageTitles[position]);
		if (message == null || message.isEmpty()) {
			viewHolder.messageState.setText(messageState[position]);
		} else {

		}
		return convertView;
	}

	public class ViewHolder {
		ImageView messageImage;
		TextView messageTitle;
		TextView messageState;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (position) {
		case 1:
			Users curUser = MyApplication.getCurrentUser();
			if (curUser != null) {
				//从sharepreference中获取用户及 好友信息
				userIdList = new ArrayList<Friend>();
				SharedPreferences sp = context.getSharedPreferences("friendInfo", Context.MODE_PRIVATE);
				String friendList = sp.getString("friendList", null);
				Gson gson = new Gson();
				Type type = new TypeToken<ArrayList<Friend>>() {}.getType();
				if(friendList != null){
					userIdList = gson.fromJson(friendList, type);
					Log.i("FriendList", "adapter not null:"+friendList);
				}else{
					Log.i("FriendList", "adapter is null:"+friendList);
					userIdList.add(new Friend(curUser.getId()+"", curUser.getName(), curUser.getPhoto() == null?"null":curUser.getPhoto()));					
				}
				if (MyApplication.getCurToken() != null) {
					RongIM.getInstance().startConversationList(context);
				} else {
					Toast.makeText(context,"正在连接服务器...", Toast.LENGTH_SHORT).show();
					getToken(MyApplication.getCurrentUser().getId(),MyApplication.getCurrentUser().getName(),MyApplication.getCurrentUser().getPhoto());
				}
			} else {
				Toast.makeText(context, "请先登录！", Toast.LENGTH_SHORT).show();
			}
			break;
		case 2:
			if (MyApplication.getCurrentUser() != null) {
				context.startActivity(new Intent(context, CommentActivity.class));
			} else {
				Toast.makeText(context, "请先登录！", Toast.LENGTH_SHORT).show();
			}
			break;
		case 3:
			if (MyApplication.getCurrentUser() != null) {
				context.startActivity(new Intent(context,
						SystemMsgActivity.class));
			} else {
				Toast.makeText(context, "请先登录！", Toast.LENGTH_SHORT).show();
			}
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
		http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				Log.i("RongCloudDemo", "--result" + arg0.result);
				// Toast.makeText(MainActivity.this, arg0.result, 1).show();
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
					// TODO Auto-generated catch block
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

		if (context.getApplicationInfo().packageName.equals(MyApplication
				.getCurProcessName(context.getApplicationContext()))) {

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
					Toast.makeText(context, "连接成功", 1).show();
					if (RongIM.getInstance() != null)
		                  // RongIM.getInstance().startConversationList(context);
						RongIM.getInstance().startConversationList(context);
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

	@Override
	public UserInfo getUserInfo(String s) {
		for (Friend i : userIdList) {
            if (i.getUserId().equals(s)) {
                Log.i("getUserInfo", "adapter:"+i.getUserName());
                return new UserInfo(i.getUserId(),i.getUserName(), Uri.parse(i.getPortraitUri()));
            }
        }
		return null;
	}

}
