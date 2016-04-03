package com.geminno.erhuo.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.geminno.erhuo.GoodsDetialActivity;
import com.geminno.erhuo.MyApplication;
import com.geminno.erhuo.R;
import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.entity.Remark;
import com.geminno.erhuo.entity.Users;
import com.geminno.erhuo.utils.Url;
import com.geminno.erhuo.view.RefreshListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author LuoShiHeng
 * @version 创建时间:2016-3-31下午7:23:05
 */
public class CommentListAdapter extends BaseAdapter implements
		OnItemClickListener {

	private Context context;
	private List<Map<Remark, Users>> listRemarkUsers;
	private List<Map<Map<Goods, Users>, List<String>>> listAll;
	private Remark remark = null;
	private List<Remark> listRemarks = new ArrayList<>();
	private ImageLoader imageLoader;
	private Map<Map<Goods, Users>, List<String>> map2 = new HashMap<Map<Goods, Users>, List<String>>();
	private List<String> urls = new ArrayList<String>();
	private Goods goods;
	private List<Goods> listGoods = new ArrayList<Goods>();
	private Users user;
	private Users goodsUser;
	private Users currentUser;
	private float scale;// 屏幕密度
	private boolean isRefresh;// 是否是刷新操作
	private List<Object> userGoodsUrls = new ArrayList<Object>();
	private boolean first = true;
	private boolean second = false;
	private boolean third = false;
	private RefreshListView refreshListView;
	private ArrayList<Users> listCommentUsers = new ArrayList<Users>();
	private int action;
	private final int REPLY = 1;
	private final int JUMP = 2;
	private final int NONE = 0;

	public CommentListAdapter(Context context,
			List<Map<Remark, Users>> listRemarkUsers,
			List<Map<Map<Goods, Users>, List<String>>> listAll,
			RefreshListView refreshListView, boolean isRefresh) {
		this.context = context;
		this.listRemarkUsers = listRemarkUsers;
		this.listAll = listAll;
		this.refreshListView = refreshListView;
		this.isRefresh = isRefresh;
		scale = context.getResources().getDisplayMetrics().density;
		imageLoader = ImageLoader.getInstance();
		currentUser = MyApplication.getCurrentUser();
		refreshListView.setOnItemClickListener(this);
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.msg_comment_item, null);
			holder.userHead = (ImageView) convertView
					.findViewById(R.id.msg_comment_user_head);
			holder.userName = (TextView) convertView
					.findViewById(R.id.msg_comment_user_name);
			holder.commentTime = (TextView) convertView
					.findViewById(R.id.msg_comment_time);
			holder.goodsImage = (ImageView) convertView
					.findViewById(R.id.msg_comment_goods_image);
			holder.goodsName = (TextView) convertView
					.findViewById(R.id.msg_comment_goods_name);
			holder.commentReply = (TextView) convertView
					.findViewById(R.id.msg_reply);
			holder.goodsPrice = (TextView) convertView
					.findViewById(R.id.msg_goods_sold_price);
			holder.goodsBuyPrice = (TextView) convertView
					.findViewById(R.id.msg_goods_buy_price);
			holder.goodsInfo = (TextView) convertView
					.findViewById(R.id.msg_comment_goods_info);
			holder.commentContent = (TextView) convertView
					.findViewById(R.id.msg_comment_userandcontent);
			holder.goodsContainer = (LinearLayout) convertView
					.findViewById(R.id.msg_goods_container);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (isRefresh) {
			listRemarks.clear();
			userGoodsUrls.clear();// 如果是刷新操作，清空集合
			listCommentUsers.clear();
			listGoods.clear();
			isRefresh = false;
		}
		Map<Remark, Users> map = listRemarkUsers.get(position);
		Set<Entry<Remark, Users>> entry = map.entrySet();
		for (Map.Entry<Remark, Users> en : entry) {
			remark = en.getKey();
			if (!listRemarks.contains(remark)) {
				listRemarks.add(remark);
			}
			user = en.getValue();// 评论者
			if (!listCommentUsers.contains(user)) {
				listCommentUsers.add(user);
			}
		}
		if (user.getPhoto() != null) {
			imageLoader.displayImage(user.getPhoto(), holder.userHead);
		} else {
			holder.userHead.setImageResource(R.drawable.header_default);
		}
		holder.userName.setText(user.getName());
		holder.commentTime.setText(remark.getComment_time().substring(5, 16));
		map2 = listAll.get(position);
		Set<Map.Entry<Map<Goods, Users>, List<String>>> entry2 = map2
				.entrySet();
		for (Map.Entry<Map<Goods, Users>, List<String>> en : entry2) {
			Map<Goods, Users> goodsUsers = en.getKey();// Map中key（商品用户对象）
			urls = en.getValue();
			Set<Entry<Goods, Users>> entry1 = goodsUsers.entrySet();
			for (Map.Entry<Goods, Users> en1 : entry1) {
				// 取得最里面的map中的goods和users对象
				goods = en1.getKey();
				goodsUser = en1.getValue();// 商品拥有者
				if (!listGoods.contains(goods)) {
					listGoods.add(goods);
				}
			}
			// 将数据放入集合，以便商品详情页使用
			userGoodsUrls.add(goodsUser);
			userGoodsUrls.add(goods);
			userGoodsUrls.add(urls);
		}
		imageLoader.displayImage(urls.get(0), holder.goodsImage);
		holder.goodsName.setText(goods.getName());
		holder.goodsPrice.setText("￥" + goods.getSoldPrice());
		holder.goodsBuyPrice.setText("原价￥" + goods.getBuyPrice());
		holder.goodsInfo.setText(goods.getImformation());
		Spannable sp = new SpannableString(user.getName() + " 回复 你:"
				+ remark.getComment_content());
		int start = user.getName().length();
		// 将回复设置为蓝色
		sp.setSpan(
				new ForegroundColorSpan(context.getResources().getColor(
						R.color.my_blue)), start + 1, start + 3,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		holder.commentContent.setText(sp);
		// 商品contianer点击事件
		holder.goodsContainer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				action = JUMP;
				onItemClick(refreshListView, v, position, position);
				action = NONE;
			}
		});
		return convertView;
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

	class ViewHolder {
		ImageView userHead;
		TextView userName;
		TextView commentTime;
		ImageView goodsImage;
		TextView goodsName;
		TextView goodsPrice;
		TextView goodsBuyPrice;
		TextView goodsInfo;
		TextView commentContent;
		TextView commentReply;
		LinearLayout goodsContainer;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view,
			final int position, long id) {
		switch (action) {
		case JUMP:
			if (!userGoodsUrls.isEmpty()) {
				Intent intent = new Intent(context, GoodsDetialActivity.class);
				Bundle bundle = new Bundle();
				int i = position;
				Goods goods = null;
				for (int j = i * 3; j < i * 3 + 3; j++) {
					if (first) {
						Users user = (Users) userGoodsUrls.get(j);
						bundle.putSerializable("user", user);
						first = false;
						second = true;
					} else if (second) {
						goods = (Goods) userGoodsUrls.get(j);
						bundle.putSerializable("goods", goods);
						second = false;
						third = true;
					} else if (third) {
						ArrayList<String> urls = (ArrayList<String>) userGoodsUrls
								.get(j);
						bundle.putStringArrayList("urls", urls);
						third = false;
						first = true;
					}
				}
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
			break;
		default:
			// 弹出Popupwindow
			View contentView = LayoutInflater.from(context).inflate(
					R.layout.comment_popup, null);
			int px = (int) (60 * scale + 0.5f);
			final PopupWindow pop = new PopupWindow(contentView,
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
			pop.setContentView(contentView);
			final EditText commentContent = (EditText) contentView
					.findViewById(R.id.pop_comment_content);
			ImageView commentSend = (ImageView) contentView
					.findViewById(R.id.pop_comment_send);
			Log.i("erhuo", "position的值：" + position);
			commentContent.setHint("回复 "
					+ listCommentUsers.get(position - 1).getName() + ":");
			// 当评论不为空时 发送留言，存入数据库
			commentSend.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!TextUtils.isEmpty(commentContent.getText())) {
						HttpUtils http = new HttpUtils();
						String urlHead = Url.getUrlHead();
						String url = urlHead + "/AddCommentServlet";
						RequestParams params = new RequestParams();
						// 设置为不缓存，及时获取数据
						http.configCurrentHttpCacheExpiry(0);
						params.addBodyParameter("goodsId",
								listGoods.get(position - 1).getId() + "");
						params.addBodyParameter("userId", currentUser.getId()
								+ "");
						params.addBodyParameter("commentContent",
								commentContent.getText().toString());
						params.addBodyParameter("fatherId", listRemarks.get(position - 1).getId() + "");
						params.addBodyParameter("id", listRemarks.get(position - 1).getId() + "");
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
										// initCommentData(); // 调用加载数据方法，及时更新数据
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
			break;
		}
	}

}
