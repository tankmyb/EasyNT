package com.easynetty4.bean;

import java.util.HashMap;
import java.util.Map;

import com.easynetty4.Enum.MessageType;

public class Header {

	/**
	 * 会话ID
	 */
	private long sessionId;
	/**
	 * 消息类型
	 */
	private MessageType messageType;
	/**
	 * 业务类型
	 */
	private int serviceType;
	/**
	 * 附件
	 */
	private Map<String, Object> attachment = new HashMap<String, Object>(); // 附件

	public Map<String, Object> getAttachment() {
		return attachment;
	}
	public void setAttachment(Map<String, Object> attachment) {
		this.attachment = attachment;
	}

	public long getSessionId() {
		return sessionId;
	}
	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}
	public MessageType getMessageType() {
		return messageType;
	}
	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}
	public int getServiceType() {
		return serviceType;
	}
	public void setServiceType(int serviceType) {
		this.serviceType = serviceType;
	}
}
