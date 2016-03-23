package com.geminno.erhuo.entity;

import java.util.Date;

public class Collections {
	private int userId;
	private int GoodId;
	private Date colTime;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getGoodId() {
		return GoodId;
	}

	public void setGoodId(int goodId) {
		GoodId = goodId;
	}

	public Date getColTime() {
		return colTime;
	}

	public void setColTime(Date colTime) {
		this.colTime = colTime;
	}

}
