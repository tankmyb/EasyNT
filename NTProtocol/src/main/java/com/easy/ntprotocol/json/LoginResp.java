package com.easy.ntprotocol.json;

import java.io.Serializable;
import java.util.List;

public class LoginResp implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private int ret;
	private String userKey;
	private String ip;
	private List<String> friends;
	public List<String> getFriends() {
		return friends;
	}
	public void setFriends(List<String> friends) {
		this.friends = friends;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	private int port;
	public int getRet() {
		return ret;
	}
	public void setRet(int ret) {
		this.ret = ret;
	}
	public String getUserKey() {
		return userKey;
	}
	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}
}
