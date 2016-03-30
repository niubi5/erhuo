package com.geminno.erhuo.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.geminno.erhuo.MyApplication;
import com.geminno.erhuo.R;
import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.entity.Remark;
import com.geminno.erhuo.entity.Users;
import com.geminno.erhuo.utils.Url;
import com.geminno.erhuo.view.PullUpToLoadListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * @author LuoShiHeng
 * @version 创建时间:2016-3-29下午2:09:19
 */
public class RemarkAdapter extends BaseAdapter implements OnItemClickListener {

	private Context context;
	private List<Map<Remark, Users>> listRemarkUsers;
	private List<Remark> listRemarks = new ArrayList<Remark>();
	private List<Users> listUsers = new ArrayList<Users>();
	private Users currentUser = MyApplication.getCurrentUser();
	private PullUpToLoadListView pullUpToLoadListView;
	private Goods goods;
	private int currentFloor;

	public RemarkAdapter(Context context,
			List<Map<Remark, Users>> listRemarkUsers, Goods goods,
			PullUpToLoadListView pullUpToLoadListView) {
		this.context = context;
		this.listRemarkUsers = listRemarkUsers;
		this.goods = goods;
		this.pullUpToLoadListView = pullUpToLoadListView;
		pullUpToLoadListView.setOnItemClickListener(this);
		for (Map<Remark, Users> map : listRemarkUsers) {
			Set<Entry<Remark, Users>> entry = map.entrySet();
			for (Map.Entry<Remark, Users> en : entry) {
				Remark remark = en.getKey();
				Users user = en.getValue();
				listRemarks.add(remark);
				listUsers.add(user);
			}
		}
	}

	@Override
	public int getCount() {
		return listRemarkUsers.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.remark_item, null);
			viewHolder.userHead = (ImageView) convertView
					.findViewById(R.id.comment_userhead);
			viewHolder.userName = (TextView) convertView
					.findViewById(R.id.comment_username);
			viewHolder.commentContent = (TextView) convertView
					.findViewById(R.id.comment_content);
			viewHolder.commentTime = (TextView) convertView
					.findViewById(R.id.comment_time);
//			viewHolder.commentFloor = (TextView) convertView
//					.findViewById(R.id.comment_floor);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Remark remark = listRemarks.get(position);
		Users user = listUsers.get(position);
		viewHolder.userHead.setImageResource(R.drawable.header_default);
		viewHolder.commentContent.setText(remark.getComment_content());
		viewHolder.commentTime.setText(remark.getComment_time()
				.substring(5, 16));
		if (remark.getFatherId() == 0) {
			// 没有父评论ID 说明是一级评论 只有一个用户名
			viewHolder.userName.setText(user.getName());
		} else {
			for (Users user1 : listUsers) {// 遍历User集合 找出父评论用户的用户名
				if (remark.getFatherId() == user1.getId()) {
					Spannable sp = new SpannableString(user.getName() + " 回复 "
							+ user1.getName());
					int start = user.getName().length();
					// 将回复设置为蓝色
					sp.setSpan(new ForegroundColorSpan(context.getResources()
							.getColor(R.color.my_blue)), start + 1, start + 3,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					viewHolder.userName.setText(sp);
				}
			}
		}
		return convertView;
	}

	class ViewHolder {
		ImageView userHead;
		TextView userName;
		TextView commentContent;
		TextView commentTime;
		ScrollView scroll;
//		TextView commentFloor;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view,
			final int position, long id) {
		// 当前用户未登录 则弹窗提示
		if (currentUser == null) {
			Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
		} else {
			// 弹出Popupwindow
			View contentView = LayoutInflater.from(context).inflate(
					R.layout.comment_popup, null);
			final PopupWindow pop = new PopupWindow(contentView,
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
			pop.setContentView(contentView);
			final EditText commentContent = (EditText) contentView
					.findViewById(R.id.pop_comment_content);
			ImageView commentSend = (ImageView) contentView
					.findViewById(R.id.pop_comment_send);
			commentContent.setHint("回复 " + listUsers.get(position).getName()
					+ ":");

			// 发送留言
			commentSend.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 当评论不为空时 发送留言，存入数据库
					if (commentContent.getText() != null) {
						HttpUtils http = new HttpUtils();
						String urlHead = Url.getUrlHead();
						String url = urlHead + "/AddCommentServlet";
						RequestParams params = new RequestParams();
						params.addBodyParameter("goodsId", goods.getId() + "");
						params.addBodyParameter("userId", currentUser.getId()
								+ "");
						params.addBodyParameter("commentContent",
								commentContent.getText().toString());
						params.addBodyParameter("fatherId",
								listRemarks.get(position).getId() + "");
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
									}
								});
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
			popupInputMethodWindow();
		}

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

}
