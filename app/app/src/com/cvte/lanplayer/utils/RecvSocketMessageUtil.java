package com.cvte.lanplayer.utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.cvte.lanplayer.GlobalData;
import com.cvte.lanplayer.entity.SocketTranEntity;
import com.cvte.lanplayer.service.RecvLanDataService;

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

	private static RecvSocketMessageUtil instance = null;

	private static Thread mRecvThread = null;

	ServerSocket mServerSocket = null;
	List<Socket> mSocketList = new ArrayList<Socket>();

	// 用于收到消息后回调
	private static RecvLanDataService mRkdService;

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
	public static synchronized RecvSocketMessageUtil getInstance(
			RecvLanDataService rkdService) {

		// mContext = context;
		mRkdService = rkdService;

		if (instance == null) {
			instance = new RecvSocketMessageUtil();
		}

		return instance;
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
							GlobalData.TranPort.SOCKET_TRANSMIT_PORT);
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

						// 回调service处理收到的数据
						mRkdService.handleSocketCommand(songList, socket
								.getInetAddress().getHostAddress());

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

	

	

}
