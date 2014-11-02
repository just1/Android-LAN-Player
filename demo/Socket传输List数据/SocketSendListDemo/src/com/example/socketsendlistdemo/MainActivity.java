package com.example.socketsendlistdemo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

	private static final String TAG = "SocketSendListDemo";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		new Thread() {
			public void run() {

				try {
					// 封装一个对象实例
					SongList city = new SongList();
					List list = new ArrayList();
					list.add("北京");
					list.add("上海");
					list.add("天津");
					list.add("重庆");
					city.SetSongList(list);
					// 连接到服务器端
					Socket socketConnection = new Socket("192.168.159.4", 6688);
					// 使用ObjectOutputStream和ObjectInputStream进行对象数据传输
					ObjectOutputStream out = new ObjectOutputStream(
							socketConnection.getOutputStream());
					ObjectInputStream in = new ObjectInputStream(
							socketConnection.getInputStream());
					// 将客户端的对象数据流输出到服务器端去
					out.writeObject(city);
					out.flush();
					// 读取服务器端返回的对象数据流
					SongList cityBack = (SongList) in.readObject();
					List backList = cityBack.GetSongList();
					for (int i = 0; i < backList.size(); i++) {

						Log.d(TAG, "客户端得到返回城市数据：" + backList.get(i).toString());

					}
					out.close();
					in.close();

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			};
		}.start();

	}

}
