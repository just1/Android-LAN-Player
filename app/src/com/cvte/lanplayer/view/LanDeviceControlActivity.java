package com.cvte.lanplayer.view;

import com.cvte.lanplayer.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class LanDeviceControlActivity extends Activity {
	private TextView tv_target_ip;
	private String targetIp = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Òþ²ØTitle
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
		tv_target_ip.setText("Ä¿±êIP£º"+targetIp);
	}
	
	private void InitData(){
		Intent intent = getIntent();
		targetIp = (String) intent.getSerializableExtra("ip");
	}
}
