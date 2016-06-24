package com.newnius.chatroom.util;

import com.newnius.util.CRObject;

public class Message extends CRObject{
//	private long msgId;
//	private int msgType;
//	private String msgContent;
//	private long groupId;
//	private String username;
//	private long time;

	public static final int MESSAGE_TYPE_NORMAL = 0;
	public static final int MESSAGE_TYPE_SYSTEM = 0;

	public Message(long msgId, int msgType, String msgContent, long groupId, String username, long time) {
		super();
		super.set("msgId", msgId+"");
		super.set("msgType", msgType);
		super.set("msgContent", msgContent);
		super.set("groupId", groupId+"");
		super.set("username", username);
		super.set("time", time+"");
		
	}
	
	public Message(String msgContent, long groupId) {
		this(msgContent, groupId, Message.MESSAGE_TYPE_NORMAL);
	}

	public Message(String msgContent, long groupId, int msgType) {
		super();
		super.set("msgContent", msgContent);
		super.set("groupId", groupId+"");
		super.set("msgType", msgType);
	}

	public long getMsgId() {
		return super.getLong("msgId");
	}

	public void setMsgId(long msgId) {
		super.set("msgId", msgId+"");
	}

	public int getMsgType() {
		return super.getInt("msgType");
	}

	public String getMsgContent() {
		return super.get("msgContent");
	}

	public long getGroupId() {
		return super.getLong("groupId");
	}

	public String getUsername() {
		return super.get("username");
	}

	public long getTime() {
		return super.getLong("time");
	}

}
