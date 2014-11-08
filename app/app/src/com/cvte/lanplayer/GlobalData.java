package com.cvte.lanplayer;

public class GlobalData {

	/*
	 * 用于接收和发送LAN UDP socket的 线程的同步锁
	 */
	public static byte[] UDP_SOCKET_LOCK = new byte[0];

	/*
	 * 用于接收和发送LAN TCP socket的 线程的同步锁
	 */
	public static byte[] TCP_SOCKET_LOCK = new byte[0];

	public class TranPort {
		/*
		 * UDP广播的端口
		 */
		public static final int UDP_PORT = 43708;

		/*
		 * Socket通信的端口
		 */
		public static final int SOCKET_TRANSMIT_PORT = 38281;

		/*
		 * Socket文件传输端口
		 */
		public static final int SOCKET_FILE_TRANSMIT_PORT = 38381;

	}

	public class LANScanCtrl {

		/**
		 * 接收LAN扫描
		 */
		/*
		 * 控制扫描启动或停止的接收器
		 */
		public static final String CTRL_LAN_RECV_ACTION = "android.intent.action.ctrl_recv";

		/*
		 * 启动LAN接收
		 */
		public static final int STARE_LAN_RECV = 1;

		/*
		 * 停止LAN接收
		 */
		public static final int STOP_LAN_RECV = 2;

	}

	/**
	 * SOCKET通信指令
	 */
	public class SocketTranCommand {

		/*
		 * 收到简单的文本信息
		 */
		public static final int COMMAND_RECV_MSG = 1;

		/*
		 * 请求获取音乐列表
		 */
		public static final int COMMAND_REQUSET_MUSIC_LIST = 2;

		/*
		 * 发送音乐列表
		 */
		public static final int COMMAND_SEND_MUSIC_LIST = 3;

		/*
		 * 收到LAN扫描的确认包
		 */
		public static final int COMMAND_LAN_ASK = 4;

		/*
		 * 请求获取某个音乐文件
		 */
		public static final int COMMAND_REQUSET_MUSIC_FILE = 5;

		/*
		 * 请求获取接收器的Bundle的指令
		 */
		public static final String GET_BUNDLE_COMMANT = "getcommant";

		/*
		 * 请求获取通用的Bundle里面的数据
		 */
		public static final String GET_BUNDLE_COMMON_DATA = "getdata";

		/*
		 * 负责局域网接收的service向各个组件发送广播的接收器
		 */
		public static final String RECV_SOCKET_FROM_SERVICE_ACTION = "android.intent.action.recv_lan_socket_from_service";

	}

	/**
	 * 其他配置参数
	 * 
	 * @author JHYin
	 * 
	 */
	public class Other {
		/*
		 * 局域网发过来的音乐文件存储路径
		 * 
		 * 注意：加上“sdcard” 注意：windows和linux的下划线方式是不同的
		 */
		public static final String SAVE_LAN_FILE_DIR = "LanDownloads";
	}

	/**
	 * 应用内部的通知消息
	 * 
	 * 
	 * @author JHYin
	 *
	 */
	public class AppInform{
		
		
		/*
		 * 收到更新音乐列表的命令
		 */
		public static final int COMMAND_REFLASH_MUSIC_LIST = 1;
		
		/*
		 * 收到显示TOAST消息的命令
		 */
		public static final int COMMAND_SHOW_TOAST_MSG = 2;
		

		/*
		 * 负责局域网接收的应用内部的通知消息的接收器
		 */
		public static final String RECV_APP_INFORM_ACTION = "android.intent.action.recv_app_inform_action";

	}
	
	
}
