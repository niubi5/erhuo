package com.geminno.erhuo.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.geminno.erhuo.R;
import com.geminno.erhuo.entity.Messages;

/**
 * @author LuoShiHeng
 * @version 创建时间:2016-3-27下午5:12:49
 */
public class MessagePageAdapter extends BaseAdapter {

	private Context context;
	// 消息图标
	private int[] messageImages = new int[] { R.drawable.message_chat,
			R.drawable.message_pinglun, R.drawable.message_system };
	// 消息标题
	private String[] messageTitles = new String[]{"私聊", "收到的评论", "系统消息"};
	private String[] messageState = new String[]{"还没有收到私聊哦", "还没有收到评论哦", "暂无消息"};
	private List<Messages> message;
	
	public MessagePageAdapter(Context context) {
		this.context = context;
	}
	
	public MessagePageAdapter(Context context, List<Messages> message){
		this.context = context;
		this.message = message;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
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
		if(message == null || message.isEmpty()){
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

}
