package com.cvte.lanplayer.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.cvte.lanplayer.R;
import com.cvte.lanplayer.adapter.ListViewAdapter;
import com.cvte.lanplayer.constant.AppConstant;
import com.cvte.lanplayer.data.MusicData;
import com.cvte.lanplayer.interfaces.IOnServiceConnectComplete;
import com.cvte.lanplayer.service.PlayerService;

public class LocalPlayerFragment extends Fragment implements
		IOnServiceConnectComplete {
	private final String TAG = "LocalPlayerFragment";

	private Activity activity;
	private ListView mListView;
	private static ListViewAdapter lvAdapter;
	private UIManager mUIManager;

	private Intent startIntent;
	private ActivityReceive activityReceive;

	private View myView;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.music_list, container, false);

		mListView = (ListView) view.findViewById(R.id.music_list);
		myView = view;
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		// 初始化音乐文件
		getMyList();

		lvAdapter = new ListViewAdapter(activity,
				AppConstant.MusicPlayData.myMusicList);
		mListView.setAdapter(lvAdapter);

		mUIManager = new UIManager();

		activityReceive = new ActivityReceive();
		// 创建IntentFilter
		IntentFilter filter = new IntentFilter();
		// 指定BroadcastReceiver监听的Action
		filter.addAction(AppConstant.MusicPlayVariate.UPDATE_ACTION);
		// 注册BroadcastReceiver
		activity.registerReceiver(activityReceive, filter);
		startIntent = new Intent(activity, PlayerService.class);
		// 启动后台Service
		activity.startService(startIntent);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				// 开始播放新音乐
				if (AppConstant.MusicPlayData.CURRENT_PLAY_INDEX != position) {
					AppConstant.MusicPlayData.IS_PLAY_NEW = true;
					AppConstant.MusicPlayData.CURRENT_PLAY_INDEX = position;
					// 广播播放
					SendBroadcast(AppConstant.MusicPlayControl.MUSIC_CONTROL_PLAY);
				
				} else {
					if (AppConstant.MusicPlayState.CURRENT_PLAY_STATE == AppConstant.MusicPlayState.PLAY_STATE_PAUSE) {
						// 暂停状态，则继续播放
						SendBroadcast(AppConstant.MusicPlayControl.MUSIC_CONTROL_PLAY);
					} else if (AppConstant.MusicPlayState.CURRENT_PLAY_STATE == AppConstant.MusicPlayState.PLAY_STATE_PLAYING) {
						// 播放状态，则暂停播放
						SendBroadcast(AppConstant.MusicPlayControl.MUSIC_CONTROL_PAUSE);
					}
				}
			}

		});
	}

	/*
	 * 获取音乐列表
	 */
	private void getMyList() {
		// ArrayList<MusicData> list = null;
		AppConstant.MusicPlayData.myMusicList.clear();
		// 清除所有歌曲信息
		Cursor cur = activity.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Media._ID,
						MediaStore.Audio.Media.DISPLAY_NAME,
						MediaStore.Audio.Media.TITLE,
						MediaStore.Audio.Media.DURATION,
						MediaStore.Audio.Media.ARTIST,
						MediaStore.Audio.Media.ALBUM,
						MediaStore.Audio.Media.YEAR,
						MediaStore.Audio.Media.MIME_TYPE,
						MediaStore.Audio.Media.SIZE,
						MediaStore.Audio.Media.DATA }, null, null, null);
		// 歌曲索引号
		int index = 1;
		while (cur.moveToNext()) {
			MusicData _song = new MusicData();
			_song.setMusicID(index);
			_song.setFileName(cur.getString(1));
			_song.setMusicName(cur.getString(2));
			_song.setMusicArtist(cur.getString(4));
			_song.setMusicDuration(cur.getInt(3));
			_song.setMusicAlbum(cur.getString(5));
			if (cur.getString(6) != null) {
				_song.setMusicYear(cur.getString(6));
			} else {
				_song.setMusicYear("undefine");
			}
			if ("audio/mpeg".equals(cur.getString(7).trim())) {// file type
				_song.setFileType("mp3");
			} else if ("audio/x-ms-wma".equals(cur.getString(7).trim())) {
				_song.setFileType("wma");
			}
			_song.setFileType(cur.getString(7));
			if (cur.getString(8) != null) {// fileSize
				float temp = cur.getInt(8) / 1024f / 1024f;
				String sizeStr = (temp + "").substring(0, 4);
				_song.setFileSize(sizeStr + "M");
			} else {
				_song.setFileSize("undefine");
			}
			_song.setFileSize(cur.getString(8));
			if (cur.getString(9) != null) {
				_song.setFilePath(cur.getString(9));
			}
			index++;
			AppConstant.MusicPlayData.myMusicList.add(_song);
		}
		cur.close();

	}

	/*
	 * 自定义的BroadcastReceive，负责监听从Service传回来的广播
	 */
	private class ActivityReceive extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			// 获取Intent中的playState消息，playState代表播放状态
			int playState = intent.getIntExtra(
					AppConstant.MusicPlayVariate.MUSIC_PLAY_STATE_STR, -1);
			// 获取Intent中的playIndex消息，playIndex代表当前正在播放的歌曲
			int playIndex = intent.getIntExtra(
					AppConstant.MusicPlayVariate.MUSIC_INDEX_STR, -1);
			// 获取Intent中的playdata消息，playdata代表当前播放歌曲信息标志
			int playData = intent.getIntExtra(
					AppConstant.MusicPlayVariate.MUSIC_PLAY_DATA, -1);
			switch (playState) {
			case AppConstant.MusicPlayState.PLAY_STATE_PLAYING:
				mUIManager.mImgBtnListPlay
						.setImageResource(R.drawable.handle_pause_normal);

				if (playIndex >= 0) {
					mUIManager.setCurrPlayState(
							AppConstant.MusicPlayData.CURRENT_PLAY_INDEX,
							AppConstant.MusicPlayState.PLAY_STATE_PLAYING);
				}
				break;
			case AppConstant.MusicPlayState.PLAY_STATE_PAUSE:
				mUIManager.mImgBtnListPlay
						.setImageResource(R.drawable.handle_play_normal);

				if (playIndex >= 0) {
					mUIManager.setCurrPlayState(
							AppConstant.MusicPlayData.CURRENT_PLAY_INDEX,
							AppConstant.MusicPlayState.PLAY_STATE_PAUSE);
				}
				break;
			}
			//
			if (playData == AppConstant.MusicPlayVariate.MUSIC_PALY_DATA_INT) {
				// 更新歌曲信息
				// 获取歌曲播放到多少ms
				AppConstant.MusicPlayData.CURRENT_PLAY_POSITION = PlayerService
						.getMusicCurrPosition();
				// 歌曲名称
				mUIManager.mTxtListPlayName
						.setText(AppConstant.MusicPlayData.myMusicList.get(
								AppConstant.MusicPlayData.CURRENT_PLAY_INDEX)
								.getFileName());

				// /设置歌曲索引
				// mUIManager.mSliderDrawerManager.mTxtSongNum.setText(String.format("%d/%d",
				// AppConstant.mCurrentIndex+1,AppConstant.mySongList.size()));
				// 进度条
				mUIManager.mListSeekBar
						.setMax(PlayerService.getMusicDuration());
			}
		}
	}

	/*
	 * 发送广播命令 controlType命令类型
	 * AppConstant.MusicPlayControl.MUSIC_CONTROL_PLAY:播放命令
	 * AppConstant.MusicPlayControl.MUSIC_CONTROL_PAUSE:暂停命令
	 * AppConstant.MusicPlayControl.MUSIC_CONTROL_PREVIOUS:上一首命令
	 * AppConstant.MusicPlayControl.MUSIC_CONTROL_NEXT:下一首命令
	 * AppConstant.MusicPlayControl.MUSIC_CONTROL_SEEKBAR:进度条命令
	 */
	private void SendBroadcast(int controlType) {
		Intent sendIntent = new Intent("org.crazyit.action.CTL_ACTION");
		// 启动服务
		activity.startService(startIntent);
		// 传递命令
		sendIntent.putExtra(AppConstant.MusicPlayVariate.MUSIC_CONTROL_STR,
				controlType);
		// 广播
		activity.sendBroadcast(sendIntent);
	}

	public class UIManager extends Activity {

		private Handler seekbarHandler = new Handler();
		private Thread seekbarThread;

		private ImageButton mImgBtnListPlay;
		private ImageButton mImgBtnListPlayNext;
		private SeekBar mListSeekBar;
		private TextView mTxtListPlayName;
		private ImageButton mImgBtnArtistPhoto;

		public UIManager() {
			initUIManager();
		}

		private void initUIManager() {
			mImgBtnListPlay = (ImageButton) myView
					.findViewById(R.id.music_list_play);
			mImgBtnListPlayNext = (ImageButton) myView
					.findViewById(R.id.music_list_playNext);
			mListSeekBar = (SeekBar) myView
					.findViewById(R.id.music_list_seekBar);
			mTxtListPlayName = (TextView) myView
					.findViewById(R.id.txt_musicname);
			mImgBtnArtistPhoto = (ImageButton) myView
					.findViewById(R.id.music_artist_photo);

			// 开启新线程
			StartThread();

			/*
			 * 播放按钮
			 */
			mImgBtnListPlay.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (AppConstant.MusicPlayState.CURRENT_PLAY_STATE == 
							AppConstant.MusicPlayState.PLAY_STATE_PLAYING) {
						// 当前为播放状态，则暂停
						SendBroadcast(AppConstant.MusicPlayControl.MUSIC_CONTROL_PAUSE);
					} else {
						// 当前为暂停状态，则播放
						SendBroadcast(AppConstant.MusicPlayControl.MUSIC_CONTROL_PLAY);
					}
				}

			});

			/*
			 * 下一首
			 */
			mImgBtnListPlayNext.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					SendBroadcast(AppConstant.MusicPlayControl.MUSIC_CONTROL_NEXT);
				}

			});

			/*
			 * 列表进度条事件
			 */
			mListSeekBar
					.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

						@Override
						public void onProgressChanged(SeekBar arg0,
								int position, boolean arg2) {
							// TODO Auto-generated method stub
							if (arg2 == true) {
								AppConstant.MusicPlayData.CURRENT_PLAY_POSITION = position;
							}
						}

						@Override
						public void onStartTrackingTouch(SeekBar arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onStopTrackingTouch(SeekBar arg0) {
							// TODO Auto-generated method stub
							SendBroadcast(AppConstant.MusicPlayControl.MUSIC_CONTROL_SEEKBAR);
						}

					});

		}

		/*
		 * 初始化线程
		 */
		private void StartThread() {
			seekbarThread = new Thread() {
				@Override
				public void run() {
					while (true) {
						try {
							Thread.sleep(250);
							seekbarHandler.post(r);
						} catch (Throwable t) {

						}
					}
				}
			};
			// 启动线程
			seekbarThread.start();
		}

		Runnable r = new Runnable() {
			@Override
			public void run() {
				// 0-正在播放
				if (AppConstant.MusicPlayState.CURRENT_PLAY_STATE == AppConstant.MusicPlayState.PLAY_STATE_PLAYING) {
					// 获取歌曲播放到多少ms
					AppConstant.MusicPlayData.CURRENT_PLAY_POSITION = PlayerService
							.getMusicCurrPosition();

					// 设置进度条当前位置
					mListSeekBar
							.setProgress(AppConstant.MusicPlayData.CURRENT_PLAY_POSITION);

				}
				// 每隔250ms更新一次
				// seekbarHandler.postDelayed(r, 250);
			}
		};

		/*
		 * 设置ListView列表中播放状态图标 currIndex当前播放索引 currState当前播放状态
		 */
		public void setCurrPlayState(int currIndex, int currState) {
			lvAdapter.setPlayState(currIndex, currState);
		}
	}

	public void DisplayToast(String str) {
		Toast.makeText(activity, str, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void OnServiceConnectComplete() {
		// TODO Auto-generated method stub

	}

}
