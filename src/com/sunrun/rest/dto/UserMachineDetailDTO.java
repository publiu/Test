package com.sunrun.rest.dto;
import com.sunrun.washer.entity.*;
/**
 * 文 件 名 : UserMachineDTO.java
 * 创 建 人： 金明明
 * 日 期：2017-7-30
 * 修 改 人： 
 * 日 期： 
 * 描 述：用户关联洗衣机DTO
 */
public class UserMachineDetailDTO extends BaseDTO {
	public enum UserMachineDetailDTOEnum implements BaseStateDTOEnum{
		/**
		 * 2202.用户关联洗衣机不存在
		 */
		IS_NOT_EXIST(2202, "用户关联洗衣机不存在");
		
		private Integer stateCode;
		private String msg;
		private UserMachineDetailDTOEnum(Integer stateCode, String msg) {
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
	
		
}

