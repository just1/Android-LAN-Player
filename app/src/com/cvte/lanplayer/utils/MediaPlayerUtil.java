package com.cvte.lanplayer.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.cvte.lanplayer.R;
import com.cvte.lanplayer.constant.AppConstant;
import com.cvte.lanplayer.service.PlayerService;

public class MediaPlayerUtil {
	private Intent startIntent;
	//private ActivityReceive activityReceive;
	private static Activity mActivity;
	private static MediaPlayerUtil instance = null;

	/**
	 * 私有默认构造子
	 */
	private MediaPlayerUtil() {
//		// 创建IntentFilter
//		IntentFilter filter = new IntentFilter();
//		// 指定BroadcastReceiver监听的Action
//		filter.addAction(AppConstant.MusicPlayVariate.UPDATE_ACTION);
//		// 注册BroadcastReceiver
//		mActivity.registerReceiver(activityReceive, filter);
//		startIntent = new Intent(mActivity, PlayerService.class);
//		// 启动后台Service
//		mActivity.startService(startIntent);

	}

	/**
	 * 静态工厂方法
	 */
	public static synchronized MediaPlayerUtil getInstance(Activity activity) {

		mActivity = activity;
		if (instance == null) {
			instance = new MediaPlayerUtil();
		}

		return instance;
	}

	public void NextMusic() {

	}

	public void PauseMusic() {

	}

	public void PlayMusic() {

	}

	public void GetMusicList() {

	}
	
	

	

}
