package com.cvte.lanplayer.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cvte.lanplayer.GlobalData;

/**
 * 接收其他设备的socket消息
 * 
 * @author JHYin
 * 
 */
public class RecvSocketMessageUtil {

	private final String TAG = "RecvSocketMessageUtil";

	private static Context mContext;
	private static RecvSocketMessageUtil instance = null;

	private Thread mRecvThread = null;

	ServerSocket mServerSocket = null;
	List<Socket> mSocketList = new ArrayList<Socket>();

	/**
	 * 私有默认构造子
	 */
	private RecvSocketMessageUtil() {

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
		intent.putExtra("str", str);
		intent.setAction(GlobalData.RECV_LAN_SOCKET_MSG_ACTION);// action与接收器相同

		mContext.sendBroadcast(intent);
	}

	public void StartRecv() {

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
						
						//加入队列之中，方便日后清理
						mSocketList.add(socket);
						InputStream nameStream = socket.getInputStream();
						InputStreamReader streamReader = new InputStreamReader(
								nameStream);
						BufferedReader br = new BufferedReader(streamReader);
						String recv_data = br.readLine();

						Log.d(TAG, "来自服务器的数据：" + recv_data);
						SendMessage(recv_data);
											
						br.close();
						streamReader.close();
						nameStream.close();
												
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		mRecvThread.start();

	}

	public void StopRecv() throws IOException {
		if (mRecvThread != null) {
			mRecvThread.interrupt();
		}

		if (mServerSocket != null) {
			mServerSocket.close();
		}

		
		//清理所有有可能开过的线程
		for(int i=0;i<mSocketList.size();i++){
			if(mSocketList.get(i) != null){
				mSocketList.get(i).close();
			}
		}
	}
}
