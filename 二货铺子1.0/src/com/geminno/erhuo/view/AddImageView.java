package com.geminno.erhuo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.geminno.erhuo.R;

public class AddImageView extends RelativeLayout {

	private ImageView ivAddImage;
	private TextView tvAddText;

	public AddImageView(Context context) {
		this(context,null);
	}
	
	public AddImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		this(context,attrs);
	}


	public AddImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 导入布局
		View v = LayoutInflater.from(context).inflate(R.layout.add_image, this,true);
		ivAddImage = (ImageView) v.findViewById(R.id.iv_add_image);
		tvAddText = (TextView) v.findViewById(R.id.tv_add_image_text);
	}
}
