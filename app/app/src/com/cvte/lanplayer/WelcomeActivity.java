package com.cvte.lanplayer;

import java.util.Timer;
import java.util.TimerTask;

import com.cvte.lanplayer.utils.CheckSDCardUtil;
import com.cvte.lanplayer.utils.CheckWIFIConnectUtil;
import com.cvte.lanplayer.utils.ShowToastMsgUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;

public class WelcomeActivity extends Activity {

	private boolean mIsHaveWIFI = false;
	private boolean mIsHaveSDCard = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 隐藏Title
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcome);

		mIsHaveWIFI = CheckWIFIConnectUtil.isWifiConnected(this);
		mIsHaveSDCard = CheckSDCardUtil.IsHaveSDcard();

		Timer mTimer = new Timer();
		// 任务
		TimerTask mTask = new TimerTask() {
			@Override
			public void run() {
				Intent intent = new Intent();
				intent.setClass(WelcomeActivity.this, MainActivity.class);
				startActivity(intent);
				WelcomeActivity.this.finish();
			}
		};

		// 如果已接入WIFI和存在SD卡，则跳转页面
		if (mIsHaveWIFI == true) {
			if (mIsHaveSDCard == true) {
				// 计时2m之后跳转到主页面
				mTimer.schedule(mTask, 2000);
			} else {
				//使用Toast显示SD卡未插入
		        ShowToastMsgUtil.getInstance(this).ShowToastMsg("SD卡未插入,请退出应用");
			}
		} else {
			//使用Toast显示WIFI未连接
	        ShowToastMsgUtil.getInstance(this).ShowToastMsg("WIFI未连接,请退出应用");
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

}
