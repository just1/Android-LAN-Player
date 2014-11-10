package com.cvte.lanplayer.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;


/**
 * 检查SD卡是否装载
 * 
 * @author JHYin
 *
 */
public class CheckSDCardUtil {

	private static final String TAG = "CheckSDCardUtil";
	
	/**
	 * 判断SD卡是否插入
	 * 
	 * @return
	 */
	public static boolean IsHaveSDcard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			Log.d(TAG, "存在SD卡");

			return true;
		} else {
			Log.d(TAG, "不存在SD卡");
			
			return false;
		}
	}
	
}
