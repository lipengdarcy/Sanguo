package cn.smarthse.business.controller.ASystem;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.smarthse.business.model.AdminUser;
import cn.smarthse.business.service.security.UserManager;
import cn.smarthse.framework.Constant;
import cn.smarthse.framework.model.JqGridData;
import cn.smarthse.framework.model.JqGridParam;
import cn.smarthse.framework.model.ResponseData;

/**
 * 用户管理
 */
@Controller
@RequestMapping("/user")
public class UserController {

	private Log log = LogFactory.getLog(getClass());

	private final String basePath = "user/";

	@Autowired
	private UserManager UserManager;

	/**
	 * 系统管理：用户列表页面
	 */
	@RequestMapping()
	public String list(Integer ignor, HttpSession session, ModelMap m) {
		Integer uid = (Integer) session.getAttribute(Constant.ACCOUNT_SESSION_UID);
		AdminUser user = UserManager.getUserById(uid);
		m.put("role", user.getRole());
		return basePath + "userList";
	}

	/**
	 * 系统管理：用户列表数据
	 * 
	 * @param name
	 *            用户名或者姓名
	 */
	@ResponseBody
	@RequestMapping(value = "/listData")
	public JqGridData<AdminUser> listData(HttpSession session, JqGridParam param, String name) {
		JqGridData<AdminUser> data = UserManager.getUserGridData(param, name);
		return data;
	}

	/**
	 * 获取单个用户信息
	 * 
	 * @param uid
	 *            用户id
	 */
	@ResponseBody
	@RequestMapping(value = "getUser", method = RequestMethod.POST)
	public ResponseData<AdminUser> getUser(Integer uid, HttpSession session) {
		ResponseData<AdminUser> data = new ResponseData<AdminUser>();
		AdminUser user = UserManager.getUserById(uid);
		data.setData(user);
		return data;
	}
	

	/**
	 * 编辑用户
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "edit/{id}", method = RequestMethod.GET)
	public String edit(@PathVariable("id") Integer id, ModelMap m, HttpSession session) {
		Integer cid = (Integer) session.getAttribute(Constant.ACCOUNT_COMPANYID);		
		return basePath + "user";
	}

	

	/**
	 * 新增、编辑、删除用户
	 * 
	 * @param user
	 *            用户对象
	 * @param idlist
	 *            用户id列表，逗号分隔
	 * @param oper
	 *            操作类型：add,edit,del,batchdel
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "update", method = RequestMethod.POST)
	public ResponseData<Integer> update(String oper, String idlist, AdminUser user, HttpSession session) {
		ResponseData<Integer> data = new ResponseData<Integer>();
		switch (oper) {
		case "add":
			int r = UserManager.insert(user);
			if (r < 0) {
				data.setCode(-1);
				data.setMessage("用户名已存在");
				return data;
			}
			data.setMessage("新增成功！");
			break;
		case "edit":
			UserManager.update(user);
			data.setMessage("编辑成功！");
			break;
		case "del":
			UserManager.delete(user.getId());
			data.setMessage("删除成功！");
			break;
		case "batchdel":
			String[] idList = StringUtils.split(idlist, ",");
			for (String strId : idList) {
				UserManager.delete(Integer.valueOf(strId));
			}
			data.setMessage("批量删除成功！删除记录数：" + idList.length);
			break;
		default:
			break;
		}
		return data;
	}

	/**
	 * 系统管理：个人中心
	 */
	@RequestMapping("profile")
	public String profile(HttpSession session, ModelMap m) {
		Integer uid = (Integer) session.getAttribute(Constant.ACCOUNT_SESSION_UID);
		AdminUser user = UserManager.getUserById(uid);
		m.put("user", user);
		return basePath + "profile";
	}

	/**
	 * 密码修改
	 */
	@ResponseBody
	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)
	public ResponseData<String> changePassword(String oldPassword, AdminUser user, HttpSession session) {
		ResponseData<String> data = new ResponseData<String>();
		Integer uid = (Integer) session.getAttribute(Constant.ACCOUNT_SESSION_UID);
		user.setId(uid);
		int result = UserManager.changePassword(user, oldPassword);
		if (result < 0) {
			data.setCode(result);
			data.setMessage("原密码输入错误!");
			return data;
		} else {
			log.info("用户修改密码成功!");
			data.setData("用户修改密码成功!");
			return data;
		}

	}

}
