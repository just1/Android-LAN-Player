package com.example.serverudp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
	TextView label;
	// Socket socket = null;
	// static DatagramSocket udpSocket = null;
	// static DatagramPacket udpPacket = null;

	private MyReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		label = (TextView) findViewById(R.id.label);
		label.append("\n");

		// 开始服务
		startService(new Intent(MainActivity.this, RecvLanDataService.class));

		// 注册接收器
		receiver = new MyReceiver();

		IntentFilter filter = new IntentFilter();

		filter.addAction("android.intent.action.test");
		MainActivity.this.registerReceiver(receiver, filter);
	}

	@Override
	public void onBackPressed() {
		// udpSocket.close();
		super.onBackPressed();
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
