package com.cvte.lanplayer.constant;

import java.util.ArrayList;

import com.cvte.lanplayer.data.MusicData;

public interface AppConstant {
	/*
	 * 歌曲信息
	 */
	public class MusicPlayData
	{
		//歌曲信息列表
		public static ArrayList<MusicData> myMusicList = new ArrayList<MusicData>();
		//当前播放歌曲索引号
		public static int CURRENT_PLAY_INDEX = -1;
		//当前播放歌曲进度
		public static int CURRENT_PLAY_POSITION = 0;
		//是否要重新播放标识
		public static boolean IS_PLAY_NEW = true;
	}
	/*
	 * 音乐播放状态
	 */
	public class MusicPlayState
	{
		//暂停状态
		public static final int PLAY_STATE_PAUSE = 0;
		//播放状态
		public static final int PLAY_STATE_PLAYING = 1;
		//当前播放状态，默认为暂停状态
		public static int CURRENT_PLAY_STATE = PLAY_STATE_PAUSE;
	}
	/*
	 * 音乐播放模式
	 */
	public class MusicPlayMode
	{
		//顺序播放
		public static final int PLAY_MODE_ORDER = 0;
		//列表循环
		public static final int PLAY_MODE_LIST_LOOP = 1;
		//随机播放
		public static final int PLAY_MODE_RANDOM = 2;
		//单曲循环
		public static final int PLAY_MODE_SINGLE = 3;
		//播放模式数组
		public static final int[] PLAY_MODE_ARRAY = {
				PLAY_MODE_ORDER,
				PLAY_MODE_LIST_LOOP,
				PLAY_MODE_RANDOM,
				PLAY_MODE_SINGLE};
		//当前播放模式，默认播放模式为顺序播放
		public static int CURRENT_PLAY_MODE = PLAY_MODE_ARRAY[0];
	}
	/*
	 * 命令
	 */
	public class MusicPlayControl
	{
		//播放命令
		public static final int MUSIC_CONTROL_PLAY = 0;         
		//暂停命令
		public static final int MUSIC_CONTROL_PAUSE = 1;        
		//上一首命令
		public static final int MUSIC_CONTROL_PREVIOUS = 2;     
		//下一首命令
		public static final int MUSIC_CONTROL_NEXT = 3;         
		//进度条点击命令
		public static final int MUSIC_CONTROL_SEEKBAR = 4;      
	}
	
	/*
	 * 标识符
	 */
	public class MusicPlayVariate
	{
		//当前播放索引标识符
		public static final String MUSIC_INDEX_STR = "playIndex";     
		
		public static final String MUSIC_PLAY_DATA = "playdata";
		//播放状态标识符
		public static final String MUSIC_PLAY_STATE_STR = "playState";  
		//控制命令标识符
		public static final String MUSIC_CONTROL_STR = "control";  
		//1-代表开始播放新歌，更新歌曲名，总播放时间，播放索引号
		public static final int MUSIC_PALY_DATA_INT = 1;
		
		public static String CTL_ACTION = "org.crazyit.action.CTL_ACTION";
		public static String UPDATE_ACTION = "org.crazyit.action.UPDATE_ACTION";
	}
}
