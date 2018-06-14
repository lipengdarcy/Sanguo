package cn.smarthse.business.controller;

import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.smarthse.business.model.AdminUser;
import cn.smarthse.business.service.security.UserManager;
import cn.smarthse.framework.Constant;
import cn.smarthse.framework.Message;
import cn.smarthse.framework.model.ResponseData;
import cn.smarthse.framework.security.ShiroPrincipal;
import cn.smarthse.framework.security.ShiroUsernamePasswordToken;

/**
 * 用户登录Controller
 */
@Controller
public class LoginController {

	private Log log = LogFactory.getLog(getClass());

	@Autowired
	private UserManager userManager;

	@ResponseBody
	@RequestMapping(value = "/security/login", method = RequestMethod.POST)
	public ResponseData<String> login(AdminUser user, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		ResponseData<String> r = new ResponseData<String>();		
		Subject subject = SecurityUtils.getSubject();
		// 1、收集实体/凭据信息
		ShiroUsernamePasswordToken token = new ShiroUsernamePasswordToken(1);
		token.setUsername(user.getUserName());
		token.setPassword(user.getPassWord() != null ? user.getPassWord().toCharArray() : null);
		String remember = WebUtils.getCleanParam(request, "remember");
		try {
			userManager.clearUserByNameCache(user.getUserName());
			if (remember != null && remember.equalsIgnoreCase("1")) {
				token.setRememberMe(true);
			}
			// 2、提交实体/凭据信息
			subject.login(token);
			ShiroPrincipal p = (ShiroPrincipal) subject.getPrincipal();
			// 用户id， 登录用户所在行政区域，写入session
			// 登录用户编号
			request.getSession().setAttribute(Constant.ACCOUNT_SESSION_UID, p.getUser().getId());
			// 登录用户所在行政区域id
			request.getSession().setAttribute(Constant.areaId, p.getUser().getAreaId());
			// 登录用户所在行政区域名称
			request.getSession().setAttribute(Constant.areaName, p.getUser().getAreaName());
			// 登入用户名称
			request.getSession().setAttribute(Constant.ACCOUNT_SESSION_UNAME, p.getUser().getRealName());
			// 登入用户头像
			request.getSession().setAttribute(Constant.ACCOUNT_USER_ICON, p.getUser().getUserIconUrl());
			return r;
		} catch (UnknownAccountException ue) {
			ue.printStackTrace();
			token.clear();
			r.setCode(-1);
			r.setMessage(Message.bundle.getString(Message.user_not_exist));
			return r;
		} catch (IncorrectCredentialsException ie) {
			token.clear();
			r.setCode(-2);
			r.setMessage(Message.bundle.getString(Message.user_password_error));
			return r;
		} catch (ExcessiveAttemptsException e) {
			// 密码错误次数超过5次，需要配置ehcache.xml.passwordRetryCache（锁定10分钟）
			token.clear();
			r.setCode(-2);
			r.setMessage("密码错误次数超过5次, 10分钟后重试！");
			return r;
		} catch (RuntimeException re) {
			re.printStackTrace();
			token.clear();
			r.setCode(-3);
			r.setMessage(re.getMessage());
			return r;
		}
	}

	/**
	 * 登录页面
	 */
	@RequestMapping(value = "/security/login", method = RequestMethod.GET)
	public String loginPage() throws IOException {
		return "login";
	}

	@RequestMapping(value = "/security/logout", method = RequestMethod.GET)
	public String logout() {
		Subject subject = SecurityUtils.getSubject();
		String userName = subject.getPrincipal().toString();
		if (subject.isAuthenticated()) {
			subject.logout(); // session 会销毁，在SessionListener监听session销毁，清理权限缓存
			String message = MessageFormat.format(Message.bundle.getString(Message.user_logout_success), userName);
			log.info(message);
		}
		return "redirect:/security/login";
	}

}
