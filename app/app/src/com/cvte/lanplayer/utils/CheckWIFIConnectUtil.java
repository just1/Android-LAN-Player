package com.cvte.lanplayer.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


/**
 * 检查WIFI是否连接
 * 
 * @author JHYin
 *
 */
public class CheckWIFIConnectUtil {
	private final static String TAG = "CheckWIFIConnectUtil";
	
	
	//是否连接WIFI
    public static boolean isWifiConnected(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(wifiNetworkInfo.isConnected())
        {
        	Log.d(TAG, "WIFI已连接");
        	
            return true ;
        }
     
        Log.d(TAG, "WIFI未连接");
        
        return false ;
    }
}
