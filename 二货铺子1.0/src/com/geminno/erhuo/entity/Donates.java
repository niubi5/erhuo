package com.geminno.erhuo.entity;

import java.util.Date;

public class Donates {
	private int id;
	private int helpId;
	private int userId;
	private Date donTime;
	private String logisticsCom;
	private String logisticsNum;
	
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
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public Date getDonTime() {
		return donTime;
	}
	public void setDonTime(Date donTime) {
		this.donTime = donTime;
	}
	public String getLogisticsCom() {
		return logisticsCom;
	}
	public void setLogisticsCom(String logisticsCom) {
		this.logisticsCom = logisticsCom;
	}
	public String getLogisticsNum() {
		return logisticsNum;
	}
	public void setLogisticsNum(String logisticsNum) {
		this.logisticsNum = logisticsNum;
	}
}
