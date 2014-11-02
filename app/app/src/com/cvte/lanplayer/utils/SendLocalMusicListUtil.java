package com.cvte.lanplayer.utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import android.content.Context;
import android.util.Log;

import com.cvte.lanplayer.GlobalData;
import com.cvte.lanplayer.entity.SocketTranEntity;

/**
 * 发送本机的音乐列表
 * 
 * @author JHYin
 * 
 */
public class SendLocalMusicListUtil {

	private final String TAG = "SendLocalMusicListUtil";

	private static Context mContext;
	private static SendLocalMusicListUtil instance = null;

	private Thread mSendThread = null;
	private Socket mSocket = null;

	private String mTargetIP = null;

	/**
	 * 私有默认构造子
	 */
	private SendLocalMusicListUtil() {

	}

	/**
	 * 静态工厂方法
	 */
	public static synchronized SendLocalMusicListUtil getInstance(
			Context context) {

		mContext = context;
		if (instance == null) {
			instance = new SendLocalMusicListUtil();
		}

		return instance;
	}

	/**
	 * 向局域网指定IP设备发送消息
	 * 
	 * musicList:要发送的音乐列表 targetIP：目标IP
	 */
	public void SendMusicList(final SocketTranEntity musicList, String targetIP) {

		mTargetIP = targetIP;

		mSendThread = new Thread() {
			@Override
			public void run() {
				super.run();
				// 关闭输入流、socket
				try {

					Log.d(TAG, "音乐列表正在发送");

					// 连接到服务器端
					mSocket = new Socket(mTargetIP,
							GlobalData.SOCKET_TRANSMIT_PORT);
					// 使用ObjectOutputStream和ObjectInputStream进行对象数据传输
					ObjectOutputStream out = new ObjectOutputStream(
							mSocket.getOutputStream());
					ObjectInputStream in = new ObjectInputStream(
							mSocket.getInputStream());
					// 将客户端的对象数据流输出到服务器端去
					out.writeObject(musicList);
					out.flush();

					out.close();
					in.close();
					mSocket.close();

					Log.d(TAG, "音乐列表发送完成");

				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		};

		mSendThread.start();

	}

	public void StopSendMsg() throws IOException {
		if (mSendThread != null) {
			mSendThread.interrupt();
		}

		if (mSocket != null) {
			mSocket.close();
		}
	}
}
