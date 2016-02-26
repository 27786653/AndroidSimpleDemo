package com.feiyu.smarthome.net;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetWork {
	
	public static boolean sendMessage(byte[] b)
	{
		boolean flag=true;
		try {
		//	ConnectCheckThread.check();
			Socket socket=new Socket(NetData.ip,NetData.iport);
			OutputStream out=socket.getOutputStream();
			out.write(b);
			out.flush();
			System.out.println("S: sending: '" +  "'");
			for (int i=0;i<b.length; i++){
				System.out.println(b[i]);
			}
			out.close();
			socket.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			flag=false;
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			flag=false;
			e.printStackTrace();
		}
		return flag;
	}
}
