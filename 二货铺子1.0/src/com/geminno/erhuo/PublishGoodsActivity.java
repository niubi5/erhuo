package com.geminno.erhuo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geminno.erhuo.utils.Bimp;
import com.geminno.erhuo.utils.FileUtils;
import com.geminno.erhuo.utils.ImageItem;
import com.geminno.erhuo.utils.MyAdapter;
import com.geminno.erhuo.utils.PublicWay;
import com.geminno.erhuo.utils.Res;
import com.geminno.erhuo.utils.ViewHolder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;

public class PublishGoodsActivity extends Activity implements OnClickListener {
	private ImageView ivback;
	private Spinner typeSpinner;
	// private List<String> typeList;
	List<Map<String, Object>> typeList;
	// 选择图片相关
	// String[] imageItems = { "相册", "拍照" };
	// private Uri imageUri;
	private ImageView ivSelect;
	// public static final int SELECT_PIC = 11;// 从相册选择
	// public static final int TAKE_PHOTO = 12;// 拍照
	// public static final int CROP_PHOTO = 13;// 裁剪

	// 03.16修改，多图片上传
	private GridView noScrollgridview;
	private GridAdapter adapter;
	private View parentView;
	private PopupWindow pop = null;
	private LinearLayout ll_popup;
	public static Bitmap bimap ;

	//

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 03.16修改，多图片上传
		Res.init(this);
		bimap = BitmapFactory.decodeResource(
				getResources(),
				R.drawable.icon_addpic_unfocused);
		PublicWay.activityList.add(this);
		parentView = getLayoutInflater().inflate(R.layout.activity_publish_goods, null);
		setContentView(parentView);
		Init();

		// 调用MainActivity的setColor()方法，实现沉浸式状态栏
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));

		typeSpinner = (Spinner) findViewById(R.id.spn_types);
		ivback = (ImageView) findViewById(R.id.iv_pub_return);
		//ivSelect = (ImageView) findViewById(R.id.iv_img_select);
		//ivSelect.setOnClickListener(this);
		ivback.setOnClickListener(this);
		typeList = getspinner3data();

		// 通用适配器
		MyAdapter<Map<String, Object>> myadapter = new MyAdapter<Map<String, Object>>(
				this, typeList, R.layout.spinner_item) {
			@Override
			public void convert(ViewHolder holder, Map<String, Object> t) {
				holder.setImageResource(R.id.iv_type_inco, (int) t.get("inco"));
				holder.setText(R.id.tv_type_name, (String) t.get("name"));
			}
		};
		typeSpinner.setAdapter(myadapter);

		// // 获取sd卡目录：创建文件：当前时间.jpg
		// File file = new File(Environment.getExternalStorageDirectory(),
		// getNowTime() + ".jpg");
		// imageUri = Uri.fromFile(file);// 存放拍照后的图片；
	}

	// 03.16修改，多图片上传
public void Init() {
		
		pop = new PopupWindow(PublishGoodsActivity.this);
		
		View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);

		ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
		
		pop.setWidth(LayoutParams.MATCH_PARENT);
		pop.setHeight(LayoutParams.WRAP_CONTENT);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setContentView(view);
		
		RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
		Button bt1 = (Button) view
				.findViewById(R.id.item_popupwindows_camera);
		Button bt2 = (Button) view
				.findViewById(R.id.item_popupwindows_Photo);
		Button bt3 = (Button) view
				.findViewById(R.id.item_popupwindows_cancel);
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
				overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
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
					ll_popup.startAnimation(AnimationUtils.loadAnimation(PublishGoodsActivity.this,R.anim.activity_translate_in));
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
			loading();
		}

		public int getCount() {
			if(Bimp.tempSelectBitmap.size() == 9){
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

			if (position ==Bimp.tempSelectBitmap.size()) {
				holder.image.setImageBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.icon_addpic_unfocused));
				if (position == 9) {
					holder.image.setVisibility(View.GONE);
				}
			} else {
				holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position).getBitmap());
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
			}
			break;
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			for(int i=0;i<PublicWay.activityList.size();i++){
				if (null != PublicWay.activityList.get(i)) {
					PublicWay.activityList.get(i).finish();
				}
			}
			System.exit(0);
		}
		return true;
	}
	/**
	 * ###############################################################
	 * 
	 */
	// 获取当前时间
	private String getNowTime() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSS");
		return dateFormat.format(date);
	}

	// 分类下拉列表数据
	public static List<Map<String, Object>> getspinner3data() {
		List<Map<String, Object>> spinnerdata = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("inco", R.drawable.buyingbook);
		map.put("name", "书籍文体");
		spinnerdata.add(map);

		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("inco", R.drawable.buyingclothes);
		map2.put("name", "服装鞋靴");
		spinnerdata.add(map2);

		Map<String, Object> map3 = new HashMap<String, Object>();
		map3.put("inco", R.drawable.buyingphone);
		map3.put("name", "手机电脑");
		spinnerdata.add(map3);

		Map<String, Object> map4 = new HashMap<String, Object>();
		map4.put("inco", R.drawable.buyingother);
		map4.put("name", "其他二手");
		spinnerdata.add(map4);

		return spinnerdata;

	}

	// 界面点击响应事件
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_pub_return:
			this.finish();
			break;
		//case R.id.iv_img_select:
			// // 点击添加图片--》相册||拍摄
			// new AlertDialog.Builder(this)
			// .setTitle("添加图片")
			// .setItems(imageItems,
			// new DialogInterface.OnClickListener() {
			//
			// @Override
			// public void onClick(DialogInterface dialog,
			// int which) {
			// switch (which) {
			// case 0:
			// // 相册选取
			// Intent intent = new Intent();
			// intent.setAction(Intent.ACTION_PICK);
			// intent.setType("image/*");
			// // 裁剪
			// intent.putExtra("crop", "true");
			// // 宽高比例
			// intent.putExtra("aspectX", 1);
			// intent.putExtra("aspectY", 1);
			// // 定义宽和高
			// intent.putExtra("outputX", 300);
			// intent.putExtra("outputY", 300);
			// // 图片是否缩放
			// intent.putExtra("scale", true);
			// // 图片输出格式
			// intent.putExtra("outputFormat",
			// Bitmap.CompressFormat.JPEG
			// .toString());
			// // 是否要返回值
			// intent.putExtra("return-data", false);
			// // 把图片存放到imageUri
			// intent.putExtra(
			// MediaStore.EXTRA_OUTPUT,
			// imageUri);
			// intent.putExtra("noFaceDetection", true);
			// startActivityForResult(intent,
			// SELECT_PIC);
			// break;
			// case 1:
			// // 拍照
			// Intent intent1 = new Intent(
			// MediaStore.ACTION_IMAGE_CAPTURE);
			// intent1.putExtra(
			// MediaStore.EXTRA_OUTPUT,
			// imageUri);
			// startActivityForResult(intent1,
			// TAKE_PHOTO);
			// break;
			// }
			//
			// }
			// }).show();
		//	break;
		}

	}

	// // 获取选择的图片
	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// // TODO Auto-generated method stub
	// super.onActivityResult(requestCode, resultCode, data);
	// switch (requestCode) {
	// case TAKE_PHOTO:
	// InputStream is = null;
	// try {
	// is = getContentResolver().openInputStream(imageUri);
	// // 内存中的图片
	// Bitmap bm = BitmapFactory.decodeStream(is);
	// //代码新建控件
	// LinearLayout imagelayout = (LinearLayout)
	// findViewById(R.id.image_layout);
	// ImageView image = new ImageView(this);
	// image.setImageBitmap(bm);
	//
	// LinearLayout.LayoutParams layoutParams = new
	// LinearLayout.LayoutParams(50, 50);
	// imagelayout.addView(image,layoutParams);
	// //ivSelect.setImageBitmap(bm);
	// } catch (FileNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } finally {
	// try {
	// is.close();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// break;
	// case SELECT_PIC:
	// // 从相册选择
	// InputStream is1 = null;
	// try {
	// is1 = getContentResolver().openInputStream(imageUri);
	// Log.i("PublishGoodsActivity", "返回结果：" + is1);
	// // 内存中的图片
	// Bitmap bm1 = BitmapFactory.decodeStream(is1);
	// Log.i("PublishGoodsActivity", "返回结果：" + bm1);
	// ivSelect.setImageBitmap(bm1);
	// } catch (FileNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } finally {
	// try {
	// is1.close();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// break;
	// }
	// }
}
