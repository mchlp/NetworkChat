package chat;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Enumeration;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public abstract class Chat extends JFrame {

	private final int DING_TIME_DELAY = 1;
	private final String HELP_COM = "/?";
	private final String URL_COM = "/url:";
	private final String NAME_COM = "/name:";

	private Clip ding;

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
	protected long lastDing;

	public Chat(String title) {
		super(title);
	}

	public Chat(String title, String inputName, int inputPort) {
		super(title);
		port = inputPort;
		name = inputName;
		lastDing = 0;
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
		chatWindow.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		chatWindow.setEditable(false);
		chatWindow.setBorder(new EmptyBorder(10, 10, 10, 10));
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setLocationRelativeTo(null);
		setSize(600, 600);
		setVisible(true);

		try {
			AudioInputStream audio = AudioSystem
					.getAudioInputStream(this.getClass().getClassLoader().getResource("ding.wav"));
			ding = AudioSystem.getClip();
			ding.open(audio);
		} catch (UnsupportedAudioFileException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (LineUnavailableException e1) {
			e1.printStackTrace();
		}
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
		if ((System.currentTimeMillis() - lastDing) > (DING_TIME_DELAY * 1000)) {
			lastDing = System.currentTimeMillis();
			ding.setFramePosition(0);
			ding.start();
		}
		try {
			Object inputObj = input.readObject();
			if (inputObj instanceof URI) {
				int result = JOptionPane.showConfirmDialog(this,
						otherName + " sent a link: " + inputObj.toString() + " Would you like to open it?",
						otherName + " sent a link!", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					Desktop.getDesktop().browse((URI) inputObj);
				}
			} else if (inputObj instanceof Name) {
				otherName = ((Name) inputObj).getName();
			} else {
				message = (String) inputObj;
				showMessage("\n" + message);
			}
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
		sendTryLoop: try {
			if (message.equals(HELP_COM)) {
				showMessage("\nHelp Text");
				break sendTryLoop;
			} else if (message.startsWith(URL_COM)) {
				message = message.replace(URL_COM, "");
				if (!(message.startsWith("http://"))) {
					message = "http://" + message;
				}
				URI link = URI.create(message);
				try {
					link.toURL();
					message = "Sent a link.";
					output.writeObject(name + ": " + message);
					output.writeObject(link);
				} catch (MalformedURLException | IllegalArgumentException e) {
					message = "ERROR - Invalid URL";
				}
			} else if (message.startsWith(NAME_COM)) {
				name = message.replace(NAME_COM, "").toUpperCase();
				output.writeObject(new Name(name));
				break sendTryLoop;
			} else {
				output.writeObject(name + ": " + message);
			}
			output.flush();
			showMessage("\n" + name + ": " + message);

		} catch (IOException e) {
			chatWindow.append("\nError - Unable to Send Message");
		} catch (Exception e) {
			e.printStackTrace();
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

	protected String getAllIP() throws UnknownHostException, SocketException {
		String allIP = "<html>";
		Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
		while (e.hasMoreElements()) {
			NetworkInterface n = (NetworkInterface) e.nextElement();
			Enumeration<InetAddress> ee = n.getInetAddresses();
			while (ee.hasMoreElements()) {
				InetAddress i = (InetAddress) ee.nextElement();
				allIP = allIP.concat(i.getHostAddress() + "<br>");
			}
		}
		allIP = allIP.concat("</html>");
		return allIP;
	}
}
