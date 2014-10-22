package com.cvte.lanplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.cvte.lanplayer.adapter.MusicListAdapter;

public class MainActivity extends Activity {
	private final String TAG = "MainActivity";

	// 播放对象
	private MediaPlayer myMediaPlayer;
	// 播放列表
	private List<String> myMusicList = new ArrayList<String>();
	// 当前播放歌曲的索引
	private int currentListItem = 0;
	// 音乐的路径
	private static final String MUSIC_PATH = new String("/sdcard/");

	// 控件
	private ListView lv_songlist;
	private MusicListAdapter music_lv_adapter;
	private Button btn_play;
	private Button btn_stop;
	private Button btn_pause;
	private Button btn_next;
	private Button btn_last;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		myMediaPlayer = new MediaPlayer();

		// 实例化控件
		lv_songlist = (ListView) findViewById(R.id.lv_songlist);
		btn_play = (Button) findViewById(R.id.btn_play);
		btn_stop = (Button) findViewById(R.id.btn_stop);
		btn_pause = (Button) findViewById(R.id.btn_pause);
		btn_next = (Button) findViewById(R.id.btn_next);
		btn_last = (Button) findViewById(R.id.btn_last);

		// 初始化音乐文件
		InitMusicList();

		// 初始化按键监听
		InitListen();
	}

	private void InitListen() {
		btn_play.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				playMusic(MUSIC_PATH + myMusicList.get(currentListItem));
				Log.d(TAG,
						"play:" + MUSIC_PATH + myMusicList.get(currentListItem));
			}
		});

		btn_stop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (myMediaPlayer.isPlaying()) {
					myMediaPlayer.reset();
				}

			}
		});

		btn_pause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (myMediaPlayer.isPlaying()) {
					myMediaPlayer.pause();
				} else {
					myMediaPlayer.start();
				}
			}
		});

		btn_next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				nextMusic();
			}
		});

		btn_last.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				lastMusic();
			}
		});

		// 点击音乐列表
		lv_songlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				currentListItem = position;
				playMusic(MUSIC_PATH + myMusicList.get(currentListItem));
				Log.d(TAG,
						"play:" + MUSIC_PATH + myMusicList.get(currentListItem));
			}
		});
	}

	private void InitMusicList() {

		File home = new File(MUSIC_PATH);
		if (home.listFiles(new MusicFilter()).length > 0) {
			for (File file : home.listFiles(new MusicFilter())) {
				myMusicList.add(file.getName());
				Log.d(TAG, "find:" + file.getName());
			}

		}
		music_lv_adapter = new MusicListAdapter(myMusicList,
				getApplicationContext());
		lv_songlist.setAdapter(music_lv_adapter);

	}

	// 播放音乐
	void playMusic(String path) {
		try {
			myMediaPlayer.reset();
			myMediaPlayer.setDataSource(path);
			myMediaPlayer.prepare();
			myMediaPlayer.start();
			myMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					nextMusic();
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// 下一首
	void nextMusic() {
		if (++currentListItem >= myMusicList.size()) {
			currentListItem = 0;
		} else {
			playMusic(MUSIC_PATH + myMusicList.get(currentListItem));
		}
	}

	// 上一首
	void lastMusic() {
		if (currentListItem != 0) {
			if (--currentListItem >= 0) {
				currentListItem = myMusicList.size();
			} else {
				playMusic(MUSIC_PATH + myMusicList.get(currentListItem));
			}
		} else {
			playMusic(MUSIC_PATH + myMusicList.get(currentListItem));
		}
	}

}
