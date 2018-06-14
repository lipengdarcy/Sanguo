package cn.smarthse.business.service.jms;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * 
 * 方式一：写一个JmsMessageConsumer 同步接收消息
 */

@Component
public class JmsMessageConsumer {
	private final Log log = LogFactory.getLog(getClass());

	@Autowired
	private JmsTemplate template;

	public String receiveMessages() {
		TextMessage textMessage = (TextMessage) template.receive();
		String m = null;
		try {
			m = textMessage.getText();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		log.info(m);
		return m;
	}
}