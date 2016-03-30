package com.geminno.erhuo.adapter;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.geminno.erhuo.R;
import com.geminno.erhuo.entity.Remark;
import com.geminno.erhuo.entity.Users;

/**
 * @author LuoShiHeng
 * @version 创建时间:2016-3-29下午2:09:19
 */
public class RemarkAdapter extends BaseAdapter {

	private Context context;
	private List<Map<Remark, Users>> listRemarkUsers;

	public RemarkAdapter(Context context,
			List<Map<Remark, Users>> listRemarkUsers) {
		this.context = context;
		this.listRemarkUsers = listRemarkUsers;
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
			viewHolder.commentIv = (ImageView) convertView
					.findViewById(R.id.comment_iv);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Map<Remark, Users> remarkUsers = listRemarkUsers.get(position);
		Set<Entry<Remark, Users>> entry = remarkUsers.entrySet();
		for (Map.Entry<Remark, Users> en : entry) {
			Remark remark = en.getKey();
			Users user = en.getValue();
			viewHolder.userHead.setImageResource(R.drawable.header_default);
			viewHolder.userName.setText(user.getName());
			viewHolder.commentContent.setText(remark.getComment_content());
			viewHolder.commentTime.setText(remark.getComment_time());
		}
		// 子评论按钮
		viewHolder.commentIv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		return convertView;
	}

	class ViewHolder {
		ImageView userHead;
		TextView userName;
		TextView commentContent;
		TextView commentTime;
		ImageView commentIv;
	}

}
