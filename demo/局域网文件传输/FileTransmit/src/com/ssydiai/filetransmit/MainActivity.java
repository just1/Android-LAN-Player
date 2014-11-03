package com.ssydiai.filetransmit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private TextView tvMsg;
	private EditText txtIP, txtPort, txtEt;
	private Button btnSend;
	private Handler handler;
	private SocketManager socketManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tvMsg = (TextView)findViewById(R.id.tvMsg);
		txtIP = (EditText)findViewById(R.id.txtIP);
		txtPort = (EditText)findViewById(R.id.txtPort);
		txtEt = (EditText)findViewById(R.id.et);
		btnSend = (Button)findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), FilesViewActivity.class);
				startActivityForResult(intent, 0);
			}
		});
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what){
				case 0:
					SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
					txtEt.append("\n[" + format.format(new Date()) + "]" + msg.obj.toString());
					break;
				case 1:
					tvMsg.setText("本机IP：" + GetIpAddress() + " 监听端口:" + msg.obj.toString());
					break;
				case 2:
					Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
					break;
				}
			}
		};
		socketManager = new SocketManager(handler);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//选择了文件发送
		if (resultCode == RESULT_OK){
			final ArrayList<String> fileName = data.getStringArrayListExtra("fileName");
			final ArrayList<String> safeFileName = data.getStringArrayListExtra("safeFileName");
			final String ipAddress = txtIP.getText().toString();
			final int port = Integer.parseInt(txtPort.getText().toString());
			Message.obtain(handler, 0, "正在发送至" + ipAddress + ":" +  port).sendToTarget();
			Thread sendThread = new Thread(new Runnable(){
				@Override
				public void run() {
					socketManager.SendFile(fileName, safeFileName, ipAddress, port);
				}
			});
			sendThread.start();
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.exit(0);
	}
	public String GetIpAddress() {     
	    WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);     
	    WifiInfo wifiInfo = wifiManager.getConnectionInfo();     
	    int i = wifiInfo.getIpAddress();
	    return (i & 0xFF) + "." +     
	    	   ((i >> 8 ) & 0xFF) + "." +     
	    	   ((i >> 16 ) & 0xFF)+ "." +     
	           ((i >> 24 ) & 0xFF );     
	}    
}
