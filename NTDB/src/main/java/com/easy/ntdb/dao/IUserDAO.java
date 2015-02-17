package com.easy.ntdb.dao;

import java.util.List;

import com.easy.ntdb.entity.mongo.UserFriendMongo;
import com.easy.ntdb.entity.mongo.UserMongo;

public interface IUserDAO {

	void addUser(UserMongo user);
	
	void addUserFriend(String userNo,String friendUserNo);
	
	UserMongo getUser(String userNo);
	
	List<UserFriendMongo> getFriend(String userNo);
	void saveLogin(String userNo,String connectorName);
	
	String getLogin(String userNo);
}
