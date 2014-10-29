package com.cvte.lanplayer.service;

import java.io.IOException;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.cvte.lanplayer.GlobalData;
import com.cvte.lanplayer.utils.ScanLanDeviceUtil;

public class SendLanDataService extends Service {

	private final int STARE_SCAN = 1;

	private static String TAG = "SendLanDataService";
	private ScanCtrlReceiver mScanCtrl;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// 注册扫描控制的接收器
		mScanCtrl = new ScanCtrlReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalData.CTRL_SCAN_ACTION);
		registerReceiver(mScanCtrl, filter);
		
		Log.d(TAG,"onCreate SendLanDataService");

	}

	// 接收命令
	public class ScanCtrlReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			Bundle bundle = intent.getExtras();
			int control = bundle.getInt(GlobalData.GET_BUNDLE_COMMANT);

			switch (control) {
			case GlobalData.STARE_SCAN:// 开始扫描
				// 调用工具类的扫描方法
				ScanLanDeviceUtil.getInstance(SendLanDataService.this)
						.StartScan();
				Log.d(TAG,"recv StartScanLAN Broadcast ");
				break;

			case GlobalData.STOP_SCAN:// 停止扫描
				// 调用工具类的停止扫描方法
				try {
					ScanLanDeviceUtil.getInstance(SendLanDataService.this)
							.StopScan();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Log.d(TAG,"recv StopScanLAN Broadcast ");
				
				break;

			}
		}
	}

	public void onDestroy() {
		// 调用工具类的stopScan()
		try {
			ScanLanDeviceUtil.getInstance(SendLanDataService.this).StopScan();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 解除注册接收器
		this.unregisterReceiver(mScanCtrl);
		
		Log.d(TAG,"onDestroy SendLanDataService");
	}

}
