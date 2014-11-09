package com.cvte.lanplayer.view.device;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.cvte.lanplayer.GlobalData;
import com.cvte.lanplayer.R;

public class LanDeviceControlActivity extends FragmentActivity {

	private final String TAG = "LanDeviceControlActivity";

	private String targetIp = null;

	private TextView tv_target_ip;
	private Button btn_back;

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
		setContentView(R.layout.activity_landevice_ctrl);

		InitData();
		InitView();

	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		// TODO Auto-generated method stub
		return super.onCreateView(name, context, attrs);
	}

	/**
	 * 实例化控件
	 */
	private void InitView() {

		tv_target_ip = (TextView) findViewById(R.id.tv_target_ip);
		btn_back = (Button) findViewById(R.id.btn_back);
		
		tv_target_ip.setText("目标IP：" + targetIp);
		btn_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LanDeviceControlActivity.this.finish();
			}
		});
		

		vp = (ViewPager) findViewById(R.id.vp);
		adapter = new MyFragmentPagerAdapter(getSupportFragmentManager());

		// 初始化LanMusicFragment
		LanMusicFragment lanMusicFragment = new LanMusicFragment();
		Bundle musitBundle = new Bundle();
		musitBundle.putString(GlobalData.GetBundle.GET_IP, targetIp);
		lanMusicFragment.setArguments(musitBundle);
		
		// 初始化LanChatFragment
		LanChatFragment lanChatFragment = new LanChatFragment();
		Bundle chatBundle = new Bundle();
		chatBundle.putString(GlobalData.GetBundle.GET_IP, targetIp);
		lanChatFragment.setArguments(chatBundle);

		
		fragmentList.add(lanMusicFragment);
		fragmentList.add(lanChatFragment);
		
		titleList.add("Music");
		titleList.add("Chat");

		vp.setAdapter(adapter);

	}

	/**
	 * 初始化数据
	 */
	private void InitData() {
		Intent intent = getIntent();
		targetIp = (String) intent.getSerializableExtra("ip");

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
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
