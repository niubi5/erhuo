package com.geminno.erhuo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.geminno.erhuo.adapter.LogisticsAdapter;
import com.geminno.erhuo.entity.Logistics;
import com.geminno.erhuo.utils.DonateDialog;
import com.geminno.erhuo.view.AddImageView;

/**
 * 点击捐赠页面添加按钮时跳转的页面
 * 
 * @author Administrator
 * 
 */
public class DonateRequestActivity extends Activity implements OnClickListener {

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
	 * 请求捐赠电话
	 */
	private EditText etPhone;
	/**
	 * 请求捐赠地址
	 */
	private EditText etAddress;
	/**
	 * 发布
	 */
	private Button btnDonate;

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
		addImageView = (AddImageView) findViewById(R.id.addImageView);
		init();
		initDatas();

		// 设置适配器
		LogisticsAdapter adapter = new LogisticsAdapter(logistics, this);
		logisticSpinner.setAdapter(adapter);
	}

	/**
	 * 发布请求
	 */
	@Override
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
			String phone = etPhone.getText().toString();
			String address = etAddress.getText().toString();
			// 设置Toast显示的位置
			toast.setGravity(Gravity.CENTER, 0, 0);
			// 设置Toast显示的时间
			toast.setDuration(Toast.LENGTH_LONG);
			// 要显示的view
			toast.setView(view);
			toast.show();
			if (title.equals("")) {
				toastText.setText("标题不能为空");
			} else if (content.equals("")) {
				toastText.setText("描述不能为空");
			} else if (phone.equals("")) {
				toastText.setText("电话不能为空");
			} else if (address.equals("")) {
				toastText.setText("地址不能为空");
			}
			break;
		}
	}

	/**
	 * 实例化控件对象
	 */
	public void init() {
		logisticSpinner = (Spinner) findViewById(R.id.sp_logistics);
		etTitle = (EditText) findViewById(R.id.et_donation_title);
		etContent = (EditText) findViewById(R.id.et_donation_content);
		etPhone = (EditText) findViewById(R.id.et_donation_phone);
		etAddress = (EditText) findViewById(R.id.et_donation_address);
		btnDonate = (Button) findViewById(R.id.btn_request_donate);
		btnDonate.setOnClickListener(this);
		ivBack = (ImageView) findViewById(R.id.iv_back);
		// 返回
		ivBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		ivDonationDialog = (ImageView) findViewById(R.id.iv_donation_dialog);
		// 提示
		ivDonationDialog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DonateDialog.Builder dialog = new DonateDialog.Builder(
						DonateRequestActivity.this);
				
				dialog.create().show();
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
