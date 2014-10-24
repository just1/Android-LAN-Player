package com.cvte.lanplayer;

public class GlobalData {

	/*
	 * UDP广播的端口
	 */
	public static final int UDP_PORT = 43718;

	
	
	/*
	 * Socket扫描用的的端口
	 */
	public static final int SOCKET_PORT = 38381;
	
	/*
	 * Socket通信的端口
	 */
	public static final int SOCKET_TRANSMIT_PORT = 38281;

	/*
	 * 控制扫描启动或停止的接收器
	 */
	public static final String CTRL_SCAN_ACTION = "android.intent.action.ctrl_scan";

	/*
	 * 启动扫描
	 */
	public static final int STARE_SCAN = 1;

	/*
	 * 停止扫描
	 */
	public static final int STOP_SCAN = 2;

	/*
	 * 返回扫描结果的接收器
	 */
	public static final String GET_SCAN_DATA_ACTION = 
			"android.intent.action.get_scan_data";

	/*
	 * 局域网其他设备扫描到本机时候的接收器
	 */
	public static final String IS_SCANED_ACTION = 
			"android.intent.action.is_scaned";

	/*
	 * 局域网内收到其他设备消息的接收器
	 */
	public static final String RECV_LAN_SOCKET_MSG_ACTION = 
			"android.intent.action.recv_lan_socket_msg";

}
