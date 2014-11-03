package com.cvte.lanplayer.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cvte.lanplayer.GlobalData;

public class SocketManager {

	private final String TAG = "SocketManager";

	private static SocketManager instance = null;

	private ServerSocket server;
	private Handler handler = null;

	/**
	 * 私有默认构造子
	 */
	private SocketManager() {

	}

	/**
	 * 静态工厂方法
	 */
	public static synchronized SocketManager getInstance() {

		if (instance == null) {
			instance = new SocketManager();
		}

		return instance;
	}

	void SendMessage(int what, Object obj) {
		if (handler != null) {
			Message.obtain(handler, what, obj).sendToTarget();
		}
	}

	public void StartRecv() {

		// 启动socket监听
		try {
			server = new ServerSocket(
					GlobalData.TranPort.SOCKET_FILE_TRANSMIT_PORT);
		} catch (Exception e) {
		}

		// 轮询接收监听
		Thread receiveFileThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {// 接收文件
					ReceiveFile();
				}
			}
		});
		receiveFileThread.start();
	}

	// 接收文件
	public void ReceiveFile() {
		try {
			// 接收文件名
			Socket name = server.accept();
			InputStream nameStream = name.getInputStream();
			InputStreamReader streamReader = new InputStreamReader(nameStream);
			BufferedReader br = new BufferedReader(streamReader);
			String fileName = br.readLine();
			br.close();
			streamReader.close();
			nameStream.close();
			name.close();
			// SendMessage(0, "正在接收:" + fileName);
			Log.d(TAG, "正在接收:" + fileName);

			// 接收文件数据
			Socket data = server.accept();
			InputStream dataStream = data.getInputStream();
			String savePath = Environment.getExternalStorageDirectory()
					.getPath() + "/" + fileName;
			FileOutputStream file = new FileOutputStream(savePath, false);
			byte[] buffer = new byte[1024];
			int size = -1;
			while ((size = dataStream.read(buffer)) != -1) {
				file.write(buffer, 0, size);
			}
			file.close();
			dataStream.close();
			data.close();
			// SendMessage(0, fileName + " 接收完成");
			Log.d(TAG, fileName + " 接收完成");
		} catch (Exception e) {
			// SendMessage(0, "接收错误:\n" + e.getMessage());
			Log.d(TAG, "接收错误:\n" + e.getMessage());
		}
	}

	public void SendFile(ArrayList<String> fileName, ArrayList<String> path,
			String ipAddress, int port) {
		try {
			for (int i = 0; i < fileName.size(); i++) {
				Socket name = new Socket(ipAddress, port);
				OutputStream outputName = name.getOutputStream();
				OutputStreamWriter outputWriter = new OutputStreamWriter(
						outputName);
				BufferedWriter bwName = new BufferedWriter(outputWriter);
				bwName.write(fileName.get(i));
				bwName.close();
				outputWriter.close();
				outputName.close();
				name.close();
				// SendMessage(0, "正在发送" + fileName.get(i));
				Log.d(TAG, "正在发送" + fileName.get(i));

				Socket data = new Socket(ipAddress, port);
				OutputStream outputData = data.getOutputStream();
				FileInputStream fileInput = new FileInputStream(path.get(i));
				int size = -1;
				byte[] buffer = new byte[1024];
				while ((size = fileInput.read(buffer, 0, 1024)) != -1) {
					outputData.write(buffer, 0, size);
				}
				outputData.close();
				fileInput.close();
				data.close();
				// SendMessage(0, fileName.get(i) + " 发送完成");
				Log.d(TAG, fileName.get(i) + " 发送完成");
			}
			// SendMessage(0, "所有文件发送完成");
			Log.d(TAG, "所有文件发送完成");
		} catch (Exception e) {
			// SendMessage(0, "发送错误:\n" + e.getMessage());
			Log.d(TAG, "发送错误:\n" + e.getMessage());
		}
	}
}
