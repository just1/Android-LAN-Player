package com.cvte.lanplayer.utils;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

/**
 * 显示TOAST消息
 * 
 * @author JHYin
 * 
 */
public class ShowToastMsgUtil {

	private final String TAG = "ShowToastMsgUtil";
	private static ShowToastMsgUtil instance = null;

	// 用于收到消息后回调
	private static Context mContext;

	/**
	 * 私有默认构造子
	 */
	private ShowToastMsgUtil() {

	}

	/**
	 * 静态工厂方法
	 */
	public static synchronized ShowToastMsgUtil getInstance(Context context) {

		// mContext = context;
		mContext = context;

		if (instance == null) {
			instance = new ShowToastMsgUtil();
		}

		return instance;
	}

	/**
	 * 显示Toast 消息
	 * 
	 * @param msg
	 */
	public void ShowToastMsg(final String msg) {

		new Thread(new Runnable() {
			public void run() {
				// Log.d(TAG, "Service in Thread: " + "\n" + "当前线程名称："
				// + Thread.currentThread().getName() + "," + "当前线程名称："
				// + Thread.currentThread().getId());

				Looper.prepare();

				Toast.makeText(mContext.getApplicationContext(), msg,
						Toast.LENGTH_SHORT).show();

				Looper.loop();
			}
		}).start();

	}
}
