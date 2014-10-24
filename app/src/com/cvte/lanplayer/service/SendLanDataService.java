package com.cvte.lanplayer.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;

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

		// ע��ɨ����ƵĽ�����
		mScanCtrl = new ScanCtrlReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalData.CTRL_SCAN_ACTION);
		registerReceiver(mScanCtrl, filter);

	}

	public class ScanCtrlReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			Bundle bundle = intent.getExtras();
			int control = bundle.getInt("int");

			switch (control) {
			case GlobalData.STARE_SCAN:// ��ʼɨ��
				// ���ù������ɨ�跽��
				ScanLanDeviceUtil.getInstance(SendLanDataService.this)
						.StartScan();
				break;
				
			case GlobalData.STOP_SCAN:// ֹͣɨ��
				// ���ù������ֹͣɨ�跽��
				ScanLanDeviceUtil.getInstance(SendLanDataService.this)
						.StopScan();
				break;

			}
		}
	}

	public void onDestroy() {
		// ���ù������stopScan()
		ScanLanDeviceUtil.getInstance(SendLanDataService.this)
			.StopScan();
		
		// ���ע�������
		this.unregisterReceiver(mScanCtrl);
	}

}