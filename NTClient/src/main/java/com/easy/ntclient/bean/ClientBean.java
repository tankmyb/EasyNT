package com.easy.ntclient.bean;

import java.net.SocketAddress;
import java.util.List;

import com.easynetty4.bootstrap.IEasyNettyConnector;

public class ClientBean {

	private List<String> friends;
	private SocketAddress address;
	private IEasyNettyConnector client;
	public List<String> getFriends() {
		return friends;
	}
	public void setFriends(List<String> friends) {
		this.friends = friends;
	}
	public SocketAddress getAddress() {
		return address;
	}
	public void setAddress(SocketAddress address) {
		this.address = address;
	}
	public IEasyNettyConnector getClient() {
		return client;
	}
	public void setClient(IEasyNettyConnector client) {
		this.client = client;
	}
}
