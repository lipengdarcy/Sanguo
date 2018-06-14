package cn.smarthse.framework.security;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import cn.smarthse.business.model.AdminUser;
import cn.smarthse.business.service.security.UserManager;
import cn.smarthse.framework.Message;
import cn.smarthse.framework.util.Encodes;

/**
 * 用户的认证授权域
 */
public class ShiroRealm extends AuthorizingRealm {

	private Log log = LogFactory.getLog(getClass());

	@Resource
	private UserManager userManager;

	/**
	 * 构造函数，设置安全的初始化信息
	 */
	public ShiroRealm() {
		super();
		setAuthenticationTokenClass(ShiroUsernamePasswordToken.class);
	}

	/**
	 * 根据认证方式（如表单）获取用户名称、密码
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		ShiroUsernamePasswordToken upToken = (ShiroUsernamePasswordToken) token;
		String username = upToken.getUsername();
		if (StringUtils.isEmpty(username)) {
			// 登录账号为空
			throw new AccountException(Message.bundle.getString(Message.username_cannotbe_null));
		}
		AdminUser user = null;
		try {
			user = userManager.getUserByName(username);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// 账号不存在
		if (user == null) {
			throw new UnknownAccountException(Message.bundle.getString(Message.user_not_exist));
		}
		byte[] salt = Encodes.hexDecode(user.getSalt());
		ShiroPrincipal subject = new ShiroPrincipal(user);
		return new SimpleAuthenticationInfo(subject, user.getPassWord(), ByteSource.Util.bytes(salt), getName());
	}

	/**
	 * 获取当前认证实体的授权信息（授权包括：角色、权限）
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		ShiroPrincipal admin = null;
		// 获取当前登录的用户名
		Object obj = super.getAvailablePrincipal(principals);
		if (obj instanceof ShiroPrincipal) {
			admin = (ShiroPrincipal) obj;
		} else {
			return info;
		}
		String username = admin.getUser().getUserName();
		Integer userId = admin.getUser().getId();
		try {
			if (!admin.isAuthorized()) {
				// 根据用户id，获取该用户所有角色列表
				//List<String> rolelist = userManager.getRolesName(userId);
				//admin.setRoles(rolelist);
				//admin.setAuthorized(true);
				log.info("监管用户【" + username + "】 角色列表为：" + admin.getRoles());
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new AuthorizationException("监管用户【" + username + "】授权失败");
		}
		// 给当前用户设置角色
		info.addRoles(admin.getRoles());
		return info;
	}

	/**
	 * 认证密码匹配调用方法
	 */
	@Override
	protected void assertCredentialsMatch(AuthenticationToken authcToken, AuthenticationInfo info)
			throws AuthenticationException {
		ShiroUsernamePasswordToken token = (ShiroUsernamePasswordToken) authcToken;
		// 单点登录不验证密码
		if (token.getLoginType() == 0)
			return;
		super.assertCredentialsMatch(token, info);
	}

}
