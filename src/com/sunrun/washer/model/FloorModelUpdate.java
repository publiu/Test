package com.sunrun.washer.model;

/**
 * 文 件 名 : FloorModelUpdate.java
 * 创 建 人： 金明明
 * 日 期：2017-8-1
 * 修 改 人： 
 * 日 期： 
 * 描 述：楼 查询条件
 */
public class FloorModelUpdate {
	
	private Integer floorId;
	private String name; // 楼名
	private String addressDetail; // 地址详情
	public Integer getFloorId() {
		return floorId;
	}
	public void setFloorId(Integer floorId) {
		this.floorId = floorId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddressDetail() {
		return addressDetail;
	}
	public void setAddressDetail(String addressDetail) {
		this.addressDetail = addressDetail;
	}
	
	

}

