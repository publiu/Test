package com.sunrun.washer.model;

/**
 * 文 件 名 : UserMachineModel.java
 * 创 建 人： 金明明
 * 日 期：2017-7-30
 * 修 改 人： 
 * 日 期： 
 * 描 述：用户关联洗衣机 查询条件
 */
public class UserMachineModel {
	private Integer userId;
	private String machineNo; // 洗衣机序列号，模糊查询
	private Integer queryType = 0; // 查询类型 0.所有洗衣机 1.未投放的洗衣机 默认0
	public UserMachineModel(){}
	public UserMachineModel(Integer userId) {
		this.userId = userId;
	}
	
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getMachineNo() {
		return machineNo;
	}

	public void setMachineNo(String machineNo) {
		this.machineNo = machineNo;
	}

	public Integer getQueryType() {
		return queryType;
	}

	public void setQueryType(Integer queryType) {
		this.queryType = queryType;
	}
}

