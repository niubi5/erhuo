package com.geminno.erhuo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.geminno.erhuo.adapter.LogisticsAdapter;
import com.geminno.erhuo.entity.Donation;
import com.geminno.erhuo.entity.Logistics;
import com.geminno.erhuo.entity.Users;
import com.geminno.erhuo.utils.Bimp;
import com.geminno.erhuo.utils.FileUtils;
import com.geminno.erhuo.utils.ImageItem;
import com.geminno.erhuo.utils.PublicWay;
import com.geminno.erhuo.utils.Res;
import com.geminno.erhuo.utils.Url;
import com.geminno.erhuo.view.AddImageView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

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
	 * 请求捐赠收货人名字
	 */
	private EditText consignee;
	/**
	 * 请求捐赠地址
	 */
	private EditText etAddress;
	/**
	 * 发布
	 */
	private Button btnDonate;
	/**
	 * 物流名称
	 */
	private String logisticsName;
	/**
	 *  收货人联系电话
	 */
	private EditText etPhone;
	/**
	 * 多图片上传
	 */
	private GridView doantion_noScrollgridview;
	private GridAdapter adapter;
	private View parentView;
	private PopupWindow pop = null;
	private LinearLayout ll_popup;
	public static Bitmap bimap;
	private List<String> imagePathList;
	private String filePath;
	private String fileName;
	private Builder builder;
	private Users user;

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
		// setContentView(R.layout.activity_donate_request);
		// 03.16修改，多图片上传
		// 初始化文件夹路径和R资源
		Res.init(this);
		// 初始化添加图片的按钮
		bimap = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon_addpic_unfocused);
		// 将当前activity存放到PublicWay.activityList里
		PublicWay.activityList.add(this);
		parentView = getLayoutInflater().inflate(
				R.layout.activity_donate_request, null);
		setContentView(parentView);
		Init();

		initView();
		initDatas();

		// 设置适配器
		LogisticsAdapter adapter = new LogisticsAdapter(logistics, this);
		logisticSpinner.setAdapter(adapter);
		// 下拉列表item监听
		logisticSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				logisticsName = verifys.get(position);
				// Log.i("logisticsName", logisticsName);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * 发布请求
	 */
	@SuppressLint("ShowToast")
	@Override
	public void onClick(View v) {
		// Toast toast = new Toast(getApplicationContext());
		switch (v.getId()) {
		case R.id.et_donate_title:
			if(user.getName().equals("")){
				Toast.makeText(this, "您还没有设置用户名，请先完善资料", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(this,EditUserInfoActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.btn_request_donate:
			Log.i("MyToast", "点击了");
			// View view = getLayoutInflater().inflate(
			// R.layout.donate_value_null_toast,
			// (ViewGroup) findViewById(R.id.mytoast));
			// TextView toastText = (TextView) view
			// .findViewById(R.id.mytoast_text);
			/**
			 * 获取请求捐赠页面用户输入值
			 */
			String title = etTitle.getText().toString();
			String content = etContent.getText().toString();
			String geterName = consignee.getText().toString();
			String phone = etPhone.getText().toString();
			String address = etAddress.getText().toString();
			String logistics = logisticsName;
			//
			// // 设置Toast显示的位置
			// toast.setGravity(Gravity.CENTER, 0, 0);
			// // 设置Toast显示的时间
			// toast.setDuration(Toast.LENGTH_SHORT);
			// // 要显示的view
			// toast.setView(view);
			// toast.show();
			
			if (!title.equals("")) {
				if (!content.equals("")) {
					if (!geterName.equals("")) {
						if(!phone.equals("")){
							
						
						if (!address.equals("")) {
							// toastText.setText("务必把收货地址填写上");
							/**
							 * 获得用户输入的信息,将其封装成Donation对象
							 */
							Donation donation = new Donation();
							// 获取当前用户id
							int userID = MyApplication.getCurrentUser().getId();
//							if(String.valueOf(userID) == null){
//								Toast.makeText(this, "请登录", Toast.LENGTH_SHORT).show();
//								break;
//							}else{
							donation.setUserId(userID);
							donation.setTitle(title);
							donation.setDetail(content);
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd hh:mm:ss");
							donation.setPubTime(sdf.format(new Date()));
							donation.setLogistics(logistics);
							donation.setConsignee(geterName);
							donation.setPhone(phone);
							donation.setAddress(address);

							Log.i("donation",
									"" + donation.getUserId()
											+ donation.getTitle()
											+ donation.getDetail()
											+ donation.getPubTime()
											+ donation.getLogistics()
											+ donation.getAddress()
											+ donation.getConsignee()
											+ donation.getPhone());
							Gson gson = new GsonBuilder().setDateFormat(
									"yyyy-MM-dd hh:mm:ss").create();
							String donationGson = gson.toJson(donation);

							// 传参数
//							String url = "http://10.201.1.20:8080/secondHandShop/HelpsServlet";
							String url = Url.getUrlHead() + "/HelpsServlet";
							RequestParams rp = new RequestParams();
							rp.addBodyParameter("DonationRequest", donationGson);

							int count = 0;
							for (ImageItem image : Bimp.tempSelectBitmap) {
								File file = new File(image.getImagePath());
								rp.addBodyParameter(1 + count + getNowTime(),
										file);
								Log.i("uploadimage", 1 + count + getNowTime());
								count++;
								Log.i("donation", String.valueOf(count));
							}
							Log.i("params", rp.toString());
							// 连接服务器
							HttpUtils hu = new HttpUtils();
							hu.send(HttpMethod.POST, url, rp,
									new RequestCallBack<String>() {

										@Override
										public void onFailure(
												HttpException arg0, String arg1) {
											Log.i("Request", "请求失败");
											Toast.makeText(
													getApplicationContext(),
													"请求失败,请检查您的网络设置",
													Toast.LENGTH_LONG).show();
										}

										@Override
										public void onSuccess(
												ResponseInfo<String> arg0) {
											Log.i("Request", "请求成功");
											if (arg0.result != null) {
												Toast.makeText(
														getApplicationContext(),
														"您的请求发送成功",
														Toast.LENGTH_SHORT)
														.show();
												DonateRequestActivity.this.finish();
											}
										}
									});
						} else {

							Toast.makeText(this, "务必把收货地址填写上",
									Toast.LENGTH_SHORT).show();
							break;
						}
						}else{
							Toast.makeText(this, "联系方式必须要",
									Toast.LENGTH_SHORT).show();
							break;
						}
						
					} else {
						// toastText.setText("收货人是谁呢？");
						Toast.makeText(this, "收货人是谁呢？", Toast.LENGTH_SHORT)
								.show();
						break;
					}
				} else {
					// toastText.setText("别忘了描述一下您的需要哦！");
					Toast.makeText(this, "别忘了详细描述一下您的需要哦！", Toast.LENGTH_SHORT)
							.show();
					break;
				}
			} else {
				Toast.makeText(this, "取个标题吧", Toast.LENGTH_SHORT).show();
				// toastText.setText("取个标题吧");
				break;
			}
		}
		// Intent intent = new Intent(this,MainActivity.class);
		// startActivity(intent);

	}

	/**
	 * 实例化控件对象
	 */
	public void initView() {
		ivBack = (ImageView) findViewById(R.id.iv_donaton_request_back);
		ivBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});
		logisticSpinner = (Spinner) findViewById(R.id.sp_logistics);
		etTitle = (EditText) findViewById(R.id.et_donation_title);
		etTitle.setOnClickListener(this);
		etContent = (EditText) findViewById(R.id.et_donation_content);
		spLogistics = (Spinner) findViewById(R.id.sp_logistics);
		// 收货人姓名，电话，收货地址
		consignee = (EditText) findViewById(R.id.et_donation_getername);
		etPhone = (EditText) findViewById(R.id.et_donation_consignee_phone);
		etAddress = (EditText) findViewById(R.id.et_donation_address);
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
		
		user = MyApplication.getCurrentUser();

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

	@SuppressWarnings("deprecation")
	@SuppressLint("InflateParams")
	// 初始化数据
	public void Init() {
		// 初始化popupWindow
		pop = new PopupWindow(DonateRequestActivity.this);
		// 解析popupwindow布局
		View view = getLayoutInflater().inflate(R.layout.item_popupwindows,
				null);
		ll_popup = (LinearLayout) view.findViewById(R.id.ll_pop);
		// 设置popupwindow属性
		pop.setWidth(LayoutParams.MATCH_PARENT);
		pop.setHeight(LayoutParams.WRAP_CONTENT);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setContentView(view);
		// 根布局
		RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
		// 拍照
		Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
		// 相册
		Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
		// 取消
		Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);
		// 点击根布局取消popupWindow
		parent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		// 点击拍照
		bt1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				photo();
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		// 点击进入相册
		bt2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(DonateRequestActivity.this,
						AlbumActivity.class);

				intent.putExtra("activity", "donateRequestActivity");
				startActivity(intent);
				// activity切换时动画
				overridePendingTransition(R.anim.activity_translate_in,
						R.anim.activity_translate_out);
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		// 点击取消
		bt3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		// 初始化图片选择框GridView
		doantion_noScrollgridview = (GridView) findViewById(R.id.doantion_noScrollgridview);

		// 隐藏
		// 设置点击GridView时出现背景
		doantion_noScrollgridview.setSelector(new ColorDrawable(
				Color.TRANSPARENT));
		// 初始化适配器
		adapter = new GridAdapter(this);
		adapter.update();
		// 设置适配器
		doantion_noScrollgridview.setAdapter(adapter);
		// 设置GridView的item点击事件
		doantion_noScrollgridview
				.setOnItemClickListener(new OnItemClickListener() {

					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						if (arg2 == Bimp.tempSelectBitmap.size()) { // 如果点击的是添加图片按钮
							Log.i("ddddddd", "----------");
							// 点击item切换弹出popupwindow时的动画
							// 隐藏输入法
							InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext()
									.getSystemService(
											Context.INPUT_METHOD_SERVICE);
							inputMethodManager.hideSoftInputFromWindow(
									doantion_noScrollgridview.getWindowToken(),
									0);
							ll_popup.startAnimation(AnimationUtils
									.loadAnimation(DonateRequestActivity.this,
											R.anim.activity_translate_in));
							// 设置popupwindow显示位置
							pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
						} else {// 点击的是已添加的图片
							// 跳转到进行图片浏览时的界面
							Intent intent = new Intent(
									DonateRequestActivity.this,
									GalleryActivity.class);
							intent.putExtra("position", "1");
							intent.putExtra("ID", arg2);
							startActivity(intent);
						}
					}
				});

	}

	// GridView的适配器
	@SuppressLint("HandlerLeak")
	public class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private int selectedPosition = -1;
		private boolean shape;

		public boolean isShape() {
			return shape;
		}

		public void setShape(boolean shape) {
			this.shape = shape;
		}

		public GridAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		// 更新数据
		public void update() {
			// loading();//会造成OOM
			adapter.notifyDataSetChanged();
		}

		// 获得选中的图片的数量
		public int getCount() {
			if (Bimp.tempSelectBitmap.size() == 9) {
				return 9;
			}
			return (Bimp.tempSelectBitmap.size() + 1);
		}

		// 获得指定item
		public Object getItem(int arg0) {
			return null;
		}

		// 获得指定item的id
		public long getItemId(int arg0) {
			return 0;
		}

		// 设置当前选中的position
		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		// 获得当前选中的position
		public int getSelectedPosition() {
			return selectedPosition;
		}

		// 获得view
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_published_grida,
						parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView
						.findViewById(R.id.item_grida_image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position == Bimp.tempSelectBitmap.size()) {
				holder.image.setImageBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.icon_addpic_unfocused));
				if (position == 9) {
					holder.image.setVisibility(View.GONE);
				}
			} else {
				holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position)
						.getBitmap());
			}
			return convertView;
		}

		public class ViewHolder {
			public ImageView image;
		}

		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					adapter.notifyDataSetChanged();
					break;
				}
				super.handleMessage(msg);
			}
		};

		public void loading() {
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						if (Bimp.max == Bimp.tempSelectBitmap.size()) {
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
							break;
						} else {
							Bimp.max += 1;
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
						}
					}
				}
			}).start();
		}
	}

	public String getString(String s) {
		String path = null;
		if (s == null)
			return "";
		for (int i = s.length() - 1; i > 0; i++) {
			s.charAt(i);
		}
		return path;
	}

	protected void onRestart() {
		adapter.update();
		super.onRestart();
	}

	private static final int TAKE_PICTURE = 0x000001;

	// 照相机
	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	// 照相机返回的照片
	protected void onActivityResult(int requestCode, int resultCode,
			final Intent data) {

		switch (requestCode) {
		case TAKE_PICTURE:
			if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {

				String fileName = String.valueOf(System.currentTimeMillis());
				// 获得拍照返回的图片
				Bitmap bm = (Bitmap) data.getExtras().get("data");
				String takePhotoUrl = FileUtils.saveBitmap(bm, fileName);

				ImageItem takePhoto = new ImageItem();
				takePhoto.setBitmap(bm);
				takePhoto.setImagePath(takePhotoUrl);
				Bimp.tempSelectBitmap.add(takePhoto);
			}
			break;
		}
	}

	// 当activity销毁时，清空选中的图片
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (!Bimp.tempSelectBitmap.isEmpty()) {
			Bimp.tempSelectBitmap.clear();
		}
	}

	// 获取系统当前时间
	public String getNowTime() {
		return System.currentTimeMillis() + "";
	}

}
