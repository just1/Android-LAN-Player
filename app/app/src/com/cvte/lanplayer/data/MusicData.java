package com.cvte.lanplayer.data;

import java.io.Serializable;

public class MusicData implements Serializable{
	
	
	//索引号
	private int _MusicID;
	//文件名
	private String _FileName;
	//歌曲名
	private String _MusicName;
	//歌曲播放总时间
	private int _MusicDuration;
	//演唱者
	private String _MusicArtist;
	//专辑
	private String _MusicAlbum;
	//年份
	private String _MusicYear;
	//文件类型
	private String _FileType;
	//文件大小
	private String _FileSize;
	//文件路径
	private String _FilePath;
	
	public int getMusicID()
	{
		return _MusicID;
	}
	
	public void setMusicID(int musicID)
	{
		this._MusicID = musicID;
	}

	//文件名
	public String getFileName()
	{
		return _FileName;
	}
	
	public void setFileName(String fileName)
	{
		this._FileName = fileName;
	}
	//歌曲名称
	public String getMusicName()
	{
		return _MusicName;
	}
	
	public void setMusicName(String musicName)
	{
		this._MusicName = musicName;
	}
	//歌曲播放总时间
	public int getMusicDuration()
	{
		return _MusicDuration;
	}
	
	public void setMusicDuration(int musicDuration)
	{
		this._MusicDuration = musicDuration;
	}
	//演唱者
	public String getMusicArtist()
	{
		return _MusicArtist;
	}
	
	public void setMusicArtist(String musicArtist)
	{
		this._MusicArtist = musicArtist;
	}
	//专辑
	public String getMusicAlbum()
	{
		return _MusicAlbum;
	}
	
	public void setMusicAlbum(String musicAlbum)
	{
		this._MusicAlbum = musicAlbum;
	}
	//年份
	public String getMusicYear()
	{
		return _MusicYear;
	}
	
	public void setMusicYear(String musicYear)
	{
		this._MusicYear = musicYear;
	}
	//文件类型
	public String getFileType()
	{
		return _FileType;
	}
	
	public void setFileType(String fileType)
	{
		this._FileType = fileType;
	}
	//文件大小
	public String getFileSize()
	{
		return _FileSize;
	}
	
	public void setFileSize(String fileSize)
	{
		this._FileSize = fileSize;
	}
	//文件路径
	public String getFilePath()
	{
		return _FilePath;
	}
	
	public void setFilePath(String filePath)
	{
		this._FilePath = filePath;
	}
}
