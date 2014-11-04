package com.cvte.lanplayer.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cvte.lanplayer.GlobalData;

/**
 * 发起局域网UDP扫描
 * 
 * 
 * @author JHYin
 * 
 */
public class ScanLanDeviceUtil {

	private static String TAG = "ScanLanDeviceUtil";
	private static Context mContext;
	private static ScanLanDeviceUtil instance = null;


	private DatagramSocket udpSocket;

	private static final int MAX_DATA_PACKET_LENGTH = 40;
	private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];

	/**
	 * 私有默认构造子
	 */
	private ScanLanDeviceUtil() {
	}

	/**
	 * 静态工厂方法
	 */
	public static synchronized ScanLanDeviceUtil getInstance(Context context) {

		mContext = context;
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

		if (udpSocket != null) {
			udpSocket.close();
		}

	}

	/**
	 * 开始扫描
	 * 
	 * 开始扫描之后，接收方要启动一个GlobalData.GET_SCAN_DATA_ACTION 的广播接收器才能接收搜索到的IP地址
	 */
	public void StartScan() {

		Log.d(TAG, "StartScan");

		// 每按下一次，就开启一个UDP线程进行扫描
		new BroadCastUdpThread(getLocalIPAddress().toString()).start();

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

			/*
			 * 关闭接收端口扫描 必须放在synchronized()同步代码块之前，否则无法获得锁
			 */
			StopLANRecv();

			/*
			 * 使用同步锁进行线程同步
			 * 
			 * 无须使用wait()和nodify() 因为在进入synchronized()的代码块的时候，就代表请求锁，然后假如没有锁，
			 * 那么就要进行等待，而不用wait(); 当synchronized()的代码块执行完毕，就会自动释放锁，而不用nodify()通知
			 */
			synchronized (GlobalData.UDP_SOCKET_LOCK) {
				DatagramPacket dataPacket = null;
				try {
					udpSocket = new DatagramSocket(GlobalData.TranPort.UDP_PORT);

					dataPacket = new DatagramPacket(buffer,
							MAX_DATA_PACKET_LENGTH);
					byte[] data = dataString.getBytes();
					dataPacket.setData(data);
					dataPacket.setLength(data.length);
					dataPacket.setPort(GlobalData.TranPort.UDP_PORT);

					InetAddress broadcastAddr;

					broadcastAddr = InetAddress.getByName("255.255.255.255");
					dataPacket.setAddress(broadcastAddr);
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}

				try {
					// 发起多次广播 -- 或者不用发出多次广播
					for (int i = 0; i < BroadCastUdpCount; i++) {
						udpSocket.send(dataPacket);

						Log.d(TAG, "send udpSocket in BroadCastUdpThread");

						// 休眠100ms
						sleep(100);
					}
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				} finally {
					if (udpSocket != null) {
						udpSocket.close();
					}

				}

				// 重新启动接收端口
				StartLANRecv();
			}
		}
	}

	/**
	 * 启动扫描
	 */
	private void StartLANRecv() {
		Intent intent = new Intent();

		Bundle data = new Bundle();
		data.putInt(GlobalData.SocketTranCommand.GET_BUNDLE_COMMANT,
				GlobalData.LANScanCtrl.STARE_LAN_RECV);

		intent.putExtras(data);
		intent.setAction(GlobalData.LANScanCtrl.CTRL_LAN_RECV_ACTION);// action与接收器相同

		mContext.sendBroadcast(intent);
	}

	/**
	 * 停止扫描
	 */
	private void StopLANRecv() {
		Intent intent = new Intent();

		Bundle data = new Bundle();
		data.putInt(GlobalData.SocketTranCommand.GET_BUNDLE_COMMANT,
				GlobalData.LANScanCtrl.STOP_LAN_RECV);

		intent.putExtras(data);
		intent.setAction(GlobalData.LANScanCtrl.CTRL_LAN_RECV_ACTION);// action与接收器相同

		mContext.sendBroadcast(intent);
	}

}
