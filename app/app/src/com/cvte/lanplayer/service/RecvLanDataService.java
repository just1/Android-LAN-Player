package com.cvte.lanplayer.service;

import java.io.IOException;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.cvte.lanplayer.GlobalData;
import com.cvte.lanplayer.constant.AppConstant;
import com.cvte.lanplayer.entity.SocketTranEntity;
import com.cvte.lanplayer.utils.RecvLanScanDeviceUtil;
import com.cvte.lanplayer.utils.RecvSocketFileUtil;
import com.cvte.lanplayer.utils.RecvSocketMessageUtil;
import com.cvte.lanplayer.utils.SendSocketFileUtil;
import com.cvte.lanplayer.utils.SendSocketMessageUtil;

public class RecvLanDataService extends Service {

	private static String TAG = "RecvLanDataService";

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

		// 注册接收控制的接收器
		mRecvCtrlReceiver = new RecvCtrlReceiver();
		IntentFilter recvCtrlFilter = new IntentFilter();
		recvCtrlFilter.addAction(GlobalData.LANScanCtrl.CTRL_LAN_RECV_ACTION);
		registerReceiver(mRecvCtrlReceiver, recvCtrlFilter);

		// 启动被其他局域网设备扫描到的接收监听
		RecvLanScanDeviceUtil.getInstance(this).StartRecv();
		RecvSocketMessageUtil.getInstance(this).StartRecv();

		// 启动文件接收监听
		RecvSocketFileUtil.getInstance(this).StartRecv();

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

	/**
	 * 从Util里面接收到文件传入
	 * 
	 * @param fileName
	 */
	public void RecvFileFromUtil(String fileName) {

		// 向android系统发起广播，扫描SD卡，更新音乐列表

		/* 方法1：4.4不能用 */
		// ScanSdCardToReflashMusicList();

		/* 方法2 缺少扫描后广播通知 */
		String filePath = Environment.getExternalStorageDirectory().getPath()
				+ '/' + GlobalData.Other.SAVE_LAN_FILE_DIR + "/" + fileName;

		Uri data = Uri.parse("file://" + filePath);
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));

		/* 方法3 */
		// MediaScannerConnection.scanFile(this, new String[] { Environment
		// .getExternalStorageDirectory().getPath()
		// + '/'
		// + GlobalData.Other.SAVE_LAN_FILE_DIR + "/" + fileName }, null,
		// null);

	}

	/**
	 * 放在RecvSocketMessageUtil里面回调，处理socket传入的消息
	 */
	public void handleSocketCommand(SocketTranEntity data, String targetIP) {
		int command = data.getmCommant();

		switch (command) {
		// 文本类型消息
		case GlobalData.SocketTranCommand.COMMAND_RECV_MSG:
			Log.d(TAG, "收到数据包：COMMAND_RECV_MSG");
			// 收到文本信息：
			Log.d(TAG, "收到文本类型消息：" + data.getmMessage());

			// 进行广播，把消息转发出去
			Intent msg_intent = new Intent();

			Bundle msg_bundle = new Bundle();
			msg_bundle.putInt(GlobalData.SocketTranCommand.GET_BUNDLE_COMMANT,
					GlobalData.SocketTranCommand.COMMAND_RECV_MSG);
			msg_bundle.putString(
					GlobalData.SocketTranCommand.GET_BUNDLE_COMMON_DATA,
					data.getmMessage());

			msg_intent.putExtras(msg_bundle);
			msg_intent
					.setAction(GlobalData.SocketTranCommand.RECV_SOCKET_FROM_SERVICE_ACTION);// action与接收器相同

			sendBroadcast(msg_intent);

			break;

		// 请求获取音乐列表
		case GlobalData.SocketTranCommand.COMMAND_REQUSET_MUSIC_LIST:
			Log.d(TAG, "收到数据包：COMMAND_REQUSET_MUSIC_LIST");

			Log.d(TAG, "收到IP: " + targetIP + " 请求获取本机的音乐列表");

			// 获取本机的音乐列表

			// 打印本机前5首歌的音乐文件名
			Log.d(TAG, "打印前5首歌");
			for (int i = 0; (i < 5)
					&& (i < AppConstant.MusicPlayData.myMusicList.size()); i++) {

				if (AppConstant.MusicPlayData.myMusicList.get(i) != null) {
					Log.d(TAG, AppConstant.MusicPlayData.myMusicList.get(i)
							.getFileName());

				}
			}

			// 发送本机的音乐列表
			// 封装一个对象实例,把音乐列表传过来
			SocketTranEntity musicList = new SocketTranEntity();

			musicList
					.setmCommant(GlobalData.SocketTranCommand.COMMAND_SEND_MUSIC_LIST);
			musicList.setmMusicList(AppConstant.MusicPlayData.myMusicList);

			// 发送音乐列表
			SendSocketMessageUtil.getInstance(RecvLanDataService.this)
					.SendMessage(musicList, targetIP);

			break;

		// 收到回传的音乐列表
		case GlobalData.SocketTranCommand.COMMAND_SEND_MUSIC_LIST:
			Log.d(TAG, "收到数据包：COMMAND_SEND_MUSIC_LIST");
			// 暂时在这里输出音乐列表
			for (int i = 0; i < data.getmMusicList().size(); i++) {
				Log.d(TAG, "收到" + data.getmMusicList().get(i).getFileName());
			}

			// 进行广播，把消息转发出去
			Intent music_list_intent = new Intent();

			Bundle music_list_bundle = new Bundle();
			music_list_bundle.putInt(
					GlobalData.SocketTranCommand.GET_BUNDLE_COMMANT,
					GlobalData.SocketTranCommand.COMMAND_SEND_MUSIC_LIST);
			music_list_bundle.putSerializable(
					GlobalData.SocketTranCommand.GET_BUNDLE_COMMON_DATA, data);

			music_list_intent.putExtras(music_list_bundle);
			music_list_intent
					.setAction(GlobalData.SocketTranCommand.RECV_SOCKET_FROM_SERVICE_ACTION);// action与接收器相同

			sendBroadcast(music_list_intent);

			break;

		// 收到局域网扫描的确认包
		case GlobalData.SocketTranCommand.COMMAND_LAN_ASK:
			Log.d(TAG, "收到数据包：COMMAND_LAN_ASK");

			// 进行广播，把消息转发出去
			Intent lan_ask_intent = new Intent();
			Bundle lan_ask_bundle = new Bundle();

			// 输入传输命令
			lan_ask_bundle.putInt(
					GlobalData.SocketTranCommand.GET_BUNDLE_COMMANT,
					GlobalData.SocketTranCommand.COMMAND_LAN_ASK);

			// 输入发送方的IP地址
			lan_ask_bundle.putString(
					GlobalData.SocketTranCommand.GET_BUNDLE_COMMON_DATA,
					data.getmSendIP());

			lan_ask_intent.putExtras(lan_ask_bundle);
			lan_ask_intent
					.setAction(GlobalData.SocketTranCommand.RECV_SOCKET_FROM_SERVICE_ACTION);// action与接收器相同

			sendBroadcast(lan_ask_intent);

			break;

		// 收到获取音乐文件请求
		case GlobalData.SocketTranCommand.COMMAND_REQUSET_MUSIC_FILE:
			Log.d(TAG, "收到数据包：COMMAND_REQUSET_MUSIC_FILE");

			// 获取需要发送的音乐ID号
			int musicID = Integer.parseInt(data.getmMessage());

			// 发送音乐文件
			SendSocketFileUtil.getInstance().SendFile(
					AppConstant.MusicPlayData.myMusicList.get(musicID)
							.getFileName(),
					AppConstant.MusicPlayData.myMusicList.get(musicID)
							.getFilePath(), data.getmSendIP());

			break;

		}
	}

	// 接收命令
	private class RecvCtrlReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			Bundle bundle = intent.getExtras();
			int control = bundle
					.getInt(GlobalData.SocketTranCommand.GET_BUNDLE_COMMANT);

			switch (control) {
			case GlobalData.LANScanCtrl.STARE_LAN_RECV:// 开始扫描
				Log.d(TAG, "recv StartRecvLAN Broadcast ");

				// 调用工具类的扫描方法
				RecvLanScanDeviceUtil.getInstance(RecvLanDataService.this)
						.StartRecv();
				break;

			case GlobalData.LANScanCtrl.STOP_LAN_RECV:// 停止扫描

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

	/**
	 * 当文件接收成功，向android系统发送广播，通知其扫描应用音乐文件的下载目录
	 * 不过在小米1S和红米测试，发现可能由于系统限制，无法发出该广播
	 */
	private void ScanSdCardToReflashMusicList() {

		Log.d(TAG, "向Android系统发起广播，请求扫描音乐列表");

		// 只刷新指定目录，这样速度才快
		// sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
		// Uri.parse("file://" + Environment.getExternalStorageDirectory()
		// + "/" + GlobalData.Other.SAVE_LAN_FILE_DIR)));

		sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
				Uri.parse("file://"
						+ Environment.getExternalStorageDirectory().getPath()
						+ '/' + GlobalData.Other.SAVE_LAN_FILE_DIR)));

	}


}
