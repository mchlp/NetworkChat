package chat;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class Client extends Chat {

	private String serverIP;

	public Client(String host, int inputPort) {
		super("Client Messenger", "CLIENT", inputPort);
		serverIP = host;
	}

	public void startRunning() {
		try {
			connectToServer();
			setupStreams();
			whileChatting();
		} catch (EOFException e) {
			showMessage("\nClient terminated the connection.");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeDown();
		}
	}

	private void connectToServer() throws IOException {
		showMessage("Attempting connection...\n");
		connection = new Socket(serverIP, port);
		showMessage("Connected to: " + connection.getInetAddress().getHostName());
	}
}
