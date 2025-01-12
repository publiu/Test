package com.jeecms.cms.action.admin.main;

import static com.jeecms.common.page.SimplePage.cpn;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jeecms.cms.entity.assist.CmsWebservice;
import com.jeecms.common.page.Pagination;
import com.jeecms.common.web.CookieUtils;
import com.jeecms.common.web.RequestUtils;
import com.jeecms.core.entity.CmsGroup;
import com.jeecms.core.entity.CmsRole;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.entity.CmsUserExt;
import com.jeecms.core.entity.CmsUserSite;
import com.jeecms.core.web.WebErrors;
import com.jeecms.core.web.util.CmsUtils;
import com.sunrun.washer.entity.Area;
/**
 * 
 * @author wangcy
 * @ClassName CmsAdminPoliceAct.java
 * @CreateDate  2017-6-25
 * @descrintion  用户民警管理
 * @editor 
 * @editDate
 */
@Controller
public class CmsAdminPoliceAct extends CmsAdminAbstract{
	private static final Logger log = LoggerFactory
			.getLogger(CmsAdminPoliceAct.class);

	@RequiresPermissions("admin_police:v_list")
	@RequestMapping("/admin_police/v_list.do")
	public String list(Integer provinceId,Integer cityId,String queryUsername, String queryEmail,
			Integer queryGroupId, Boolean queryDisabled, 
			String queryRealName,Integer queryRoleId,
			Boolean queryAllChannel,Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		/*获取省列表*/
		List<Area> proList = areaMng.findByPareantId(45062);
		queryGroupId =3;//民警管理员
		CmsSite site = CmsUtils.getSite(request);
		CmsUser currUser = CmsUtils.getUser(request);
		Pagination pagination = manager.getPage(null,provinceId,cityId,queryUsername, queryEmail, site
				.getId(), queryGroupId, queryDisabled, true,
				currUser.getRank(), queryRealName,queryRoleId,
				queryAllChannel,cpn(pageNo), CookieUtils
						.getPageSize(request));
		List<CmsRole> roleList = cmsRoleMng.getList(currUser.getTopRoleLevel());
		model.addAttribute("roleList", roleList);
		model.addAttribute("pagination", pagination);
		appendQueryParam(model, provinceId,cityId,queryUsername, queryEmail, queryGroupId, 
				queryDisabled, queryRealName, queryRoleId,
				queryAllChannel);
		model.addAttribute("groupList", cmsGroupMng.getList());
		model.addAttribute("proList", proList);

		return "admin/police/list";
	}

	@RequiresPermissions("admin_police:v_add")
	@RequestMapping("/admin_police/v_add.do")
	public String add(HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser currUser = CmsUtils.getUser(request);
		List<CmsGroup> groupList = cmsGroupMng.getList();
		List<CmsRole> roleList = cmsRoleMng.getList(currUser.getTopRoleLevel());
		/*获取省列表*/
		List<Area> proList = areaMng.findByPareantId(45062);
		model.addAttribute("proList", proList);
		model.addAttribute("site", site);
		model.addAttribute("groupList", groupList);
		model.addAttribute("roleList", roleList);
		model.addAttribute("currRank", currUser.getRank());
		return "admin/police/add";
	}

	@RequiresPermissions("admin_police:v_edit")
	@RequestMapping("/admin_police/v_edit.do")
	public String edit(Integer id, String queryUsername, String queryEmail,
			Integer queryGroupId, Boolean queryDisabled, 
			String queryRealName,Integer queryRoleId,
			Boolean queryAllChannel,Integer provinceId,Integer cityId,
			HttpServletRequest request,
			HttpServletResponse  response,ModelMap model) throws IOException {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser currUser = CmsUtils.getUser(request);
		WebErrors errors = validateEdit(id, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsUser admin = manager.findById(id);
		CmsUserSite userSite = admin.getUserSite(site.getId());

		List<CmsGroup> groupList = cmsGroupMng.getList();
		List<CmsRole> roleList = cmsRoleMng.getList(currUser.getTopRoleLevel());
		/*获取省列表*/
		List<Area> proList = areaMng.findByPareantId(45062);
		model.addAttribute("proList", proList);
		model.addAttribute("cmsAdmin", admin);
		model.addAttribute("site", site);
		model.addAttribute("userSite", userSite);
		model.addAttribute("roleIds", admin.getRoleIds());
		model.addAttribute("groupList", groupList);
		model.addAttribute("roleList", roleList);
		model.addAttribute("currRank", currUser.getRank());

		appendQueryParam(model, provinceId,cityId,queryUsername, queryEmail, queryGroupId, 
				queryDisabled, queryRealName,  queryRoleId,
				queryAllChannel);
		return "admin/police/edit";
	}

	@RequiresPermissions("admin_police:o_save")
	@RequestMapping("/admin_police/o_save.do")
	public String save(CmsUser bean, CmsUserExt ext, String username,
			String email, String password, Boolean selfAdmin, 
			Integer rank, Integer groupId,
			Integer[] roleIds, Integer[] channelIds,
			Byte step, Boolean allChannel, Integer provinceId,Integer cityId,
			HttpServletRequest request,ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		WebErrors errors = validateSave(bean, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		Integer[] siteIds = new Integer[] { site.getId() };
		step =1;//20170624页面值取消，此处固定
		rank=8;//20170624页面值取消，此处固定
		allChannel =false;//20170624页面值取消，此处固定
		Byte[] steps = new Byte[]{step};
		Boolean[] allChannels = new Boolean[]{allChannel};
		String ip = RequestUtils.getIpAddr(request);
		bean = manager.saveAdmin(username, email, password, ip, false,
				selfAdmin, rank, groupId, roleIds, channelIds,
				siteIds, steps, allChannels,provinceId, cityId, ext);
		cmsWebserviceMng.callWebService("true",username, password, email, ext,CmsWebservice.SERVICE_TYPE_ADD_USER);
		log.info("save CmsAdmin id={}", bean.getId());
		cmsLogMng.operating(request, "cmsUser.log.save", "id=" + bean.getId()
				+ ";username=" + bean.getUsername());
		return "redirect:v_list.do";
	}

	@RequiresPermissions("admin_police:o_update")
	@RequestMapping("/admin_police/o_update.do")
	public String update(CmsUser bean, CmsUserExt ext, String password,
			Integer groupId,Integer[] roleIds,
			Integer[] channelIds, Byte step, Boolean allChannel,
			String queryUsername, String queryEmail, Integer queryGroupId,
			Boolean queryDisabled, 
			String queryRealName,Integer queryRoleId,
			Boolean queryAllChannel,Integer provinceId,Integer cityId,
			Integer pageNo, HttpServletRequest request,ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		step =1;//20170624页面值取消，此处固定
		bean.setRank(8);//20170624页面值取消，此处固定
		allChannel =false;//20170624页面值取消，此处固定
		WebErrors errors = validateUpdate(bean.getId(),bean.getRank(), request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		bean = manager.updateAdmin( provinceId,cityId,bean, ext, password, groupId,
				roleIds,channelIds, site.getId(), step, 
				allChannel);
		cmsWebserviceMng.callWebService("true",bean.getUsername(), 
				password, null, ext,CmsWebservice.SERVICE_TYPE_UPDATE_USER);
		log.info("update CmsAdmin id={}.", bean.getId());
		cmsLogMng.operating(request, "cmsUser.log.update", "id=" + bean.getId()
				+ ";username=" + bean.getUsername());
		return list( provinceId, cityId,queryUsername, queryEmail, queryGroupId, queryDisabled,
				queryRealName,queryRoleId,
				queryAllChannel,pageNo, request, model);
	}

	@RequiresPermissions("admin_police:o_delete")
	@RequestMapping("/admin_police/o_delete.do")
	public String delete(Integer[] ids, Integer queryGroupId,
			Boolean queryDisabled,String queryRealName,
			Integer queryRoleId,Boolean queryAllChannel,
			Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		String queryUsername = RequestUtils.getQueryParam(request,
				"queryUsername");
		String queryEmail = RequestUtils.getQueryParam(request, "queryEmail");
		WebErrors errors = validateDelete(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsUser[] beans = manager.deleteByIds(ids);
		CmsUser user =CmsUtils.getUser(request);
		boolean deleteCurrentUser=false;
		for (CmsUser bean : beans) {
			Map<String,String>paramsValues=new HashMap<String, String>();
			paramsValues.put("username", bean.getUsername());
			paramsValues.put("admin", "true");
			cmsWebserviceMng.callWebService(
					CmsWebservice.SERVICE_TYPE_DELETE_USER, paramsValues);
			log.info("delete CmsAdmin id={}", bean.getId());
			if(user.getUsername().equals(bean.getUsername())){
				deleteCurrentUser=true;
			}else{
				cmsLogMng.operating(request, "cmsUser.log.delete", "id="
						+ bean.getId() + ";username=" + bean.getUsername());
			}
		}
		if(deleteCurrentUser){
			 Subject subject = SecurityUtils.getSubject();
			 subject.logout();
			 return "login";
		}
		return list(null,null,queryUsername, queryEmail, queryGroupId, queryDisabled,
				queryRealName,queryRoleId,
				queryAllChannel,pageNo, request, model);
	}

	@RequiresPermissions("admin_police:v_channels_add")
	@RequestMapping(value = "/admin_police/v_channels_add.do")
	public String channelsAdd(Integer siteId, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		return channelsAddJson(siteId, request, response, model);
	}

	@RequiresPermissions("admin_police:v_channels_edit")
	@RequestMapping(value = "/admin_police/v_channels_edit.do")
	public String channelsEdit(Integer userId, Integer siteId,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		return channelsEditJson(userId, siteId, request, response, model);
	}

	@RequiresPermissions("admin_police:v_check_username")
	@RequestMapping(value = "/admin_police/v_check_username.do")
	public void checkUsername(HttpServletRequest request, HttpServletResponse response) {
		checkUserJson(request, response);
	}

	@RequiresPermissions("admin_police:v_check_email")
	@RequestMapping(value = "/admin_police/v_check_email.do")
	public void checkEmail(String email, HttpServletResponse response) {
		checkEmailJson(email, response);
	}
	
	private void appendQueryParam(ModelMap model,Integer provinceId,Integer cityId,
			String queryUsername, String queryEmail,
			Integer queryGroupId, Boolean queryDisabled, 
			String queryRealName,Integer queryRoleId,
			Boolean queryAllChannel){
		model.addAttribute("queryUsername", queryUsername);
		model.addAttribute("queryEmail", queryEmail);
		model.addAttribute("queryGroupId", queryGroupId);
		model.addAttribute("queryDisabled", queryDisabled);
		model.addAttribute("queryRealName", queryRealName);
		model.addAttribute("queryRoleId", queryRoleId);
		model.addAttribute("queryAllChannel", queryAllChannel);
		model.put("provinceId", provinceId);
		model.put("cityId", cityId);
	}

	private WebErrors validateSave(CmsUser bean, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		return errors;
	}

	private WebErrors validateEdit(Integer id, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		if (vldExist(id, errors)) {
			return errors;
		}
		return errors;
	}

	private WebErrors validateUpdate(Integer id,Integer rank, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		if (vldExist(id, errors)) {
			return errors;
		}
		if (vldParams(id,rank, request, errors)) {
			return errors;
		}
		return errors;
	}

	private WebErrors validateDelete(Integer[] ids, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		errors.ifEmpty(ids, "ids");
		for (Integer id : ids) {
			vldExist(id, errors);
		}
		return errors;
	}

	private boolean vldExist(Integer id, WebErrors errors) {
		if (errors.ifNull(id, "id")) {
			return true;
		}
		CmsUser entity = manager.findById(id);
		if (errors.ifNotExist(entity, CmsUser.class, id)) {
			return true;
		}
		return false;
	}

	private boolean vldParams(Integer id,Integer rank, HttpServletRequest request,
			WebErrors errors) {
		CmsUser user = CmsUtils.getUser(request);
		CmsUser entity = manager.findById(id);
		//提升等级大于当前登录用户
		if (rank > user.getRank()) {
			errors.addErrorCode("error.noPermissionToRaiseRank", id);
			return true;
		}
		//修改的用户等级大于当前登录用户 无权限
		if (entity.getRank() > user.getRank()) {
			errors.addErrorCode("error.noPermission", CmsUser.class, id);
			return true;
		}
		return false;
	}

}
