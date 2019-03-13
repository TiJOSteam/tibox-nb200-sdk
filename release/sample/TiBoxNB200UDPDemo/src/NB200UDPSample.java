
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import tibox.NB200;
import tijos.framework.component.serialport.TiSerialPort;
import tijos.framework.devicecenter.TiUART;
import tijos.framework.util.Delay;

/**
 * 232采集线程类
 *
 * @author tijos
 */
class SerialPort2UDP extends Thread {

    String host;
    int port;
    DatagramSocket udpSocket;
    TiSerialPort rs232;

    public SerialPort2UDP(String host, int port, DatagramSocket udpSocket, TiSerialPort rs232) {

        this.host = host;
        this.port = port;
        this.udpSocket = udpSocket;
        this.rs232 = rs232;
    }

    @Override
    public void run() {

        while (true) {
            try {
                byte[] buff = rs232.read(50);
                if (buff != null) {
                    System.out.println("len " + buff.length + " data:" + new String(buff));
                    DatagramPacket dp = new DatagramPacket(buff, buff.length, InetAddress.getByName(host), port);
                    udpSocket.send(dp);
                    Delay.msDelay(10);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}

/**
 * UDP采集线程类
 *
 * @author tijos
 */
class UDP2SerialPort extends Thread {

    String host;
    int port;

    DatagramSocket udpSocket;
    TiSerialPort rs232;

    public UDP2SerialPort(String host, int port, DatagramSocket udpSocket, TiSerialPort rs232) {

        this.host = host;
        this.port = port;
        this.udpSocket = udpSocket;
        this.rs232 = rs232;
    }

    @Override
    public void run() {
        byte[] msg = new byte[1024];
        try {
            DatagramPacket dp = new DatagramPacket(msg, msg.length, InetAddress.getByName(host), port);
            while (true) {
                try {
                    udpSocket.receive(dp);
                    System.out.println("udp data len " + dp.getLength());
                    rs232.write(dp.getData(), 0, dp.getLength());
                    Delay.msDelay(10);

                    dp.setLength(msg.length);
                } catch (Exception e) {
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

/**
 * NB-IoT UDP 串号/UDP透传例程
 *
 * @author TiJOS
 */
public class NB200UDPSample {

    public static void main(String[] args) {

        try {

            NB200.startFlashLED();
            // 启动并注册NB网络
            NB200.networkStartup();

            NB200.stopFlashLED();
            NB200.turnOnLED();

            // 以下与标准JAVA UDP Socket使用方式一致
            System.out.println("Serial2UDP start...");

            String host = "10.11.123.4";
            int port = 1234;

            DatagramSocket udpSocket = new DatagramSocket();
            TiSerialPort rs232 = NB200.getRS232(9600, 8, 1, TiUART.PARITY_NONE);

            // 创建UDP采集线程对象
            Thread threadUDP = new UDP2SerialPort(host, port, udpSocket, rs232);

            // 创建232采集线程对象
            Thread thread232 = new SerialPort2UDP(host, port, udpSocket, rs232);

            // 启动两个线程
            threadUDP.start();
            thread232.start();

            while (true) {
                Delay.msDelay(1000);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
