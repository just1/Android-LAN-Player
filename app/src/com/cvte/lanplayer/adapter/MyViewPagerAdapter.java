package com.cvte.lanplayer.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyViewPagerAdapter extends FragmentPagerAdapter {

	private List<Fragment> mfragmentList;

	public MyViewPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
		super(fm);
		mfragmentList = fragmentList;
	}

	@Override
	public Fragment getItem(int postion) {
		return mfragmentList.get(postion);
	}

	@Override
	public int getCount() {
		return mfragmentList.size();
	}

}
