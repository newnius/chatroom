package com.newnius.chatroom.client;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import com.newnius.util.CRLogger;
import com.newnius.util.CRObject;

/**
 *
 * @author Newnius
 */
class Communicator {

	private Socket socket = null;
	private PrintWriter writer = null;
	private BufferedReader reader = null;
	private CRLogger logger = CRLogger.getLogger(Communicator.class);
	
	private String host;
	private int port;
	
	public Communicator(CRObject config){
		this.host = config.get("host");
		this.port = config.getInt("port");
	}

	private boolean init() {
		try {
            logger.info("init()");
			socket = new Socket(host, port);
			writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return true;
		} catch (Exception ex) {
            socket = null;
            writer = null;
            reader = null;
			logger.error("Init fail.");
            return false;
		}
	}

	public synchronized String send(String content) {
		if (socket == null || socket.isClosed()) {
			if(!init())
                return null;
		}
		try {
            logger.info("Sent: " + content);
			writer.println(content);
			String str = reader.readLine();
			if(str==null)
				close();
			logger.info("Received: " + str);
			return str;
		} catch (Exception ex) {
            close();
            ex.printStackTrace();
			return null;
		}
	}


	public void close() {
		try {
            logger.info("Close socket");
            socket.close();
            writer.close();
			reader.close();

			writer = null;
			reader = null;
			socket = null;
		} catch (Exception ex) {
            logger.error("Error occur during close.");
		}
	}

}
