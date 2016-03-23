package com.geminno.erhuo.utils;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class ImageLoader {
	/**
	 * 图片缓存的核心类
	 */
	private LruCache<String, Bitmap> mLruCache;
	/**
	 * 线程池
	 */
	private ExecutorService mThreadPool;
	/**
	 * 线程池的数量，默认为1
	 */
	private int mThreadCount = 1;
	/**
	 * 队列的调度方式
	 */
	private Type mType = Type.LIFO;
	/**
	 * 任务队列
	 */
	private LinkedList<Runnable> mTasks;
	/**
	 * 轮询的线程
	 */
	private Thread mPoolThread;
	/**
	 * 子线程handler，用于执行耗时任务
	 */
	private Handler mPoolThreadHandler;
	/**
	 * 运行在UI线程的Handler，用于给ImageView设置图片
	 */
	private Handler mHandler;
	/**
	 * 引入一个值为1的信号量，防止mPoolThreadHandler未初始化完成
	 */
	private volatile Semaphore mSemaphore = new Semaphore(1);
	/**
	 * 引入一个值为1的信号量，由于线程池内部也有一个阻塞线程，防止加入任务的速度过快，使LIFO效果不明显
	 */
	private volatile Semaphore mPoolSemphore;
	/**
	 * ImageLoader实例
	 */
	private static ImageLoader mInstance;

	/**
	 * 队列的调度方式
	 */
	public enum Type {
		FIFO, LIFO
	}

	/**
	 * 单例获得该实例对象
	 */
	public static ImageLoader getInstance() {
		if (mInstance == null) {
			synchronized (ImageLoader.class) {
				if (mInstance == null) {
					mInstance = new ImageLoader(1, Type.LIFO);
				}

			}
		}
		return mInstance;
	}

	private ImageLoader(int threadCount, Type type) {
		init(threadCount, type);
	}

	private void init(int threadCount, Type type) {
		// 轮询线程
		mPoolThread = new Thread() {

			@Override
			public void run() {
				super.run();
				// 请求一个信号量
				try {
					mSemaphore.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// 给线程创建一个消息循环
				Looper.prepare();
				// 轮询线程handler
				mPoolThreadHandler = new Handler() {

					@Override
					public void handleMessage(Message msg) {
						super.handleMessage(msg);
						mThreadPool.execute(getTask());
						try {
							mPoolSemphore.acquire();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

				};
				// 释放信号量
				mSemaphore.release();
				// 在当前线程运行消息队列
				Looper.loop();
			}

		};
		// 启动轮询线程
		mPoolThread.start();

		// 获取应用程序最大可用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		// 设置缓存占用内存大小
		mLruCache = new LruCache<String, Bitmap>(cacheSize) {

			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}

		};
		mThreadPool = Executors.newFixedThreadPool(threadCount);
		mPoolSemphore = new Semaphore(threadCount);
		mTasks = new LinkedList<Runnable>();
		mType = type == null ? Type.LIFO : type;
	}

	/**
	 * 加载图片
	 * 
	 * @param path
	 * @param iamgeView
	 */
	public void loadImage(final String path, final ImageView imageView) {
		// 给imageView设置一个tag
		imageView.setTag(path);
		// UI线程Handler
		if (mHandler == null) {
			mHandler = new Handler() {

				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					// 取出子线程发送的消息
					ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
					ImageView imageView = holder.imageView;
					Bitmap bm = holder.bitmap;
					String path = holder.path;
					if (imageView.getTag().toString().equals(path)) {
						// 设置imageView
						imageView.setImageBitmap(bm);
					}
				}

			};
		}
		// 从缓存中取出Bitmap
		Bitmap bm = getBitmapFromLruCache(path);
		if (bm != null) {
			ImgBeanHolder holder = new ImgBeanHolder();
			holder.bitmap = bm;
			holder.imageView = imageView;
			holder.path = path;
			Message message = Message.obtain();
			message.obj = holder;
			mHandler.sendMessage(message);
		} else {
			addTask(new Runnable() {
				@Override
				public void run() {
					ImageSize imageSize = getImageViewWidth(imageView);
					int reqWidth = imageSize.width;
					int reqHeight = imageSize.height;
					Bitmap bm = decodeSampledBitmapFromResource(path, reqWidth,
							reqHeight);
					addBitmapToLruCache(path, bm);
					ImgBeanHolder holder = new ImgBeanHolder();
					holder.bitmap = getBitmapFromLruCache(path);
					holder.imageView = imageView;
					holder.path = path;
					Message message = Message.obtain();
					message.obj = holder;
					// 发送从缓存中取出的Bitmap
					mHandler.sendMessage(message);
					// 释放信号量
					mPoolSemphore.release();
				}
			});
		}
	}

	/**
	 * 添加一个任务到任务队列
	 * 
	 * @param runnable
	 *            要添加的任务
	 */
	private synchronized void addTask(Runnable runnable) {
		// 请求信号量，防止mPoolThreadHandler为null
		if (mPoolThreadHandler == null) {
			try {
				mSemaphore.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mTasks.add(runnable);
		mPoolThreadHandler.sendEmptyMessage(0x110);
	}

	/**
	 * 取出一个任务
	 */
	private synchronized Runnable getTask() {
		if (mType == Type.FIFO) {
			return mTasks.removeFirst();
		} else if (mType == Type.LIFO) {
			return mTasks.removeLast();
		}
		return null;
	}

	/**
	 * 单例获得该实例对象
	 */
	public static ImageLoader getInstance(int threadCount, Type type) {
		if (mInstance == null) {
			synchronized (ImageLoader.class) {
				if (mInstance == null) {
					mInstance = new ImageLoader(threadCount, type);
				}
			}
		}
		return mInstance;
	}

	/**
	 * 根据ImageView获得适当的压缩的宽和高
	 * 
	 * @param imageView
	 * @return
	 */
	private ImageSize getImageViewWidth(ImageView imageView) {
		ImageSize imageSize = new ImageSize();
		final DisplayMetrics displayMetrics = imageView.getContext()
				.getResources().getDisplayMetrics();
		final LayoutParams params = imageView.getLayoutParams();
		int width = params.width == LayoutParams.WRAP_CONTENT ? 0 : imageView
				.getWidth();
		if (width <= 0) {
			// 在容器里的宽度
			width = params.width;
		}
		if (width <= 0) {
			// imageView 设置最大值
			width = getImageViewFieldValue(imageView, "mMaxWidth");
		}
		if (width <= 0) {
			// 屏幕宽度
			width = displayMetrics.widthPixels;
		}
		int height = params.height == LayoutParams.WRAP_CONTENT ? 0 : imageView
				.getHeight();
		if (height <= 0) {
			height = params.height;
		}
		if (height <= 0) {
			height = getImageViewFieldValue(imageView, "mMaxHeight");
		}
		if (height <= 0) {
			height = displayMetrics.heightPixels;
		}
		imageSize.width = width;
		imageSize.height = height;
		return imageSize;
	}

	/**
	 * 从LruCache中获取一张图片，如果不存在就返回null
	 * 
	 * @param key
	 * @return
	 */
	private Bitmap getBitmapFromLruCache(String key) {
		return mLruCache.get(key);
	}

	/**
	 * 往LruCache中添加一张图片
	 * 
	 * @param key
	 * @param bitmap
	 */
	private void addBitmapToLruCache(String key, Bitmap bitmap) {
		// 判断根据指定的bitmap在mLruCache中是否存在
		if (getBitmapFromLruCache(key) == null) {
			if (bitmap != null) {
				mLruCache.put(key, bitmap);
			}
		}
	}

	/**
	 * 计算inSampleSize，用于压缩图片
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private int calculateInSamleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// 源图片的宽度
		int width = options.outWidth;
		int height = options.outHeight;
		int inSampleSize = 1;

		if (width > reqWidth && height > reqHeight) {
			// 计算出实际宽度和目标宽度的比率
			int widthRatio = Math.round(((float) width / (float) reqWidth));
			int heightRation = Math.round((float) width / (float) reqWidth);
			inSampleSize = Math.max(widthRatio, heightRation);
		}
		return inSampleSize;
	}

	private Bitmap decodeSampledBitmapFromResource(String pathName,
			int reqWidth, int reqHeight) {
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		/*
		 * inJustDecodeBounds
		 * 如果将其设为true的话，在decode时将会返回null,通过此设置可以去查询一个bitmap的属性
		 * ，比如bitmap的长与宽，而不占用内存大小
		 */
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, options);
		// 调用上面定义的方法计算inSampleSize的值
		options.inSampleSize = calculateInSamleSize(options, reqWidth,
				reqHeight);
		// 使用获取到的inSampleSize值再次解析图片
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);
		return bitmap;
	}


	/**
	 * 反射获得ImageView设置的最大宽度和高度
	 * 
	 * @param object
	 * @param fieldName
	 * @return
	 */
	private static int getImageViewFieldValue(Object object, String fieldName) {
		int value = 0;
		try {
			Field field = ImageView.class.getDeclaredField(fieldName);
			// 访问类中的私有成员变量必须要设置，否则会报异常
			field.setAccessible(true);
			int fieldValue = (Integer) field.get(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
				value = fieldValue;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	

	/**
	 * bitmap imageView path
	 * 
	 * @author Administrator
	 * 
	 */
	private class ImgBeanHolder {
		Bitmap bitmap;
		ImageView imageView;
		String path;
	}

	/**
	 * imageSize，图片的宽和高
	 */
	private class ImageSize {
		int width;
		int height;
	}
}
