package com.cvte.lanplayer.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import com.cvte.lanplayer.GlobalData;
import com.cvte.lanplayer.entity.SocketTranEntity;

/**
 * 接收其他设备在局域网扫描时候的响应
 * 
 * 采用标志位来控制线程的中断 不对socket进行管理，只在线程结束的时候进行一些清理操作
 * 
 * 
 * 
 * 
 * @author JHYin
 * 
 * 
 * 
 */
public class RecvLanScanDeviceUtil {

	private static String TAG = "RecvLanScanDeviceUtil";

	private static Service mService;
	private static RecvLanScanDeviceUtil instance = null;

	// private Socket socket = null;
	private DatagramSocket udpSocket = null;
	private DatagramPacket udpPacket = null;

	private Thread mRecvThread;

	// 控制线程的标志位
	private boolean isRunThread = false;

	/**
	 * 私有默认构造子
	 */
	private RecvLanScanDeviceUtil() {

	}

	/**
	 * 静态工厂方法
	 */
	public static synchronized RecvLanScanDeviceUtil getInstance(Service service) {

		mService = service;
		if (instance == null) {
			instance = new RecvLanScanDeviceUtil();

		}

		return instance;
	}

	// private void SendMessage(String str) {
	// Intent intent = new Intent();
	//
	// Bundle data = new Bundle();
	// data.putString(GlobalData.RECV_SCAN, str);
	//
	// intent.putExtras(data);
	// intent.setAction(GlobalData.IS_SCANED_ACTION);// action与接收器相同
	//
	// mService.sendBroadcast(intent);
	// }

	/**
	 * 开始接收数据
	 */
	public void StartRecv() {

		// 如果接收线程没有初始化，则进行初始化
		if (mRecvThread == null) {
			InitRecvThread();

			Log.d(TAG, "InitRecvThread");
		}

		// 如果线程已经结束了，则重新开启线程
		if (mRecvThread.getState() == Thread.State.NEW) {

			isRunThread = true;
			mRecvThread.start();

			Log.d(TAG, "StartRecv");
		}

	}

	/**
	 * 初始化接收线程
	 */
	private void InitRecvThread() {
		mRecvThread = new Thread(new Runnable() {

			@Override
			public void run() {

				/*
				 * 使用同步锁进行线程同步
				 * 
				 * 无须使用wait()和nodify()
				 * 因为在进入synchronized()的代码块的时候，就代表请求锁，然后假如没有锁，
				 * 那么就要进行等待，而不用wait();
				 * 当synchronized()的代码块执行完毕，就会自动释放锁，而不用nodify()通知
				 */
				synchronized (GlobalData.UDP_SOCKET_LOCK) {

					byte[] data = new byte[256];
					try {
						udpSocket = new DatagramSocket(
								GlobalData.TranPort.UDP_PORT);
						udpPacket = new DatagramPacket(data, data.length);
					} catch (SocketException e1) {
						e1.printStackTrace();
					}
					while (isRunThread) { // 控制线程的标志位

						try {
							udpSocket.receive(udpPacket);
						} catch (Exception e) {
						}
						if (null != udpPacket.getAddress()) {
							final String quest_ip = udpPacket.getAddress()
									.toString();
							final String codeString = new String(data, 0,
									udpPacket.getLength());

							Log.d(TAG, "收到IP地址：" + quest_ip + "的UDP请求\n"
									+ "地址代码：" + codeString);

							// SendMessage("收到IP地址：" + quest_ip + "的UDP请求\n"
							// + "地址代码：" + codeString + "\n\n");

							final String ip = udpPacket.getAddress().toString()
									.substring(1);

							// SendMessage("建立socket通信：" + ip + "\n");
							// Log.d(TAG, "建立socket通信：" + ip + "\n");

							// 发送通信密钥
							// socket = new Socket(ip,
							// GlobalData.TranPort.SOCKET_TRANSMIT_PORT);

							// 实例化传输对象
							SocketTranEntity msg = new SocketTranEntity();
							msg.setmCommant(GlobalData.SocketTranCommand.COMMAND_LAN_ASK);
							// 设置发送方IP地址
							msg.setmSendIP(getIpAddress());
							// 设置接收方IP
							msg.setmRecvIP(ip);

							// 加入通信密钥
							// msg.setmMessage(et_context.getText().toString());

							// 直接发送消息
							SendSocketMessageUtil.getInstance(mService)
									.SendMessage(msg, ip);

							Log.d(TAG, "向IP：" + ip + "发送确认数据包");
						}
					}

				}

			}
		});
	}

	/**
	 * 停止接收数据 打断线程并清理现场
	 * 
	 * @throws IOException
	 */
	public void StopRecv() throws IOException {

		Log.d(TAG, "StopRecv");

		/*
		 * 两种控制线程终止的方法都能用 1.使用interrupt 2.使用标志位，在线程的while循环里面判断该标志位。通过更改标志位让线程停掉
		 * 
		 * 最后采用线程里面加标志位的方法，因为这样能完成最后的socket关闭等处理
		 */

		/* 方法1 */
		// if (mRecvThread != null) {
		// mRecvThread.interrupt();
		//
		// mRecvThread = null;
		// }

		/* 方法2 */

		// 控制标志位来停止线程的循环
		isRunThread = false;
		// 这样直接赋值为null不知道好不好
		mRecvThread = null;

		// if (socket != null) {
		// socket.close();
		// }

		if (udpSocket != null) {
			udpSocket.close();

			// 这样直接赋值为null不知道好不好
			udpSocket = null;
		}

	}

	/**
	 * 发送LAN扫描确认包
	 */
	private void SendLanAskPackbag(String targetIP) {

	}

	/**
	 * 获取本机的IP地址
	 */
	private String getIpAddress() {
		WifiManager wifiManager = (WifiManager) mService
				.getSystemService(mService.WIFI_SERVICE);

		WifiInfo wifiInfo = wifiManager.getConnectionInfo();

		int ipAddress = wifiInfo.getIpAddress();
		// Log.d("TAG","IP:"+ String.valueOf(ipAddress));

		return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
				+ (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));

	}

}
