package com.cvte.lanplayer.view.device;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cvte.lanplayer.GlobalData;
import com.cvte.lanplayer.R;
import com.cvte.lanplayer.entity.SocketTranEntity;
import com.cvte.lanplayer.utils.SendSocketMessageUtil;

/**
 * 与目标IP进行聊天的页面
 * 
 * @author JHYin
 * 
 */
public class LanChatFragment extends Fragment {

	private final String TAG = "LanChatFragment";
	private Activity mActivity;

	private String targetIp = null;

	private Button btn_send_msg;
	private EditText et_send_msg;
	private LinearLayout ll_msg;

	private RecvSocketReceiver mRecvScoketMsgReceiver;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_lan_chat, container,
				false);

		btn_send_msg = (Button) view.findViewById(R.id.btn_send_msg);
		et_send_msg = (EditText) view.findViewById(R.id.et_send_msg);
		ll_msg = (LinearLayout) view.findViewById(R.id.ll_msg);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

		// 通过Bundle获取Activity里面的数据
		Bundle bundle = getArguments();
		targetIp = bundle.getString(GlobalData.GetBundle.GET_IP);

		// 注册接收器
		mRecvScoketMsgReceiver = new RecvSocketReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalData.SocketTranCommand.RECV_SOCKET_FROM_SERVICE_ACTION);
		mActivity.registerReceiver(mRecvScoketMsgReceiver, filter);

		// 设置监听
		SetListener();

	}

	/**
	 * 设置监听
	 */
	private void SetListener() {
		// TODO Auto-generated method stub
		btn_send_msg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Log.d(TAG, "on click button");

				// 实例化传输对象
				SocketTranEntity msg = new SocketTranEntity();
				msg.setmCommant(GlobalData.SocketTranCommand.COMMAND_RECV_MSG);
				msg.setmMessage(et_send_msg.getText().toString());

				// 直接发送消息
				SendSocketMessageUtil.getInstance(mActivity).SendMessage(msg,
						targetIp);
				
				
				// 把发出的数据显示出来
				TextView textView = new TextView(mActivity);
				textView.setText(et_send_msg.getText().toString());
				
				RelativeLayout rl = new RelativeLayout(mActivity);
				rl.addView(textView);
				
				// 使用相对布局
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				// 自己发出的消息靠右
				lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
						RelativeLayout.TRUE);
				lp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
				textView.setLayoutParams(lp);
				
				//加入到消息框里面
				ll_msg.addView(rl);
			}
		});

	}

	// 获取发送过来的文本消息的接收器
	private class RecvSocketReceiver extends BroadcastReceiver {

		// 自定义一个广播接收器
		@Override
		public void onReceive(Context context, Intent intent) {

			Bundle bundle = intent.getExtras();

			// 获取指令
			int commant = bundle
					.getInt(GlobalData.SocketTranCommand.GET_BUNDLE_COMMANT);

			// 根据指令来进行处理
			if (commant == GlobalData.SocketTranCommand.COMMAND_RECV_MSG) {
				String str = bundle
						.getString(GlobalData.SocketTranCommand.GET_BUNDLE_COMMON_DATA);

				// 把收到的数据显示出来
				TextView textView = new TextView(mActivity);
				textView.setText(str);
				
				RelativeLayout rl = new RelativeLayout(mActivity);
				rl.addView(textView);
				
				// 使用相对布局
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				// 对方发来的消息靠左
				lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
						RelativeLayout.TRUE);
				lp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
				textView.setLayoutParams(lp);
				
				//加入到消息框里面
				ll_msg.addView(rl);
				
			}

		}
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();

		// 结束的时候取消注册广播接收器，否则报错
		mActivity.unregisterReceiver(mRecvScoketMsgReceiver);
	}
}
