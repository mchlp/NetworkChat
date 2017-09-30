package chat;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Server extends JFrame {
	
	private JLabel infoLabel;
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	private int port = 22;
	
	
	public Server(int inputPort) throws SocketException {
		super("Server Messenger");
		port = inputPort;
		
		try {
			infoLabel = new JLabel("My IP Address: " + getIP().getHostAddress() + "  |  Client IP Address: Waiting...  |  Port: " + port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(infoLabel, BorderLayout.NORTH);
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						sendMessage(event.getActionCommand());
						userText.setText("");
					}
				}
		);
		userText.setBorder(new EmptyBorder(10, 10, 10, 10));
		add(userText, BorderLayout.SOUTH);
		chatWindow = new JTextArea();
		chatWindow.setEditable(false);
		chatWindow.setBorder(new EmptyBorder(10, 10, 10, 10));
		add(new JScrollPane(chatWindow));
		setLocationRelativeTo(null);
		setSize(600, 600);
		setVisible(true);
	}
	
	public void startRunning() {
		try {
			server = new ServerSocket(port, 100, getIP());
			while(true) {
				try {
					waitForConnection();
					setupStreams();
					whileChatting();
					
				} catch(EOFException e) {
					showMessage("\nServer ended the connection!");
				} finally {
					closeDown();
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void waitForConnection() throws IOException {
		showMessage("Waiting for someone to connect...\n");
		connection = server.accept();
		showMessage("Connected to " + connection.getInetAddress().getHostName());
	}
	
	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\nInput and output streams are now set up!\n");
		try {
			infoLabel.setText("My IP Address: " + getIP().getHostAddress() + "  |  Client IP Address: " + connection.getInetAddress().getHostAddress() + "  |  Port: " + port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	private void whileChatting() throws IOException {
		String message = "You are now connected!";
		sendMessage(message);
		ableToType(true);
		do {
			try {
				message = (String) input.readObject();
				showMessage("\n" + message);
			} catch(ClassNotFoundException e) {
				showMessage("\nUnable to recognize incoming message.");
			}
		} while(!message.equals("CLIENT: END"));
	}
	
	private void closeDown() throws IOException {
		showMessage("\nClosing connections...\n");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendMessage(String message) {
		try {
			output.writeObject("SERVER: " + message);
			output.flush();
			showMessage("\nSERVER: " + message);
		} catch (IOException e) {
			chatWindow.append("\nError - Unable to Send Message");
		}
	}
	
	private void showMessage(final String text) {
		SwingUtilities.invokeLater(
			new Runnable() {
				@Override
				public void run() {
					chatWindow.append(text);
				}
			}
		);
	}
	
	private void ableToType(final boolean canType) {
		SwingUtilities.invokeLater(
			new Runnable() {	
				@Override
				public void run() {
					userText.setEditable(canType);
				}
			}
		);
	}
	
	public static InetAddress getIP() throws SocketException, UnknownHostException {	
		InetAddress ip = InetAddress.getByName("192.168.1.1");
		Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
		while (e.hasMoreElements()) {
			NetworkInterface n = (NetworkInterface) e.nextElement();
			Enumeration<InetAddress> ee = n.getInetAddresses();
			while (ee.hasMoreElements()) {
				InetAddress i = (InetAddress) ee.nextElement();
				if (i.getHostAddress().contains("192")) {
					ip = i;
				}
			}
		}
		return ip;	
	}
}


