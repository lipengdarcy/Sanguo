package cn.smarthse.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.pam.FirstSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

import cn.smarthse.framework.security.DefaultModularRealmAuthenticator;
import cn.smarthse.framework.security.HSEShiroFilterFactoryBean;
import cn.smarthse.framework.security.HSEShiroSessionDao;
import cn.smarthse.framework.security.ShiroAuthFilter;
import cn.smarthse.framework.security.ShiroAuthorizingCredentialsMatcher;
import cn.smarthse.framework.security.ShiroRealm;

/**
 * Shiro 配置文件
 **/

@Configuration
@Import({ EhcacheConfig.class })
@Order(1)
public class SpringShiroConfig implements EnvironmentAware {

	private final Log log = LogFactory.getLog(getClass());

	@Override
	public void setEnvironment(Environment environment) {
		this.env = environment;
	}

	@Autowired
	private Environment env;

	// shiro缓存管理器
	@Bean(name = "shirocacheManager")
	@DependsOn({ "HSECacheManager" })
	public CacheManager shirocacheManager(net.sf.ehcache.CacheManager manager) {
		log.info("1. Shiro 缓存管理器");
		EhCacheManager a = new EhCacheManager();
		a.setCacheManager(manager);
		return a;
	}

	// 凭证匹配器
	@Bean(name = "credentialsMatcher")
	public CredentialsMatcher credentialsMatcher(CacheManager cacheManager) {
		log.info("2. Shiro 凭证匹配器");
		ShiroAuthorizingCredentialsMatcher a = new ShiroAuthorizingCredentialsMatcher(cacheManager);
		a.setHashAlgorithmName("SHA-1");
		a.setHashIterations(1024);
		a.setStoredCredentialsHexEncoded(true);
		a.setRetryCount(5);
		return a;
	}

	// 医院端认证授权域
	@Bean(name = "shiroRealm")
	public ShiroRealm shiroRealm(CredentialsMatcher cm) {
		log.info("3.1 ShiroRealm 医院端认证授权域 ");
		ShiroRealm a = new ShiroRealm();
		a.setCredentialsMatcher(cm);
		// a.setCachingEnabled(false);
		return a;
	}

	// 监管端认证授权域
	@Bean(name = "businessShiroRealm")
	public ShiroRealm businessShiroRealm(CredentialsMatcher cm) {
		log.info("3.2 ShiroRealm 用户认证授权域");
		ShiroRealm a = new ShiroRealm();
		a.setCredentialsMatcher(cm);
		return a;
	}

	// 多个realm的认证
	@Bean(name = "modularRealmAuthenticator")
	public ModularRealmAuthenticator modularRealmAuthenticator(ShiroRealm shiroRealm) {
		log.info("3.3. Shiro Authenticator： 多个realm的认证");
		DefaultModularRealmAuthenticator a = new DefaultModularRealmAuthenticator();
		// a.setAuthenticationStrategy(new AtLeastOneSuccessfulStrategy());
		Collection<Realm> realms = new ArrayList<Realm>();
		realms.add(shiroRealm);
		//realms.add(adminShiroRealm);
		a.setRealms(realms);
		Map<String, Object> definedRealms = new HashMap<String, Object>();
		definedRealms.put("shiroRealm", shiroRealm);
		//definedRealms.put("adminShiroRealm", adminShiroRealm);
		a.setDefinedRealms(definedRealms);
		a.setAuthenticationStrategy(new FirstSuccessfulStrategy());
		return a;
	}

	// 多个realm的授权
	@Bean(name = "modularRealmAuthorizer")
	public ModularRealmAuthorizer modularRealmAuthorizer(ShiroRealm shiroRealm) {
		log.info("3.4 Shiro Authorizer： 多个realm的授权");
		ModularRealmAuthorizer a = new ModularRealmAuthorizer();
		Collection<Realm> realms = new ArrayList<Realm>();
		realms.add(shiroRealm);
		//realms.add(adminShiroRealm);
		a.setRealms(realms);
		return a;
	}

	// 会话ID生成器
	@Bean(name = "sessionIdGenerator")
	public SessionIdGenerator sessionIdGenerator() {
		log.info("4. Shiro 会话ID生成器 ");
		JavaUuidSessionIdGenerator a = new JavaUuidSessionIdGenerator();
		return a;
	}

	// 会话Cookie模板
	@Bean(name = "sessionIdCookie")
	public Cookie sessionIdCookie() {
		log.info("5. Shiro 会话Cookie模板 ");
		SimpleCookie a = new SimpleCookie();
		a.setName("sessionIdCookie-HSE");
		a.setHttpOnly(true);
		a.setMaxAge(-1);
		return a;
	}

	// 记住密码Cookie模板
	@Bean(name = "rememberMeCookie")
	public Cookie rememberMeCookie() {
		log.info("6. Shiro 记住密码Cookie模板 ");
		SimpleCookie a = new SimpleCookie();
		a.setName("rememberMeCookie-HSE");
		a.setHttpOnly(true);
		a.setMaxAge(2592000);// 30天
		return a;
	}

	// 记住密码Cookie模板
	@Bean(name = "rememberMeManager")
	@DependsOn({ "rememberMeCookie" })
	public RememberMeManager rememberMeManager(Cookie rememberMeCookie) {
		log.info("7. Shiro 记住密码管理器");
		CookieRememberMeManager a = new CookieRememberMeManager();
		byte[] key = org.apache.shiro.codec.Base64.decode("4AvVhmFLUs0KTA3Kprsdag==");
		a.setCipherKey(key);
		a.setCookie(rememberMeCookie);
		return a;
	}

	// 会话DAO
	@Bean(name = "sessionDAO")
	@DependsOn({ "sessionIdGenerator" })
	public SessionDAO sessionDAO(SessionIdGenerator sessionIdGenerator) {
		log.info("8. Shiro 会话DAO");
		HSEShiroSessionDao a = new HSEShiroSessionDao();
		a.setActiveSessionsCacheName("shiro-activeSessionCache");
		a.setSessionIdGenerator(sessionIdGenerator);
		a.setRedisCachable(Boolean.valueOf(env.getProperty("redis.cacheable")));
		return a;
	}

	@Bean(name = "sessionManager")
	@DependsOn({ "sessionIdCookie" })
	public SessionManager sessionManager(Cookie sessionIdCookie, SessionDAO sessionDAO) {
		log.info("9. Shiro sessionManager 会话管理器");
		DefaultWebSessionManager a = new DefaultWebSessionManager();
		a.setGlobalSessionTimeout(3600000);
		a.setDeleteInvalidSessions(true);
		a.setSessionIdCookieEnabled(true);
		a.setSessionDAO(sessionDAO);
		a.setSessionIdCookie(sessionIdCookie);
		return a;
	}

	// 安全管理器
	@Bean(name = "securityManager")
	@DependsOn({ "shiroRealm", "sessionManager", "shirocacheManager", "rememberMeManager" })
	public org.apache.shiro.mgt.SecurityManager securityManager(ModularRealmAuthenticator authenticator,
			ModularRealmAuthorizer authorizer, SessionManager b, CacheManager c, RememberMeManager d) {
		log.info("10. Shiro securityManager 安全管理器");
		DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
		manager.setAuthenticator(authenticator); // 认证器
		manager.setAuthorizer(authorizer);// 授权器
		manager.setSessionManager(b);
		manager.setCacheManager(c);
		manager.setRememberMeManager(d);
		return manager;
	}

	@Bean(name = "shiroFilter")
	public ShiroFilterFactoryBean shiroFilter(org.apache.shiro.mgt.SecurityManager sm, SessionDAO dao) {

		log.info("11. Shiro shiroFilter");
		HSEShiroFilterFactoryBean a = new HSEShiroFilterFactoryBean();
		a.setSecurityManager(sm);
		// 配置我们的登录请求地址
		a.setLoginUrl("/security/login");
		// 配置我们在登录页登录成功后的跳转地址，如果你访问的是非/login地址，则跳到您访问的地址 -->
		a.setSuccessUrl("/home");
		// 跳转到/403请求地址 -->
		a.setUnauthorizedUrl("/403");

		// 定义医院用户过滤器
		Map<String, Filter> filters = new HashMap<String, Filter>();
		filters.put("sysAuth", new ShiroAuthFilter(dao));
		LogoutFilter logoutFilter = new LogoutFilter();
		logoutFilter.setRedirectUrl("/security/login");
		filters.put("logout", logoutFilter);

		// 定义监管用户过滤器
		LogoutFilter adminLogoutFilter = new LogoutFilter();
		adminLogoutFilter.setRedirectUrl("/admin/login");
		filters.put("adminLogout", adminLogoutFilter);
		a.setFilters(filters);

		// 定义过滤规则
		Map<String, String> filterChainDefinitionMap = new HashMap<String, String>();
		filterChainDefinitionMap.put("/**", "sysAuth"); // 表示此地址需要过滤器的过滤
		filterChainDefinitionMap.put("/fileupload/upload", "anon");
		filterChainDefinitionMap.put("/security/login", "anon");
		filterChainDefinitionMap.put("/security/login/cas", "anon");
		filterChainDefinitionMap.put("/security/validateCode", "anon");
		filterChainDefinitionMap.put("/static/**", "anon"); // anon表示此地址不需要任何权限即可访问
		filterChainDefinitionMap.put("/security/logout", "logout");

		// 监管用户部分
		// 监管端的url统一用admin开头
		filterChainDefinitionMap.put("/admin/login", "anon");
		filterChainDefinitionMap.put("/admin/logout", "adminLogout");

		a.setFilterChainDefinitionMap(filterChainDefinitionMap);

		return a;
	}

	/**
	 * 1.Shiro生命周期处理器
	 */
	@Bean(name = "lifecycleBeanPostProcessor")
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	/**
	 * 3. 开启shiro aop注解支持. 使用代理方式;所以需要开启代码支持;
	 * 
	 * @param securityManager
	 * @return
	 */
	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(
			org.apache.shiro.mgt.SecurityManager sm) {
		AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
		authorizationAttributeSourceAdvisor.setSecurityManager(sm);
		return authorizationAttributeSourceAdvisor;
	}

}
