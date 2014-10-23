package com.example.sendudpbroadcasttest;

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

import com.example.sendudpbroadcasttest.MainActivity.MyReceiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class SendLanDataService extends Service {

	private final int STARE_SCAN = 1;

	private static String LOG_TAG = "SendLanDataService";
	private boolean start = true;
	private String address;
	public static final int DEFAULT_PORT = 43708;
	private static final int MAX_DATA_PACKET_LENGTH = 40;
	private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];

	private MyReceiver receiver;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// 注册接收器
		receiver = new MyReceiver();

		IntentFilter filter = new IntentFilter();

		filter.addAction("android.intent.action.recv_contrl");
		registerReceiver(receiver, filter);

		// 开启线程等待TCP消息到达
		new Thread(new TcpReceive()).start();
		address = getLocalIPAddress();

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
			Log.e(LOG_TAG, ex.toString());
		}
		return null;
	}

	public class BroadCastUdp extends Thread {
		private String dataString;
		private DatagramSocket udpSocket;

		public BroadCastUdp(String dataString) {
			this.dataString = dataString;
		}

		public void run() {
			DatagramPacket dataPacket = null;

			try {
				udpSocket = new DatagramSocket(DEFAULT_PORT);

				dataPacket = new DatagramPacket(buffer, MAX_DATA_PACKET_LENGTH);
				byte[] data = dataString.getBytes();
				dataPacket.setData(data);
				dataPacket.setLength(data.length);
				dataPacket.setPort(DEFAULT_PORT);

				InetAddress broadcastAddr;

				broadcastAddr = InetAddress.getByName("255.255.255.255");
				dataPacket.setAddress(broadcastAddr);
			} catch (Exception e) {
				Log.e(LOG_TAG, e.toString());
			}
			// while( start ){
			try {
				udpSocket.send(dataPacket);
				sleep(10);
			} catch (Exception e) {
				Log.e(LOG_TAG, e.toString());
			}
			// }

			udpSocket.close();
		}
	}

	private class TcpReceive implements Runnable {
		public void run() {
			while (true) {
				Socket socket = null;
				ServerSocket ss = null;
				BufferedReader in = null;
				try {
					Log.i("TcpReceive", "ServerSocket +++++++");
					ss = new ServerSocket(8080);

					socket = ss.accept();

					Log.i("TcpReceive", "connect +++++++");
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

						SendMessage("发现IP：" + ipString + "\n");
						// label.post(new Runnable() {
						//
						// @Override
						// public void run() {
						// label.append("发现IP：" + ipString + "\n");
						//
						// }
						// });
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

	// 启动广播
	private void StartScan() {
		start = true;

		// 启动广播
		new BroadCastUdp(getLocalIPAddress().toString()).start();

	}

	public class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			Bundle bundle = intent.getExtras();
			int control = bundle.getInt("int");

			switch (control) {
			case STARE_SCAN:// 继续播放
				StartScan();
				break;

			}
		}
	}

	private void SendMessage(String str) {
		Intent intent = new Intent();

		intent.putExtra("str", str);

		// i++;
		intent.setAction("android.intent.action.recvip");// action与接收器相同

		sendBroadcast(intent);
	}

}
