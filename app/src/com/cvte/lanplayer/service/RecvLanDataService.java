package com.cvte.lanplayer.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RecvLanDataService extends Service {

	Socket socket = null;
	static DatagramSocket udpSocket = null;
	static DatagramPacket udpPacket = null;

	Thread mRecvThread;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mRecvThread = new Thread(new Runnable() {

			@Override
			public void run() {
				byte[] data = new byte[256];
				try {
					udpSocket = new DatagramSocket(43708);
					udpPacket = new DatagramPacket(data, data.length);
				} catch (SocketException e1) {
					e1.printStackTrace();
				}
				while (true) {
					//不用连续扫描
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						udpSocket.receive(udpPacket);
					} catch (Exception e) {
					}
					if (null != udpPacket.getAddress()) {
						final String quest_ip = udpPacket.getAddress()
								.toString();
						final String codeString = new String(data, 0,
								udpPacket.getLength());

						SendMessage("收到IP地址：" + quest_ip + "的UDP请求\n" + "地址代码："
								+ codeString + "\n\n");

						try {
							final String ip = udpPacket.getAddress().toString()
									.substring(1);

							SendMessage("建立socket通信：" + ip + "\n");

							socket = new Socket(ip, 8080);

						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							try {
								if (null != socket) {
									socket.close();
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}

			}
		});

		mRecvThread.start();

	}

	private void SendMessage(String str) {
		Intent intent = new Intent();

		intent.putExtra("str", str);

		// i++;
		intent.setAction("android.intent.action.test");// action与接收器相同

		sendBroadcast(intent);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		if (mRecvThread != null) {
			mRecvThread.interrupt();
		}

		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (udpSocket != null) {
			udpSocket.close();
		}

	}

}
