package com.sunrun.washer.controller;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jeecms.common.page.Pagination;
import com.jeecms.common.page.SimplePage;
import com.jeecms.common.web.CookieUtils;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.manager.CmsUserMng;
import com.sunrun.washer.entity.Machine;
import com.sunrun.washer.entity.UserMachine;
import com.sunrun.washer.manager.MachineMng;
import com.sunrun.washer.manager.UserMachineMng;
import com.sunrun.washer.model.MachineModel;
import com.sunrun.washer.model.MachineModelSave;
import com.sunrun.washer.model.MachineModelUpdate;
import com.sunrun.washer.model.MachineWithUserModel;
import com.sunrun.washer.model.UserMachineModel;
/**
 * 文 件 名 : MachineAct.java
 * 创 建 人： 金明明
 * 日 期：2017-7-31
 * 修 改 人： 
 * 日 期： 
 * 描 述：洗衣机 Act层
 */
@Controller
public class MachineAct {

	@Autowired
	private MachineMng machineMng;
	@Autowired
	private UserMachineMng userMachineMng;
	@Autowired
	private CmsUserMng cmsUserMng;
	
	/**
	 * 查询渠道商洗衣机列表
	 * @param userMachineModel 洗衣机管理查询条件
	 * @param pageNo 当前页
	 * @return
	 */
	@RequestMapping("/machine/queryUserMachineByModel.do")
	public String queryUserMachineByModel(UserMachineModel userMachineModel, Integer pageNo, HttpServletRequest request, ModelMap model) {
		Pagination pagination = userMachineMng.queryUserMachineByModel(userMachineModel, SimplePage.cpn(pageNo), CookieUtils.getPageSize(request));
		CmsUser user = cmsUserMng.findById(userMachineModel.getUserId());
		model.addAttribute("user", user);
		model.addAttribute("userMachineModel", userMachineModel);
		model.addAttribute("pagination", pagination);
		model.addAttribute("pageNo", pageNo);
		return "machine/listMachineWithUser";
	}
	
	/**
	 * 查询渠道商洗衣机列表-可查询授权情况
	 * @param userMachineModel 洗衣机管理查询条件
	 * @param pageNo 当前页
	 * @return
	 */
	@RequestMapping("/machine/queryMachineWithUserByModel.do")
	public String queryMachineWithUserByModel(MachineWithUserModel machineWithUserModel, Integer pageNo, HttpServletRequest request, ModelMap model) {
		Pagination pagination = machineMng.queryMachineWithUserByModel(machineWithUserModel, SimplePage.cpn(pageNo), CookieUtils.getPageSize(request));
		model.addAttribute("machineWithUserModel", machineWithUserModel);
		model.addAttribute("pagination", pagination);
		model.addAttribute("pageNo", pageNo);
		return "machine/machineSelectlist";
	}
	
	/**
	 * 取消授权
	 * @param machineModelUpdate 修改内容
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/machine/deleteMachineConnect.do")
	public String deleteMachineConnect(Integer userId, Integer machineId, ModelMap model, HttpServletRequest request) {
		MachineModelUpdate machineModelUpdate = new MachineModelUpdate();
		machineModelUpdate.setMachineId(machineId);
		machineModelUpdate.setUserName(null);
		Machine machine = machineMng.updateMachine(machineModelUpdate);
		return queryUserMachineByModel(new UserMachineModel(userId), 1, request, model);
	}
	
	/**
	 * 授权洗衣机
	 * @param machineId 洗衣机ID
	 * @param userName 用户名
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/machine/addMachineConnect.do")
	public String addMachineConnect(Integer userId, Integer machineId, String userName, ModelMap model, HttpServletRequest request) {
		MachineModelUpdate machineModelUpdate = new MachineModelUpdate();
		machineModelUpdate.setMachineId(machineId);
		machineModelUpdate.setUserName(userName);
		Machine machine = machineMng.updateMachine(machineModelUpdate);
		return queryUserMachineByModel(new UserMachineModel(userId), 1, request, model);
	}
	
	
	/**
	 * 查询洗衣机管理列表
	 * @param machineModel 洗衣机管理查询条件
	 * @param pageNo 当前页
	 * @return
	 */
	@RequestMapping("/machine/queryMachineByModel.do")
	public String queryMachineByModel(MachineModel machineModel, Integer pageNo, HttpServletRequest request, ModelMap model) {
		Pagination pagination = machineMng.queryMachineByModel(machineModel, SimplePage.cpn(pageNo), CookieUtils.getPageSize(request));
		model.addAttribute("machineModel", machineModel);
		model.addAttribute("pagination", pagination);
		model.addAttribute("pageNo", pageNo);
		// 编写代码
		return "machine/list";
	}

	/**
	 * 添加洗衣机管理
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/machine/addMachine.do")
	public String addMachine(ModelMap model) {
		return "machine/add";
	}
	
	/**
	 * 保存洗衣机管理
	 * @param machineModelSave
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/machine/saveMachine.do")
	public String saveMachine(MachineModelSave machineModelSave, HttpServletRequest request, ModelMap model) {
		// 保存
		machineMng.saveMachine(machineModelSave);
		return queryMachineByModel(new MachineModel(), 1, request, model);
	}

	/**
	 * 跳转至洗衣机管理修改页
	 * @param machineId 洗衣机管理Id
	 * @param model 
	 * @return
	 */
	@RequestMapping(value = "/machine/editMachine.do")
	public String editMachine(Integer machineId, ModelMap model, HttpServletRequest request) {
		Machine machine = machineMng.findById(machineId);
		// 获取原渠道商
		List<UserMachine>  userMachines = userMachineMng.findUserMachineListByMachine(machineId);
		if (userMachines != null && userMachines.size() > 0) {
			model.put("userName", userMachines.get(0).getCmsUser().getUsername());
		}
		model.put("bean", machine);
		return "machine/edit";
	}
	
	/**
	 * 更新洗衣机管理
	 * @param machineModelUpdate 修改内容
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/machine/updateMachine.do")
	public String updateMachine(MachineModelUpdate machineModelUpdate, ModelMap model, HttpServletRequest request) {
		Machine machine = machineMng.updateMachine(machineModelUpdate);
		return queryMachineByModel(new MachineModel(), 1, request, model);
	}

	/**
	 * 删除洗衣机管理
	 * @param machineId 洗衣机管理Id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/machine/deleteMachine.do")
	public String deleteMachine(Integer machineId, ModelMap model, HttpServletRequest request) {
		Machine machine = machineMng.deleteById(machineId);
		return queryMachineByModel(new MachineModel(), 1, request, model);
	}

	/**
	 * 查询洗衣机管理详情
	 * @param id 洗衣机管理id
	 * @return
	 */
	@RequestMapping("/machine/viewMachine.do")
	public String queryMachineByModel(Integer machineId, HttpServletRequest request, ModelMap model) {
		Machine machine = machineMng.findById(machineId);
		model.addAttribute("bean", machine);
		return "machine/view";
	}


}

