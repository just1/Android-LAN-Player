package com.cvte.lanplayer;

public class GlobalData {

	/*
	 * UDP广播的端口
	 */
	public static final int UDP_PORT = 43718;

	/*
	 * Socket通信的端口
	 */
	public static final int Socket_PORT = 38281;

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
	public static final String GET_SCAN_DATA_ACTION = "android.intent.action.get_scan_data";

}
