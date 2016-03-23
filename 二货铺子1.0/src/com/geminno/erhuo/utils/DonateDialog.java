package com.geminno.erhuo.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.geminno.erhuo.R;

public class DonateDialog extends Dialog {
	
	DonateDialog dialog;

	/**
	 * 构造方法 在里面设置Dialog显示在哪个activity
	 * 
	 * @param context
	 */
	public DonateDialog(Context context) {
		super(context);
		// 设置显示在哪个activity
		setOwnerActivity((Activity) context);
	}
	
	

	/**
	 * 静态内部类
	 * 
	 * @author Administrator
	 * 
	 */
	public static class Builder {
		/**
		 * 显示的activity
		 */
		private Context context;
		/**
		 * 标题，显示发布技巧
		 */
		private String title;
		/**
		 * 发布技巧内容
		 */
		private String detailOne;
		private String detail0neNext;
		private String detailTwo;
		private String detailThree;
		/**
		 * 发布技巧图片对比1
		 */
		private String imageTextOne;
		private Integer imageOne;
		private Integer imageOneNext;
		/**
		 * 发布技巧图片对比2
		 */
		private String imageTextTwo;
		private Integer imageTwo;
		private Integer imageTwoNext;
		/**
		 * 作者提示信息
		 */
		private String warning;
		/**
		 * 退出图片按钮
		 */
		private Integer btnBack;
		/**
		 * 
		 */
		private View contentView;

		public Builder(Context context) {
			this.context = context;
		}

		public void setTitle(){
			
		}
//		public void setContext(Context context) {
//			this.context = context;
//		}
//
//		public void setTitle(String title) {
//			this.title = title;
//		}
//
//		public void setDetailOne(String detailOne) {
//			this.detailOne = detailOne;
//		}
//
//		public void setDetail0neNext(String detail0neNext) {
//			this.detail0neNext = detail0neNext;
//		}
//
//		public void setDetailTwo(String detailTwo) {
//			this.detailTwo = detailTwo;
//		}
//
//		public void setDetailThree(String detailThree) {
//			this.detailThree = detailThree;
//		}
//
//		public void setImageTextOne(String imageTextOne) {
//			this.imageTextOne = imageTextOne;
//		}
//
//		public void setImageOne(Integer imageOne) {
//			this.imageOne = imageOne;
//		}
//
//		public void setImageOneNext(Integer imageOneNext) {
//			this.imageOneNext = imageOneNext;
//		}
//
//		public void setImageTextTwo(String imageTextTwo) {
//			this.imageTextTwo = imageTextTwo;
//		}
//
//		public void setImageTwo(Integer imageTwo) {
//			this.imageTwo = imageTwo;
//		}
//
//		public void setImageTwoNext(Integer imageTwoNext) {
//			this.imageTwoNext = imageTwoNext;
//		}
//
//		public void setWarning(String warning) {
//			this.warning = warning;
//		}
//
//		public void setBtnBack(Integer btnBack) {
//			this.btnBack = btnBack;
//		}
//
//		public void setContentView(View contentView) {
//			this.contentView = contentView;
//		}

		/**
		 * 创建一个DonateDialog
		 * 
		 * @return
		 */
		public DonateDialog create() {
			View view = ((LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
					.inflate(R.layout.donate_dialog_guide, null);
			final DonateDialog dialog = new DonateDialog(context);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.
			dialog.addContentView(view, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			TextView textView = (TextView)view.findViewById(R.id.tv_title);
            ImageView imageView = (ImageView) view.findViewById(R.id.donation_dialog_back);
            imageView.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
            dialog.setContentView(view);
			return dialog;

		}
	}

}
