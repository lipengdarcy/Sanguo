package cn.smarthse.business.service.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.smarthse.business.model.system.ASyslog;
import cn.smarthse.business.service.jms.business.LogMessageHandler;

/**
 * 方式二：写一个JmsMessageListener监听器异步接收消息
 */

@Component
public class JmsMessageListener implements MessageListener {
	private final Log log = LogFactory.getLog(getClass());

	@Autowired
	private LogMessageHandler LogMessageHandler;

	public void onMessage(Message message) {
		try {
			Object msg = ((ObjectMessage) message).getObject();
			if (msg instanceof ASyslog) {
				LogMessageHandler.handle(message);
			}
			if (msg instanceof BusinessObject) {
				LogMessageHandler.handle(message);
				log.debug("消息接收(BusinessObject): " + msg.toString());
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}

		log.debug("消息接收: " + message.toString());
	}
}
