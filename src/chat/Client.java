package chat;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Client extends Chat {

	private String serverIP;

	public Client(String host, int inputPort) {
		super("Client Messenger", "CLIENT", inputPort);
		serverIP = host;
		setOtherIP(serverIP);
	}

	public void startRunning() {
		try {
			connectToServer();
			setupStreams();
			whileChatting();
		} catch (EOFException e) {
			showMessage("\nClient terminated the connection.");
		} catch (UnknownHostException e) {
			JLabel errorMsgLabel = new JLabel("<html>Unable to connect to the IP address and port you entered.<br>"
					+ "Ensure that you have entered a valid IP address and port.<br>"
					+ "Please reopen the program and try again.</html>", JLabel.CENTER);
			JOptionPane.showMessageDialog(this, errorMsgLabel, "Cannot Connect to IP Address",
					JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeDown();
		}
	}

	private void connectToServer() throws IOException {
		showMessage("Attempting connection...\n");
		System.out.println(serverIP + " " + port);
		connection = new Socket(serverIP, port);
		showMessage("Connected to: " + connection.getInetAddress().getHostName());
	}
}
