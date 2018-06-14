package cn.smarthse.framework.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.smarthse.business.model.AdminUser;
import lombok.Data;

/**
 * 用户认证主体
 */
public @Data class ShiroPrincipal implements Serializable {

	private static final long serialVersionUID = 1428196040744555722L;

	// 用户对象
	private AdminUser user;

	// 用户角色列表
	private List<String> roles = new ArrayList<String>();

	private boolean isAuthorized = false;

	// 登入用户的IP地址
	private String loginUserIp;

	/**
	 * 构造函数，参数为AdminUser对象
	 * 
	 * @param user
	 */
	public ShiroPrincipal(AdminUser user) {
		this.user = user;
	}

	/**
	 * <shiro:principal/>标签显示中文名称
	 * PS:此处只能返回用户名，KickoutSessionControlFilter过滤器根据这个登录名进行验证是否重复登录
	 */
	@Override
	public String toString() {
		return this.user.getUserName();
	}

}
