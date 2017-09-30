package chat;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class Client extends Chat {

	private JLabel infoLabel;
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message;
	private String serverIP;
	private Socket connection;
	private int port = 6789;

	public Client(String host, int inputPort) {
		super("Client Messenger");
		serverIP = host;
		port = inputPort;
		try {
			infoLabel = new JLabel(
					"My IP Address: " + getIP().toString() + "  |  Server IP Address: Connecting...  |  Port: " + port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(infoLabel, BorderLayout.NORTH);
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendData(e.getActionCommand());
				userText.setText("");
			}
		});
		userText.setBorder(new EmptyBorder(10, 10, 10, 10));
		add(userText, BorderLayout.SOUTH);
		chatWindow = new JTextArea();
		chatWindow.setEditable(false);
		chatWindow.setBorder(new EmptyBorder(10, 10, 10, 10));
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setLocationRelativeTo(null);
		setSize(600, 600);
		setVisible(true);
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

	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\nInput and output streams are now set up!\n");
		try {
			infoLabel.setText("My IP Address: " + getIP().getHostAddress() + "  |  Server IP Address: "
					+ connection.getInetAddress().getHostAddress() + "  |  Port: " + port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	private void whileChatting() throws IOException {
		ableToType(true);
		do {
			try {
				message = (String) input.readObject();
				showMessage("\n" + message);
			} catch (ClassNotFoundException e) {
				showMessage("\nUnable to recognize incoming message.");
			}
		} while (!message.equals("SERVER: END"));
	}

	private void closeDown() {
		showMessage("\nClosing connections...\n");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendData(String message) {
		try {
			output.writeObject("CLIENT: " + message);
			output.flush();
			showMessage("\nCLIENT: " + message);
		} catch (IOException e) {
			chatWindow.append("\\nError - Unable to Send Message");
		}
	}

	private void showMessage(final String message) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				chatWindow.append(message);
			}
		});
	}

	private void ableToType(final boolean tof) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				userText.setEditable(tof);
			}
		});
	}
}
