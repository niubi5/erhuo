package com.geminno.erhuo;

import java.io.File;
import java.util.List;

import android.app.Application;
import android.os.Environment;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.geminno.erhuo.R;
import com.geminno.erhuo.entity.Markets;
import com.geminno.erhuo.entity.Users;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class MyApplication extends Application {

	private static Users curUser;
	private static BDLocation curLocation;
	private static List<Markets> MarketsList;

	@Override
	public void onCreate() {
		super.onCreate();
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		SDKInitializer.initialize(this);
		// 初始化ImageLoader
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.icon_stub) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.icon_empty) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.icon_error) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
				.imageScaleType(ImageScaleType.NONE)// 设置图片以如何的编码方式显示
				.considerExifParams(true)// 启用EXIF和JPEG图像格式
//				.displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间
//				.displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
				.build(); // 创建配置过得DisplayImageOption对象

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this)
				.defaultDisplayImageOptions(options)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCache(new UsingFreqLimitedMemoryCache(2* 1024 * 1024))
				.memoryCacheSize(2 * 1024 * 1024)// 内存缓存大小
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())// 将保存的时候的URI名称用MD5 加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
//				.discCache(new UnlimitedDiscCache(cacheDir))// 硬盘缓存路径
				.build();
		ImageLoader.getInstance().init(config);

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
	
}
