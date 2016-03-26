package com.geminno.erhuo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.entity.Markets;
import com.geminno.erhuo.utils.Bimp;
import com.geminno.erhuo.utils.FileUtils;
import com.geminno.erhuo.utils.ImageItem;
import com.geminno.erhuo.utils.MyAdapter;
import com.geminno.erhuo.utils.PublicWay;
import com.geminno.erhuo.utils.Res;
import com.geminno.erhuo.utils.ViewHolder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.R.bool;
import android.R.layout;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class PublishGoodsActivity extends Activity implements OnClickListener {
	private ImageView ivback;
	private Spinner typeSpinner;
	private Spinner marketSpinner;
	List<Map<String, Object>> typeList;
	List<String> marketList;
	// 03.16修改，多图片上传
	private GridView noScrollgridview;
	private GridAdapter adapter;
	private View parentView;
	private PopupWindow pop = null;
	private LinearLayout ll_popup;
	public static Bitmap bimap;
	private List<String> imagePathList;
	private String filePath;
	private String fileName;
	private Builder builder;
	// 用户输入的商品信息
	private EditText etName;
	private EditText etBrief;
	private EditText etPrice;
	private EditText etOldPrice;
	private String typeName;

	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 03.16修改，多图片上传
		// 初始化文件夹路径和R资源
		Res.init(this);
		// 初始化添加图片的按钮
		bimap = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon_addpic_unfocused);
		// 将当前activity存放到PublicWay.activityList里
		PublicWay.activityList.add(this);
		parentView = getLayoutInflater().inflate(
				R.layout.activity_publish_goods, null);
		setContentView(parentView);
		Init();
		// 调用MainActivity的setColor()方法，实现沉浸式状态栏
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
		// 初始化控件
		typeSpinner = (Spinner) findViewById(R.id.spn_types);
		marketSpinner = (Spinner) findViewById(R.id.spn_markets);
		ivback = (ImageView) findViewById(R.id.iv_pub_return);
		ivback.setOnClickListener(this);
		etName = (EditText) findViewById(R.id.et_goods_name);
		etBrief = (EditText) findViewById(R.id.et_goods_brief);
		etPrice = (EditText) findViewById(R.id.et_goods_price);
		etOldPrice = (EditText) findViewById(R.id.et_goods_old_price);
		// 获取分类及集市下拉列表的数据
		typeList = getSpinnerTypeData();
		marketList = getSpinnerMarketData();

		// 初始化分类的适配器， 通用适配器
		MyAdapter<Map<String, Object>> typeAdapter = new MyAdapter<Map<String, Object>>(
				this, typeList, R.layout.spinner_item) {
			@Override
			public void convert(ViewHolder holder, Map<String, Object> t) {
				holder.setImageResource(R.id.iv_type_inco, (int) t.get("inco"));
				holder.setText(R.id.tv_type_name, (String) t.get("name"));
			}
		};
		// 设置分类的适配器
		typeSpinner.setAdapter(typeAdapter);
		// 分类下拉列表事件监听
		typeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				typeName = (String) typeList.get(position).get("name");
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		// 初始化集市的适配器
		MyAdapter<String> marketAdapter = new MyAdapter<String>(this,
				marketList, R.layout.spinner_item) {
			@Override
			public void convert(com.geminno.erhuo.utils.ViewHolder holder,
					String t) {
				ImageView image = holder.getView(R.id.iv_type_inco);
				image.setVisibility(View.GONE);
				holder.setText(R.id.tv_type_name, t);

			}

		};
		// 设置集市的适配器
		marketSpinner.setAdapter(marketAdapter);
		/**
		 * 
		 */

	}

	/**
	 * 03.16修改，多图片上传
	 * 
	 * @author Heikki
	 * 
	 * */
	@SuppressWarnings("deprecation")
	@SuppressLint("InflateParams")
	// 初始化数据
	public void Init() {
		// 初始化popupWindow
		pop = new PopupWindow(PublishGoodsActivity.this);
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
				Intent intent = new Intent(PublishGoodsActivity.this,
						AlbumActivity.class);
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
		noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
		// 设置点击GridView时出现背景
		noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		// 初始化适配器
		adapter = new GridAdapter(this);
		adapter.update();
		// 设置适配器
		noScrollgridview.setAdapter(adapter);
		// 设置GridView的item点击事件
		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				if (arg2 == Bimp.tempSelectBitmap.size()) { // 如果点击的是添加图片按钮
					Log.i("ddddddd", "----------");
					// 点击item切换弹出popupwindow时的动画
					ll_popup.startAnimation(AnimationUtils.loadAnimation(
							PublishGoodsActivity.this,
							R.anim.activity_translate_in));
					// 设置popupwindow显示位置
					pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
				} else {// 点击的是已添加的图片
					// 跳转到进行图片浏览时的界面
					Intent intent = new Intent(PublishGoodsActivity.this,
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
			 if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK)
			 {
			
			 String fileName = String.valueOf(System.currentTimeMillis());
			 //获得拍照返回的图片
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
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			for (int i = 0; i < PublicWay.activityList.size(); i++) {
//				if (null != PublicWay.activityList.get(i)) {
//					PublicWay.activityList.get(i).finish();
//				}
//			}
//		}
//		return true;
//	}

	// 分类下拉列表数据
	public static List<Map<String, Object>> getSpinnerTypeData() {
		List<Map<String, Object>> spinnerTypeData = new ArrayList<Map<String, Object>>();
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("inco", R.drawable.types_others);
		map1.put("name", "其他闲置");
		spinnerTypeData.add(map1);

		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("inco", R.drawable.types_pad);
		map2.put("name", "手机电脑");
		spinnerTypeData.add(map2);

		Map<String, Object> map3 = new HashMap<String, Object>();
		map3.put("inco", R.drawable.types_3c);
		map3.put("name", "相机数码");
		spinnerTypeData.add(map3);

		Map<String, Object> map4 = new HashMap<String, Object>();
		map4.put("inco", R.drawable.types_card);
		map4.put("name", "书籍文体");
		spinnerTypeData.add(map4);

		Map<String, Object> map5 = new HashMap<String, Object>();
		map5.put("inco", R.drawable.types_luggage);
		map5.put("name", "服装鞋包");
		spinnerTypeData.add(map5);

		Map<String, Object> map6 = new HashMap<String, Object>();
		map6.put("inco", R.drawable.types_perfume);
		map6.put("name", "美容美体");
		spinnerTypeData.add(map6);

		return spinnerTypeData;

	}

	// 集市下拉列表数据
	public static List<String> getSpinnerMarketData() {
		List<String> spinnerMarketData = new ArrayList<String>();
		spinnerMarketData.add("选择集市更容易售出哦!");
		if (MyApplication.getMarketsList() != null) {
			for (Markets markets : MyApplication.getMarketsList()) {
				spinnerMarketData.add(markets.getName());
			}
		}
		// spinnerMarketData.add("爱书人的圈子");
		// spinnerMarketData.add("八一八你败过的数码");
		// spinnerMarketData.add("苹果专区肾宝岛");
		// spinnerMarketData.add("宝宝的惊喜");
		// spinnerMarketData.add("闲置好车等您来骑");
		return spinnerMarketData;
	}

	// 界面点击响应事件
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_pub_return:
			finish();
			break;
		case R.id.btn_publish_goods:
			// 发布商品，判断用户输入信息
			if (TextUtils.isEmpty(etName.getText())) {
				Toast.makeText(this, "给宝贝取个名字吧！", Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(etBrief.getText())) {
				Toast.makeText(this, "描述一下你的宝贝吧！", Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(etPrice.getText())) {
				Toast.makeText(this, "说说你的宝贝想卖多少钱吧！", Toast.LENGTH_SHORT)
						.show();
			} else {
				// Toast.makeText(this,
				// typeSpinner.getSelectedItem().toString(),
				// Toast.LENGTH_LONG).show();
				//将用户输入的信息封装成Goods对象
				Goods goods = new Goods();
				final int USERID = 2;// 仅做测试用，正式版应从MyApplication.getCurrentUser().getId()获取
				goods.setUserId(USERID);
				goods.setName(etName.getText().toString());
				goods.setImformation(etBrief.getText().toString());
				goods.setTypeId(getTypeId(typeName));
				goods.setSoldPrice(Double.parseDouble(etPrice.getText()
						.toString()));
				if (TextUtils.isEmpty(etOldPrice.getText())) {
					goods.setBuyPrice(0);
				} else {
					goods.setBuyPrice(Double.parseDouble(etOldPrice.getText()
							.toString()));
				}
				goods.setMarketId(getmarketsId());
				if (MyApplication.getLocation() == null) {
					goods.setLongitude(0);
					goods.setLatitude(0);
				} else {
					goods.setLongitude(MyApplication.getLocation()
							.getLongitude());
					goods.setLatitude(MyApplication.getLocation().getLatitude());// 33.8640844584,112.4709425635
				}
				goods.setPubTime(new Date(System.currentTimeMillis()));
				goods.setState(1);
				//
				// 商品转化为Josn数据
				Gson gson = new GsonBuilder().setDateFormat(
						"yyyy-MM-dd HH:mm:ss").create();
				String goodsJson = gson.toJson(goods);
				// 服务器地址(测试，后期从配置文件获取)
				// String url = null;
				Properties prop = new Properties();
				String headUrl = null;
				try {
					prop.load(PublishGoodsActivity.class
							.getResourceAsStream("/com/geminno/erhuo/utils/url.properties"));
					headUrl = prop.getProperty("url");
				} catch (IOException e) {
					e.printStackTrace();
				}

				final String url = headUrl + "/AddGoodServlet";
				// final String url =
				// "http://10.201.1.23:8080/secondHandShop/AddGoodServlet";
				RequestParams rp = new RequestParams();
				rp.addBodyParameter("goodJson", goodsJson);

				// 处理商品图片
				// Log.i("PublishGoodsActivity",imagePathList.get(0));
				int count = 0;
				for (ImageItem image : Bimp.tempSelectBitmap) {
					File file = new File(image.getImagePath());
					rp.addBodyParameter(USERID + count + getNowTime(), file);
					Log.i("uploadimage", USERID + count + getNowTime());
					count++;
				}
				final ViewGroup la = (ViewGroup) findViewById(R.id.fl_base);
				//禁用界面控件
				disableSubControls(la,false);
				final LinearLayout ll = (LinearLayout) findViewById(R.id.ll_progress);
				//显示进度动画
				ll.setVisibility(View.VISIBLE);
				HttpUtils hu = new HttpUtils();
				//发送请求，连接服务器，传送数据
				hu.send(HttpMethod.POST, url, rp,
						new RequestCallBack<String>() {

							@Override
							public void onFailure(HttpException error,
									String msg) {
								disableSubControls(la, true);
								ll.setVisibility(View.INVISIBLE);
								Toast.makeText(PublishGoodsActivity.this,
										"连接服务器失败!", Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onSuccess(
									ResponseInfo<String> responseInfo) {
								String result = responseInfo.result;
								String info = null;
								Log.i("onSuccess", result);
								disableSubControls(la, true);
								ll.setVisibility(View.INVISIBLE);
								if (result != null
										&& !"null".equals(result.trim())) {
									info = "发布成功！" + result;
									finish();
								} else {
									info = "发布失败！";
								}
								Toast.makeText(PublishGoodsActivity.this, info,
										Toast.LENGTH_SHORT).show();

							}
						});

			}
			break;
		}

	}
	//禁用界面控件，防止发布过程中再次编辑界面控件内容
	public static void disableSubControls(ViewGroup viewGroup,boolean flag) {
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			View v = viewGroup.getChildAt(i);
			if (v instanceof ViewGroup) {
				if (v instanceof Spinner) {
					Spinner spinner = (Spinner) v;
					spinner.setClickable(flag);
					spinner.setEnabled(flag);
				} else if (v instanceof ListView) {
					((ListView) v).setClickable(flag);
					((ListView) v).setEnabled(flag);
				} else if (v instanceof GridView) {
					((GridView) v).setClickable(flag);
					((GridView) v).setEnabled(flag);
				} else {
					disableSubControls((ViewGroup) v, flag);
				}
			} else if (v instanceof EditText) {
				((EditText) v).setEnabled(flag);
				((EditText) v).setClickable(flag);
			}
		}
	}

	// 获得分类id
	public int getTypeId(String typeName) {
		int typeId = 1;
		switch (typeName) {
		case "其他闲置":
			typeId = 1;
			break;
		case "手机电脑":
			typeId = 2;
			break;
		case "相机数码":
			typeId = 3;
			break;
		case "书籍文体":
			typeId = 4;
			break;
		case "服装鞋包":
			typeId = 5;
			break;
		case "美容美体":
			typeId = 6;
			break;
		}
		Log.i("typeId", typeName + "," + typeId);
		return typeId;
	}

	// 获得集市id
	public int getmarketsId() {
		String marketsName = marketSpinner.getSelectedItem().toString();
		if(MyApplication.getMarketsList() != null ){
			for(Markets markets : MyApplication.getMarketsList()){
				if(markets.getName().equals(marketsName)){
					return markets.getId();
				}
			}
		}	
		return 0;
	}

	// 获取当前时间
	private String getNowTime() {
		// Date date = new Date(System.currentTimeMillis());
		// SimpleDateFormat dateFormat = new
		// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// return dateFormat.format(date);
		return System.currentTimeMillis() + "";
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

}
