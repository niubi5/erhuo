package com.geminno.erhuo.entity;


public class Helps {
	private int id;
	private int userId;
	private String title;
	private String detail;
	private String pubTime;
	private int state;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getPubTime() {
		return pubTime;
	}

	public void setPubTime(String pubTime) {
		this.pubTime = pubTime;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}
