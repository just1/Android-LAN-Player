package com.cvte.lanplayer.service;

import java.io.IOException;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;

import com.cvte.lanplayer.GlobalData;
import com.cvte.lanplayer.constant.AppConstant;
import com.cvte.lanplayer.utils.MediaPlayerUtil;
import com.cvte.lanplayer.utils.RecvLanScanDeviceUtil;
import com.cvte.lanplayer.utils.RecvSocketMessageUtil;

public class RecvLanDataService extends Service {

	// 收到消息，再分发处理
	private RecvScoketMsgReceiver mRecvScoketMsgReceiver;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// 注册接收器
		mRecvScoketMsgReceiver = new RecvScoketMsgReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalData.RECV_LAN_SOCKET_MSG_ACTION);
		registerReceiver(mRecvScoketMsgReceiver, filter);

		// 启动被其他局域网设备扫描到的接收监听
		RecvLanScanDeviceUtil.getInstance(this).StartRecv();
		RecvSocketMessageUtil.getInstance(this).StartRecv();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		// 停止被其他局域网设备扫描到的接收监听
		try {
			RecvLanScanDeviceUtil.getInstance(this).StopRecv();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			RecvSocketMessageUtil.getInstance(this).StopRecv();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private class RecvScoketMsgReceiver extends BroadcastReceiver {

		// 自定义一个广播接收器
		@Override
		public void onReceive(Context context, Intent intent) {

			Bundle bundle = intent.getExtras();
			// 获取指令
			int commant = bundle.getInt(GlobalData.GET_BUNDLE_COMMANT);

			switch (commant) {
			case GlobalData.RECV_MSG:


				Intent msg_intent = new Intent();
				msg_intent.putExtras(bundle);
				// action与接收器相同
				msg_intent
						.setAction(GlobalData.RECV_SOCKET_FROM_SERVICE_ACTION);

				sendBroadcast(msg_intent);

				break;
			case GlobalData.REQUSET_MUSIC_LIST:

				//获取本机的音乐列表
				//MediaPlayerUtil.getInstance(RecvLanDataService.this).getMusicList();
				//AppConstant.MusicPlayData.myMusicList;
				
				
				//发送本机的音乐列表
				
				
				break;
			}

		}
	}

}
