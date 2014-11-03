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
		 * Socket扫描用的的端口
		 */
		public static final int SOCKET_PORT = 8080;

		/*
		 * Socket通信的端口
		 */
		public static final int SOCKET_TRANSMIT_PORT = 38281;
	}

	public class LANScanCtrl {
		/**
		 * 开始LAN扫描
		 */

		/*
		 * 控制扫描启动或停止的接收器
		 */
		public static final String CTRL_LAN_SCAN_ACTION = "android.intent.action.ctrl_scan";

		/*
		 * 启动扫描
		 */
		public static final int STARE_SCAN = 1;

		/*
		 * 停止扫描
		 */
		public static final int STOP_SCAN = 2;

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

}
