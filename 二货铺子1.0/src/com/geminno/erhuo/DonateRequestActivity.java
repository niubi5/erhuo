package com.geminno.erhuo;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.geminno.erhuo.adapter.LogisticsAdapter;
import com.geminno.erhuo.entity.Logistics;
import com.geminno.erhuo.view.AddImageView;

/**
 * 点击捐赠页面添加按钮时跳转的页面
 * 
 * @author Administrator
 * 
 */
public class DonateRequestActivity extends Activity implements OnClickListener{

	/**
	 * 请求捐赠标题
	 */
	private EditText etTitle;
	/**
	 * 返回图片
	 */
	private ImageView ivBack;
	/**
	 * 发布技巧图片
	 */
	private ImageView ivDonationDialog;
	/**
	 * 请求捐赠内容
	 */
	private EditText etContent;
	/**
	 * 添加图片控件
	 */
	private AddImageView addImage;
	/**
	 * 存储拍得得照片
	 */
	private List<ImageView> images;
	/**
	 * 物流下拉菜单
	 */
	private Spinner spLogistics;
	/**
	 * 物流名称
	 */
	private TextView tvLogistics;
	/**
	 * 请求捐赠收货人名字
	 */
	private EditText etGeterName;
	/**
	 * 请求捐赠地址
	 */
	private EditText etAddress;
	/**
	 * 发布
	 */
	private Button btnDonate;

	Dialog dialog;

	private AddImageView addImageView;
	private Spinner logisticSpinner;
	// 存放物流信息
	List<Logistics> logistics;
	// 存放物流图片
	List<Integer> imageIds;
	// 存放物流类别
	List<String> verifys;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_donate_request);

		init();
		initDatas();

		// 设置适配器
		LogisticsAdapter adapter = new LogisticsAdapter(logistics, this);
		logisticSpinner.setAdapter(adapter);
	}

	/**
	 * 发布请求
	 */
     @SuppressLint("ShowToast") @Override
	public void onClick(View v) {
		Toast toast = new Toast(getApplicationContext());
		switch (v.getId()) {
		case R.id.btn_request_donate:
			Log.i("MyToast", "点击了");
			View view = getLayoutInflater().inflate(
					R.layout.donate_value_null_toast,
					(ViewGroup) findViewById(R.id.mytoast));
			TextView toastText = (TextView) view
					.findViewById(R.id.mytoast_text);
			String title = etTitle.getText().toString();
			String content = etContent.getText().toString();
			String geterName = etGeterName.getText().toString();
			String address = etAddress.getText().toString();
			// 设置Toast显示的位置
			toast.setGravity(Gravity.CENTER, 0, 0);
			// 设置Toast显示的时间
			toast.setDuration(Toast.LENGTH_SHORT);
			// 要显示的view
			toast.setView(view);
			toast.show();
			if (!title.equals("")) {
				if (!content.equals("")) {
					if (!etGeterName.equals("")) {
						if (address.equals("")) {
							toastText.setText("务必把收货地址填写上");
							break;
//							Toast.makeText(this, "务必把收货地址填写上", Toast.LENGTH_SHORT);
						}
					} else {
						toastText.setText("收货人是谁呢？");
						break;
//						Toast.makeText(this, "收货人是谁呢？", Toast.LENGTH_SHORT);
					}
				} else {
					toastText.setText("别忘了描述一下您的需要哦！");
					break;
//					Toast.makeText(this, "别忘了详细描述一下您的需要哦！", Toast.LENGTH_SHORT);
				}
			} else {
//				Toast.makeText(this, "取个标题吧", Toast.LENGTH_SHORT);
				toastText.setText("取个标题吧");
				break;
			}
		}
	}

	/**
	 * 实例化控件对象
	 */
	public void init() {
		ivBack = (ImageView) findViewById(R.id.iv_donaton_request_back);
		ivBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});
		logisticSpinner = (Spinner) findViewById(R.id.sp_logistics);
		etTitle = (EditText) findViewById(R.id.et_donation_title);
		etContent = (EditText) findViewById(R.id.et_donation_content);
		spLogistics = (Spinner) findViewById(R.id.sp_logistics);
		etGeterName= (EditText) findViewById(R.id.et_donation_getername);
		etAddress = (EditText) findViewById(R.id.et_donation_address);
		addImageView = (AddImageView) findViewById(R.id.addImageView);
		btnDonate = (Button) findViewById(R.id.btn_request_donate);
		// 发布捐赠，将信息提交到Servlet
        btnDonate.setOnClickListener(this);
		ivDonationDialog = (ImageView) findViewById(R.id.iv_donation_dialog);
		// 点击提示对话框
		ivDonationDialog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 设置对话框样式为全屏
				dialog = new Dialog(DonateRequestActivity.this,
						R.style.DonationDialog);
				// 去标题
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				View dialogView = LayoutInflater.from(
						DonateRequestActivity.this).inflate(
						R.layout.donate_dialog_guide, null);
				dialog.setContentView(dialogView);
				ImageView image = (ImageView) dialogView
						.findViewById(R.id.donation_dialog_back);
				image.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				Window window = dialog.getWindow();
				// 背景透明
				window.setBackgroundDrawableResource(android.R.color.transparent);
				dialog.show();
			}
		});

	}

	/**
	 * 设置物流下拉菜单数据源
	 */
	public void initDatas() {
		// 物流图片
		imageIds = new ArrayList<Integer>();
		imageIds.add(R.drawable.shengtong);
		imageIds.add(R.drawable.zhongtong);
		imageIds.add(R.drawable.shunfeng);
		imageIds.add(R.drawable.tiantian);
		imageIds.add(R.drawable.ems);
		imageIds.add(R.drawable.youzheng);
		imageIds.add(R.drawable.quanfeng);
		imageIds.add(R.drawable.yunda);

		// 物流类别
		verifys = new ArrayList<String>();
		verifys.add("申 通 快 递");
		verifys.add("中 通 快 递");
		verifys.add("顺 丰 快 递");
		verifys.add("天 天 快 递");
		verifys.add("ems 快 递");
		verifys.add("邮 政 快 递");
		verifys.add("全 峰 快 递");
		verifys.add("韵 达 快 递");

		// 物流信息
		logistics = new ArrayList<Logistics>();
		for (int i = 0; i < imageIds.size(); i++) {
			logistics.add(new Logistics(imageIds.get(i), verifys.get(i)));
		}
	}

}
