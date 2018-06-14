package cn.smarthse.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 静态资源配置
 * */
@Configuration
public class StaticResourceConfig extends WebMvcConfigurerAdapter{

	private final Log log = LogFactory.getLog(getClass());

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		log.info("指定静态资源目录：classpath:/static/");
		registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
	}
}
