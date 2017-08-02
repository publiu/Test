package com.sunrun.rest.dto;


import java.util.List;

/**
 * 文 件 名 : UserFloorQueryDTO.java
 * 创 建 人： 金明明
 * 日 期：2017-7-31
 * 修 改 人： 
 * 日 期： 
 * 描 述：用户关联楼DTO 查询结果
 */
public class UserFloorQueryDTO extends BaseDTO {
	public enum UserFloorQueryDTOEnum implements BaseStateDTOEnum{
		;
		
		private Integer stateCode;
		private String msg;
		private UserFloorQueryDTOEnum(Integer stateCode, String msg) {
			this.stateCode = stateCode;
			this.msg = msg;
		}
		@Override
		public String getMsg() {
			return this.msg;
		}

		@Override
		public Integer getStateCode() {
			return this.stateCode;
		}
		
	}
	private Integer pageNo; // 当前页
	private Integer pageSize; // 每页数据量
	private Integer totalCount; // 所有数据量

	private List<UserFloorDTO> userFloorDTOs;
	
	public Integer getPageNo() {
		return pageNo;
	}
	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}
	public List<UserFloorDTO> getUserFloorDTOs() {
		return userFloorDTOs;
	}
	public void setUserFloorDTOs(
			List<UserFloorDTO> userFloorDTOs) {
		this.userFloorDTOs = userFloorDTOs;
	}
}

