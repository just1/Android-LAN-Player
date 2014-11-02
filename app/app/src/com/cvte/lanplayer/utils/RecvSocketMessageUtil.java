package com.cvte.lanplayer.utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cvte.lanplayer.GlobalData;
import com.cvte.lanplayer.entity.SocketTranEntity;

/**
 * 接收其他设备的socket消息
 * 
 * 比如接收发送文本消息和请求获取音乐列表的socket文字通信都在这里
 * 
 * 这里的职责分配有点问题，以后改进。
 * 
 * @author JHYin
 * 
 */
public class RecvSocketMessageUtil {

	private final String TAG = "RecvSocketMessageUtil";

	private static Context mContext;
	private static RecvSocketMessageUtil instance = null;

	private static Thread mRecvThread = null;

	ServerSocket mServerSocket = null;
	List<Socket> mSocketList = new ArrayList<Socket>();

	/**
	 * 私有默认构造子
	 */
	private RecvSocketMessageUtil() {

		// 如果接收线程没有初始化，则进行初始化
		if (mRecvThread == null) {
			InitRecvThread();

			Log.d(TAG, "InitRecvThread");
		}

	}

	/**
	 * 静态工厂方法
	 */
	public static synchronized RecvSocketMessageUtil getInstance(Context context) {

		mContext = context;
		if (instance == null) {
			instance = new RecvSocketMessageUtil();
		}

		return instance;
	}

	/**
	 * 收到相应的IP地址的数据，则进行广播转发出去
	 * 
	 * @param str
	 */
	private void SendMessage(String str) {
		Intent intent = new Intent();

		Bundle data = new Bundle();
		data.putInt(GlobalData.GET_BUNDLE_COMMANT, GlobalData.COMMAND_RECV_MSG);
		data.putString(GlobalData.GET_BUNDLE_DATA, str);

		intent.putExtras(data);
		intent.setAction(GlobalData.RECV_LAN_SOCKET_MSG_ACTION);// action与接收器相同

		mContext.sendBroadcast(intent);
	}

	public void StartRecv() {

		// 如果线程已经结束了，则重新开启线程
		if (mRecvThread.getState() == Thread.State.NEW) {
			mRecvThread.start();

			Log.d(TAG, "StartRecv");
		}

	}

	/**
	 * 初始化接收线程
	 */
	private void InitRecvThread() {
		mRecvThread = new Thread() {

			@Override
			public void run() {

				try {
					mServerSocket = new ServerSocket(
							GlobalData.SOCKET_TRANSMIT_PORT);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				while (true) {
					// 接收文件名
					Socket socket;
					try {
						socket = mServerSocket.accept();

						// 加入队列之中，方便日后清理
						mSocketList.add(socket);

						// 对象数据的输入与输出，需要用ObjectInputStream和ObjectOutputStream进行
						ObjectInputStream in = new ObjectInputStream(
								socket.getInputStream());
						ObjectOutputStream out = new ObjectOutputStream(
								socket.getOutputStream());
						// 读取客户端的对象数据流
						SocketTranEntity songList = (SocketTranEntity) in
								.readObject();

						// 处理收到的数据
						HandleMessage(songList, socket.getInetAddress()
								.getHostAddress());

						// List cityList = songList.GetSongList();

						// InputStream nameStream = socket.getInputStream();
						// InputStreamReader streamReader = new
						// InputStreamReader(
						// nameStream);
						// BufferedReader br = new BufferedReader(streamReader);
						// String recv_data = br.readLine();
						//
						// Log.d(TAG, "来自： "
						// + socket.getInetAddress().getHostAddress()
						// + " 的消息" + recv_data);
						//
						// // 处理接收到的消息
						// HandleMessage(recv_data, socket.getInetAddress()
						// .getHostAddress());
						//
						// br.close();
						// streamReader.close();
						// nameStream.close();

						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
	}

	public void StopRecv() throws IOException {
		if (mRecvThread != null) {
			mRecvThread.interrupt();
		}

		if (mServerSocket != null) {
			mServerSocket.close();
		}

		// 清理所有有可能开过的线程
		for (int i = 0; i < mSocketList.size(); i++) {
			if (mSocketList.get(i) != null) {
				mSocketList.get(i).close();
			}
		}

		Log.d(TAG, "StopRecv");
	}

	/**
	 * 处理发过来的数据
	 * 
	 * str:发过来的原始文本消息 targetIP：发送方的IP地址
	 */
	private void HandleMessage(SocketTranEntity data, String targetIP) {

		// if (str.startsWith(GlobalData.COMMAND_HEAD_SEND_MSG)) { // 文本消息通信
		//
		// // 收到文本信息：
		// Log.d(TAG,
		// "收到文本类型消息："
		// + str.substring(GlobalData.COMMAND_HEAD_SEND_MSG
		// .length()));
		//
		// // 截断消息位再发送
		// SendMessage(str
		// .substring(GlobalData.COMMAND_HEAD_SEND_MSG.length()));
		// }
		// // 如果是要请求获取本机的音乐列表
		// else if (str.startsWith(GlobalData.COMMAND_HEAD_REQUSET_MUSIC_LIST))
		// {
		// Log.d(TAG, "收到IP: " + targetIP + " 请求获取本机的音乐列表");
		//
		// Intent intent = new Intent();
		//
		// Bundle data = new Bundle();
		// data.putInt(GlobalData.GET_BUNDLE_COMMANT,
		// GlobalData.COMMAND_REQUSET_MUSIC_LIST);
		// data.putString(GlobalData.GET_BUNDLE_DATA, targetIP);
		//
		// intent.putExtras(data);
		// intent.setAction(GlobalData.RECV_LAN_SOCKET_MSG_ACTION);//
		// action与接收器相同
		//
		// mContext.sendBroadcast(intent);
		// }

		int command = data.getmCommant();

		switch (command) {
		case GlobalData.COMMAND_RECV_MSG:
			Log.d(TAG,"收到数据包：COMMAND_RECV_MSG");
			// 收到文本信息：
			Log.d(TAG,
					"收到文本类型消息："
							+ data.getmMessage().substring(GlobalData.COMMAND_HEAD_SEND_MSG
									.length()));

			// 截断消息位再发送
			SendMessage(data.getmMessage());
			
			
			break;

		case GlobalData.COMMAND_REQUSET_MUSIC_LIST:
			Log.d(TAG,"收到数据包：COMMAND_REQUSET_MUSIC_LIST");

			break;

		case GlobalData.COMMAND_SEND_MUSIC_LIST:
			Log.d(TAG,"收到数据包：COMMAND_SEND_MUSIC_LIST");

			break;

		}

	}

}
