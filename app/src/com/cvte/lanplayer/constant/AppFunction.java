package com.cvte.lanplayer.constant;

import java.util.Random;

import android.content.Context;
import android.widget.Toast;

public class AppFunction {
	
	/*
	 * 将ms转换为分秒时间显示函数
	 */
	public static String ShowTime(int time)
	{
		//将ms转换为s
		time /=1000;
		//求分
		int minute = time / 60;
		//求秒
		int second = time % 60;
		return String.format("%02d:%02d", minute,second);
	}
	
	/*
	 * 产生随机数      
	 * num最大范围   
	 * index固定值（产生的随机数不能等于index）
	 */
	public static int GenerateRandom(int num, int index)
	{
		Random ran = new Random();
		int ranNum = ran.nextInt(num);
		if(num > 0)
		{
			if(ranNum == index)
			{
				ranNum = GenerateRandom(num,index);
			}
		}
		return ranNum;
	}
	
	/*
	 * Toast显示
	 * 
	 */
	public static void DisplayToast(Context context, String str)
	{
		Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	}
}
