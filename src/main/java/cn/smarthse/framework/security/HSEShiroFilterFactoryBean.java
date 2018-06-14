package cn.smarthse.framework.security;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;

/**
 * 自定义 ShiroFilterFactoryBean, URL去掉JSESSIONID
 */
public class HSEShiroFilterFactoryBean extends ShiroFilterFactoryBean {

	@SuppressWarnings("rawtypes")
	@Override
	public Class getObjectType() {
		return ShiroAuthFilter.class;
	}

}