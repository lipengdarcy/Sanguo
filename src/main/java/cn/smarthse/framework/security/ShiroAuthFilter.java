package cn.smarthse.framework.security;

import java.io.Serializable;
import java.util.Collection;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;

import cn.smarthse.framework.util.HttpUtil;

/**
 * 自定义权限过滤器：1.过滤所有非静态资源的请求; 2.踢出重复登录的用户
 * 
 */
public class ShiroAuthFilter extends FormAuthenticationFilter {

	private final Log log = LogFactory.getLog(getClass());
	private SessionDAO sessionDAO;

	public ShiroAuthFilter() {
	}

	public ShiroAuthFilter(SessionDAO dao) {
		this.setSessionDAO(dao);
	}

	protected ServletResponse wrapServletResponse(HttpServletResponse orig, ShiroHttpServletRequest request) {
		return new HSEShiroHttpServletResponse(orig, getServletContext(), request);
	}

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		String uri = req.getServletPath();
		/**
		 * jdk 1.7编译的情况下，静态资源也会进入此过滤器，因此额外增加静态资源的判断（jdk1.8正常，静态资源不会进入此过滤器， shiro配置有效）
		 * 静态资源和验证码不需要过滤
		 */
		if (HttpUtil.isStaticResource(uri) || uri.startsWith("/security/validateCode")) {
			return true;
		}

		Subject subject = getSubject(request, response);
		Object p = subject.getPrincipal();
		
		//异步请求不拦截，方便监管端使用医院端的功能
		if (req.getHeader("x-requested-with") != null
				&& req.getHeader("x-requested-with").equals("XMLHttpRequest")) {
			resp.setHeader("sessionstatus", "timeout");
			return true;
		}
		
		// 1.没有登录
		if (p == null) {			
			return false;
		}
		
		Serializable thisSessionId = subject.getSession().getId();
		String username = subject.getPrincipal().toString();
		Collection<Session> sessionList = sessionDAO.getActiveSessions();
		for (Session a : sessionList) {
			// 当前session在列表，说明已经登录，需要踢出原来的
			String id = String.valueOf(a.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY));
			if (!a.getId().equals(thisSessionId) && StringUtils.equals(id, username)) {
				log.info("账号:" + id + "在别处登录，你被踢出系统了……");
				a.setTimeout(0);
			}
		}
		return super.isAccessAllowed(request, response, mappedValue)
				|| (!isLoginRequest(request, response) && isPermissive(mappedValue));
	}

	/**
	 * @return the sessionDAO
	 */
	public SessionDAO getSessionDAO() {
		return sessionDAO;
	}

	/**
	 * @param sessionDAO
	 *            the sessionDAO to set
	 */
	public void setSessionDAO(SessionDAO sessionDAO) {
		this.sessionDAO = sessionDAO;
	}

}
