package chat;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public abstract class Chat extends JFrame {

	protected JLabel infoLabel;
	protected JTextArea chatWindow;
	protected JTextField userText;
	protected ObjectOutputStream output;
	protected ObjectInputStream input;
	protected String message;
	protected String name;
	protected String otherName;
	protected Socket connection;
	protected int port;

	public Chat(String title) {
		super(title);
	}

	public Chat(String title, String inputName, int inputPort) {
		super(title);
		port = inputPort;
		name = inputName;
		if (name == "CLIENT") {
			otherName = "Server";
		} else {
			otherName = "Client";
		}
		try {
			infoLabel = new JLabel("My IP Address: " + getIP().getHostAddress() + "  |  " + otherName
					+ " IP Address: Waiting...  |  Port: " + port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(infoLabel, BorderLayout.NORTH);
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage(e.getActionCommand());
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

	protected void whileChatting() throws IOException {
		message = "";
		ableToType(true);
		do {
			receiveMessage();
		} while (!message.equals("CLIENT: END"));
	}

	protected void receiveMessage() throws IOException {
		try {
			String message = (String) input.readObject();
			showMessage("\n" + message);
		} catch (ClassNotFoundException e) {
			showMessage("\nUnable to recognize incoming message.");
		}
	}

	protected void showMessage(final String message) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				chatWindow.append(message);
			}
		});
	}

	protected void ableToType(final boolean canType) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				userText.setEditable(canType);
			}
		});
	}

	protected void sendMessage(String message) {
		try {
			output.writeObject(name + ": " + message);
			output.flush();
			showMessage("\n" + name + ": " + message);
		} catch (IOException e) {
			chatWindow.append("\nError - Unable to Send Message");
		}
	}

	protected void closeDown() {
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

	protected void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\nInput and output streams are now set up!\n");
		try {
			infoLabel.setText("My IP Address: " + getIP().getHostAddress() + "  |  " + otherName + " IP Address: "
					+ connection.getInetAddress().getHostAddress() + "  |  Port: " + port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}
