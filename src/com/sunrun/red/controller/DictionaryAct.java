package com.sunrun.red.controller;

import static com.jeecms.common.page.SimplePage.cpn;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jeecms.common.page.Pagination;
import com.jeecms.common.web.CookieUtils;
import com.jeecms.common.web.ResponseUtils;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.manager.CmsLogMng;
import com.jeecms.core.web.WebErrors;
import com.jeecms.core.web.util.CmsUtils;
import com.sunrun.red.entity.Dictionary;
import com.sunrun.red.manager.DictionaryMng;

@Controller
public class DictionaryAct {
	private static final Logger log = LoggerFactory.getLogger(DictionaryAct.class);

//	@RequiresPermissions("dictionary:v_list")
	@RequestMapping("/dictionary/v_list.do")
	public String list(String queryType,Integer pageNo, HttpServletRequest request, ModelMap model) {
		Pagination pagination = manager.getPage(queryType,cpn(pageNo), CookieUtils
				.getPageSize(request));
		List<String>typeList=manager.getTypeList();
		model.addAttribute("pagination",pagination);
		model.addAttribute("pageNo",pagination.getPageNo());
		model.addAttribute("typeList",typeList);
		model.addAttribute("queryType",queryType);
		return "dictionary/list";
	}

//	@RequiresPermissions("dictionary:v_add")
	@RequestMapping("/dictionary/v_add.do")
	public String add(ModelMap model) {
		return "dictionary/add";
	}

//	@RequiresPermissions("dictionary:v_edit")
	@RequestMapping("/dictionary/v_edit.do")
	public String edit(Integer id, Integer pageNo, HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateEdit(id, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		model.addAttribute("Dictionary", manager.findById(id));
		model.addAttribute("pageNo",pageNo);
		return "dictionary/edit";
	}
	
//	@RequiresPermissions("dictionary:v_ajax_edit")
	@RequestMapping("/dictionary/v_ajax_edit.do")
	public void ajaxEdit(Integer id, HttpServletRequest request,HttpServletResponse response, ModelMap model) throws JSONException {
		JSONObject object = new JSONObject();
		Dictionary dic=manager.findById(id);
		if(dic!=null){
			object.put("id", dic.getId());
			object.put("name", dic.getName());
			object.put("type", dic.getType());
			object.put("value", dic.getValue());
			object.put("remark", dic.getRemark());
			object.put("isRequired", dic.getIsRequired());
		}
		ResponseUtils.renderJson(response, object.toString());
	}

//	@RequiresPermissions("dictionary:o_save")
	@RequestMapping("/dictionary/o_save.do")
	public String save(Dictionary bean, HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateSave(bean, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		bean = manager.save(bean);
		log.info("save Dictionary id={}", bean.getId());
		cmsLogMng.operating(request, "Dictionary.log.save", "id="
				+ bean.getId() + ";name=" + bean.getName());
		return "redirect:v_list.do";
	}

//	@RequiresPermissions("dictionary:o_update")
	@RequestMapping("/dictionary/o_update.do")
	public String update(Dictionary bean,String queryType, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		WebErrors errors = validateUpdate(bean, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		bean = manager.update(bean);
		log.info("update Dictionary id={}.", bean.getId());
		cmsLogMng.operating(request, "Dictionary.log.update", "id="
				+ bean.getId() + ";name=" + bean.getName());
		return list(queryType,pageNo, request, model);
	}

//	@RequiresPermissions("dictionary:o_delete")
	@RequestMapping("/dictionary/o_delete.do")
	public String delete(Integer[] ids, String queryType,Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		WebErrors errors = validateDelete(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		Dictionary[] beans = manager.deleteByIds(ids);
		for (Dictionary bean : beans) {
			log.info("delete Dictionary id={}", bean.getId());
			cmsLogMng.operating(request, "Dictionary.log.delete", "id="
					+ bean.getId() + ";name=" + bean.getName());
		}
		return list(queryType,pageNo, request, model);
	}
	
//	@RequiresPermissions("dictionary:v_check_value")
	@RequestMapping(value = "/dictionary/v_check_value.do")
	public void checkDateValue(String value, String type,HttpServletResponse response) throws JSONException {
		JSONObject json=new JSONObject();
		String pass;
		if (StringUtils.isBlank(value)) {
			pass = "false";
		} else {
			pass = manager.dicDeplicateValue(value, type) ? "true" : "false";
		}
		json.put("pass", pass);
		ResponseUtils.renderJson(response, json.toString());
	}

	private WebErrors validateSave(Dictionary bean, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		if(manager.dicDeplicateValue(bean.getValue(), bean.getType())){
			errors.addErrorCode("Dictionary.value.deplicate",bean.getType(), bean.getValue());
		}
		return errors;
	}
	
	private WebErrors validateEdit(Integer id, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		if (vldExist(id, site.getId(), errors)) {
			return errors;
		}
		return errors;
	}

	private WebErrors validateUpdate(Dictionary bean, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		Dictionary entity=manager.findById(bean.getId());
		if (vldExist(bean.getId(), site.getId(), errors)) {
			return errors;
		}
		if(!entity.getValue().equals(bean.getValue())&&!entity.getType().equals(bean.getType())){
			if(manager.dicDeplicateValue(bean.getValue(), bean.getType())){
				errors.addErrorCode("Dictionary.value.deplicate",bean.getType(), bean.getValue());
			}
		}
		return errors;
	}

	private WebErrors validateDelete(Integer[] ids, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		if (errors.ifEmpty(ids, "ids")) {
			return errors;
		}
		for (Integer id : ids) {
			vldExist(id, site.getId(), errors);
		}
		return errors;
	}

	private boolean vldExist(Integer id, Integer siteId, WebErrors errors) {
		if (errors.ifNull(id, "id")) {
			return true;
		}
		Dictionary entity = manager.findById(id);
		if(errors.ifNotExist(entity, Dictionary.class, id)) {
			return true;
		}
		return false;
	}
	
	@Autowired
	private DictionaryMng manager;
	@Autowired
	private CmsLogMng cmsLogMng;
}