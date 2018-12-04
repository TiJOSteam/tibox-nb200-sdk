
 
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import tibox.NB200;

 
/**
 * NB-IoT UDP 例程
 * 由于电信对数据转发服务器有限制, 请使用中国移动NB-IoT物联卡进行测试
 * @author TiJOS
 *
 */
public class NB200UDPSample {

	public static void main(String[] args) {

	try {
			//启动并注册NB网络
			NB200.networkStartup();
			
			//以下与标准JAVA UDP Socket使用方式一致
		   	System.out.println("UdpDemo start...");
			DatagramSocket udpSocket  = null;
	    	try
	    	{
		    		
	    		//UDP Socket
				udpSocket = new DatagramSocket();
		        String host = "10.111.2.11";
		        int port = 8080;
		        
		        byte [] msg = ("Hello Server").getBytes();
		       
		        DatagramPacket dp = new DatagramPacket(msg, msg.length, InetAddress.getByName(host), port);
		        udpSocket.send(dp);
		        
		        byte [] buffer = new byte[1024];
	        	dp.setData(buffer);
	        	dp.setAddress(null);
		        	
	            udpSocket.receive(dp);
		            
	            String info = new String(dp.getData(), 0, dp.getLength());
	            System.out.println("Received: " + info);
	            System.out.println("Remote :" + dp.getAddress().getHostAddress());
	            
	    	}
	    	catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	    	finally
	    	{
	    		udpSocket.close();
	    	}
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
