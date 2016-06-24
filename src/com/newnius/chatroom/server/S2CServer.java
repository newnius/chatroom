package com.newnius.chatroom.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Set;

import com.google.gson.Gson;
import com.newnius.util.CRLogger;
import com.newnius.util.CRMsg;
import com.newnius.util.CRObject;

public class S2CServer implements Runnable {

	private static HashMap<String, S2CStaff> clients = new HashMap<String, S2CStaff>();
	private ServerSocket server;
	private CRLogger logger = CRLogger.getLogger(this.getClass().getName());
	private int port;
	private boolean isRun = true;

	public S2CServer(int port) {
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
			logger.info("S2CServer opened, waiting for client.");
			while (isRun) {
				Socket socket = server.accept();// 等待客户端连接
				new S2CStaff(socket, this).start();
			}
			server.close();
		} catch (Exception ex) {
			logger.error(ex);
		}
	}

	public static void broadcast(Set<CRObject> users, CRMsg msg) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				synchronized (clients) {
					try {
						String sendStr = new Gson().toJson(msg);
						CRLogger.debug(getClass().getName(), "S2C sent: " + sendStr);

						for (CRObject user : users) {
							CRLogger.debug(getClass().getName(), "to:" + user.get("username"));
							S2CStaff staff = clients.get(user.get("username"));
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

	public static void addStaff(String username, S2CStaff staff) {
		synchronized (clients) {
			if (clients.containsKey(username)) {
				clients.remove(username);
				CRLogger.info(S2CServer.class.getName(), "tick out " + username);
			}
			clients.put(username, staff);
			CRLogger.info(S2CServer.class.getName(), "add " + username);
			
//			List<User> u = new ArrayList<>();
//			u.add(new User(username, null, null));
//			S2CServer.broadcast(u, new Msg(91200, null));
		}
	}

	public static void removeStaff(String username) {
		synchronized (clients) {
			if (clients.containsKey(username)) {
				clients.remove(username);
				CRLogger.info(S2CServer.class.getName(), "tick out " + username);
			}
		}
	}

}
