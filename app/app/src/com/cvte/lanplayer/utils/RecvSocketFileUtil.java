package com.cvte.lanplayer.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.cvte.lanplayer.GlobalData;
import com.cvte.lanplayer.service.RecvLanDataService;

/**
 * 接收其他设备传入的文件 （假如同时传入多个文件，还要开多个线程）
 * 
 * 
 * 
 * @author JHYin
 * 
 */
public class RecvSocketFileUtil {

	private final String TAG = "RecvSocketFileUtil";
	private static RecvSocketFileUtil instance = null;

	private Thread mRecvFileThread;
	ServerSocket mServerSocket = null;

	// 用于收到消息后回调
	private static RecvLanDataService mRkdService;

	/**
	 * 私有默认构造子
	 */
	private RecvSocketFileUtil() {

	}

	/**
	 * 静态工厂方法
	 */
	public static synchronized RecvSocketFileUtil getInstance(
			RecvLanDataService rkdService) {

		// mContext = context;
		mRkdService = rkdService;

		if (instance == null) {
			instance = new RecvSocketFileUtil();
		}

		return instance;
	}

	/**
	 * 启动文件接收
	 */
	public void StartRecv() {

		// 启动socket监听
		try {
			mServerSocket = new ServerSocket(
					GlobalData.TranPort.SOCKET_FILE_TRANSMIT_PORT);
		} catch (Exception e) {
		}

		// 轮询接收监听
		mRecvFileThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {// 接收文件
					ReceiveFile();
				}
			}
		});
		mRecvFileThread.start();
	}

	/**
	 * 停止文件接收
	 */
	public void StopRecv() {
		// 停止线程
		if (mRecvFileThread != null) {
			mRecvFileThread.interrupt();
		}

	}

	// 接收文件
	public void ReceiveFile() {
		try {
			// 接收文件名
			Socket name = mServerSocket.accept();
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
			ShowToastMsgUtil.getInstance(mRkdService).ShowToastMsg(
					"正在接收:" + fileName);

			// 接收文件数据
			Socket data = mServerSocket.accept();
			InputStream dataStream = data.getInputStream();

			// 判断是否有SD卡
			if (IsHaveSDcard()) {
				// 在SD卡创建文件夹
				CreateDir(GlobalData.Other.SAVE_LAN_FILE_DIR);

				String savePath = Environment.getExternalStorageDirectory()
						.getPath()
						+ '/'
						+ GlobalData.Other.SAVE_LAN_FILE_DIR
						+ "/" + fileName;

				FileOutputStream file = new FileOutputStream(savePath, false);
				byte[] buffer = new byte[1024];
				int size = -1;
				while ((size = dataStream.read(buffer)) != -1) {
					file.write(buffer, 0, size);
				}

				file.close();

			}

			dataStream.close();
			data.close();
			// SendMessage(0, fileName + " 接收完成");
			Log.d(TAG, fileName + " 接收完成");
			ShowToastMsgUtil.getInstance(mRkdService).ShowToastMsg(
					fileName + " 接收完成");

			// 回调recvService的方法
			mRkdService.RecvFileFromUtil(fileName);

			// String savePath = Environment.getExternalStorageDirectory()
			// .getPath() + "/" + fileName;

		} catch (Exception e) {
			Log.d(TAG, "接收错误:\n" + e.getMessage());
			ShowToastMsgUtil.getInstance(mRkdService).ShowToastMsg("接收错误");
		}
	}

	/**
	 * 判断SD卡是否插入
	 * 
	 * @return
	 */
	private boolean IsHaveSDcard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			Log.d(TAG, "存在SD卡");

			return true;
		} else {
			Log.d(TAG, "不存在SD卡");

			return false;
		}
	}

	/**
	 * 保存在SD卡中 如果不存在文件夹，则创建
	 */
	private void CreateDir(String dirName) {

		File destDir = new File(Environment.getExternalStorageDirectory()
				.getPath() + '/' + dirName);

		if (!destDir.exists()) {
			Log.d(TAG, "创建文件夹：" + dirName);
			destDir.mkdirs();
		}
	}

}
