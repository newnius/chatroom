/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newnius.chatroom.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Set;

import com.google.gson.Gson;
import com.newnius.util.CRLogger;
import com.newnius.util.CRMsg;
import com.newnius.util.CRObject;

/**
 * 
 * @author Newnius
 */
public class C2SServer implements Runnable {

	private ServerSocket server;
	private CRLogger logger = CRLogger.getLogger(this.getClass().getName());
	private static HashMap<String, C2SStaff> clients = new HashMap<String, C2SStaff>();

	private int port;
	private boolean isRun = true;

	public C2SServer(int port) {
		this.port = port;
	}

	public void terminate() {
		isRun = false;
	}
	
	public boolean isRun(){
		return isRun;
	}

	@Override
	public void run() {
		try {
			server = new ServerSocket(port);// 创建服务器套接字
			logger.info("C2SServer opened, waiting for client.");
			while (isRun) {
				Socket socket = server.accept();// 等待客户端连接
				new C2SStaff(socket, this).start();
			}
			server.close();
		} catch (Exception ex) {
			logger.error(ex);
		}
	}
	
	public void addStaff(String username, C2SStaff staff) {
		synchronized (clients) {
			if (clients.containsKey(username)) {
				logger.info("tick out " + username);
				clients.remove(username);
			}
			clients.put(username, staff);
			logger.info("add " + username);
			logger.info(clients.size() + "");
		}
	}

	public void removeStaff(String username) {
		synchronized (clients) {
			if (clients.containsKey(username)) {
				clients.remove(username);
				logger.info("tick out " + username);
			}
			logger.info(clients.size() + "");
		}
	}
	
	public void broadcast(Set<CRObject> users, CRMsg msg) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				synchronized (clients) {
					try {
						String sendStr = new Gson().toJson(msg);
						CRLogger.debug(getClass().getName(), "S2C sent: " + sendStr);

						for (CRObject user : users) {
							CRLogger.debug(getClass().getName(), "to:" + user.get("username"));
							C2SStaff staff = clients.get(user.get("username"));
							if (staff == null)
								continue;
							staff.send(sendStr);
						}
					} catch (Exception ex) {
						CRLogger.error(getClass().getName(), ex);
					}
				}
			}
		}).start();
	}



}
