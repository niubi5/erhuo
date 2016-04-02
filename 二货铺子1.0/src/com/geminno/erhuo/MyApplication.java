package com.geminno.erhuo;

import io.rong.imkit.RongIM;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.geminno.erhuo.R;
import com.geminno.erhuo.entity.Address;
import com.geminno.erhuo.entity.Markets;
import com.geminno.erhuo.entity.Users;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class MyApplication extends Application {

	private static Users curUser;
	private static BDLocation curLocation;
	private static List<Markets> MarketsList;
	private static com.geminno.erhuo.entity.Address userAdds;
	private static String curToken;
	private static ArrayList<Integer> goodsIds;
	private static List<Markets> listMarkets;
	private static Address useAddress;

	@Override
	public void onCreate() {
		super.onCreate();
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		SDKInitializer.initialize(this);
		// 初始化融云
		RongIM.init(this);
		// 初始化ImageLoader
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.icon_stub) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.icon_empty) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.icon_error) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(false) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)// 设置图片以如何的编码方式显示
				.considerExifParams(true)// 启用EXIF和JPEG图像格式
				.bitmapConfig(Bitmap.Config.RGB_565)// 比默认的ARGB_8888少消耗2倍内存
				// .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间
				// .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
				.build(); // 创建配置过得DisplayImageOption对象

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).defaultDisplayImageOptions(options)
				.threadPriority(Thread.NORM_PRIORITY - 2).threadPoolSize(3)
				// .memoryCache(new UsingFreqLimitedMemoryCache(2* 1024 * 1024))
				// .memoryCacheSize(4 * 1024 * 1024)// 内存缓存大小
				.denyCacheImageMultipleSizesInMemory()
				// .discCacheFileNameGenerator(new Md5FileNameGenerator())//
				// 将保存的时候的URI名称用MD5 加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				// .discCache(new UnlimitedDiscCache(cacheDir))// 硬盘缓存路径
				.build();
		ImageLoader.getInstance().init(config);

	}

	/**
	 * 获得当前进程的名字
	 * 
	 * @param context
	 * @return 进程号
	 */
	public static String getCurProcessName(Context context) {

		int pid = android.os.Process.myPid();

		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
				.getRunningAppProcesses()) {

			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		return null;
	}

	// 当前登录用户
	public static Users getCurrentUser() {

		return curUser;

	}

	public static void setUsers(Users users) {
		curUser = users;
	}

	// 当前经纬度
	public static void setLocation(BDLocation location) {
		curLocation = location;
	}

	public static BDLocation getLocation() {
		return curLocation;
	}

	// 获取集市集合
	public static void setMarketsList(List market) {
		MarketsList = market;
	}

	public static List<Markets> getMarketsList() {
		return MarketsList;
	}

	// 获取当前用户的默认收货地址
	public static void setCurUserDefAddress(
			com.geminno.erhuo.entity.Address userAddress) {
		userAdds = userAddress;
	}

	public static com.geminno.erhuo.entity.Address getCurUserDefAddress() {
		return userAdds;
	}

	// 获取当前用户融云Token
	public static void setCurToken(String token) {
		curToken = token;
	}

	public static String getCurToken() {
		return curToken;
	}

	// 获取当前用户收藏
	public static void setCollections(ArrayList<Integer> goodsIdList) {
		goodsIds = goodsIdList;
	}

	public static ArrayList<Integer> getCollection() {
		return goodsIds;
	}

	public static List<Markets> getMyMarkets() {
		return listMarkets;
	}
	
	public static void setMyMarkets(List<Markets> myMarkets){
		listMarkets = myMarkets;
	}
	
	// 获取当前用户使用的收货地址
	public static Address getUseAddress() {
		return useAddress;
	}

	public static void setUseAddress(Address useAddress) {
		MyApplication.useAddress = useAddress;
	}

}
