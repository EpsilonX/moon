package com.shunote.Entity;

/**
 * 用户实体类
 * @author Jeffrey
 * @since 2012-3-31
 */
public class User {
	
	private int id;
	private String username;
	private String pwd;
	private String nickname;
	
	public User(int id, String username, String pwd, String nickname) {
		super();
		this.id = id;
		this.username = username;
		this.pwd = pwd;
		this.nickname = nickname;
	}
	
	public int getId() {
		return id;
	}
	public String getUsername() {
		return username;
	}
	public String getPwd() {
		return pwd;
	}
	public String getNickname() {
		return nickname;
	}
	

}
