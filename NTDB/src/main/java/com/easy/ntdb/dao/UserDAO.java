package com.easy.ntdb.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.easy.ntdb.entity.mongo.UserFriendMongo;
import com.easy.ntdb.entity.mongo.UserMongo;

@Repository
public class UserDAO implements IUserDAO{

	@Resource
	protected MongoTemplate mongoTemplate;
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	@Override
	public void addUser(UserMongo user) {
		mongoTemplate.save(user);
	}
	@Override
	public void addUserFriend(String userNo, String friendUserNo) {
		UserFriendMongo friend1 = new UserFriendMongo();
		friend1.setFriendUserNo(friendUserNo);
		friend1.setUserNo(userNo);
		mongoTemplate.save(friend1);
		UserFriendMongo friend2 = new UserFriendMongo();
		friend2.setFriendUserNo(userNo);
		friend2.setUserNo(friendUserNo);
		mongoTemplate.save(friend2);
	}
	@Override
	public UserMongo getUser(String userNo) {
		return this.mongoTemplate.findOne(new Query(Criteria.where("userNo").is(userNo)), UserMongo.class);
	}
	@Override
	public void saveLogin(String userNo, String connectorName) {
		stringRedisTemplate.opsForValue().set(userNo, connectorName);
	}
	@Override
	public String getLogin(String userNo) {
		return stringRedisTemplate.opsForValue().get(userNo);
	}
	@Override
	public List<UserFriendMongo> getFriend(String userNo) {
		return this.mongoTemplate.find(new Query(Criteria.where("userNo").is(userNo)), UserFriendMongo.class);
	}

	
}
