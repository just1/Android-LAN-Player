package com.cvte.lanplayer.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.cvte.lanplayer.GlobalData;
import com.cvte.lanplayer.R;
import com.cvte.lanplayer.adapter.MyListAdapter;
import com.cvte.lanplayer.constant.AppConstant;
import com.cvte.lanplayer.entity.SocketTranEntity;
import com.cvte.lanplayer.utils.SendSocketMessageUtil;
import com.cvte.lanplayer.utils.SocketManager;

public class LanTestFileFragment extends Fragment {

	private final String TAG = "LanMusicListTestFragment";

	private Button btn_test_send;
	private TextView tv_recv;
	private EditText et_ip;
	private ListView lv_music_list;

	private List<String> mMusicList = new ArrayList<String>();
	private MyListAdapter mMusicListAdapter;
	// 被选中的音乐文件ID
	private int mSelectMusicID = -1;

	private Activity mActivity;

	private RecvScoketMsgReceiver mRecvScoketMsgReceiver;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_lan_file_test,
				container, false);
		btn_test_send = (Button) view.findViewById(R.id.btn_test_send);
		tv_recv = (TextView) view.findViewById(R.id.tv_recv_data);
		et_ip = (EditText) view.findViewById(R.id.et_ip);
		lv_music_list = (ListView) view.findViewById(R.id.lv_music_list);

		Init();
		SetListener();

		return view;
	}

	private void Init() {

		// 为ListView设置Adapter
		mMusicListAdapter = new MyListAdapter(mMusicList, mActivity);
		lv_music_list.setAdapter(mMusicListAdapter);

		// 注册接收器
		mRecvScoketMsgReceiver = new RecvScoketMsgReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalData.SocketTranCommand.RECV_SOCKET_FROM_SERVICE_ACTION);
		mActivity.registerReceiver(mRecvScoketMsgReceiver, filter);

	}

	private void SetListener() {

		btn_test_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Log.d(TAG, "on click button");

				// 发送获取音乐列表的请求
				// 实例化传输对象
				SocketTranEntity msg = new SocketTranEntity();
				msg.setmCommant(GlobalData.SocketTranCommand.COMMAND_REQUSET_MUSIC_LIST);

				SendSocketMessageUtil.getInstance(mActivity).SendMessage(msg,
						et_ip.getText().toString());

			}
		});

		// 设置ListView监听
		lv_music_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 打开确定请求获取对话框
				ShowRequsetDialog(position);
			}
		});
	}

	/**
	 * 弹出是否请求拉取对话框
	 * 
	 */
	private void ShowRequsetDialog(int MusicID) {

		String musicName = AppConstant.MusicPlayData.myMusicList.get(MusicID)
				.getFileName();
		mSelectMusicID = MusicID;

		AlertDialog.Builder builder = new Builder(mActivity);

		// 设置标题
		builder.setTitle("获取音乐");
		// 设置描述信息
		builder.setMessage("是否拉取：" + musicName);

		// 确认
		builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				if (mSelectMusicID < 0)
					return;

				final ArrayList<String> fileName = new ArrayList<String>();
				final ArrayList<String> safeFileName = new ArrayList<String>();

				fileName.add(AppConstant.MusicPlayData.myMusicList.get(
						mSelectMusicID).getFileName());
				Log.d(TAG,
						"send name: "
								+ AppConstant.MusicPlayData.myMusicList.get(
										mSelectMusicID).getFileName());

				safeFileName.add(AppConstant.MusicPlayData.myMusicList.get(
						mSelectMusicID).getFilePath());
				Log.d(TAG,
						"send path: "
								+ AppConstant.MusicPlayData.myMusicList.get(
										mSelectMusicID).getFilePath());

				Thread sendThread = new Thread(new Runnable() {
					@Override
					public void run() {
						Log.d(TAG, "start Thread to send");

						SocketManager.getInstance().SendFile(fileName,
								safeFileName, et_ip.getText().toString(),
								GlobalData.TranPort.SOCKET_FILE_TRANSMIT_PORT);
					}
				});
				sendThread.start();

				// 还原回-1；
				mSelectMusicID = -1;

			}
		});

		// 取消
		builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});

		builder.show();

	}

	// 获取扫描出来的IP地址的接收器
	private class RecvScoketMsgReceiver extends BroadcastReceiver {

		// 自定义一个广播接收器
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "RECV:COMMAND");

			Bundle bundle = intent.getExtras();

			// 获取指令
			int commant = bundle
					.getInt(GlobalData.SocketTranCommand.GET_BUNDLE_COMMANT);

			// 假如是发送音乐列表
			if (commant == GlobalData.SocketTranCommand.COMMAND_SEND_MUSIC_LIST) {
				Log.d(TAG, "RECV:COMMAND_SEND_MUSIC_LIST");

				SocketTranEntity data = (SocketTranEntity) bundle
						.getSerializable(GlobalData.SocketTranCommand.GET_BUNDLE_COMMON_DATA);

				// 把收到的数据显示出来
				mMusicList.clear(); // 清空列表
				for (int i = 0; i < data.getmMusicList().size(); i++) {
					mMusicList.add(data.getmMusicList().get(i).getFileName());
				}

				// 更新数据
				mMusicListAdapter.notifyDataSetChanged();

			}

		}
	}

}
