package com.newnius.chatroom.client;

import com.newnius.chatroom.util.Message;
import com.newnius.util.CRObject;

public class Main {
	public static void main(String[] args) {

		try {
			CRObject config = new CRObject();
			config.set("host", "127.0.0.1");
			config.set("port", "1888");
			Server server = new Server(config);

			CRObject user = new CRObject();
			user.set("username", "newnius");
			server.login(user);
			Message message = new Message("Hello world!", 1);
			server.sendMsg(message);
			server.quit(user);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
