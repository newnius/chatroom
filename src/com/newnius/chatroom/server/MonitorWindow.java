package com.newnius.chatroom.server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import com.newnius.chatroom.util.Message;
import com.newnius.util.CRObject;

public class MonitorWindow extends JFrame{
	private Dimension windowSize = new Dimension(800, 600);
	private JPanel membersPanel;
	private JScrollPane leftPanel;
	private JScrollPane messagesScrollPanel;
	private JPanel messagesPanel;
	private JPanel inputPanel;
	private JScrollPane inputTextScrollPanel;
	private JTextArea textArea;
	private JButton button_send;
	private int margin = 40;// this is used to make scroll bar disappear when
							// not necessary
	private ArrayList<Message> messages;
	private HashMap<String, JLabel> userLabels;

	private boolean isOdd = false;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2011098747377530563L;

	public static void main(String[] args) {
		MonitorWindow monitorWindow = new MonitorWindow();
		CRObject config = new CRObject();
		config.set("port", "1888");
		new Thread(new C2SServer(monitorWindow, config)).start();
		
	}

	public MonitorWindow() {
		messages = new ArrayList<>();
		userLabels = new HashMap<>();
		initGUI();
	}

	public void initGUI() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBackground(Color.gray);
		setPreferredSize(windowSize);
		setTitle("chatroom");
		setLayout(new BorderLayout());

		JPanel panel1 = new JPanel();
		panel1.setLayout(new FlowLayout());
		membersPanel = new JPanel();
		GridLayout membersLayout = new GridLayout(0, 1);
		membersPanel.setLayout(membersLayout);
		panel1.add(membersPanel);
		leftPanel = new JScrollPane(panel1);
		leftPanel.setPreferredSize(new Dimension(windowSize.width * 1 / 5, windowSize.height));

		add(leftPanel, BorderLayout.WEST);

		messagesPanel = new JPanel();

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());

		GridLayout messagesLayout = new GridLayout(0, 1);
		messagesPanel.setLayout(messagesLayout);

		panel.add(messagesPanel);
		messagesScrollPanel = new JScrollPane(panel);

		inputPanel = new JPanel();
		inputPanel.setLayout(new BorderLayout());

		textArea = new JTextArea();
		textArea.setPreferredSize(
				new Dimension(windowSize.width * 4 / 5 * 4 / 5 - margin - margin, windowSize.height * 1 / 5));
		textArea.setFont(new Font("Serif", Font.PLAIN, 20));

		DefaultCaret caret = (DefaultCaret) textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		inputTextScrollPanel = new JScrollPane(textArea);

		button_send = new JButton("send");
		inputPanel.add(inputTextScrollPanel, BorderLayout.WEST);
		inputPanel.add(button_send, BorderLayout.CENTER);

		add(messagesScrollPanel, BorderLayout.CENTER);

		add(inputPanel, BorderLayout.SOUTH);

		setVisible(true);
		pack();
	}

	public void newMessage(Message message) {
		messages.add(message);
		String str = "";

		JPanel messagePanel = new JPanel();

		str += message.getMsgType() == Message.MESSAGE_TYPE_NORMAL ? message.getUsername() : "系统通知";
		str += "：";
		str += message.getMsgContent();
		JLabel label = new JLabel(str);
		label.setPreferredSize(new Dimension(windowSize.width * 4 / 5 - margin, 50));

		isOdd = !isOdd;
		messagePanel.setBackground(isOdd ? Color.lightGray : Color.white);
		messagePanel.add(label);
		messagesPanel.add(messagePanel);
		messagesPanel.updateUI();
	}

	public void memberJoin(CRObject user) {
		if (userLabels.containsKey(user.get("username"))) {
			JLabel label = userLabels.get(user.get("username"));
			membersPanel.remove(label);
		}

		JLabel label = new JLabel(user.get("username"));
		label.setPreferredSize(new Dimension(windowSize.width * 1 / 5 - margin, 50));
		membersPanel.add(label);
		membersPanel.updateUI();
		userLabels.put(user.get("username"), label);
	}

	public void memberQuit(CRObject user) {
		if (userLabels.containsKey(user.get("username"))) {
			JLabel label = userLabels.get(user.get("username"));
			membersPanel.remove(label);
			membersPanel.updateUI();
			userLabels.remove(user.get("username"));
		}
	}

}
