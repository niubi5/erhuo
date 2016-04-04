package com.geminno.erhuo;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.geminno.erhuo.entity.Address;
import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.entity.Orders;
import com.geminno.erhuo.entity.Users;
import com.geminno.erhuo.utils.MySdf;
import com.geminno.erhuo.utils.Url;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pingplusplus.android.PaymentActivity;
import com.pingplusplus.android.PingppLog;
import com.pingplusplus.libone.PayResultCallBack;
import com.pingplusplus.libone.PingppOnePayment;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class BuyGoodsActivity extends FragmentActivity implements
		View.OnClickListener, PayResultCallBack {
	public Activity buyGoodsActivity;
	private Users user;
	private Goods good;
	private String[] goodUrl;
	private String buyGoods = new String("buy");
	private int userAddId;
	/**
	 * @author heikki 2016.03.25 16:00
	 * */
	// 在线支付
	private PopupWindow pop;
	private LinearLayout ll_popup;
	private View parentView;
	private ImageView ivAlipay;
	private ImageView ivMm;
	private ImageView ivUnionpay;
	private ImageView ivJd;
	private ImageView ivBaidu;
	private TextView tvPayAmount;
	private String currentAmount = "";
	//收货地址
	private TextView tvReceiveName;
	private TextView tvReceivePhone;
	private TextView tvReceiveAddress;
	private Button btnPayWay;
	/**
	 * 开发者需要填一个服务端URL 该URL是用来请求支付需要的charge。务必确保，URL能返回json格式的charge对象。
	 * 服务端生成charge 的方式可以参考ping++官方文档，地址
	 * https://pingxx.com/guidance/server/import
	 * 
	 * 【 http://218.244.151.190/demo/charge 】是 ping++ 为了方便开发者体验 sdk 而提供的一个临时 url
	 * 。 该 url 仅能调用【模拟支付控件】，开发者需要改为自己服务端的 url 。
	 */
	private static String YOUR_URL = "http://218.244.151.190/demo/charge";
	public static final String URL = YOUR_URL;
	private static final int REQUEST_CODE_PAYMENT = 1;
	// 银联支付渠道
	private static final String CHANNEL_UPACP = "upacp";
	// 微信支付渠道
	private static final String CHANNEL_WECHAT = "wx";
	// 支付支付渠道
	private static final String CHANNEL_ALIPAY = "alipay";
	// 百度支付渠道
	private static final String CHANNEL_BFB = "bfb";
	// 京东支付渠道
	private static final String CHANNEL_JDPAY_WAP = "jdpay_wap";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		parentView = getLayoutInflater().inflate(R.layout.activity_buy_goods,
				null);
		setContentView(parentView);
		buyGoodsActivity = this;
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));

		user = (Users) getIntent().getSerializableExtra("user");
		good = (Goods) getIntent().getSerializableExtra("good");
		goodUrl = getIntent().getStringArrayExtra("url");
		Log.i("BuyGoodsActivity", good.toString());
		initData();
		initPay();
	}
	//获取当前用户使用的收货地址
	public void getCurUserAddress(){
		RelativeLayout rlAddress  = (RelativeLayout) findViewById(R.id.rl_center);
		RelativeLayout rlNewAddress = (RelativeLayout) findViewById(R.id.rl_no_address);
		btnPayWay = (Button) findViewById(R.id.btn_pay_way);
		if(MyApplication.getUseAddress() != null){
			rlAddress.setVisibility(View.VISIBLE);
			rlNewAddress.setVisibility(View.INVISIBLE);
			Address curUserAddress = MyApplication.getUseAddress();
			userAddId = curUserAddress.getId();
			tvReceiveName = (TextView) findViewById(R.id.tv_receive_name);
			tvReceivePhone = (TextView) findViewById(R.id.tv_receive_phone);
			tvReceiveAddress = (TextView) findViewById(R.id.tv_receive_address);
			
			tvReceiveName.setText(curUserAddress.getName());
			tvReceivePhone.setText(curUserAddress.getPhone());
			tvReceiveAddress.setText(curUserAddress.getAddress());
			Log.i("BuyCurAddress", tvReceiveName.getText().toString());
			btnPayWay.setBackgroundColor(getResources().getColor(R.color.main_red));
			btnPayWay.setEnabled(true);
		}else if(MyApplication.getCurUserDefAddress() != null){
			rlAddress.setVisibility(View.VISIBLE);
			rlNewAddress.setVisibility(View.INVISIBLE);
			MyApplication.setUseAddress(MyApplication.getCurUserDefAddress());
			Address curUserAddress = MyApplication.getUseAddress();
			userAddId = curUserAddress.getId();
			tvReceiveName = (TextView) findViewById(R.id.tv_receive_name);
			tvReceivePhone = (TextView) findViewById(R.id.tv_receive_phone);
			tvReceiveAddress = (TextView) findViewById(R.id.tv_receive_address);
			
			tvReceiveName.setText(curUserAddress.getName());
			tvReceivePhone.setText(curUserAddress.getPhone());
			tvReceiveAddress.setText(curUserAddress.getAddress());
			Log.i("BuyCurAddress", tvReceiveName.getText().toString());
			btnPayWay.setBackgroundColor(getResources().getColor(R.color.main_red));
			btnPayWay.setEnabled(true);
		}else{
			rlAddress.setVisibility(View.INVISIBLE);
			rlNewAddress.setVisibility(View.VISIBLE);
			btnPayWay.setBackgroundColor(getResources().getColor(R.color.btn_no_click));
			btnPayWay.setEnabled(false);
		}
	}
	
	// 初始化支付
	public void initPay() {
		pop = new PopupWindow(this);
		View view = getLayoutInflater().inflate(R.layout.pay_popupwindow, null);
		ll_popup = (LinearLayout) view.findViewById(R.id.ll_pay_popup);
		pop.setWidth(LayoutParams.MATCH_PARENT);
		pop.setHeight(LayoutParams.WRAP_CONTENT);
		pop.setBackgroundDrawable(new ColorDrawable(00000));
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setContentView(view);

		RelativeLayout parent = (RelativeLayout) view
				.findViewById(R.id.rl_parent);
		tvPayAmount = (TextView) findViewById(R.id.tv_goods_pay);
		ivAlipay = (ImageView) view.findViewById(R.id.iv_zhifubao);
		ivMm = (ImageView) view.findViewById(R.id.iv_weixin);
		ivUnionpay = (ImageView) view.findViewById(R.id.iv_yinlian);
		ivJd = (ImageView) view.findViewById(R.id.iv_jingdong);
		ivBaidu = (ImageView) view.findViewById(R.id.iv_baidu);

		parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		ivAlipay.setOnClickListener(this);
		ivMm.setOnClickListener(this);
		ivUnionpay.setOnClickListener(this);
		ivJd.setOnClickListener(this);
		ivBaidu.setOnClickListener(this);

		// 设置需要使用的支付方式,true:显示该支付通道，默认为false
		PingppOnePayment.SHOW_CHANNEL_WECHAT = true;
		PingppOnePayment.SHOW_CHANNEL_UPACP = true;
		PingppOnePayment.SHOW_CHANNEL_BFB = true;
		PingppOnePayment.SHOW_CHANNEL_ALIPAY = true;

		// 设置支付通道的排序,最小的排在最前
		PingppOnePayment.CHANNEL_UPACP_INDEX = 1;
		PingppOnePayment.CHANNEL_ALIPAY_INDEX = 2;
		PingppOnePayment.CHANNEL_WECHAT_INDEX = 3;
		PingppOnePayment.CHANNEL_BFB_INDEX = 4;

		// 提交数据的格式，默认格式为json
		// PingppOnePayment.CONTENT_TYPE = "application/x-www-form-urlencoded";
		PingppOnePayment.CONTENT_TYPE = "application/json";

		PingppLog.DEBUG = true;

		tvPayAmount.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (!s.toString().equals(currentAmount)) {
					tvPayAmount.removeTextChangedListener(this);
					String replaceable = String.format("[%s, \\s.]",
							NumberFormat.getCurrencyInstance(Locale.CHINA)
									.getCurrency().getSymbol(Locale.CHINA));
					String cleanString = s.toString().replaceAll(replaceable,
							"");

					if (cleanString.equals("")
							|| new BigDecimal(cleanString).toString().equals(
									"0")) {
						tvPayAmount.setText(null);
					} else {
						double parsed = Double.parseDouble(cleanString);
						String formatted = NumberFormat.getCurrencyInstance(
								Locale.CHINA).format((parsed / 100));
						currentAmount = formatted;
						tvPayAmount.setText(formatted);
						// tvPayAmount.setSelection(formatted.length());
					}
					tvPayAmount.addTextChangedListener(this);
				}
			}
		});
	}

	// 点击事件
	public void onclick(View v) {
		switch (v.getId()) {
		case R.id.iv_buy_return:
			finish();
			break;
		case R.id.btn_pay_way:
			ll_popup.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.activity_translate_in));
			pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
			break;
		case R.id.ll_user_address:
			Intent intent = new Intent(BuyGoodsActivity.this,ShipAddressActivity.class);
			intent.putExtra("jumpActivity", buyGoods);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	// 显示数据
	public void initData() {
		ImageView ivGoodImage = (ImageView) findViewById(R.id.iv_buy_good);
		TextView tvGoodName = (TextView) findViewById(R.id.tv_goods_name);
		TextView tvGoodBrief = (TextView) findViewById(R.id.tv_goods_imformation);
		TextView tvGoodPrice = (TextView) findViewById(R.id.tv_goods_price);
		TextView tvGoodPay = (TextView) findViewById(R.id.tv_goods_pay);
		Properties prop = new Properties();
		String headUrl = null;
		try {
			prop.load(PublishGoodsActivity.class
					.getResourceAsStream("/com/geminno/erhuo/utils/url.properties"));
			headUrl = prop.getProperty("headUrl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		final String userHeadUrl = headUrl + goodUrl[0];
		ImageLoader.getInstance().displayImage(goodUrl[0], ivGoodImage);
		tvGoodName.setText(good.getName());
		tvGoodBrief.setText(good.getImformation());
		tvGoodPrice.setText("¥ " + good.getSoldPrice());
		tvGoodPay.setText("¥ " + good.getSoldPrice());
	}

	@Override
	public void getPayResult(Intent data) {
		if (data != null) {
			/**
			 * result：支付结果信息 code：支付结果码 -2:用户自定义错误、 -1：失败、 0：取消、1：成功
			 */
			Toast.makeText(
					this,
					data.getExtras().getString("result") + "  "
							+ data.getExtras().getInt("code"),
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onClick(View view) {
		// String amountText = tvPayAmount.getText().toString();
		// if (amountText.equals("")) return;
		//
		// String replaceable = String.format("[%s, \\s.]",
		// NumberFormat.getCurrencyInstance(Locale.CHINA).getCurrency().getSymbol(Locale.CHINA));
		// String cleanString = amountText.toString().replaceAll(replaceable,
		// "");
		int amount = Integer.valueOf(new BigDecimal(good.getSoldPrice())
				.toString());

		// 支付宝，微信支付，银联，百度钱包 按键的点击响应处理
		if (view.getId() == R.id.iv_yinlian) {
//			new PaymentTask()
//					.execute(new PaymentRequest(CHANNEL_UPACP, amount));
			
			pop.dismiss();
			ll_popup.clearAnimation();
			showMsg("success", "cancel", "cancel");
		} else if (view.getId() == R.id.iv_zhifubao) {
//			new PaymentTask()
//					.execute(new PaymentRequest(CHANNEL_ALIPAY, amount));
			pop.dismiss();
			ll_popup.clearAnimation();
			showMsg("success", "cancel", "cancel");
		} else if (view.getId() == R.id.iv_weixin) {
//			new PaymentTask()
//					.execute(new PaymentRequest(CHANNEL_WECHAT, amount));
			pop.dismiss();
			ll_popup.clearAnimation();
			showMsg("success", "cancel", "cancel");
		} else if (view.getId() == R.id.iv_baidu) {
//			new PaymentTask().execute(new PaymentRequest(CHANNEL_BFB, amount));
			pop.dismiss();
			ll_popup.clearAnimation();
			showMsg("success", "cancel", "cancel");
		} else if (view.getId() == R.id.iv_jingdong) {
			new PaymentTask().execute(new PaymentRequest(CHANNEL_JDPAY_WAP,
					amount));
			pop.dismiss();
			ll_popup.clearAnimation();
//			showMsg("success", "cancel", "cancel");
		} else {
			// 壹收款调用示例如下
			String orderNo = new SimpleDateFormat("yyyyMMddhhmmss")
					.format(new Date());

			// 计算总金额（以分为单位）
			JSONArray billList = new JSONArray();

			// 构建账单json对象
			JSONObject bill = new JSONObject();
			JSONObject displayItem = new JSONObject();

			// 自定义的额外信息 选填
			JSONObject extras = new JSONObject();
			try {
				extras.put("extra1", "extra1");
				extras.put("extra2", "extra2");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			try {
				displayItem.put("name", "商品");
				displayItem.put("contents", billList);
				JSONArray display = new JSONArray();
				display.put(displayItem);
				bill.put("order_no", orderNo);
				bill.put("amount", amount);
				bill.put("display", display);
				bill.put("extras", extras);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			// 壹收款: 创建支付通道的对话框
			PingppOnePayment.createPayChannel(getSupportFragmentManager(),
					bill.toString(), URL, null);
		}

	}

	class PaymentTask extends AsyncTask<PaymentRequest, Void, String> {

		@Override
		protected void onPreExecute() {

			// 按键点击之后的禁用，防止重复点击
			ivAlipay.setOnClickListener(null);
			ivBaidu.setOnClickListener(null);
			ivJd.setOnClickListener(null);
			ivUnionpay.setOnClickListener(null);
			ivMm.setOnClickListener(null);
		}

		@Override
		protected String doInBackground(PaymentRequest... pr) {

			PaymentRequest paymentRequest = pr[0];
			String data = null;
			String json = new Gson().toJson(paymentRequest);
			try {
				// 向Your Ping++ Server SDK请求数据
				data = postJson(URL, json);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return data;
		}

		/**
		 * 获得服务端的charge，调用ping++ sdk。
		 */
		@Override
		protected void onPostExecute(String data) {
			if (null == data) {
				showMsg("支付失败！", "网络异常", "请稍后再试");
				return;
			}
			Log.d("charge", data);
			Intent intent = new Intent(BuyGoodsActivity.this,
					PaymentActivity.class);
			intent.putExtra(PaymentActivity.EXTRA_CHARGE, data);
			startActivityForResult(intent, REQUEST_CODE_PAYMENT);
		}

	}

	/**
	 * onActivityResult 获得支付结果，如果支付成功，服务器会收到ping++ 服务器发送的异步通知。 最终支付成功根据异步通知为准
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		ivAlipay.setOnClickListener(this);
		ivMm.setOnClickListener(this);
		ivUnionpay.setOnClickListener(this);
		ivJd.setOnClickListener(this);
		ivBaidu.setOnClickListener(this);

		// 支付页面返回处理
		if (requestCode == REQUEST_CODE_PAYMENT) {
			if (resultCode == Activity.RESULT_OK) {
				String result = data.getExtras().getString("pay_result");
				/*
				 * 处理返回值 "success" - payment succeed "fail" - payment failed
				 * "cancel" - user canceld "invalid" - payment plugin not
				 * installed
				 */
				Log.i("payResult", result);
				String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
				String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
				showMsg(result, errorMsg, extraMsg);
			}
		}
	}

	//向服务器发送请求，提交订单
	public void commitOrder(){
		Orders order = new Orders();
		order.setGoodId(good.getId());
		//int userId = 3;//测试用，正式应从MyApplication.getCurrentUser().getId()获取
		int userId = MyApplication.getCurrentUser().getId();
		order.setUserId(userId);
		order.setOrderNum(getNowTime()+userId+good.getId());
		order.setCreateTime(MySdf.getDateToString(new Date(System.currentTimeMillis())));
		order.setPayTime(MySdf.getDateToString(new Date(System.currentTimeMillis())));
		order.setSendTime(null);
		order.setCompleteTime(null);
		order.setState(1);
		order.setAddId(userAddId);
		order.setLogisticsCom(null);
		order.setLogisticsNum(null);
		//将订单转换为json数据
		Gson gson = new GsonBuilder().setDateFormat(
				"yyyy-MM-dd HH:mm:ss").create();
		String orderJson = gson.toJson(order);
		Log.i("commitOrder", orderJson);
		//从配置文件获取服务器url
//		Properties prop = new Properties();
//		try {
//			prop.load(BuyGoodsActivity.class.getResourceAsStream("/com/geminno/erhuo/utils/url.properties"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		String url = prop.getProperty("heikkiUrl")+"/AddGoodOrderServlet";
		String headUrl = Url.getUrlHead();
		// 拼接url
		String url = headUrl + "/AddGoodOrderServlet";
		RequestParams rp = new RequestParams();
		rp.addBodyParameter("orderJosn",orderJson);
		HttpUtils hu = new HttpUtils();
		hu.send(HttpRequest.HttpMethod.POST, url, rp, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.i("commitOrder", "失败");
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				Log.i("commitOrder", "成功");
			}
		});
	}
	// 获取当前时间
	private String getNowTime() {
		 Date date = new Date(System.currentTimeMillis());
		 SimpleDateFormat dateFormat = new
		 SimpleDateFormat("yyyyMMddHHmmss");
		 return dateFormat.format(date);
		//return System.currentTimeMillis() + "";
	}
	
	// 显示支付结果
	public void showMsg(final String title, String msg1, String msg2) {
		String str = title;
		// if (null != msg1 && msg1.length() != 0) {
		// str += "\n" + msg1;
		// }
		// if (null != msg2 && msg2.length() != 0) {
		// str += "\n" + msg2;
		// }
		if ("success".equals(title)) {
			str = "支付成功！";
			commitOrder();
		} else if ("cancel".equals(title)) {
			str = "支付已取消！";
		} else if ("fail".equals(title)) {
			str = "支付失败！";
		}
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage(str);
		builder.setTitle("支付结果");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if ("success".equals(title)) {
					buyGoodsActivity.finish();
					GoodsDetialActivity.goodsDetialActivity.finish();
				}

			}
		});
		builder.create().show();
	}

	private static String postJson(String url, String json) throws IOException {
		MediaType type = MediaType.parse("application/json; charset=utf-8");
		RequestBody body = RequestBody.create(type, json);
		Request request = new Request.Builder().url(url).post(body).build();

		OkHttpClient client = new OkHttpClient();
		Response response = client.newCall(request).execute();

		return response.body().string();
	}

	class PaymentRequest {
		String channel;
		int amount;

		public PaymentRequest(String channel, int amount) {
			this.channel = channel;
			this.amount = amount;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getCurUserAddress();
	}
	

}
