package com.cvte.lanplayer.service;

import java.io.IOException;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.cvte.lanplayer.GlobalData;
import com.cvte.lanplayer.utils.RecvLanScanDeviceUtil;
import com.cvte.lanplayer.utils.RecvSocketMessageUtil;

public class RecvLanDataService extends Service {

	private static String TAG = "RecvLanDataService";

	// 收到消息，再分发处理
	private RecvScoketMsgReceiver mRecvScoketMsgReceiver;

	// 控制接收
	private RecvCtrlReceiver mRecvCtrlReceiver;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate RecvLanDataService");

		// 注册转发消息的接收器
		mRecvScoketMsgReceiver = new RecvScoketMsgReceiver();
		IntentFilter recvScoketFilter = new IntentFilter();
		recvScoketFilter.addAction(GlobalData.RECV_LAN_SOCKET_MSG_ACTION);
		registerReceiver(mRecvScoketMsgReceiver, recvScoketFilter);

		// 注册接收控制的接收器
		mRecvCtrlReceiver = new RecvCtrlReceiver();
		IntentFilter recvCtrlFilter = new IntentFilter();
		recvCtrlFilter.addAction(GlobalData.CTRL_RECV_ACTION);
		registerReceiver(mRecvCtrlReceiver, recvCtrlFilter);

		// 启动被其他局域网设备扫描到的接收监听
		// RecvLanScanDeviceUtil.getInstance(this).StartRecv();
		RecvSocketMessageUtil.getInstance(this).StartRecv();

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		Log.d(TAG, "onDestroy SendLanDataService");

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

	// 把收到的消息转发出去
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

				// 获取本机的音乐列表
				// MediaPlayerUtil.getInstance(RecvLanDataService.this).getMusicList();
				// AppConstant.MusicPlayData.myMusicList;

				// 发送本机的音乐列表

				break;
			}

		}
	}

	// 接收命令
	private class RecvCtrlReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			Bundle bundle = intent.getExtras();
			int control = bundle.getInt(GlobalData.GET_BUNDLE_COMMANT);

			switch (control) {
			case GlobalData.STARE_LAN_RECV:// 开始扫描
				Log.d(TAG, "recv StartRecvLAN Broadcast ");

				// 调用工具类的扫描方法
				RecvLanScanDeviceUtil.getInstance(RecvLanDataService.this)
						.StartRecv();
				break;

			case GlobalData.STOP_LAN_RECV:// 停止扫描

				Log.d(TAG, "recv StopRecvLAN Broadcast ");
				// 调用工具类的停止扫描方法
				try {
					RecvLanScanDeviceUtil.getInstance(RecvLanDataService.this)
							.StopRecv();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;
			}
		}
	}

}
