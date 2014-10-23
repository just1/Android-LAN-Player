package com.cvte.lanplayer.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;
import android.widget.Toast;

import com.cvte.lanplayer.constant.AppConstant;
import com.cvte.lanplayer.constant.AppFunction;

public class PlayerService extends Service implements Runnable,MediaPlayer.OnCompletionListener {

	/*定义一个多媒体对象*/
	private static MediaPlayer mMediaPlayer = null;
	/*
	 * 接收参数 
	 * 1-播放 
	 * 2-暂停 
	 * 3-上一首
	 * 4-下一首
	 * 5-进度条点击事件
	 */
	private int MSG;
	private int state;
	private MyReceiver serviceReceiver;
	private AssetManager am;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		if(mMediaPlayer != null)
		{
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		
		am = getAssets();
		//创建BroadcastReceiver
		serviceReceiver = new MyReceiver();
		//创建IntentFilter
		IntentFilter filter = new IntentFilter();
		filter.addAction(AppConstant.MusicPlayVariate.CTL_ACTION);
		registerReceiver(serviceReceiver, filter);
		mMediaPlayer = new MediaPlayer();
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		
		if(mMediaPlayer != null)
		{
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}
	
	/*启动service是执行的方法*/
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	public class MyReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			int control = intent.getIntExtra("control", -1);
			switch(control)
			{
			case AppConstant.MusicPlayControl.MUSIC_CONTROL_PLAY://继续播放
				playMusic();
				break;
			case AppConstant.MusicPlayControl.MUSIC_CONTROL_PAUSE://暂停播放
				playPause();
				break;
			case AppConstant.MusicPlayControl.MUSIC_CONTROL_PREVIOUS:
				//上一首歌曲
				playPreviousMusic();
				break;
			case AppConstant.MusicPlayControl.MUSIC_CONTROL_NEXT:
				//下一首歌曲
				playNextMusic();
				break;
			case AppConstant.MusicPlayControl.MUSIC_CONTROL_SEEKBAR:
				//进度条命令
				SeekBarChange();
				break;
			}
		}
	}
	
	/*
	 * 返回当前播放歌曲总时间
	 */
	public static int getMusicDuration()
	{
		return mMediaPlayer.getDuration();
	}
	
	/*
	 * 返回当前播放歌曲播放位置
	 */
	public static int getMusicCurrPosition()
	{
		return mMediaPlayer.getCurrentPosition();
	}
	
	/*
	 * 播放音乐
	 */
	private void playMusic()
	{
		try
		{
			//获取当前播放音乐索引号判断
			int checkIndex = checkNowSong();
			Intent sendIntent = new Intent(AppConstant.MusicPlayVariate.UPDATE_ACTION);
			//判断是否是播放新歌
			if(AppConstant.MusicPlayData.IS_PLAY_NEW)
			{
				//重置多媒体
				mMediaPlayer.reset();
				//设置当前正在播放的音乐对象
				//AppConstant.mNowSong = AppConstant.mySongList.get(AppConstant.mCurrentIndex);
				//装载音乐文件
				mMediaPlayer.setDataSource(AppConstant.MusicPlayData.myMusicList.get(AppConstant.MusicPlayData.CURRENT_PLAY_INDEX).getFilePath());
				//准备播放
				mMediaPlayer.prepare();
				AppConstant.MusicPlayData.IS_PLAY_NEW = false;
				//DisplayToast(AppConstant.mNowSong.getFileName());
				sendIntent.putExtra(AppConstant.MusicPlayVariate.MUSIC_PLAY_DATA, AppConstant.MusicPlayVariate.MUSIC_PALY_DATA_INT);
			}
			else
			{
				if(AppConstant.MusicPlayState.CURRENT_PLAY_STATE == AppConstant.MusicPlayState.PLAY_STATE_PAUSE)
				{
					DisplayToast("继续播放");
				}
			}
			//设置当前状态为播放状态
			AppConstant.MusicPlayState.CURRENT_PLAY_STATE = AppConstant.MusicPlayState.PLAY_STATE_PLAYING;
			//开始播放
			mMediaPlayer.start();
			sendIntent.putExtra(AppConstant.MusicPlayVariate.MUSIC_PLAY_STATE_STR, AppConstant.MusicPlayState.PLAY_STATE_PLAYING);
			sendIntent.putExtra(AppConstant.MusicPlayVariate.MUSIC_INDEX_STR, AppConstant.MusicPlayData.CURRENT_PLAY_INDEX);
			//发送广播通知Activity
			sendBroadcast(sendIntent);
			//监听音乐是否播放完毕
			mMediaPlayer.setOnCompletionListener(new MediaPlayerOnCompletionListener());
		}
		catch(Exception e)
		{
			
		}
	}
	
	
	
	
	/*
	 * 监听音乐是否播放完毕，完毕则自动播放下一首
	 */
	private class MediaPlayerOnCompletionListener implements OnCompletionListener
	{

		@Override
		public void onCompletion(MediaPlayer arg0) {
			// TODO Auto-generated method stub
			//播放下一首音乐
			//判断播放模式是否是顺序播放且播放到最后一首
			if(AppConstant.MusicPlayData.CURRENT_PLAY_INDEX == AppConstant.MusicPlayData.myMusicList.size() - 1 && AppConstant.MusicPlayMode.CURRENT_PLAY_MODE == AppConstant.MusicPlayMode.PLAY_MODE_ORDER)
			{
				DisplayToast("已经是最后一首歌曲");
				//onDestroy();
				mMediaPlayer.pause();
				//暂停状态
				AppConstant.MusicPlayState.CURRENT_PLAY_STATE = AppConstant.MusicPlayState.PLAY_STATE_PAUSE;
				//不播放新歌
				AppConstant.MusicPlayData.IS_PLAY_NEW = false;
				//广播播放状态
				Intent sendIntent = new Intent(AppConstant.MusicPlayVariate.UPDATE_ACTION);
				sendIntent.putExtra(AppConstant.MusicPlayVariate.MUSIC_PLAY_STATE_STR, AppConstant.MusicPlayState.PLAY_STATE_PAUSE);
				sendIntent.putExtra(AppConstant.MusicPlayVariate.MUSIC_INDEX_STR, AppConstant.MusicPlayData.CURRENT_PLAY_INDEX);
				//发送广播通知Activity
				sendBroadcast(sendIntent);
			}
			else
			{
				//继续下一首
				playNextMusic();
			}
		}
	}
	
	/*
	 * 暂停播放
	 */
	private void playPause()
	{
		//暂停不重新播放新歌
		AppConstant.MusicPlayData.IS_PLAY_NEW = false;
		//设置为暂停状态
		AppConstant.MusicPlayState.CURRENT_PLAY_STATE = AppConstant.MusicPlayState.PLAY_STATE_PAUSE;
		mMediaPlayer.pause();
		//
		Intent sendIntent = new Intent(AppConstant.MusicPlayVariate.UPDATE_ACTION);
		sendIntent.putExtra(AppConstant.MusicPlayVariate.MUSIC_PLAY_STATE_STR, AppConstant.MusicPlayState.PLAY_STATE_PAUSE);
		sendIntent.putExtra(AppConstant.MusicPlayVariate.MUSIC_INDEX_STR, AppConstant.MusicPlayData.CURRENT_PLAY_INDEX);
		//发送广播通知Activity
		sendBroadcast(sendIntent);
		DisplayToast("暂停播放");
	}
	
	/*
	 * 播放上一首
	 */
	private void playPreviousMusic()
	{
		//获取当前播放音乐索引号判断
		int checkIndex = checkNowSong();
		if(checkIndex == 0)
		{
			DisplayToast("不存在音乐文件");
			return;
		}
		else if(checkIndex == 1)
		{
			//索引号判断正常
			//根据播放模式设置当前要播放的索引号
			AppConstant.MusicPlayData.CURRENT_PLAY_INDEX = 
					setCurrIndexByPlayType(AppConstant.MusicPlayState.CURRENT_PLAY_STATE, 
											AppConstant.MusicPlayControl.MUSIC_CONTROL_PREVIOUS, 
											AppConstant.MusicPlayData.CURRENT_PLAY_INDEX, 
											AppConstant.MusicPlayData.myMusicList.size());
		}
		else if(checkIndex == 2)
		{
			//索引号异常，默认加载第一首歌曲信息，直接播放
		}
		else 
		{
			//判断异常
			return;
		}
		//播放新歌标识
		AppConstant.MusicPlayData.IS_PLAY_NEW = true;
		//播放状态
		AppConstant.MusicPlayState.CURRENT_PLAY_STATE = AppConstant.MusicPlayState.PLAY_STATE_PLAYING;
		//开始播放
		playMusic();
	}
	
	/*
	 * 播放下一首
	 */
	private void playNextMusic()
	{
		//获取当前播放音乐索引号判断
		int checkIndex = checkNowSong();
		if(checkIndex == 0)
		{
			DisplayToast("不存在音乐文件");
			return;
		}
		else if(checkIndex == 1)
		{
			//索引号判断正常
			//根据播放模式设置当前要播放的索引号
			AppConstant.MusicPlayData.CURRENT_PLAY_INDEX = setCurrIndexByPlayType(AppConstant.MusicPlayState.CURRENT_PLAY_STATE, AppConstant.MusicPlayControl.MUSIC_CONTROL_NEXT, AppConstant.MusicPlayData.CURRENT_PLAY_INDEX, AppConstant.MusicPlayData.myMusicList.size());
		}
		else if(checkIndex == 2)
		{
			//索引号异常，默认加载第一首歌曲信息，直接播放
		}
		else 
		{
			//判断异常
			return;
		}
		//播放新歌标识
		AppConstant.MusicPlayData.IS_PLAY_NEW = true;
		//播放状态
		AppConstant.MusicPlayState.CURRENT_PLAY_STATE = AppConstant.MusicPlayState.PLAY_STATE_PLAYING;
		//开始播放
		playMusic();
	}
	
	/*
	 * 歌曲进度条点击事件
	 */
	private void SeekBarChange()
	{
		//播放进度跳转到当前位置
		mMediaPlayer.seekTo(AppConstant.MusicPlayData.CURRENT_PLAY_POSITION);
	}
	
	
	/*
	 * 根据播放模式设置当前音乐索引
	 * playmode
	 * 	AppConstant.MusicPlayMode.MUSIC_ORDER_PLAY-顺序播放
	 * 	AppConstant.MusicPlayMode.MUSIC_LIST_LOOP_PLAY-循环播放
	 * 	AppConstant.MusicPlayMode.MUSIC_RANDOM_PLAY-随机播放
	 * 	AppConstant.MusicPlayMode.MUSIC_SINGLE_LOOP_PLAY-单曲播放
	 * playDirection
	 *  AppConstant.MusicPlayControl.MUSIC_CONTROL_PREVIOUS-前一首
	 *  AppConstant.MusicPlayControl.MUSIC_CONTROL_NEXT-下一首
	 * index当前播放音乐索引号
	 * allCount音乐列表总数
	 * 默认为0
	 */
	private int setCurrIndexByPlayType(int playmode, int playDirection, int index, int allCount)
	{
		int newIndex = index;
		switch(playmode)
		{
		case AppConstant.MusicPlayMode.PLAY_MODE_ORDER:
			//顺序播放
			if(playDirection == AppConstant.MusicPlayControl.MUSIC_CONTROL_PREVIOUS)
			{
				//前一首
				//判断是否是第一首歌曲
				if(index == 0)
				{
					DisplayToast("已经是第一首歌曲");
					newIndex = index;
				}
				else
				{
					newIndex = index - 1;
				}
			}
			else if(playDirection == AppConstant.MusicPlayControl.MUSIC_CONTROL_NEXT)
			{
				//下一首
				//判断是否是最后一首歌曲
				if(index == allCount - 1)
				{
					DisplayToast("已经是最后一首歌曲");
					newIndex = index;
				}
				else
				{
					newIndex = index + 1;
				}
			}
			break;
		case AppConstant.MusicPlayMode.PLAY_MODE_LIST_LOOP:
			//循环播放
			if(playDirection == AppConstant.MusicPlayControl.MUSIC_CONTROL_PREVIOUS)
			{
				//判断是否是第一首歌曲
				if(index == 0)
				{
					newIndex = allCount - 1;
				}
				else
				{
					newIndex = index - 1;
				}
			}
			else if(playDirection == AppConstant.MusicPlayControl.MUSIC_CONTROL_NEXT)
			{
				//判断是否是最后一首歌曲
				if(index == allCount - 1)
				{
					newIndex = 0;
				}
				else
				{
					newIndex = index + 1;
				}
			}
			break;
		case AppConstant.MusicPlayMode.PLAY_MODE_RANDOM:
			//随机播放
			newIndex = AppFunction.GenerateRandom(allCount - 1, index);
			break;
		case AppConstant.MusicPlayMode.PLAY_MODE_SINGLE:
			//单曲播放
			newIndex = index;
			break;
		default:break;
		}
		//返回要播放的索引号
		return newIndex;
	}
	
	/*
	 * 判断当前正在播放的音乐对象
	 * 0-不存在音乐
	 * 1-索引号正常
	 * 2-索引号错误，设置第一个音乐文件
	 */
	private int checkNowSong()
	{
		//默认不存在音乐
		int isCorrect = 0;
		try
		{
			//判断索引号
			if(AppConstant.MusicPlayData.CURRENT_PLAY_INDEX >= 0 && AppConstant.MusicPlayData.CURRENT_PLAY_INDEX < AppConstant.MusicPlayData.myMusicList.size())
			{
				//AppConstant.mNowSong = AppConstant.mySongList.get(AppConstant.mCurrentIndex);
				//索引号正常
				isCorrect = 1;
			}
			else
			{
				//索引号错误，判断音乐列表是否存在音乐信息
				if(AppConstant.MusicPlayData.myMusicList.size() > 0)
				{
					//设置当前正在播放的音乐的索引号
					AppConstant.MusicPlayData.CURRENT_PLAY_INDEX = 0;
					//索引号错误，装载第一首音乐
					isCorrect = 2;
				}
				else
				{
					//设置默认值
					AppConstant.MusicPlayData.CURRENT_PLAY_INDEX = -1;
					//音乐列表不存在歌曲
					isCorrect = 0;
				}
			}
			return isCorrect;
		}
		catch(Exception e)
		{
			isCorrect = 0;
			return isCorrect;
		}
	}
	
	/*
	 * Toast显示
	 * 
	 */
	private void DisplayToast(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
