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
	//发送方IP
	private String mSendIP;	
	//接收方IP
	private String mRecvIP;	
	
	
	
	public String getmSendIP() {
		return mSendIP;
	}

	public void setmSendIP(String mSendIP) {
		this.mSendIP = mSendIP;
	}

	public String getmRecvIP() {
		return mRecvIP;
	}

	public void setmRecvIP(String mRecvIP) {
		this.mRecvIP = mRecvIP;
	}

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
