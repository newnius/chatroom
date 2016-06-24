package com.newnius.chatroom.server;

import com.google.gson.Gson;
import com.newnius.util.CRErrorCode;
import com.newnius.util.CRMsg;
import com.newnius.util.CRObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Newnius
 */
public class S2CStaff extends Thread {

	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	private CRObject currentUser;
	private S2CServer server;
	

	public S2CStaff(Socket socket, S2CServer server) {
		this.socket = socket;
		this.server = server;
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));// 获得客户端的输入流
			writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);// 获得客户端输出流)
		} catch (Exception ex) {
			Logger.getLogger(S2CStaff.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void run() {
		try {
			if (socket.isConnected()) {
				Logger.getLogger(S2CStaff.class.getName()).log(Level.INFO,
						"客户端" + socket.getInetAddress().getHostAddress() + "  连入！");
			} else {
				return;
			}

			String str = reader.readLine();
			if (str == null) {
				// socket.close();
				System.out.println(socket.getInetAddress().getHostAddress() + "quit");
				return;
			}
			// str = new String(str.getBytes(), "utf-8");
			Logger.getLogger(S2CStaff.class.getName()).log(Level.INFO, "S2CStaff received：" + str);
			CRMsg msg = new Gson().fromJson(str, CRMsg.class);
			String tmp = new Gson().toJson(msg.getObject(""));

			CRObject u = new Gson().fromJson(tmp, CRObject.class);
			if ("".equals("ToBeDelete")) {
				currentUser = u;
				S2CServer.addStaff(currentUser.get("username"), this);
				send(new Gson().toJson(new CRMsg(CRErrorCode.SUCCESS)));
			} else {
				send(new Gson().toJson(new CRMsg(CRErrorCode.FAIL)));
				return;
			}

			while (server.isRun() && socket!=null && !socket.isClosed() && reader.readLine()!=null) {
				//for heart packet
				writer.println();
			}
			stopConn();
		} catch (Exception ex) {
			try {
				Logger.getLogger(S2CStaff.class.getName()).log(Level.SEVERE, null, ex);
				stopConn();
			} catch (Exception e) {
				Logger.getLogger(S2CStaff.class.getName()).log(Level.SEVERE, null, e);
			}
		}
	}

	public void send(String msg) {
		writer.println(msg);
		writer.flush();
		System.out.println("S2CServer sent to " + currentUser.get("username") + ": " + msg);
	}

	public void stopConn() {
		// has been stopped
		if (socket == null) {
			return;
		}

		try {
			reader.close();
			writer.close();
			socket.close();
			socket = null;
			S2CServer.removeStaff(currentUser.get("username"));
		} catch (IOException e) {
			Logger.getLogger(S2CServer.class.getName()).log(Level.SEVERE, null, e);
		}
	}

}
