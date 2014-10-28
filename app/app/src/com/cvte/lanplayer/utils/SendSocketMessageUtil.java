package com.cvte.lanplayer.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import android.content.Context;
import android.util.Log;

import com.cvte.lanplayer.GlobalData;

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
	public void SendMessage(final String str, String targetIP) {

		mTargetIP = targetIP;
		mSendData = str;

		mSendThread = new Thread() {
			@Override
			public void run() {
				super.run();
				// 关闭输入流、socket
				try {
					
					mSocket = new Socket(mTargetIP, GlobalData.SOCKET_TRANSMIT_PORT);
					OutputStream outputName = mSocket.getOutputStream();
					OutputStreamWriter outputWriter = new OutputStreamWriter(outputName);
					BufferedWriter bwName = new BufferedWriter(outputWriter);
					bwName.write(str);
					bwName.close();
					outputWriter.close();
					outputName.close();
					mSocket.close();
					
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
