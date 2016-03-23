package com.geminno.erhuo.view;

import com.geminno.erhuo.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyTitleBar extends LinearLayout {
	private ImageView leftImage;
	private ImageView rightImage;
	private TextView tvTitle;
	private LinearLayout titleLayout;

	public MyTitleBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context, null);
	}

	public MyTitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context, attrs);
	}

	public MyTitleBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		LayoutInflater.from(context).inflate(R.layout.my_title_bar, this);
		leftImage = (ImageView) findViewById(R.id.iv_left);
		rightImage = (ImageView) findViewById(R.id.iv_right);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		titleLayout = (LinearLayout) findViewById(R.id.root);
		parseStyle(context, attrs);
	}

	@SuppressWarnings("deprecation")
	private void parseStyle(Context context, AttributeSet attrs) {
		if (attrs != null) {
			TypedArray ta = context.obtainStyledAttributes(attrs,
					R.styleable.mytitlebar);
			// 获取文本
			String text = ta.getString(R.styleable.mytitlebar_titleBarTitle);
			tvTitle.setText(text);
			// 默认字体颜色
			int color = ta.getColor(R.styleable.mytitlebar_titleBarTextColor,
					0XFFFFFFFF);
			tvTitle.setTextColor(color);

			Drawable leftDrawable = ta
					.getDrawable(R.styleable.mytitlebar_titleBarLeftImage);
			// 如果设置了左边图片
			if (null != leftDrawable) {
				leftImage.setImageDrawable(leftDrawable);
			}

			Drawable rightDrawable = ta
					.getDrawable(R.styleable.mytitlebar_titleBarRightImage);
			// 如果设置了右边图片
			if (null != rightDrawable) {
				rightImage.setImageDrawable(rightDrawable);
			}

			Drawable background = ta
					.getDrawable(R.styleable.mytitlebar_titleBarBackground);
			// 如果设置了背景
			if (null != background) {
				titleLayout.setBackgroundDrawable(background);
			}
			ta.recycle();
		}
	}

	public void setLeftImageResource(int resId) {
		leftImage.setImageResource(resId);
	}

	public void setRightImageResource(int resId) {
		rightImage.setImageResource(resId);
	}

	public void setTitle(String title) {
		tvTitle.setText(title);
	}

	public void setBackgroundColor(int color) {
		titleLayout.setBackgroundColor(color);
	}
}
