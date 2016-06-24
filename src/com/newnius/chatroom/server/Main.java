package com.newnius.chatroom.server;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
	public static void main(String[] args) {
//		if (args.length < 1) {
//			Logger.getLogger("chatroom").log(Level.WARNING, "Error: Port not specified.");
//			System.exit(1);
//		}
//		int port = Integer.parseInt(args[0]);
		
		int port = 1888;

		new Thread(new C2SServer(port)).start();
	}
}
