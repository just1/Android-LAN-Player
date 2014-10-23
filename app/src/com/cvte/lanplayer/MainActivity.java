package com.cvte.lanplayer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;

import com.cvte.lanplayer.service.RecvLanDataService;
import com.cvte.lanplayer.view.LocalPlayerFragment;
import com.cvte.lanplayer.view.ScanLanDeviceFragment;
import com.cvte.lanplayer.view.fragment_test;

public class MainActivity extends FragmentActivity {
	private final String TAG = "MainActivity";

	private final int port = 9598; // 默认端口号
	private final String KEY = "welcome to cvte";

	private MyFragmentPagerAdapter adapter;
	private ViewPager vp;
	// 标题数组
	List<String> titleList = new ArrayList<String>();
	// fragment数组
	List<Fragment> fragmentList = new ArrayList<Fragment>();

	private LocalPlayerFragment mLocalPlayerFragment;

	// udp等待连接
	Socket socket = null;
	static DatagramSocket udpSocket = null;
	static DatagramPacket udpPacket = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 隐藏Title
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_main);

		vp = (ViewPager) findViewById(R.id.vp);
		adapter = new MyFragmentPagerAdapter(getSupportFragmentManager());

		fragmentList.add(new LocalPlayerFragment());
		fragmentList.add(new fragment_test());
		fragmentList.add(new ScanLanDeviceFragment());
		titleList.add("播放");
		titleList.add("歌词");
		titleList.add("扫描");

		vp.setAdapter(adapter);

		// 开始接收信息的服务
		startService(new Intent(MainActivity.this, RecvLanDataService.class));

	}

	private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public android.support.v4.app.Fragment getItem(int arg0) {
			return fragmentList.get(arg0);
		}

		@Override
		public int getCount() {
			return fragmentList.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return titleList.get(position);
		}

	}

}
