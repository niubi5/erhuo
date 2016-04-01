package com.geminno.erhuo.entity;

public class Remark {
	
	private int id;
	private int goodsId;
	private int userId;
	private String comment_content;
	private String comment_time;
	private int fatherId;
	private int isEnd;
	
	
	public int getIsEnd(){
		return isEnd;
	}
	public void setIsEnd(int isEnd){
		this.isEnd = isEnd;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getComment_content() {
		return comment_content;
	}
	public void setComment_content(String comment_content) {
		this.comment_content = comment_content;
	}
	public String getComment_time() {
		return comment_time;
	}
	public void setComment_time(String comment_time) {
		this.comment_time = comment_time;
	}
	public int getFatherId() {
		return fatherId;
	}
	public void setFatherId(int fatherId) {
		this.fatherId = fatherId;
	}
	

}
