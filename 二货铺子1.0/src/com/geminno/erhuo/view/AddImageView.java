package com.geminno.erhuo.view;

import com.geminno.erhuo.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

public class AddImageView extends FrameLayout {

	public AddImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// huagnjie 10:46
		LayoutInflater.from(context).inflate(1, this);

		System.out.println();
		LayoutInflater.from(context).inflate(2, this);

	}
}
