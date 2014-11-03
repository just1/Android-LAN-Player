package com.cvte.lanplayer.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import android.content.Context;

import com.cvte.lanplayer.GlobalData;
import com.cvte.lanplayer.entity.SocketTranEntity;

/**
 * 接收其他设备的socket消息
 * 
 * @author JHYin
 * 
 */
public class SendSocketMessageUtil {

	private final String TAG = "SendSocketMessageUtil";

	private static Context mContext;
	private static SendSocketMessageUtil instance = null;

	private Thread mSendThread = null;
	private Socket mSocket = null;

	private String mTargetIP = null;
	private String mSendData = null;

	/**
	 * 私有默认构造子
	 */
	private SendSocketMessageUtil() {

	}

	/**
	 * 静态工厂方法
	 */
	public static synchronized SendSocketMessageUtil getInstance(Context context) {

		mContext = context;
		if (instance == null) {
			instance = new SendSocketMessageUtil();
		}

		return instance;
	}

	/**
	 * 向局域网指定IP设备发送消息
	 * 
	 * @param str
	 */
	public void SendMessage(final SocketTranEntity data, String targetIP) {

		mTargetIP = targetIP;

		mSendThread = new Thread() {
			@Override
			public void run() {
				super.run();

				synchronized (GlobalData.TCP_SOCKET_LOCK) {
					try {

						// 连接到服务器端
						mSocket = new Socket(mTargetIP,
								GlobalData.TranPort.SOCKET_TRANSMIT_PORT);
						// 使用ObjectOutputStream和ObjectInputStream进行对象数据传输
						ObjectOutputStream out = new ObjectOutputStream(
								mSocket.getOutputStream());
						ObjectInputStream in = new ObjectInputStream(
								mSocket.getInputStream());
						// 将客户端的对象数据流输出到服务器端去
						out.writeObject(data);
						out.flush();

						out.close();
						in.close();
						mSocket.close();

					} catch (IOException e) {
						e.printStackTrace();
					}

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
