package com.easy.ntprotocol.json;

import java.io.Serializable;

public class LoginReq implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String userNo;
	private String userPwd;
	public String getUserNo() {
		return userNo;
	}
	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}
	public String getUserPwd() {
		return userPwd;
	}
	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}
}
