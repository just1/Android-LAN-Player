package com.cvte.lanplayer;

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
import com.cvte.lanplayer.view.test.LanTestFileFragment;
import com.cvte.lanplayer.view.test.LanTestMsgFragment;
import com.cvte.lanplayer.view.test.LanTestMusicListFragment;

public class MainActivity extends FragmentActivity {
	private final String TAG = "MainActivity";

	private MyFragmentPagerAdapter adapter;
	private ViewPager vp;
	// 标题数组
	List<String> titleList = new ArrayList<String>();
	// fragment数组
	List<Fragment> fragmentList = new ArrayList<Fragment>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 隐藏Title
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);

		vp = (ViewPager) findViewById(R.id.vp);
		adapter = new MyFragmentPagerAdapter(getSupportFragmentManager());

		fragmentList.add(new LocalPlayerFragment());
		fragmentList.add(new ScanLanDeviceFragment());
		fragmentList.add(new LanTestMsgFragment());
		fragmentList.add(new LanTestMusicListFragment());
		fragmentList.add(new LanTestFileFragment());

		titleList.add("播放");
		titleList.add("扫描");
		titleList.add("测试消息通信");
		titleList.add("测试获取列表");
		titleList.add("测试发送文件");

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
