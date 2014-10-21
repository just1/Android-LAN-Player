package com.yin.lanfilesendorrev;

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

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	private EditText et_ip; // 输入IP地址
	private TextView tv_progress; // 显示进度
	private Button btn_send; // 发送按钮

	private ServerSocket server;
	int port = 9503;
	String ip = "172.18.54.68";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 实例化控件
		et_ip = (EditText) findViewById(R.id.et_ip);
		tv_progress = (TextView) findViewById(R.id.tv_progress);
		btn_send = (Button) findViewById(R.id.btn_send);

		// 初始化接收相关类ServerSocket
		try {
			server = new ServerSocket(port);
		} catch (Exception e) {

		}

		// 开辟线程，等待接收文件
		new Thread() {
			public void run() {
				while (true) { // 记得加上死循环
					RecvFile();
				}
			};
		}.start();

		
		//设置按钮监听
		btn_send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//String ipAddress = null;
				//ipAddress = et_ip.getText().toString();
				//System.out.println("IP:"+ipAddress);
				
				
				SendFile(ip,port);
			}
		});
	}

	/**
	 * 发送文件
	 */
	private void SendFile(String ipAddress, int port) {
		try {
			Socket name = new Socket(ipAddress, port);
			OutputStream outputName = name.getOutputStream();
			OutputStreamWriter outputWriter = new OutputStreamWriter(outputName);
			BufferedWriter bwName = new BufferedWriter(outputWriter);
			bwName.write("yuenanyueai.mp3");
			bwName.close();
			outputWriter.close();
			outputName.close();
			name.close();
			SetStatus("正在发送" + "yuenanyueai.mp3");

			
		
			
			Socket data = new Socket(ipAddress, port);
			OutputStream outputData = data.getOutputStream();
			//FileInputStream fileInput = new FileInputStream(
			//		"file:///android_asset/yuenanyueai.mp3");
			InputStream fileInput = getAssets().open("yuenanyueai.mp3");
			
			int size = -1;
			byte[] buffer = new byte[1024];
			while ((size = fileInput.read(buffer, 0, 1024)) != -1) {
				outputData.write(buffer, 0, size);
			}
			outputData.close();
			fileInput.close();
			data.close();
			SetStatus("yuenanyueai.mp3" + " 发送完成");

			SetStatus("所有文件发送完成");
		} catch (Exception e) {
			SetStatus("发送错误:\n" + e.getMessage());
		}
	}

	/**
	 * 接收文件
	 */
	private void RecvFile() {
		// 记得加入写入SD卡的权限

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
			SetStatus("正在接收:" + fileName);
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
			SetStatus(fileName + " 接收完成");
		} catch (Exception e) {
			SetStatus("接收错误:\n" + e.getMessage());
		}
	}

	// 更改状态栏显示的状态
	private void SetStatus(String str) {
		tv_progress.setText(str);
	}
}
