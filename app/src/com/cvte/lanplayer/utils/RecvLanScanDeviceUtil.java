package com.cvte.lanplayer.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

import com.cvte.lanplayer.GlobalData;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;


/**
 * 接收其他设备在局域网扫描时候的响应
 * @author JHYin
 *
 */
public class RecvLanScanDeviceUtil {

	private static Service mService;
	private static RecvLanScanDeviceUtil instance = null;
	

	Socket socket = null;
	static DatagramSocket udpSocket = null;
	static DatagramPacket udpPacket = null;

	Thread mRecvThread;
	
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
	
	private void SendMessage(String str) {
		Intent intent = new Intent();

		
		Bundle data = new Bundle();
		data.putString(GlobalData.RECV_SCAN, str);
		
		intent.putExtras(data);
		intent.setAction(GlobalData.IS_SCANED_ACTION);// action与接收器相同

		mService.sendBroadcast(intent);
	}
	
	/**
	 * 开始接收数据
	 */
	public void StartRecv(){
		mRecvThread = new Thread(new Runnable() {

			@Override
			public void run() {
				byte[] data = new byte[256];
				try {
					udpSocket = new DatagramSocket(GlobalData.UDP_PORT);
					udpPacket = new DatagramPacket(data, data.length);
				} catch (SocketException e1) {
					e1.printStackTrace();
				}
				while (true) {
					//不用连续扫描
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
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

							socket = new Socket(ip, GlobalData.SOCKET_PORT);
							/*
							 * 以后这里可以加入socket的秘钥
							 */
							
							
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
	
	
	/**
	 * 停止接收数据
	 * 打断线程并清理现场
	 * @throws IOException 
	 */
	public void StopRecv() throws IOException{
		if (mRecvThread != null) {
			mRecvThread.interrupt();
		}

		if (socket != null) {
			socket.close();
		}

		if (udpSocket != null) {
			udpSocket.close();
		}

	}
	
	

}
