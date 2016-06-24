
package com.newnius.chatroom.server;

import com.google.gson.Gson;

import com.newnius.util.CRMsg;
import com.newnius.util.CRObject;
import com.newnius.chatroom.util.RequestCode;
import com.newnius.util.CRErrorCode;
import com.newnius.util.CRLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author Newnius
 */
public class C2SStaff extends Thread {

	private Socket socket = null;
	private C2SServer server;
	
	private BufferedReader reader = null;
	private PrintWriter writer = null;
	private CRObject currentUser = null;
	private CRLogger logger = CRLogger.getLogger(this.getClass().getName());

	public C2SStaff(Socket socket, C2SServer server) {
		this.socket = socket;
		this.server = server;
	}

	@Override
	public void run() {
		CRMsg res = new CRMsg(CRErrorCode.FAIL, "Unknown error");

		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));// 获得客户端的输入流
			writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);// 获得客户端输出流)
			if (socket.isConnected()) {
				logger.info("Client " + socket.getInetAddress().getHostAddress() + "  connected");
			}

			while (server.isRun() && socket!=null && !socket.isClosed()) {
				String str = reader.readLine();

				if (str == null) {
					break;
				}

				logger.info("C2SStaff received：" + str);

				CRMsg msg = new Gson().fromJson(str, CRMsg.class);

				if (currentUser == null && msg.getCode() != RequestCode.LOGIN) {
					res = new CRMsg(CRErrorCode.FAIL, "Not loged");
					logger.info("C2SStaff response: " + new Gson().toJson(res));
					writer.println(new Gson().toJson(res));
					continue;
				}

				// hand out requests
				switch (msg.getCode()) {

				case RequestCode.LOGIN:// login
					currentUser = new CRObject();
					currentUser = msg.getObject("user");
					server.addStaff(currentUser.get("username"), this);
					res = new CRMsg(CRErrorCode.SUCCESS);
					break;
					
				case RequestCode.SEND_MESSAGE:// send message
					res = new CRMsg(CRErrorCode.SUCCESS);
					break;
					
				case RequestCode.QUIT:// quit
					res = new CRMsg(CRErrorCode.SUCCESS);
					stopConn();
					break;

				default:// unrecognized request
					res = new CRMsg(CRErrorCode.FAIL);
					break;
				}

				logger.info("C2SStaff response: " + new Gson().toJson(res));
				writer.println(new Gson().toJson(res));
			}

			logger.info("Staff sent: " + new Gson().toJson(res));

			stopConn();

		} catch (Exception ex) {
			try {
				writer.println(new Gson().toJson(new CRMsg(CRErrorCode.FAIL, null)));
				logger.error(ex);
				stopConn();
			} catch (Exception e) {
				logger.error(e);
			}
		}
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
			server.removeStaff(currentUser.get("username"));
			logger.info(currentUser.get("username") + " quit.");
		} catch (IOException ex) {
			logger.error(ex);
			logger.info("error");
		}
	}
	
	public void send(String msg) {
		writer.println(msg);
		writer.flush();
		System.out.println("C2SServer sent to " + currentUser.get("username") + ": " + msg);
	}
	

	


}
