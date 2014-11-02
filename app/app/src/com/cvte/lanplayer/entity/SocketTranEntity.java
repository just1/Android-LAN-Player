package com.cvte.lanplayer.entity;

import java.io.Serializable;
import java.util.List;

import com.cvte.lanplayer.data.MusicData;

/**
 * 音乐列表实体类
 * @author JHYin
 *
 */
public class SocketTranEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	//消息类型
	private int mCommant;
	//音乐列表
	private List<MusicData> mMusicList;
	//文本消息
	private String mMessage;	
	
	public int getmCommant() {
		return mCommant;
	}

	public void setmCommant(int mCommant) {
		this.mCommant = mCommant;
	}

	public String getmMessage() {
		return mMessage;
	}

	public void setmMessage(String mMessage) {
		this.mMessage = mMessage;
	}



	public List<MusicData> getmMusicList() {
		return mMusicList;
	}

	public void setmMusicList(List<MusicData> mMusicList) {
		this.mMusicList = mMusicList;
	}

}
