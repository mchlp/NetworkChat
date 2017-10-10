package chat;

import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class LogInTest {

	private static String[] logInArray;
	private static boolean server;
	private static String ip = "localhost";
	private static int port = 22;

	public static void main(String[] args) throws SocketException, UnknownHostException {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		LogIn logIn = new LogIn();
		logIn.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		logInArray = logIn.checkForLogin();
		logIn.setVisible(false);

		if (logInArray[0].equals("Server")) {
			server = true;
		} else {
			server = false;
		}
		ip = logInArray[1];
		port = Integer.parseInt(logInArray[2]);

		if (server) {
			Server server = new Server(ip, port);
			server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			server.startRunning();
		} else {
			Client client;
			client = new Client(ip, port);
			client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			client.startRunning();
		}
	}
}
