package com.easy.ntdb.entity.mongo;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="nt_user")
public class UserMongo implements Serializable{

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
