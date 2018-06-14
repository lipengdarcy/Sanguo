package cn.smarthse.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import cn.smarthse.business.service.jms.JmsMessageListener;

//@Configuration
//@EnableJms
public class JmsConfig implements JmsListenerConfigurer {

	private final Log log = LogFactory.getLog(getClass());

	@Value("${jms.enable}")
	private Boolean enable;

	@Value("${jms.url}")
	private String url;

	@Value("${jms.queueName}")
	private String queueName;

	@Value("${jms.topicName}")
	private String topicName;

	@Autowired
	private JmsMessageListener jmsMessageListener;

	// 1. <!-- 配置连接ActiveMQ的ConnectionFactory -->
	@Bean("activeMQConnectionFactory")
	public ActiveMQConnectionFactory activeMQConnectionFactory() {
		if (enable) {
			ActiveMQConnectionFactory a = new ActiveMQConnectionFactory();
			a.setBrokerURL(url);
			a.setTrustAllPackages(true); // 所有序列化的对象都可以进入mq
			log.info("JMS step1： 配置连接ActiveMQ的ConnectionFactory: " + url);
			return a;
		}
		return null;
	}

	// 2. <!--为了提高效率，配置一个连接池-->
	@Bean("cachingConnectionFactory")
	public CachingConnectionFactory cachingConnectionFactory() {
		ActiveMQConnectionFactory factory = activeMQConnectionFactory();
		if (factory == null)
			return null;
		CachingConnectionFactory a = new CachingConnectionFactory();
		a.setTargetConnectionFactory(factory);
		a.setSessionCacheSize(10);
		log.info("JMS step2： ConnectionFactory的连接池 ");
		return a;
	}

	// 3.1 <!-- 配置broker的destination，点对点的消息-->
	@Bean
	public ActiveMQQueue activeMQQueue() {
		ActiveMQQueue a = new ActiveMQQueue(queueName);
		log.info("JMS step3.1： 配置点对点的消息broker的destination: " + queueName);
		return a;
	}

	// 3.2 <!-- 配置broker的destination，订阅/发布的消息-->
	@Bean
	public ActiveMQTopic activeMQTopic() {
		ActiveMQTopic a = new ActiveMQTopic(topicName);
		log.info("JMS step3.2： 配置订阅/发布的消息broker的destination: " + topicName);
		return a;
	}

	// 4. <!-- 配置Spring的JmsTemplate -->
	@Bean
	public JmsTemplate jmsTemplate(ActiveMQQueue destination) {
		JmsTemplate a = new JmsTemplate();
		CachingConnectionFactory cachedConnectionFactory = cachingConnectionFactory();
		if (cachedConnectionFactory == null)
			return a;
		
		a.setConnectionFactory(cachedConnectionFactory);
		a.setDefaultDestination(destination);
		log.info("JMS step4： 配置Spring的JmsTemplate");
		return a;
	}

	// 5. <!-- 配置Spring的JmsMessageListener -->
	@Bean
	public JmsMessageListener jmsMessageListener() {
		JmsMessageListener a = new JmsMessageListener();
		log.info("JMS step5： 配置Spring的JmsMessageListener");
		return a;
	}

	// 6. <!-- 配置listener到listener-container当中 -->
	@Bean
	public DefaultMessageListenerContainer defaultMessageListenerContainer(ActiveMQQueue destination,
			JmsMessageListener listener) {
		CachingConnectionFactory cachedConnectionFactory = cachingConnectionFactory();
		if (cachedConnectionFactory == null)
			return null;
		DefaultMessageListenerContainer a = new DefaultMessageListenerContainer();
		a.setConnectionFactory(cachedConnectionFactory);
		a.setDestination(destination);
		a.setMessageListener(listener);
		log.info("JMS step6： 配置listener到listener-container当中");
		return a;
	}

	@Override
	public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {
		SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
		endpoint.setId("myJmsEndpoint");
		endpoint.setDestination(queueName);
		endpoint.setMessageListener(jmsMessageListener);
		registrar.registerEndpoint(endpoint);
	}

}
