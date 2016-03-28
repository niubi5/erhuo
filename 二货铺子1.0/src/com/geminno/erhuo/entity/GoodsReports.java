package com.geminno.erhuo.entity;

public class GoodsReports {
	private int id;
	private int goodId;
	private int userId;// 举报人id
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

	public int getGoodId() {
		return goodId;
	}

	public void setGoodId(int goodId) {
		this.goodId = goodId;
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
