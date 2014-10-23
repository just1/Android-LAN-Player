package com.cvte.lanplayer.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cvte.lanplayer.R;

public class IpListAdapter extends BaseAdapter {

	// 音乐文件名的List
	private List<String> musicList;
	private Context context;

	public IpListAdapter(List<String> musicList, Context context) {
		this.musicList = musicList;
		this.context = context;
	}

	@Override
	public int getCount() {
		if (musicList == null)
			return 0;

		return musicList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = View.inflate(context,
					R.layout.listview_muscilist_item, null);
			
			holder = new ViewHolder();
			holder.tv = (TextView) convertView.findViewById(R.id.tv_music_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (musicList.size() >= position) {	//不超出范围
			if (musicList.get(position) != null) {	//不为空
				holder.tv.setText(musicList.get(position));
			}
		}

		return convertView;
	}

	static class ViewHolder {
		TextView tv;
	}

}
