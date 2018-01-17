package com.newnius.chatroom.client;

import com.google.gson.Gson;
import com.newnius.chatroom.util.Message;
import com.newnius.chatroom.util.RequestCode;
import com.newnius.util.CRErrorCode;
import com.newnius.util.CRLogger;
import com.newnius.util.CRMsg;
import com.newnius.util.CRObject;

/**
 * @author Newnius
 */
public class Server {
	private Communicator communicator;
	private CRLogger logger = CRLogger.getLogger(getClass().getName());
	private final CRMsg notConnectMsg = new CRMsg(CRErrorCode.FAIL, "Not connect to server.");

	public Server(CRObject config) {
		this.communicator = new Communicator(config);
	}

	public CRMsg login(CRObject user) {
		if (communicator == null) {
			return notConnectMsg;
		}
		CRMsg msg = new CRMsg(RequestCode.LOGIN);
		msg.set("user", user);
		return send(msg);
	}

	public CRMsg sendMsg(Message message) {
		if (communicator == null) {
			return notConnectMsg;
		}
		CRMsg msg = new CRMsg(RequestCode.SEND_MESSAGE);
		msg.set("message", message);
		return send(msg);
	}

	public CRMsg quit(CRObject user) {
		if (communicator == null) {
			return notConnectMsg;
		}
		CRMsg msg = new CRMsg(RequestCode.QUIT);
		msg.set("user", user);
		return send(msg);
	}

	private CRMsg send(CRMsg msg) {
		String str = new Gson().toJson(msg);
		String response = communicator.send(str);
		CRMsg responseMsg = new Gson().fromJson(response, CRMsg.class);
		return responseMsg;
	}

}
