package com.example.socketsendlistdemo;

import java.io.Serializable;
import java.util.List;

public class SongList implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	List<String> mSongList;
	
	
	public void SetSongList(List<String> songList){
		mSongList = songList;
	}
	
	public  List<String> GetSongList(){
		return mSongList;
	}
	
}
