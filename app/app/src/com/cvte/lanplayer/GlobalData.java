package com.cvte.lanplayer;

public class GlobalData {

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

	/*
	 * 控制扫描启动或停止的接收器
	 */
	public static final String CTRL_SCAN_ACTION = "android.intent.action.ctrl_scan";

	/*
	 * 控制扫描启动或停止的接收器
	 */
	public static final String CTRL_RECV_ACTION = "android.intent.action.ctrl_recv";

	/*
	 * 启动扫描
	 */
	public static final int STARE_SCAN = 1;

	/*
	 * 停止扫描
	 */
	public static final int STOP_SCAN = 2;

	/*
	 * 启动LAN接收
	 */
	public static final int STARE_LAN_RECV = 1;

	/*
	 * 停止LAN接收
	 */
	public static final int STOP_LAN_RECV = 2;

	/*
	 * 返回扫描结果的接收器
	 */
	public static final String GET_SCAN_DATA_ACTION = "android.intent.action.get_scan_data";

	/*
	 * 局域网其他设备扫描到本机时候的接收器
	 */
	public static final String IS_SCANED_ACTION = "android.intent.action.is_scaned";

	/*
	 * 局域网内收到其他设备消息的接收器
	 */
	public static final String RECV_LAN_SOCKET_MSG_ACTION = "android.intent.action.recv_lan_socket_msg";

	/*
	 * 负责局域网接收的service向各个组件发送广播的接收器
	 */
	public static final String RECV_SOCKET_FROM_SERVICE_ACTION = "android.intent.action.recv_lan_socket_from_service";

	/*
	 * 收到简单的文本信息
	 */
	public static final String RECV_SCAN = "recv_scan";

	/*
	 * 收到简单的文本信息
	 */
	public static final int RECV_MSG = 1;

	/*
	 * 请求获取音乐列表
	 */
	public static final int REQUSET_MUSIC_LIST = 2;

	/*
	 * 请求获取接收器的指令
	 */
	public static final String GET_BUNDLE_COMMANT = "commant";

	/*
	 * 请求获取Bundle里面的数据
	 */
	public static final String GET_BUNDLE_DATA = "getdata";

	/*
	 * 发送文本消息命令：
	 */
	public static final String COMMAND_SEND_MSG = "msg";

}
