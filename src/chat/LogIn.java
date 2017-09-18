package chat;

import java.awt.*;
import java.awt.event.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LogIn extends JFrame{
	
	private JPanel btnPanel;
	private JPanel panel;
	private JTextField textIP;
	private JTextField textPort;
	private JLabel labelInfo;
	private JLabel labelIP;
	private JLabel labelPort;
	private JButton button;
	private JRadioButton clientRadioBtn;
	private JRadioButton serverRadioBtn;
	private ButtonGroup buttonGroup;
	private boolean server = false;
	private String[] returnArray = {null, null, null};
	private boolean validSubmit = false;
	
	public LogIn() {
		
		super("Log In");
		
		GridLayout gridLayout = new GridLayout(0, 2);
		panel = new JPanel(gridLayout);
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		add(panel, BorderLayout.CENTER);
		
		labelInfo = new JLabel("Select one of the options and enter connection information.");
		labelInfo.setBorder(new EmptyBorder(10, 10, 0, 10));
		add(labelInfo, BorderLayout.NORTH);
		
		clientRadioBtn = new JRadioButton("Client");
		clientRadioBtn.setActionCommand("Client");
		clientRadioBtn.setSelected(true);
		clientRadioBtn.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				changeServerClient(false);
			}
		});
		panel.add(clientRadioBtn);
		
		serverRadioBtn = new JRadioButton("Server");
		serverRadioBtn.setActionCommand("Server");
		serverRadioBtn.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				changeServerClient(true);
			}
		});
		panel.add(serverRadioBtn);
		
		buttonGroup = new ButtonGroup();
		buttonGroup.add(clientRadioBtn);
		buttonGroup.add(serverRadioBtn);
		
		labelIP = new JLabel("IP Address of the Server: ");
		panel.add(labelIP);
		
		textIP = new JTextField();
		textIP.setEditable(true);
		textIP.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				textPort.requestFocus();
			}
		});
		panel.add(textIP);
		
		labelPort = new JLabel("Port: ");
		panel.add(labelPort);
		
		textPort = new JTextField();
		textPort.setEditable(true);
		textPort.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				button.doClick();
			}
		});
		panel.add(textPort);
		
		btnPanel = new JPanel();
		add(btnPanel, BorderLayout.SOUTH);
		button = new JButton("Log In");
		btnPanel.add(button);
		
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
	}
	
	private void changeServerClient(final boolean tof) {
		server = tof;
		if (server) {
			textIP.setEditable(false);
			try {
				textIP.setText(InetAddress.getLocalHost().getHostAddress());
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		} else {
			textIP.setEditable(true);
			textIP.setText("");
		}
	}
	
	public String[] checkForLogin() {	
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (validateInfo()) {
					labelInfo.setText("Please wait. Connecting...");
					labelInfo.setForeground(Color.BLUE);
					returnArray[0] = server ? "Server" : "Client";
					returnArray[1] = textIP.getText();
					returnArray[2] = textPort.getText();
					validSubmit = true;
				}
			}
		});
		
		while(true) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			//System.out.println(validSubmit);
			if (validSubmit) {
				//dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
				return returnArray;
			}
		}
	}
	
	private boolean validateInfo() {
		if (textIP.getText().equals("")) {
			labelInfo.setText("A valid IP address must be entered.");
			labelInfo.setForeground(Color.RED);
			return false;
		}
		int port;
		try {
			port = Integer.parseInt(textPort.getText());
			if (port > 0 && port < 65536) {
				return true;
			} else {
				labelInfo.setText("Port number must be between 0 and 65536.");
				labelInfo.setForeground(Color.RED);
				return false;
			}
		} catch (NumberFormatException e) {
			labelInfo.setText("Port number must be an integer.");
			labelInfo.setForeground(Color.RED);
			return false;
		}
	}
}
