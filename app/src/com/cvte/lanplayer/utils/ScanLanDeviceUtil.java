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

	//ɨ��ͽ��շ���ָ����߳�
	private Thread mTCPThread;
	private BroadCastUdp mBroadCastUdp;

	private DatagramSocket udpSocket;
	private Socket socket = null;
	private ServerSocket ss = null;

	private boolean start = true;
	
	private static final int MAX_DATA_PACKET_LENGTH = 40;
	private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];

	/**
	 * ˽��Ĭ�Ϲ�����
	 */
	private ScanLanDeviceUtil() {

	}

	/**
	 * ��̬��������
	 */
	public static synchronized ScanLanDeviceUtil getInstance(Service service) {

		mService = service;
		if (instance == null) {
			instance = new ScanLanDeviceUtil();
		}

		return instance;
	}

	/**
	 * ֹͣɨ�� ��տ������̺߳�socket
	 */
	public void StopScan() {
		if (mTCPThread != null) {
			mTCPThread.interrupt();
		}
		if (mBroadCastUdp != null) {
			mBroadCastUdp.interrupt();
		}

		if (udpSocket != null) {
			udpSocket.close();
		}

		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (ss != null) {
			try {
				ss.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
	}

	/**
	 * ��ʼɨ��
	 * 
	 * ��ʼɨ��֮�󣬽��շ�Ҫ����һ��GlobalData.GET_SCAN_DATA_ACTION
	 * �Ĺ㲥���������ܽ�����������IP��ַ
	 */
	public void StartScan() {
		start = true;

		// �����̵߳ȴ�TCP��Ϣ����
		mTCPThread = new Thread(new TcpReceive());
		mTCPThread.start();

		// �����㲥
		mBroadCastUdp = new BroadCastUdp(getLocalIPAddress().toString());
		mBroadCastUdp.start();

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
	 * �ҵ���Ӧ��IP��ַ������й㲥
	 * @param str
	 */
	private void SendMessage(String str) {
		Intent intent = new Intent();
		intent.putExtra("str", str);
		intent.setAction(GlobalData.GET_SCAN_DATA_ACTION);// action���������ͬ

		mService.sendBroadcast(intent);
	}

	public class BroadCastUdp extends Thread {
		private String dataString;

		public BroadCastUdp(String dataString) {
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

			try {
				udpSocket.send(dataPacket);
				sleep(10);
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			} finally {
				if (udpSocket != null) {
					udpSocket.close();
				}

			}

		}
	}

	private class TcpReceive implements Runnable {
		public void run() {
			while (true) {

				BufferedReader in = null;
				try {
					ss = new ServerSocket(GlobalData.Socket_PORT);

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