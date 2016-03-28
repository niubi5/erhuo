package com.geminno.erhuo.entity;

import java.util.Date;

/**
 * 捐赠信息
 * 
 * @author Administrator
 * 
 */

public class Donation {
	/**
	 * id
	 */
	private Integer id;
	/**
	 * 用户id
	 */
	private Integer uesrId;
	/**
	 * 用户头像
	 */
	private Integer userHeadImage;
	/**
	 * 发布图片
	 */
	private Integer image;
	/**
	 * 用户名
	 */
	private String userName;
	/**
	 * 发布时间
	 */
	private String time;
	/**
	 * 发布内容
	 */
	private String detail;
	/**
	 * 请求捐赠地址图片
	 */
	private Integer addressImage;
	/**
	 * 请求捐赠收货地址
	 */
	private String address;
	/**
	 * 请求捐赠物流
	 */
	private String logistics;
	/**
	 * 请求捐赠收货人
	 */
	private String consignee;
	/**
	 * 请求捐赠状态
	 */
	private Integer state;
	/**
	 * 标题
	 */
	private String title;
	
	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Donation() {
		super();
	}

	public Donation(Integer id, Integer uesrId, Integer userHeadImage,
			Integer image, String userName, String time, String detail,
			Integer addressImage, String address, String logistics,
			String consignee, Integer state) {
		super();
		this.id = id;
		this.uesrId = uesrId;
		this.userHeadImage = userHeadImage;
		this.image = image;
		this.userName = userName;
		this.time = time;
		this.detail = detail;
		this.addressImage = addressImage;
		this.address = address;
		this.logistics = logistics;
		this.consignee = consignee;
		this.state = state;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUesrId() {
		return uesrId;
	}

	public void setUesrId(Integer uesrId) {
		this.uesrId = uesrId;
	}

	public String getLogistics() {
		return logistics;
	}

	public void setLogistics(String logistics) {
		this.logistics = logistics;
	}

	public String getConsignee() {
		return consignee;
	}

	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	/**
	 * get方法
	 * 
	 * @return
	 */
	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public Donation(Integer userHeadImage, String userName, String time,
			Integer image, String detail, Integer addressImage, String address,
			Integer button) {
		super();
		this.userHeadImage = userHeadImage;
		this.userName = userName;
		this.time = time;
		this.image = image;
		this.detail = detail;
		this.addressImage = addressImage;
		this.address = address;
	}

	public Donation(Integer userHeadImage, String userName, String time,
			Integer image, Integer addressImage, String address) {
		super();
		this.userHeadImage = userHeadImage;
		this.userName = userName;
		this.time = time;
		this.image = image;
		this.addressImage = addressImage;
		this.address = address;
	}

	public Integer getUserHeadImage() {
		return userHeadImage;
	}

	public void setUserHeadImage(Integer userHeadImage) {
		this.userHeadImage = userHeadImage;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Integer getImage() {
		return image;
	}

	public void setImage(Integer image) {
		this.image = image;
	}

	public Integer getAddressImage() {
		return addressImage;
	}

	public void setAddressImage(Integer addressImage) {
		this.addressImage = addressImage;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
