package org.darcy.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerConfig {

	private final Log log = LogFactory.getLog(getClass());

	// 服务器名，可任意设定，最多6个汉字
	@Value("${sanguo.name}")
	private String name;

	// 服务器ID，全局唯一。由于合服等需要，目前只支持1~999
	@Value("${sanguo.serverId}")
	private Integer serverId;

	// 服务器区号，可随意设定。惯例是按照开服顺序1、2、3.... 一组服务器可以配置多个区号(英文逗号分隔)，合服后专用
	@Value("${sanguo.numbers}")
	private String numbers;

	// 策划配置数据位置
	@Value("${sanguo.resourceDir}")
	private String resourceDir;

	// 服务器地址
	@Value("${sanguo.serverIp}")
	private String serverIp;

	// 服务器端口
	@Value("${sanguo.serverPort}")
	private Integer serverPort;

	// GM服务器地址
	@Value("${sanguo.gmIp}")
	private String gmIp;

	// GM服务器端口
	@Value("${sanguo.gmPort}")
	private Integer gmPort;

	// GM心跳URL
	@Value("${sanguo.gmHeart}")
	private String gmHeart;

	// 网关服务器ip地址、端口号。服务器可以配置多个网关(英文逗号分隔)，即一个服务器同时出现在多个网关的服务器列表上
	@Value("${sanguo.gates}")
	private String gates;

}
