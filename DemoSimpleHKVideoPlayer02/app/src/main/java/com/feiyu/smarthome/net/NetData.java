package com.feiyu.smarthome.net;


public class NetData {

	//用于各种电器发送指令的ip和iport
	public static String ip="192.168.1.250";//连接峰哥那边的Ip
	public static int iport=7001;//连接峰哥那边的端口
	
	//用于连接视频监控的那块开发板的ip和iport
	public static String ip1="192.168.1.150";
	public static int iport1=8000;
	
	//灯发送的指令 开与关
	public static byte[] lamp_open= {0X7E,0X01,(byte)0XFF, 0X26 ,0X01, (byte)0XFF ,0X01 ,0X01 ,0X01 ,0X7E};//开
	//public static byte[] lamp_open= {0X7E, 0X01, (byte)0XFF, 0X04, 0X01, 0X0F, 0X01, 0X70, 0X01, 0X7E};//开
	public static byte[] lamp_close= {0X7E,0X01,(byte)0XFF, 0X26 ,0X01, 0X00 ,0X01 ,0X01 ,0X01 ,0X7E};//关
	
	//电视的指令 开 关 频道+ 频道- 声音+ 声音-
	public static byte[] tv_open= {0X7E,0X01,(byte)0XFF, 0X23 ,0X01, (byte)0XFF ,0X01 ,0X01 ,0X01 ,0X7E};//开
	public static byte[] tv_close= {0X7E,0X01,(byte)0XFF, 0X23 ,0X01, 0X00 ,0X01 ,0X01 ,0X01 ,0X7E};//关
	public static byte[] tv_channelAdd= {0X7E,0X01,(byte)0XFF, 0X23 ,0X01, 0X01 ,0X01 ,0X01 ,0X01 ,0X7E};//频道+
	public static byte[] tv_channellMinus= {0X7E,0X01,(byte)0XFF, 0X23 ,0X01, 0X02 ,0X01 ,0X01 ,0X01 ,0X7E};//频道-
	public static byte[] tv_voiceAdd= {0X7E,0X01,(byte)0XFF, 0X23 ,0X01, 0X03 ,0X01 ,0X01 ,0X01 ,0X7E};//声音+
	public static byte[] tv_voiceMinus= {0X7E,0X01,(byte)0XFF, 0X23 ,0X01, 0X04 ,0X01 ,0X01 ,0X01 ,0X7E};//声音-
}
