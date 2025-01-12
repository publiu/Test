package com.jeecms.core.manager;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import com.jeecms.common.email.EmailSender;
import com.jeecms.common.email.MessageTemplate;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.entity.CmsUserExt;

public interface CmsUserMng {
	public Pagination getPage(Integer realnameStatus,Integer provinceId,Integer cityId,String username, String email, Integer siteId,
			Integer groupId, Boolean disabled, Boolean admin, Integer rank,
			String realName,Integer roleId,
			Boolean allChannel,
			int pageNo, int pageSize);
	
	public List<CmsUser> getList(String username, String email, Integer siteId,
			Integer groupId, Boolean disabled, Boolean admin, Integer rank);

	public List<CmsUser> getAdminList(Integer siteId, Boolean allChannel,
			Boolean disabled, Integer rank);
	
	public Pagination getAdminsByRoleId(Integer roleId, int pageNo, int pageSize);
	
	public Pagination getMembershipUser(String userName,String nickName,String mobile, Integer membership,int pageNo, int pageSize);

	public CmsUser findById(Integer id);
	
	public CmsUser findByIdNumber(String idNum);
	
	public CmsUser findByUsername(String username);

	public CmsUser registerMember( Integer provinceId,Integer cityId,String username, String email,
			String password, String ip, Integer groupId,Integer grain, boolean disabled,CmsUserExt userExt,Map<String,String>attr);
	//APP注册会员
	public CmsUser registerAppMember(String username,String password,Integer groupId,String ip,CmsUserExt userExt,Map<String,String>attr);
	
	public CmsUser registerMember(String username, String email,
			String password, String ip, Integer groupId, boolean disabled,CmsUserExt userExt,Map<String,String>attr, Boolean activation , EmailSender sender, MessageTemplate msgTpl)throws UnsupportedEncodingException, MessagingException ;

	public void updateLoginInfo(Integer userId, String ip,Date loginTime,String sessionId);

	public void updateUploadSize(Integer userId, Integer size);
	
	public void updateUser(CmsUser user);

	public void updatePwdEmail(Integer id, String password, String email);

	public boolean isPasswordValid(Integer id, String password);

	public CmsUser saveAdmin(String username, String email, String password,
			String ip, boolean viewOnly, boolean selfAdmin, int rank,
			Integer groupId, Integer[] roleIds, Integer[] channelIds,
			Integer[] siteIds, Byte[] steps, Boolean[] allChannels,Integer provinceId,Integer cityId,
			CmsUserExt userExt);

	public CmsUser updateAdmin(CmsUser bean, CmsUserExt ext, String password,
			Integer groupId,Integer[] roleIds, Integer[] channelIds,
			Integer[] siteIds, Byte[] steps, Boolean[] allChannels);

	public CmsUser updateAdmin(Integer provinceId,Integer cityId,CmsUser bean, CmsUserExt ext, String password,
			Integer groupId,Integer[] roleIds, Integer[] channelIds,
			Integer siteId, Byte step, Boolean allChannel);

	public CmsUser updateMember(Integer provinceId,Integer cityId,Integer id, String email, String password,
			Boolean isDisabled, CmsUserExt ext, Integer groupId,Integer grain,Map<String,String>attr);
	
	public CmsUser updateMember(Integer id, String email, String password,Integer groupId,String realname,String mobile,Boolean sex);
	
	public CmsUser updateUserConllection(CmsUser user,Integer cid,Integer operate);

	public void addSiteToUser(CmsUser user, CmsSite site, Byte checkStep);

	public CmsUser deleteById(Integer id);

	public CmsUser[] deleteByIds(Integer[] ids);

	public boolean usernameNotExist(String username);
	
	public boolean usernameNotExistInMember(String username);

	public boolean emailNotExist(String email);
	
	/**
	 * 钱包变更金额
	 * @param id 用户Id
	 * @param changeMoney 金额变更 有正负
	 * @param payWay 支付方式 (输入类型：WalletLog.WalletLogTypeEnum)
	 * @param payPlatform 交易平台（输入类型：WalletLog.WalletLogPayPlatformEnum）
	 * @param logMsg 消费日志
	 * @return
	 */
	public CmsUser updateMoney(Integer id, BigDecimal changeMoney, Integer payWay, Integer payPlatform, String logMsg);

}