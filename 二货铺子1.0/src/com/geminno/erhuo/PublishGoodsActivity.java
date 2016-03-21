package com.geminno.erhuo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geminno.erhuo.entity.Goods;
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
	// 用户输入的商品信息
	private EditText etName;
	private EditText etBrief;
	private EditText etPrice;
	private EditText etOldPrice;

	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 03.16修改，多图片上传
		Res.init(this);
		bimap = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon_addpic_unfocused);
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
		// typeSpinner.setOnitem
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
	}

	// 03.16修改，多图片上传
	@SuppressWarnings("deprecation")
	@SuppressLint("InflateParams")
	public void Init() {

		pop = new PopupWindow(PublishGoodsActivity.this);

		View view = getLayoutInflater().inflate(R.layout.item_popupwindows,
				null);

		ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);

		pop.setWidth(LayoutParams.MATCH_PARENT);
		pop.setHeight(LayoutParams.WRAP_CONTENT);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setContentView(view);

		RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
		Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
		Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
		Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);
		parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				photo();
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(PublishGoodsActivity.this,
						AlbumActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.activity_translate_in,
						R.anim.activity_translate_out);
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});

		noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
		noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new GridAdapter(this);
		adapter.update();
		noScrollgridview.setAdapter(adapter);
		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == Bimp.tempSelectBitmap.size()) {
					Log.i("ddddddd", "----------");
					ll_popup.startAnimation(AnimationUtils.loadAnimation(
							PublishGoodsActivity.this,
							R.anim.activity_translate_in));
					pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
				} else {
					Intent intent = new Intent(PublishGoodsActivity.this,
							GalleryActivity.class);
					intent.putExtra("position", "1");
					intent.putExtra("ID", arg2);
					startActivity(intent);
				}
			}
		});

	}

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

		public void update() {
			//loading();
			adapter.notifyDataSetChanged();
		}

		public int getCount() {
			if (Bimp.tempSelectBitmap.size() == 9) {
				return 9;
			}
			return (Bimp.tempSelectBitmap.size() + 1);
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
			//imagePathList = Bimp.tempSelectBitmap.get(position).getImagePath();
		}

		public int getSelectedPosition() {
			return selectedPosition;
		}

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
			//获取选取的图片的路径
//			imagePathList = new ArrayList<String>();
//			imagePathList.add(Bimp.tempSelectBitmap.get(position).getImagePath());
			Log.i("PublishGoodsActivity", Bimp.tempSelectBitmap.size()+"");
			Log.i("selectImage", position+"");
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

	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PICTURE:
			if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {

				String fileName = String.valueOf(System.currentTimeMillis());
				Bitmap bm = (Bitmap) data.getExtras().get("data");
				FileUtils.saveBitmap(bm, fileName);

				ImageItem takePhoto = new ImageItem();
				takePhoto.setBitmap(bm);
				Bimp.tempSelectBitmap.add(takePhoto);
				//String path = Bimp.tempSelectBitmap.get
			}
			break;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			for (int i = 0; i < PublicWay.activityList.size(); i++) {
				if (null != PublicWay.activityList.get(i)) {
					PublicWay.activityList.get(i).finish();
				}
			}
		}
		return true;
	}

	// 分类下拉列表数据
	public static List<Map<String, Object>> getSpinnerTypeData() {
		List<Map<String, Object>> spinnerTypeData = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("inco", R.drawable.types_others);
		map.put("name", R.string.types_others);
		spinnerTypeData.add(map);

		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("inco", R.drawable.types_phone);
		map2.put("name", R.string.types_phone);
		spinnerTypeData.add(map2);

		Map<String, Object> map3 = new HashMap<String, Object>();
		map3.put("inco", R.drawable.types_pad);
		map3.put("name", R.string.types_pad);
		spinnerTypeData.add(map3);

		Map<String, Object> map4 = new HashMap<String, Object>();
		map4.put("inco", R.drawable.buyingother);
		map4.put("name", "其他二手");
		spinnerTypeData.add(map4);

		return spinnerTypeData;

	}

	// 集市下拉列表数据
	public static List<String> getSpinnerMarketData() {
		List<String> spinnerMarketData = new ArrayList<String>();
		spinnerMarketData.add("爱书人的圈子");
		spinnerMarketData.add("八一八你败过的数码");
		spinnerMarketData.add("苹果专区肾宝岛");
		spinnerMarketData.add("宝宝的惊喜");
		spinnerMarketData.add("闲置好车等您来骑");
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
			// 发布商品
			if (TextUtils.isEmpty(etName.getText())) {
				Toast.makeText(this, "给宝贝取个名字吧！", Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(etBrief.getText())) {
				Toast.makeText(this, "描述一下你的宝贝吧！", Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(etPrice.getText())) {
				Toast.makeText(this, "说说你的宝贝想卖多少钱吧！", Toast.LENGTH_SHORT)
						.show();
			} else {
				//Toast.makeText(this, typeSpinner.getSelectedItem().toString(),
				//		Toast.LENGTH_LONG).show();
				for(ImageItem index : Bimp.tempSelectBitmap){
					Log.i("selectImagePath", index.getImagePath());
				}
				
				Goods goods = new Goods();
				goods.setUserId(1);
				goods.setName(etName.getText().toString());
				goods.setImformation(etBrief.getText().toString());
				goods.setTypeId(1);
				goods.setSoldPrice(Double.parseDouble(etPrice.getText()
						.toString()));
				if(TextUtils.isEmpty(etOldPrice.getText())){
					goods.setBuyPrice(0);
				}else{
					goods.setBuyPrice(Double.parseDouble(etOldPrice.getText()
							.toString()));					
				}
				goods.setMarketId(2);
				goods.setLongitude(33.8640844584);
				goods.setLatitude(112.4709425635);// 33.8640844584,112.4709425635
				goods.setPubTime(new Date(System.currentTimeMillis()));
				goods.setState(1);
				//	
				//商品转化为Josn数据
				Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh-mm-ss").create();
				String goodsJson = gson.toJson(goods);
				//服务器地址(测试，后期从配置文件获取)
				//String url = null;
				final String url = "http://10.201.1.23:8080/secondHandShop/AddGoodServlet";
				RequestParams rp = new RequestParams();
				rp.addBodyParameter("goodJson", goodsJson);
				//处理商品图片
				//Log.i("PublishGoodsActivity",imagePathList.get(0));
				final int USERID = 10;//仅做测试用，正式版应从MyApplication.getCurrentUser().getId()获取
				int count = 0;
				for(ImageItem image : Bimp.tempSelectBitmap){
					File file = new File(image.getImagePath());
					rp.addBodyParameter(USERID+count+getNowTime(),file);
					Log.i("uploadimage", USERID+count+getNowTime());
					count++;
				}
				//显示进度动画
				final ViewGroup la = (ViewGroup) findViewById(R.id.fl_base);
				disableSubControls(la,false);
				final LinearLayout ll = (LinearLayout) findViewById(R.id.ll_progress);
				ll.setVisibility(View.VISIBLE);
				HttpUtils hu = new HttpUtils();
				hu.send(HttpMethod.POST, url, rp,new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException error, String msg) {
						//disableSubControls(la,false);
						ll.setVisibility(View.INVISIBLE);
						Toast.makeText(PublishGoodsActivity.this, "连接服务器失败!", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						String result = responseInfo.result;
						String info = null;
						Log.i("onSuccess", result);
						//disableSubControls(la,true);
						ll.setVisibility(View.INVISIBLE);
						if(result != null && !"null".equals(result.trim())){
							info = "发布成功！"+result;
							finish();
						}else{
							info = "发布失败！";
						}
						Toast.makeText(PublishGoodsActivity.this, info, Toast.LENGTH_SHORT).show();
						
					}
				});
			}
			break;
		}

	}

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
				} else if(v instanceof GridView){
					((GridView) v).setClickable(flag);
					((GridView) v).setEnabled(flag);
				}else{
					disableSubControls((ViewGroup) v,flag);
				}
			} else if (v instanceof EditText) {
				((EditText) v).setEnabled(flag);
				((EditText) v).setClickable(flag);

			} else if (v instanceof Button) {
				((Button) v).setEnabled(flag);

			}else if(v instanceof ImageView){
				((ImageView) v).setEnabled(flag);
				((ImageView) v).setClickable(flag);
			}
		}
	}
	


	// 获取当前时间
	private String getNowTime() {
		//Date date = new Date(System.currentTimeMillis());
		//SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//return dateFormat.format(date);
		return System.currentTimeMillis()+"";
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(!Bimp.tempSelectBitmap.isEmpty()){
			Bimp.tempSelectBitmap.clear();			
		}
	}
	
}
