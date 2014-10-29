package com.cvte.lanplayer.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.Service;
import android.content.Intent;
import android.util.Log;

import com.cvte.lanplayer.GlobalData;

public class ScanLanDeviceUtil {

	private static String TAG = "ScanLanDeviceUtil";
	private static Service mService;
	private static ScanLanDeviceUtil instance = null;

	// 扫描和接收返回指令的线程
	private static Thread mTCPThread;
	private static BroadCastUdpThread mBroadCastUdpThread;

	private DatagramSocket udpSocket;
	private Socket socket = null;
	private ServerSocket ss = null;

	private boolean start = true;

	private static final int MAX_DATA_PACKET_LENGTH = 40;
	private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];

	/**
	 * 私有默认构造子
	 */
	private ScanLanDeviceUtil() {

		// 如果接收线程没有初始化，则进行初始化
		if (mTCPThread == null) {

			Log.d(TAG, "new mTCPThread");
			mTCPThread = new Thread(new TcpReceive());
		}

		if (mBroadCastUdpThread == null) {

			Log.d(TAG, "new mBroadCastUdpThread");
			mBroadCastUdpThread = new BroadCastUdpThread(getLocalIPAddress()
					.toString());
		}

	}

	/**
	 * 静态工厂方法
	 */
	public static synchronized ScanLanDeviceUtil getInstance(Service service) {

		mService = service;
		if (instance == null) {
			instance = new ScanLanDeviceUtil();
		}

		return instance;
	}

	/**
	 * 停止扫描 清空开启的线程和socket
	 * 
	 * @throws IOException
	 */
	public void StopScan() throws IOException {
		if (mTCPThread != null) {
			mTCPThread.interrupt();

			Log.d(TAG, "interrupt mTCPThread");
		}
		if (mBroadCastUdpThread != null) {
			mBroadCastUdpThread.interrupt();

			Log.d(TAG, "interrupt mBroadCastUdpThread");
		}

		if (udpSocket != null) {
			udpSocket.close();
		}

		if (socket != null) {
			socket.close();
		}

		if (ss != null) {
			ss.close();
		}

	}

	/**
	 * 开始扫描
	 * 
	 * 开始扫描之后，接收方要启动一个GlobalData.GET_SCAN_DATA_ACTION 的广播接收器才能接收搜索到的IP地址
	 */
	public void StartScan() {
		start = true;

		// 开启线程等待TCP消息到达
		if ((mTCPThread.getState() == Thread.State.TERMINATED)
				|| (mTCPThread.getState() == Thread.State.NEW)) {
			mTCPThread.start();

			Log.d(TAG, "start mTCPThread");
		}

		// 启动UDP广播
		if ((mBroadCastUdpThread.getState() == Thread.State.TERMINATED)
				|| (mBroadCastUdpThread.getState() == Thread.State.NEW)) {
			mBroadCastUdpThread.start();

			Log.d(TAG, "start mBroadCastUdpThread");
		}

	}

	private String getLocalIPAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e(TAG, ex.toString());
		}
		return null;
	}

	/**
	 * 找到相应的IP地址，则进行广播
	 * 
	 * @param str
	 */
	private void SendMessage(String str) {
		Intent intent = new Intent();
		intent.putExtra("str", str);
		intent.setAction(GlobalData.GET_SCAN_DATA_ACTION);// action与接收器相同

		mService.sendBroadcast(intent);
	}

	/**
	 * 
	 * UDP扫描进程
	 * 
	 * @author JHYin
	 * 
	 */
	public class BroadCastUdpThread extends Thread {
		private String dataString;
		private int BroadCastUdpCount = 10; // 用来记录发起的广播次数

		public BroadCastUdpThread(String dataString) {
			this.dataString = dataString;
		}

		public void run() {
			DatagramPacket dataPacket = null;

			try {
				udpSocket = new DatagramSocket(GlobalData.UDP_PORT);

				dataPacket = new DatagramPacket(buffer, MAX_DATA_PACKET_LENGTH);
				byte[] data = dataString.getBytes();
				dataPacket.setData(data);
				dataPacket.setLength(data.length);
				dataPacket.setPort(GlobalData.UDP_PORT);

				InetAddress broadcastAddr;

				broadcastAddr = InetAddress.getByName("255.255.255.255");
				dataPacket.setAddress(broadcastAddr);
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}

			// 发起多次广播
			for (int i = 0; i < BroadCastUdpCount; i++) {

				try {
					udpSocket.send(dataPacket);

					Log.d(TAG, "send udpSocket in BroadCastUdpThread");

					sleep(10);
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				} finally {
					if (udpSocket != null) {
						udpSocket.close();
					}

				}

				// 休眠100s
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	private class TcpReceive implements Runnable {
		public void run() {
			while (true) {

				BufferedReader in = null;
				try {
					ss = new ServerSocket(GlobalData.SOCKET_PORT);

					socket = ss.accept();

					if (socket != null) {

						in = new BufferedReader(new InputStreamReader(
								socket.getInputStream()));

						StringBuilder sb = new StringBuilder();
						sb.append(socket.getInetAddress().getHostAddress());
						String line = null;
						while ((line = in.readLine()) != null) {
							sb.append(line);
						}
						Log.i("TcpReceive", "connect :" + sb.toString());

						final String ipString = sb.toString().trim();// "192.168.0.104:8731";

						SendMessage(ipString);

					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (in != null)
							in.close();
						if (socket != null)
							socket.close();
						if (ss != null)
							ss.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
