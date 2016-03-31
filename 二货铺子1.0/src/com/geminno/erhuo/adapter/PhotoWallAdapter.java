package com.geminno.erhuo.adapter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.geminno.erhuo.R;
import com.geminno.erhuo.utils.DiskLruCache;
import com.geminno.erhuo.utils.DiskLruCache.Snapshot;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/***
 * GridView的适配器，负责异步从网络上下载图片并显示在照片墙上
 * 
 * @author Administrator
 * 
 */
public class PhotoWallAdapter extends ArrayAdapter<String> {

	/**
	 * 记录所有正在下载或等待下载的任务
	 */
	private Set<BitmapWorkerTask> taskCollection;
	/**
	 * 图片缓存技术的核心类，遵循最近最少原则
	 */
	private LruCache<String, Bitmap> mMemoryCache;
	/**
	 * 硬盘缓存技术的核心类
	 */
	private DiskLruCache mDiskLruCache;
	/**
	 * GirdView的实例
	 */
	private GridView mPhotoWall;
	/**
	 * 记录每个子项的高度
	 */
	private int mItemHeight = 0;

	/**
	 * 
	 * @param context
	 *            上下文
	 * @param textViewResourceId
	 *            实例化view时带的textView
	 * @param objects
	 *            url
	 * @param photoWall
	 *            gridView
	 */
	public PhotoWallAdapter(Context context, int textViewResourceId,
			ArrayList<String> objects, GridView photoWall) {
		super(context, textViewResourceId, objects);
		mPhotoWall = photoWall;
		taskCollection = new HashSet<BitmapWorkerTask>();
		// 获取应用程序最大可用内存
		long maxMemory = Runtime.getRuntime().maxMemory();
		// 设置缓存占用最大内存
		int cacheSize = (int) (maxMemory / 8);
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getByteCount();
			}
		};
		// 获取图片缓存路径
		File  cacheDir = getDiskCacheDir(context, "demo");
		// 如果不存在就创建
        if(!cacheDir.exists()){
        	cacheDir.mkdirs();
        }
        try {
        	/**
        	 *  创建DiskLruCache实例，初始化数据
        	 *  版本号更新的时候，会清除缓存，从网上重新获取
        	 */
			mDiskLruCache = DiskLruCache.open(cacheDir, getAppVersion(context),1, 30*1024*1024);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从内存缓存中取出图片
	 * 
	 * @param key
	 *            LruCache的键，url地址
	 * @return 取出的Bitmap对象，或者null
	 */
	public Bitmap getBitmapFromMemeoryCache(String key) {
		return mMemoryCache.get(key);
	}

	/**
	 * 将图片缓存到LruCache中
	 * 
	 * @param key
	 *            LruCache的键，为Url地址
	 * @param bitmap
	 *            LruCache的值，为网络上下载的Bitmap对象
	 */
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemeoryCache(key) != null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	/**
	 * 加载Bitmap对象， 此方法会在LruCache中检查所有屏幕中可见的ImageView的Bimtap对象，
	 * 如果发现任何一个ImageView的Bitmap对象不在缓存中，就开启异步线程下载图片
	 * 
	 * @param imageView
	 * @param imageUrl
	 */
	public void loadBitmaps(ImageView imageView, String imageUrl) {
		try {
			// 从缓存获取图片
			Bitmap bitmap = getBitmapFromMemeoryCache(imageUrl);
			if (bitmap == null) {
				BitmapWorkerTask task = new BitmapWorkerTask();
				taskCollection.add(task);
				// 执行任务
				task.execute(imageUrl);
			} else {
				if (imageView != null && bitmap != null) {
					imageView.setImageBitmap(bitmap);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 取消所有正在下载或等待下载的任务
	 */
	public void cancleAllTasks() {
		if (taskCollection != null) {
			for (BitmapWorkerTask task : taskCollection) {
				task.cancel(false);
			}
		}
	}

	/**
	 * 根据传入的uniqueName获取硬盘缓存的路径地址
	 * 
	 * @param context
	 * @param uniqueName
	 * @return
	 */
	public File getDiskCacheDir(Context context, String uniqueName) {
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath + File.separator + uniqueName);
	}
	
	/**
	 * 为了让文件名和图片的Url一一对应，可以采用url作为文件名，但为了防止url中的特殊字符造成命名不合法，采用MD5算法对url进行编码，
	 * 生成0-F这样的字符创，而且唯一
	 * 
	 * @param key
	 * @return
	 */
	public String hashKeyForDisk(String key) {
		String cacheKey;
		try {
			/**
			 * MessageDigest类用于为应用程序提供信息摘要算法的功能，如 MD5 或 SHA
			 * 算法。简单点说就是用于生成散列码。信息摘要是安全的单向哈希函数，它接收任意大小的数据，输出固定长度的哈希值
			 */
			MessageDigest mDigest = MessageDigest.getInstance("MD5");
			// 处理数据
			mDigest.update(key.getBytes());
			// 调用digest完成计算并返回结果
			cacheKey = bytesToHexString(mDigest.digest());

		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}

	/**
	 * 将字节数组转换成0-9,a-f的字符串
	 * 
	 * @param bytes
	 * @return
	 */
	private String bytesToHexString(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i]);
			if (bytes.length == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	/**
	 * 获取当前应用版本号
	 * 
	 * @param context
	 * @return
	 */
	public int getAppVersion(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 1;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final String url = getItem(position);
		View view;
		if(convertView == null){
			view = LayoutInflater.from(getContext()).inflate(R.layout.photo_wall, null);
		}else{
			view = convertView;
		}
		final ImageView imageView = (ImageView) view.findViewById(R.id.photo);
		if(imageView.getLayoutParams().height != mItemHeight){
			imageView.getLayoutParams().height = mItemHeight;
		}
		// 给ImageView设置tag，保证异步加载图片不会乱序
		imageView.setTag(url);
		imageView.setImageResource(R.drawable.empty_photo);
		loadBitmaps(imageView, url);
		return view;
	}

	
	/**
	 * 设置子项的高度
	 */
	public void setItemHeight(int height){
		if(height == mItemHeight){
			return;
		}
		mItemHeight = height;
		notifyDataSetChanged();
	}
	
	/**
	 * 将缓存记录同步到journal中
	 */
	public void flushCache(){
		if(mDiskLruCache != null){
			try {
				mDiskLruCache.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	/**
	 * 异步下载图片的任务
	 * 
	 * @author Administrator
	 * 
	 */
	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
		/**
		 * 图片的Url
		 */
		private String imageUrl;

		// 执行在的子线程的下载任务
		@Override
		protected Bitmap doInBackground(String... params) {
			imageUrl = params[0];
			// 用于描述文件的类
			FileDescriptor fileDescriptor = null;
			FileInputStream fileInputStream = null;
			// 输入值的快照
			Snapshot snapShot = null;
			// 生成图片Url对应的key
			try {
				String key = hashKeyForDisk(imageUrl);
				snapShot = mDiskLruCache.get(key);
				if (snapShot == null) {
					// 如果没有找到对应的缓存，则准备从网络上请求数据，并写入缓存
					DiskLruCache.Editor editor = mDiskLruCache.edit(key);
					if (editor != null) {
						OutputStream outputStream = editor.newOutputStream(0);
						if (downloadFromUrlToStream(imageUrl, outputStream)) {
							editor.commit();
						} else {
							editor.abort();
						}
					}
					mDiskLruCache.flush();
					// 缓存被写入后，再次查找key对应的缓存
					snapShot = mDiskLruCache.get(key);
				}
				if (snapShot != null) {
					// 获取缓存文件输入流
					fileInputStream = (FileInputStream) snapShot
							.getInputStream(0);
					fileDescriptor = fileInputStream.getFD();
				}
				// 将缓存数据解析成Bitamp对象
				Bitmap bitmap = null;
				if (fileDescriptor != null) {
					bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
				}
				if (bitmap != null) {
                   addBitmapToMemoryCache(params[0], bitmap);
				}
				return bitmap;
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				if(fileDescriptor != null && fileInputStream != null){
				    try {
						fileInputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			return null;
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			// 根据tag找到相应的View控件，将其显示在ImageView上
			ImageView imageView = (ImageView) mPhotoWall.findViewWithTag(imageUrl);
			if(imageView != null && result != null){
				imageView.setImageBitmap(result);
			}
			taskCollection.remove(this);
		}



		/**
		 * 建立Http请求，并获取Bitmap对象
		 * 
		 * @param imageUrl
		 *            图片的url地址
		 * @param outputStream
		 * @return
		 */
		private boolean downloadFromUrlToStream(String imageUrl,
				OutputStream outputStream) {
			HttpURLConnection urlConncection = null;
			BufferedOutputStream out = null;
			BufferedInputStream in = null;
			try {
				final URL url = new URL(imageUrl);
				urlConncection = (HttpURLConnection) url.openConnection();
				in = new BufferedInputStream(urlConncection.getInputStream(),
						2 * 1024 * 1024);
				out = new BufferedOutputStream(outputStream, 2 * 1024 * 1024);
				int b;
				while ((b = in.read()) != -1) {
					out.write(b);
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (urlConncection != null) {
					urlConncection.disconnect();
				}
				try {
					if (in != null) {
						in.close();
					}
					if (out != null) {
						out.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return false;
		}

	}

}
