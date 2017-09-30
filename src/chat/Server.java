package chat;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;

public class Server extends Chat {

	private ServerSocket server;

	public Server(int inputPort) throws SocketException {
		super("Server Messenger", "SERVER", inputPort);
	}

	public void startRunning() {
		try {
			server = new ServerSocket(port, 100, getIP());
			while (true) {
				try {
					waitForConnection();
					setupStreams();
					startChat();

				} catch (EOFException e) {
					showMessage("\nServer ended the connection!");
				} finally {
					closeDown();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void waitForConnection() throws IOException {
		showMessage("Waiting for someone to connect...\n");
		connection = server.accept();
		showMessage("Connected to " + connection.getInetAddress().getHostName());
	}

	private void startChat() throws IOException {
		message = "You are now connected!";
		sendMessage(message);
		whileChatting();
	}
}
