package com.cvte.lanplayer.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.cvte.lanplayer.utils.RecvLanScanDeviceUtil;

public class RecvLanDataService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		//�����������������豸ɨ�赽�Ľ��ռ���
		RecvLanScanDeviceUtil.getInstance(this).StartRecv();

	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		//ֹͣ�������������豸ɨ�赽�Ľ��ռ���
		RecvLanScanDeviceUtil.getInstance(this).StopRecv();

	}

}