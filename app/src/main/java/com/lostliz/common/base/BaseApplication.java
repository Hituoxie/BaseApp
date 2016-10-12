package com.lostliz.common.base;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

public class BaseApplication extends Application {
	/** 主线程的上下文*/
	private static BaseApplication mContext = null;
	/** 主线程的handler  */
	private static Handler mMainThreadHandler = null;
	/** 主线程的looper  */
	private static Looper mMainThreadLooper = null;
	/** 主线程 */
	private static Thread mMainThread = null;
	/** 主线程id上 */
	private static int mMainThreadId;
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.mContext = this;
		this.mMainThreadHandler = new Handler();
		this.mMainThreadLooper = getMainLooper();
		this.mMainThread = Thread.currentThread();
		//android.os.Progress.myUid()  用户id
		//android.os.Progress.myTid()  调用线程的id
		//android.os.Progress.myPid()  进程id
		this.mMainThreadId = android.os.Process.myTid();
		
	}

	/**
	 * @return 主线程的上下文
	 */
	public static BaseApplication getApplication() {
		return mContext;
	}

	/**
	 * @return  主线程的handler 
	 */
	public static Handler getMainThreadHandler() {
		return mMainThreadHandler;
	}

	/**
	 * @return 主线程的looper
	 */
	public static Looper getMainThreadLooper() {
		return mMainThreadLooper;
	}

	/**
	 * @return 主线程
	 */
	public static Thread getMainThread() {
		return mMainThread;
	}

	/**
	 * @return 主线程id上
	 */
	public static int getMainThreadId() {
		return mMainThreadId;
	}

}
