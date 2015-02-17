package com.easy.ntdb.dao;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.Test;

import com.easy.ntdb.BaseTest;
import com.easy.ntdb.entity.mongo.UserMongo;

public class UserDAOTest extends BaseTest{

	@Resource
	private IUserDAO userDAO;
	@Test
	public void testAddUser() {
		UserMongo user = new UserMongo();
		user.setUserNo("1");
		user.setUserPwd("123");
		userDAO.addUser(user);
	}

	@Test
	public void testAddUserFriend() {
		fail("Not yet implemented");
	}
	@Test
	public void testUserAndFriend(){
		for(int i=1;i<=200000;i++){
			UserMongo user = new UserMongo();
			user.setUserNo(i+"");
			user.setUserPwd("123");
			userDAO.addUser(user);
			if(i%5==1){
				userDAO.addUserFriend(i+"", (i+1)+"");
				userDAO.addUserFriend(i+"", (i+2)+"");
				userDAO.addUserFriend(i+"", (i+3)+"");
				userDAO.addUserFriend(i+"", (i+4)+"");
			}else if(i%5==2){
				userDAO.addUserFriend(i+"", (i+1)+"");
				userDAO.addUserFriend(i+"", (i+2)+"");
				userDAO.addUserFriend(i+"", (i+3)+"");
			}else if(i%5==3){
				userDAO.addUserFriend(i+"", (i+1)+"");
				userDAO.addUserFriend(i+"", (i+2)+"");
			}else if(i%5==4){
				userDAO.addUserFriend(i+"", (i+1)+"");
			}
		}
	}

	@Test
	public void testsaveLogin(){
		userDAO.saveLogin("a", "a");
	}
}
