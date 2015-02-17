package com.easynetty4.Enum;

/**
 * 消息类型
 *
 */
public enum MessageType {

	/**
	 * 业务处理类型
	 */
	SERVICE_HANDLE(1),
	/**
	 * 业务请求响应类型
	 */
	SERVICE_REQ_RESP(2),
	/**
	 * 心跳
	 */
	HEARTBEAT(3);

	private int value;

	private MessageType(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}
	// 普通方法  
    public static MessageType getType(int value) {  
        for (MessageType c : MessageType.values()) {  
            if (c.value == value) {  
                return c;  
            }  
        }  
        return null;  
    }  
	public static void main(String[] args) {
		MessageType m = MessageType.getType(3);
		System.out.println(m);
	}
}
