package cn.smarthse.business.service.jms.business;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import cn.smarthse.business.model.system.ASyslog;

@Component
public class LogMessageProducer {
	private final Log log = LogFactory.getLog(getClass());

	@Autowired
	protected JmsTemplate jmsTemplate;
	protected int numberOfMessages = 10;

	// 发送业务对象消息
	public void sendMessages(ASyslog m) {
		log.debug("发送业务对象消息：" + m.getDescription());
		jmsTemplate.convertAndSend(m);
	}

}
