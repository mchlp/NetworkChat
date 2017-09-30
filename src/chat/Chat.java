package chat;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import javax.swing.JFrame;

public abstract class Chat extends JFrame {

	public Chat(String name) {
		super(name);
	}

	public InetAddress getIP() throws UnknownHostException, SocketException {
		Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
		while (e.hasMoreElements()) {
			NetworkInterface n = (NetworkInterface) e.nextElement();
			Enumeration<InetAddress> ee = n.getInetAddresses();
			while (ee.hasMoreElements()) {
				InetAddress i = (InetAddress) ee.nextElement();
				if (i.getHostAddress().contains("192")) {
					return i;
				}
			}
		}
		return InetAddress.getLocalHost();
	}
}
