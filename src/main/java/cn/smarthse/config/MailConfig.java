package cn.smarthse.config;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 邮件发送配置
 */

@Configuration
public class MailConfig {

	private final Log log = LogFactory.getLog(getClass());

	@Value("${mail.host}")
	private String host;

	@Value("${mail.username}")
	private String username;

	@Value("${mail.password}")
	private String password;

	@Value("${mail.port}")
	private Integer port;

	@Value("${mail.smtp.timeout}")
	private String timeout;

	@Value("${mail.smtp.auth}")
	private String auth;

	@Value("${mail.smtp.starttls.enable}")
	private String starttls;

	@Value("${mail.port}")
	private String socketFactoryPort;

	private String socketFactoryClass = "javax.net.ssl.SSLSocketFactory";

	private String socketFactoryFallback = "false";

	@Value("${mail.from}")
	private String from; // 发件人

	// step1： Spring提供的发送电子邮件的高级抽象类
	@Bean
	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl a = new JavaMailSenderImpl();
		a.setHost(host);
		a.setPort(port);
		a.setUsername(username);
		a.setPassword(password);

		Properties p = new Properties();
		p.setProperty("mail.smtp.timeout", timeout);
		p.setProperty("mail.smtp.auth", auth);
		p.setProperty("mail.smtp.starttls.enable", starttls);
		p.setProperty("mail.smtp.socketFactory.port", socketFactoryPort);
		p.setProperty("mail.smtp.socketFactory.class", socketFactoryClass);
		p.setProperty("mail.smtp.socketFactory.fallback", socketFactoryFallback);
		a.setJavaMailProperties(p);
		log.info("Mail step1：定义JavaMailSender: " + a.toString());
		return a;
	}

	// step2： SimpleMailMessage
	@Bean
	public SimpleMailMessage simpleMailMessage() {
		SimpleMailMessage a = new SimpleMailMessage();
		a.setFrom(from);
		log.info("Mail step2：定义SimpleMailMessage: 发件人信息");
		return a;
	}

	// step3：配置线程池
	@Bean
	public ThreadPoolTaskExecutor ThreadPoolTaskExecutor() {
		ThreadPoolTaskExecutor a = new ThreadPoolTaskExecutor();
		a.setCorePoolSize(5);// 线程池维护线程的最少数量
		a.setKeepAliveSeconds(30000);// 线程池维护线程所允许的空闲时间
		a.setMaxPoolSize(50);// 线程池维护线程的最大数量
		a.setQueueCapacity(100);// 线程池所使用的缓冲队列
		log.info("Mail step3：定义ThreadPoolTaskExecutor，线程池发送邮件");
		return a;
	}

}
