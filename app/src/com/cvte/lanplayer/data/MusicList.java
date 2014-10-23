package com.cvte.lanplayer.data;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.provider.MediaStore;

public class MusicList extends Activity {
	
	private ArrayList<MusicData> getMyList()
	{
		ArrayList<MusicData> list = null;
		//清除所有歌曲信息
		Cursor cur = getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
				new String[]{MediaStore.Audio.Media._ID,
						MediaStore.Audio.Media.DISPLAY_NAME,
						MediaStore.Audio.Media.TITLE,
						MediaStore.Audio.Media.DURATION,
						MediaStore.Audio.Media.ARTIST,
						MediaStore.Audio.Media.ALBUM,
						MediaStore.Audio.Media.YEAR,
						MediaStore.Audio.Media.MIME_TYPE,
						MediaStore.Audio.Media.SIZE,
						MediaStore.Audio.Media.DATA}, 
				null, 
				null, 
				null);
		//歌曲索引号
		int index = 1;
		while(cur.moveToNext())
		{
			MusicData _song=new MusicData();
			_song.setMusicID(index);
			_song.setFileName(cur.getString(1));
			_song.setMusicName(cur.getString(2));
			_song.setMusicArtist(cur.getString(4));
			_song.setMusicDuration(cur.getInt(3));
			_song.setMusicAlbum(cur.getString(5));
			if(cur.getString(6)!=null)
			{
				_song.setMusicYear(cur.getString(6));
			}
			else
			{
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
			if(cur.getString(9)!=null)
			{
				_song.setFilePath(cur.getString(9));
			}
			index++;
			list.add(_song);
		}
		cur.close();
		return list;
	}
	
	public ArrayList<MusicData> getMusicList()
	{
		ArrayList<MusicData> list = getMyList();
		return list;
	}
}
