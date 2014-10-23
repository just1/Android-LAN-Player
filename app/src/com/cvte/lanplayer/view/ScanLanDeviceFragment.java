package com.cvte.lanplayer.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cvte.lanplayer.R;
import com.cvte.lanplayer.adapter.IpListAdapter;

public class ScanLanDeviceFragment extends Fragment {

	private final String TAG = "ScanLanDevice";

	// 通信秘钥
	private final String KEY = "welcome to cvte";

	// 本机IP
	private String mLocalIp = null;
	// 本机IP头，如：192.168.1
	private String mIpAddressHead = null;
	// 扫描出来的IP列表
	private List<String> mIpList = new ArrayList<String>();
	// 默认端口号
	private final int port = 9598;

	private Activity activity;

	// 控件
	// private TextView tv_ip;
	private Button btn_scan;
	private ListView lv_iplist;

	private IpListAdapter mIpList_adapter;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_scan_landevice,
				container, false);

		// tv_ip = (TextView) view.findViewById(R.id.tv_ip);
		btn_scan = (Button) view.findViewById(R.id.btn_scan);
		lv_iplist = (ListView) view.findViewById(R.id.lv_iplist);

		btn_scan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ScanLanDevice();
			}
		});

		mIpList_adapter = new IpListAdapter(mIpList, activity);
		lv_iplist.setAdapter(mIpList_adapter);

		mLocalIp = getIpAddress();
		mIpAddressHead = getIpAddressHead();
		Log.d(TAG, mLocalIp);

		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

	}

	/**
	 * 多线程扫描局域网里面的设备
	 */
	static int i;

	private void ScanLanDevice() {
		mIpList.clear();
		// 扫描局域网里面的IP段-开了255个线程进行扫描
		for (i = 1; i < 255;) {
			new Thread() {
				@Override
				public void run() {

					synchronized (this) {
						String ipAddress = mIpAddressHead + String.valueOf(i);
						i++;

						try {
							Socket socket = new Socket(ipAddress, port);

							// 读取通信秘钥数据
							// 将Socket对应的输入流包装成BufferedReader
							BufferedReader br = new BufferedReader(
									new InputStreamReader(
											socket.getInputStream()));
							// 进行普通IO操作
							String line = br.readLine();
							if (line.equals(KEY)) { // 判断是否符合通信秘钥
								if (!ipAddress.equals(mLocalIp)) { // 判断避免本机IP
									for (int j = 0; j < mIpList.size(); j++) {
										// 如果本机已经扫描过该IP，则不用再加入
										if (mIpList.get(j).equals(ipAddress)) {
											socket.close();
											return;
										}
									}
									mIpList.add(ipAddress);
									// 不能在线程里面更新UI组件
									// tv_ip.setText(tv_ip.getText() +
									// "  172.18.54.68");
									Log.d(TAG, ipAddress);

									handler.sendEmptyMessage(123);
								}

							}

							socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
		}
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			// 更新数据
			mIpList_adapter.notifyDataSetChanged();
		}
	};

	/**
	 * 获取本机的IP地址
	 */
	private String getIpAddress() {
		WifiManager wifiManager = (WifiManager) activity
				.getSystemService(activity.WIFI_SERVICE);

		WifiInfo wifiInfo = wifiManager.getConnectionInfo();

		int ipAddress = wifiInfo.getIpAddress();
		// Log.d("TAG","IP:"+ String.valueOf(ipAddress));

		return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
				+ (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));

	}

	/**
	 * 获取本机的IP地址的头，如192.168.1.
	 */
	private String getIpAddressHead() {
		WifiManager wifiManager = (WifiManager) activity
				.getSystemService(activity.WIFI_SERVICE);

		WifiInfo wifiInfo = wifiManager.getConnectionInfo();

		int ipAddress = wifiInfo.getIpAddress();
		// Log.d("TAG","IP:"+ String.valueOf(ipAddress));

		return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
				+ (ipAddress >> 16 & 0xff) + ".");

	}
}
