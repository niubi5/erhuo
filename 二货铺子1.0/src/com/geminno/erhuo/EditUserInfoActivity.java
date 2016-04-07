package com.geminno.erhuo;


import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.geminno.erhuo.entity.Address;
import com.geminno.erhuo.entity.Users;
import com.geminno.erhuo.utils.ActivityCollector;
import com.geminno.erhuo.utils.Url;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EditUserInfoActivity extends Activity implements
		OnClickListener {

	private TextView save;
	private ImageView editHeader;
	private EditText nickName;
	private ImageView male;
	private ImageView female;
	private EditText address;
	private TextView phone;
	private ImageView userreturn;
	private int sex;
	private Users users;
	private Context context;
	private String[] items = new String[] { "选择相册", "拍照" };
	/* 头像名称 */
	private static final String IMAGE_FILE_NAME = "headImage.jpg";
	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	private File file;
	private Address usersAddress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_edit_user_info);
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
		users = MyApplication.getCurrentUser();
		initView();
		context = this;
		if (users != null && !users.equals("null")) {
			userShow();
			if (users.getIdentity() != null) {
				sendAddress();
			}
		}
		Log.i("cheshi", "users" + users);

	}

	private void userShow() {

		if (users.getName() != null) {
			nickName.setText(users.getName());
		}
		String sex = users.getSex() + "";
		if (users.getPhoto()!=null && !users.getPhoto().equals("")) {
			Log.i("cheshi", "图片："+users.getPhoto());
			ImageLoader.getInstance().displayImage(users.getPhoto(), editHeader);
		}else {
			Log.i("cheshi", "默认图片："+users.getPhoto());
			editHeader
			.setImageResource(R.drawable.header_default);
		}
		if (sex != null && users.getSex() == 0) {
			male.setSelected(true);
		} else if (sex != null) {
			female.setSelected(true);
		}
		if (users.getIdentity() != null) {
			String head = users.getIdentity().substring(0, 3);
			String foot = users.getIdentity().substring(7, 11);
			phone.setText(head + "****" + foot);
		}

	}

	private void initView() {
		// 保存
		save = (TextView) findViewById(R.id.tv_infodata_ok);
		// 头像
		editHeader = (ImageView) findViewById(R.id.edit_header);
		// 昵称
		nickName = (EditText) findViewById(R.id.et_infodata_nickname);
		// 男点击按钮
		male = (ImageView) findViewById(R.id.chose_male);
		// 女点击按钮
		female = (ImageView) findViewById(R.id.chose_female);
		// 地址
		address = (EditText) findViewById(R.id.et_infodata_address);
		// 电话
		phone = (TextView) findViewById(R.id.tv_infodata_phone);
		// 返回
		userreturn = (ImageView) findViewById(R.id.ib_infodata_return);
		save.setOnClickListener(this);
		editHeader.setOnClickListener(this);
		nickName.setOnClickListener(this);
		male.setOnClickListener(this);
		female.setOnClickListener(this);
		userreturn.setOnClickListener(this);
		address.setOnClickListener(this);
		// phone.setOnClickListener(this);
		
	}
    
    
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private void showDialog() {
		new AlertDialog.Builder(this).setTitle("设置头像").setItems(items,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch (which) {
						case 0:
							Intent intentFromGallery = new Intent();
							intentFromGallery.setType("image/*"); // 设置文件类型
							intentFromGallery
									.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intentFromGallery,
									IMAGE_REQUEST_CODE);
							break;
						case 1:
							Intent intentFromCapture = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							// 判断存储卡是否可以用，可用进行存储
							if (hasSdcard()) {
								 intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,  
	                                        Uri.fromFile(new File(Environment.getExternalStorageDirectory(),IMAGE_FILE_NAME)));  
	                           }    
							startActivityForResult(intentFromCapture,
									CAMERA_REQUEST_CODE);
							break;
						default:
							break;
						}
					}

				}).setNegativeButton("取消",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface  dialog, int which) {
						// TODO Auto-generated method stub
						 dialog.dismiss();   
					}
				} ).show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.i("cheshi", "文件resultCode："+requestCode);
		if (resultCode!=RESULT_CANCELED) {
			switch (requestCode) {
			//相册获取
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData()); 
				break;
			//相机拍照
			case CAMERA_REQUEST_CODE:
				 if (hasSdcard()) {  
	                    File tempFile = new File(Environment.getExternalStorageDirectory().getPath(),IMAGE_FILE_NAME);  
	                    startPhotoZoom(Uri.fromFile(tempFile));  
	                } else {  
	                	Toast.makeText(context, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
	                }  
				break;
			//去的裁剪后的图片
			case RESULT_REQUEST_CODE:
				 if (data != null) {  
	                   setImageToView(data);  
	                   HttpUtils httpUtils=new HttpUtils();
	                   RequestParams params=new RequestParams();
	                   params.addBodyParameter("userId",users.getId()+"");
	                   params.addBodyParameter("file",file);
                       Log.i("setUserHead", users.getId()+"");
	                   String url = Url.getUrlHead()+ "/AddHeaderImageServlet";
//	                   String url = "http://10.201.1.16:8080/secondHandShop" + "/AddHeaderImageServlet";
	                   httpUtils.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							// TODO Auto-generated method stub
							Toast.makeText(EditUserInfoActivity.this,
									"请求上传失败", Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							// TODO Auto-generated method stub
							String result=arg0.result;
							Log.i("cheshi", "头像返回值："+result);
							if (!result.equals("null")) {
								Toast.makeText(EditUserInfoActivity.this,
										"设置成功，请保存！", Toast.LENGTH_SHORT).show();
							}else {
								Toast.makeText(EditUserInfoActivity.this,
										"上传失败", Toast.LENGTH_SHORT).show();
							}
						}
					});
	                }  
				break;

			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	 /**    
     * 裁剪图片方法实现    
     *     
     * @param uri    
     */   
	public void startPhotoZoom(Uri uri){
		if (uri==null) {
            Log.i("cheshi", "The uri is not exist.");  
		}
		 Intent intent = new Intent("com.android.camera.action.CROP");    
         intent.setDataAndType(uri, "image/*");    
         // 设置裁剪    
         intent.putExtra("crop", "true");    
         // aspectX aspectY 是宽高的比例    
         intent.putExtra("aspectX", 1);    
         intent.putExtra("aspectY", 1);    
         // outputX outputY 是裁剪图片宽高    
         intent.putExtra("outputX", 300);    
         intent.putExtra("outputY", 300);    
         intent.putExtra("return-data", true);    
         startActivityForResult(intent, 2);  
	}
	
	/**    
     * 保存裁剪之后的图片数据    
     *     
     * @param picdata    
     */    
    @SuppressWarnings("deprecation")
	private void setImageToView(Intent data) {    
        Bundle extras = data.getExtras();    
        if (extras != null) {    
            Bitmap photo = extras.getParcelable("data"); 
            saveBitmap(photo);
            Drawable drawable = new BitmapDrawable(photo);    
            editHeader.setImageDrawable(drawable);
        }    
    }    
	
    /** 保存方法 */
    public void saveBitmap(Bitmap photo) {
     Log.e("cheshi", "保存图片");
     SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
     String picName=  sDateFormat.format(new java.util.Date(System.currentTimeMillis()))+".jpg";
     file = new File( Environment.getExternalStorageDirectory().getPath(), picName);
     Log.i("cheshi", "图片路径:"+file.getAbsolutePath());
     if (file.exists()) {
      file.delete();
     }
     try {
      FileOutputStream out = new FileOutputStream(file);
      photo.compress(Bitmap.CompressFormat.PNG, 90, out);
      out.flush();
      out.close();
      Log.i("cheshi", "已经保存");
     } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
     }
    }
	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
	


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 保存
		case R.id.tv_infodata_ok:

			String username = nickName.getText().toString();
			String userphone = users.getIdentity().toString();
			String ads=address.getText().toString();
			if (username != users.getName() || sex != users.getSex()|| ads !=usersAddress.getAddress()) {
				HttpUtils httpUtils = new HttpUtils();
				httpUtils.configCurrentHttpCacheExpiry(0);
				RequestParams params = new RequestParams();
				params.addQueryStringParameter("userid", users.getId()+"");
				params.addQueryStringParameter("name", username);
				params.addQueryStringParameter("phone", userphone);
				params.addQueryStringParameter("sex", sex + "");
				if (!ads.equals("未设置地址")) {
					params.addQueryStringParameter("address",ads);
				}else {
					params.addQueryStringParameter("address",null);
				}
				
				String headUrl = Url.getUrlHead();
				String url = headUrl + "/UserInformationServlet";
//				String url = "http://10.201.1.16:8080/secondHandShop/UserInformationServlet";
				httpUtils.send(HttpMethod.GET, url, params,
						new RequestCallBack<String>() {

							@Override
							public void onFailure(HttpException arg0,
									String arg1) {
								// TODO Auto-generated method stub
								Log.i("cheshi", "fuck");
							}

							@Override
							public void onSuccess(ResponseInfo<String> arg0) {
								String result = arg0.result;
								Log.i("cheshi", "result值为：" + result);
								if (result != null && !result.equals("null")) {
									Toast.makeText(EditUserInfoActivity.this,
											"保存成功", Toast.LENGTH_SHORT).show();
									Gson gson = new Gson();
									Type type = new TypeToken<Map<Address, Users>>() {
									}.getType();
									
									Map<Address, Users> map = gson.fromJson(result,
											type);
									Users usersInfo = null;
									Address userAddress=null;
									Set<Address> keySet = map.keySet(); // key的set集合  
								    Iterator<Address> it = keySet.iterator();  
								      while(it.hasNext()){  
								        	userAddress = it.next(); // key  
								        	usersInfo = map.get(userAddress);  //value       
								      } 
								    address.setText(userAddress.getAddress());
									MyApplication.setUsers(usersInfo);
									nickName.setText(usersInfo.getName());
									
									
									ActivityCollector.finishAll();
									startActivity(new Intent(EditUserInfoActivity.this,MainActivity.class));
								    finish();
								} else {
									Toast.makeText(EditUserInfoActivity.this,
											"保存失败", Toast.LENGTH_SHORT).show();
								}
							}
						});
			}

			break;
		case R.id.edit_header:
            showDialog();
            
			break;
		case R.id.et_infodata_nickname:

			break;

		// case R.id.tv_infodata_ok:
		//
		// break;
		// case R.id.edit_header:
		//
		// break;
		// case R.id.et_infodata_nickname:
		//
		// break;
		case R.id.ib_infodata_return:
			this.finish();
			break;
		case R.id.chose_male:
			male.setSelected(true);
			female.setSelected(false);
			sex = 0;
			break;
		case R.id.chose_female:
			male.setSelected(false);
			female.setSelected(true);
			sex = 1;
			break;
		// case R.id.tv_infodata_ok:
		//
		// break;
		// case R.id.tv_infodata_ok:
		//
		// break;
		}
	}

	private void sendAddress() {

		String userid = users.getId() + "";
		if (userid != null && !userid.equals("null")) {
			HttpUtils http = new HttpUtils();
			Log.i("cheshi", "获取用户id" + userid);
			RequestParams params = new RequestParams();
			params.addQueryStringParameter("curUserId", userid);
			// 服务器路径
			String headUrl = Url.getUrlHead();
			String url = headUrl + "/UserAddressServlet";
//			 String
//			 url="http://10.201.1.16:8080/secondHandShop/UserAddressServlet";
			http.send(HttpMethod.POST, url, params,
					new RequestCallBack<String>() {

					

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							// TODO Auto-generated method stub
							Toast.makeText(EditUserInfoActivity.this, "获取地址失败",
									Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							// TODO Auto-generated method stub
							String result = arg0.result;
							Log.i("cheshi", "地址：" + result);
							if (result != null && !result.equals("null")) {
								Gson gson = new Gson();
								usersAddress = gson.fromJson(result,
										Address.class);
								address.setText(usersAddress.getAddress());
							} 
						}
					});
		}

	}

}
