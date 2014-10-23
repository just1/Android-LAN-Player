package com.example.sendudpbroadcasttest;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static String LOG_TAG = "WifiBroadcastActivity";
	private boolean start = true;
	private EditText IPAddress;
	private String address;
	public static final int DEFAULT_PORT = 43708;
	private static final int MAX_DATA_PACKET_LENGTH = 40;
	private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];
	Button startButton;
	Button stopButton;
	TextView label;
	
	private final int STARE_SCAN = 1;
	
	
	private MyReceiver receiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		IPAddress = (EditText) this.findViewById(R.id.address);
		startButton = (Button) this.findViewById(R.id.start);
		stopButton = (Button) this.findViewById(R.id.stop);
		label = (TextView) this.findViewById(R.id.label);
		startButton.setEnabled(true);
		stopButton.setEnabled(false);

		address = getLocalIPAddress();
		if (address != null) {
			IPAddress.setText(address);
		} else {
			IPAddress.setText("Can not get IP address");

			return;
		}
		
		startButton.setOnClickListener(listener);
		stopButton.setOnClickListener(listener);
		
		
		// 开始服务
		startService(new Intent(MainActivity.this, SendLanDataService.class));

		// 注册接收器
		receiver = new MyReceiver();

		IntentFilter filter = new IntentFilter();

		filter.addAction("android.intent.action.recvip");
		MainActivity.this.registerReceiver(receiver, filter);
		
	}

	private View.OnClickListener listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			label.setText("");
			if (v == startButton) {
				//启动扫描
				
				Intent intent = new Intent();
		        intent.putExtra("int", STARE_SCAN);
		        
		        intent.setAction("android.intent.action.recv_contrl");// action与接收器相同
		        sendBroadcast(intent);
				
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
			} else if (v == stopButton) {
				start = false;
				startButton.setEnabled(true);
				stopButton.setEnabled(false);
			}
		}
	};

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
	
	public class MyReceiver extends BroadcastReceiver {

		// 自定义一个广播接收器

		@Override
		public void onReceive(Context context, Intent intent) {

			// TODO Auto-generated method stub

			System.out.println("OnReceiver");

			Bundle bundle = intent.getExtras();

			String str = bundle.getString("str");

			// progressBar.setProgress(a);

			label.setText(String.valueOf(str));

			// 处理接收到的内容

		}

		public MyReceiver() {

			System.out.println("MyReceiver");

			// 构造函数，做一些初始化工作，本例中无任何作用

		}

	}

}
