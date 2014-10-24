package com.cvte.lanplayer.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.cvte.lanplayer.GlobalData;
import com.cvte.lanplayer.R;
import com.cvte.lanplayer.utils.SendSocketMessageUtil;
import com.cvte.lanplayer.view.ScanLanDeviceFragment.ScanDataReceiver;

public class LanDeviceControlActivity extends Activity {

	private String targetIp = null;

	private TextView tv_target_ip;
	private TextView tv_recv_msg;
	private Button btn_send_msg;
	
	private RecvScoketMsgReceiver mRecvScoketMsgReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 隐藏Title
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_landevice_ctrl);

		InitData();
		InitView();

	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		// TODO Auto-generated method stub
		return super.onCreateView(name, context, attrs);
	}

	private void InitView() {

		tv_target_ip = (TextView) findViewById(R.id.tv_target_ip);
		tv_recv_msg = (TextView) findViewById(R.id.tv_recv_msg);
		btn_send_msg = (Button) findViewById(R.id.btn_send_msg);

		tv_target_ip.setText("目标IP：" + targetIp);

		btn_send_msg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				// 直接发送消息
				SendSocketMessageUtil
						.getInstance(LanDeviceControlActivity.this)
						.SendMessage("welcome to cvte ", targetIp);
			}
		});
	}

	private void InitData() {
		Intent intent = getIntent();
		targetIp = (String) intent.getSerializableExtra("ip");

		// 注册接收器
		mRecvScoketMsgReceiver = new RecvScoketMsgReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalData.RECV_LAN_SOCKET_MSG_ACTION);
		this.registerReceiver(mRecvScoketMsgReceiver, filter);
	}

	// 获取扫描出来的IP地址的接收器
	public class RecvScoketMsgReceiver extends BroadcastReceiver {

		// 自定义一个广播接收器
		@Override
		public void onReceive(Context context, Intent intent) {

			Bundle bundle = intent.getExtras();
			String str = bundle.getString("str");

			// 把收到的数据显示出来
			tv_recv_msg.setText(str);

		}
	}
}
