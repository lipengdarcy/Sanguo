package cn.smarthse.config.dubbo;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Dubbo 对外提供的服务配置(xml形式)
 **/

@Configuration
@ImportResource({ "classpath:dubbo/SpringBootDubbo.xml" })
public class DubboConfig {

}