package com.cvte.lanplayer.utils;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import android.util.Log;

import com.cvte.lanplayer.GlobalData;

/**
 * 接收其他设备的socket消息
 * 
 * @author JHYin
 * 
 */
public class SendSocketFileUtil {

	private final String TAG = "SendSocketMessageUtil";

	private static SendSocketFileUtil instance = null;

	/**
	 * 私有默认构造子
	 */
	private SendSocketFileUtil() {

	}

	/**
	 * 静态工厂方法
	 */
	public static synchronized SendSocketFileUtil getInstance() {

		if (instance == null) {
			instance = new SendSocketFileUtil();
		}

		return instance;
	}

	/**
	 * 文件传输                                                                                                                                                                                                                                                                                                                                                                                                             
	 * 
	 * @param fileName
	 * @param path
	 * @param ipAddress
	 */
	public void SendFile(final String fileName, final String path,
			final String ipAddress) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				Log.d(TAG, "start Thread to send");

				try {
					Socket name = new Socket(ipAddress,
							GlobalData.TranPort.SOCKET_FILE_TRANSMIT_PORT);
					OutputStream outputName = name.getOutputStream();
					OutputStreamWriter outputWriter = new OutputStreamWriter(
							outputName);
					BufferedWriter bwName = new BufferedWriter(outputWriter);
					bwName.write(fileName);
					bwName.close();
					outputWriter.close();
					outputName.close();
					name.close();
					// SendMessage(0, "正在发送" + fileName.get(i));
					Log.d(TAG, "正在发送" + fileName);

					Socket data = new Socket(ipAddress,
							GlobalData.TranPort.SOCKET_FILE_TRANSMIT_PORT);
					OutputStream outputData = data.getOutputStream();
					FileInputStream fileInput = new FileInputStream(path);
					int size = -1;
					byte[] buffer = new byte[1024];
					while ((size = fileInput.read(buffer, 0, 1024)) != -1) {
						outputData.write(buffer, 0, size);
					}
					outputData.close();
					fileInput.close();
					data.close();
					// SendMessage(0, fileName.get(i) + " 发送完成");
					Log.d(TAG, fileName + " 发送完成");

					// SendMessage(0, "所有文件发送完成");
					Log.d(TAG, "所有文件发送完成");
				} catch (Exception e) {
					// SendMessage(0, "发送错误:\n" + e.getMessage());
					Log.d(TAG, "发送错误:\n" + e.getMessage());
				}

			}
		}).start();

	}
}
