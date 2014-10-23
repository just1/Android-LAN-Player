package com.cvte.lanplayer.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cvte.lanplayer.R;
import com.cvte.lanplayer.constant.AppConstant;
import com.cvte.lanplayer.constant.AppFunction;
import com.cvte.lanplayer.data.MusicData;
import com.cvte.lanplayer.holder.ListViewHolder;

public class ListViewAdapter extends BaseAdapter {
	
	//音乐本地列表
	private List<MusicData> mList;
	//得到一个LayoutInfalter对象用来导入布局
	private LayoutInflater mInflater;
	//音乐播放状态
	private int mPlayState;
	//播放位置
	private int mPlayIndex;
	
	//构造函数
	public ListViewAdapter(Context context,List<MusicData> list)
	{
		this.mInflater = LayoutInflater.from(context);
		this.mList = list;
	}
	
	/*
	 * 设置播放状态
	 * playindex歌曲索引号
	 * playstate播放状态
	 */
	public void setPlayState(int playindex,int playstate)
	{
		this.mPlayIndex = playindex;
		this.mPlayState = playstate;
		notifyDataSetChanged();
	}
	
	//更新歌曲列表
	public void setListAdapter(List<MusicData> list)
	{
		this.mList = list;
		notifyDataSetChanged();
	}
	
	//获取当前播放状态
	public int getPlayState()
	{
		return this.mPlayState;
	}
	
	//获取当前播放索引
	public int getPlayIndex()
	{
		return this.mPlayIndex;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ListViewHolder holder;
		if(convertView == null)
		{
			//获取listview_item布局文件视图
			convertView = mInflater.inflate(R.layout.listview_item, null);
			holder = new ListViewHolder();
			//获取各个控件
			//播放状态图标
			holder.imgState = (ImageButton)convertView.findViewById(R.id.musicplaystate);
			//音乐文件名称
			holder.sName = (TextView)convertView.findViewById(R.id.musicName);
			//音乐演唱者
			holder.sArtist = (TextView)convertView.findViewById(R.id.musicAritst);
			//音乐播放总时间
			holder.sTime = (TextView)convertView.findViewById(R.id.musicTime);
			//索引号
			holder.sPos = (TextView)convertView.findViewById(R.id.musiclistPos);
			convertView.setTag(holder);
		}
		else
		{
			//取出ViewHolder对象
			holder = (ListViewHolder)convertView.getTag();
		}
		//设置播放状态图标
		ShowPlayStateIcon(convertView,position);
		//设置TextView显示内容
		holder.sName.setText((CharSequence) mList.get(position).getFileName());
		holder.sArtist.setText((CharSequence)mList.get(position).getMusicArtist());
		holder.sTime.setText((CharSequence)AppFunction.ShowTime(mList.get(position).getMusicDuration()));
		holder.sPos.setText((CharSequence)String.format("%d. ", mList.get(position).getMusicID()));
		
		return convertView;
	}
	
	/*
	 * 设置音乐播放状态图标
	 */
	private void ShowPlayStateIcon(View view, int position)
	{
		//获取播放状态图标
		ImageButton imgbtn = (ImageButton)view.findViewById(R.id.musicplaystate);
		//判断是否是当前所选音乐的索引号
		if(position != mPlayIndex)
		{
			//不是当前播放音乐索引，播放状态图标隐藏（gone不占任何空间）
			imgbtn.setVisibility(View.GONE);
			return ;
		}
		//是当前所选的音乐，播放状态图标显示
		imgbtn.setVisibility(View.VISIBLE);
		//判断所选音乐所处状态，MUSIC_PAUSE:暂停    MUSIC_PLAYING:播放中
		if(mPlayState == AppConstant.MusicPlayState.PLAY_STATE_PAUSE)
		{
			imgbtn.setBackgroundResource(R.drawable.list_play_icon);
		}
		else
		{
			imgbtn.setBackgroundResource(R.drawable.list_pause_icon);
		}
	}
}
