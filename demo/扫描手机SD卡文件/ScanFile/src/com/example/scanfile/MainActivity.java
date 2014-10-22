package com.example.scanfile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ArrayAdapter;

public class MainActivity extends ListActivity {

	List<String> items = new ArrayList<String>();// 用于保存扫描出的.torrent文件
	private final String TAG = "ScanFile";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		new Thread() {
			public void run() {
				// 获取外部存储路径
				File exStorageDirectory = Environment
						.getExternalStorageDirectory();
				if(exStorageDirectory == null){	//没有SD卡
					throw new NullPointerException("none SD card");
				}
				
				scan(exStorageDirectory);
			};
		}.start();
		
		// 设置适配器
		ArrayAdapter<String> torrentList = new ArrayAdapter<String>(
				this, R.layout.musicitme, items);
		setListAdapter(torrentList);
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * getMenuInflater().inflate(R.menu.activity_main, menu); return true; }
	 */

	/**
	 * 扫描文件 如果是目录文件夹，则继续向下扫描 否则，判断是否是.torrent文件
	 * 
	 * @param file
	 *            待扫描的文件
	 */
	public void scan(File file) {
		if (file.isDirectory())// 是否为文件夹
		{
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				scan(files[i]);
			}
		} else {
			if (isTorrent(file)) {
				items.add(file.toString());
				Log.d(TAG, file.toString());
			}
		}

	}

	/**
	 * 判断文件是否是.mp3文件
	 * 
	 * @param file
	 *            待判断的文件
	 * @return 如果是返回true,否则返回flase
	 */
	public boolean isTorrent(File file) {
		if (file.getName().endsWith(".mp3"))// 是否以.torrent结尾
		{
			return true;
		} else {
			return false;
		}
	}

}
