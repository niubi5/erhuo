package com.geminno.erhuo.entity;

import java.util.Date;

public class Orders {
	private int id;
	private int goodId;
	private int userId;
	private String orderNum;
	private Date createTime;
	private Date payTime;
	private Date sendTime;
	private Date completeTime;
	private int state;
	private String logisticsCom;
	private String logisticsNum;

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

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public Date getCompleteTime() {
		return completeTime;
	}

	public void setCompleteTime(Date completeTime) {
		this.completeTime = completeTime;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
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
