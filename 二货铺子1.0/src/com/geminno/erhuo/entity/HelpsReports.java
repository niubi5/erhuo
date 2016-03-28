package com.geminno.erhuo.entity;


public class HelpsReports {
	private int id;
	private int helpId;
	private int userId;
	private String brief;
	private String repTime;
	private int state;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getHelpId() {
		return helpId;
	}

	public void setHelpId(int helpId) {
		this.helpId = helpId;
	}

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public String getRepTime() {
		return repTime;
	}

	public void setRepTime(String repTime) {
		this.repTime = repTime;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}
