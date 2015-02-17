package com.easy.ntdb.entity.mongo;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection="nt_user_friend")
public class UserFriendMongo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String userNo;
	private String friendUserNo;
	public String getUserNo() {
		return userNo;
	}
	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}
	public String getFriendUserNo() {
		return friendUserNo;
	}
	public void setFriendUserNo(String friendUserNo) {
		this.friendUserNo = friendUserNo;
	}

}
