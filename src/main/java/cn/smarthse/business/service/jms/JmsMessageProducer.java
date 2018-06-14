package cn.smarthse.business.service.jms;

import java.util.Date;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import cn.smarthse.framework.model.mail.Mail;

@Component
public class JmsMessageProducer {
	private final Log log = LogFactory.getLog(getClass());

	@Autowired
	protected JmsTemplate jmsTemplate;
	protected int numberOfMessages = 10;

	// 发送文本消息
	public void sendMessages() {
		StringBuilder payload = null;
		for (int i = 0; i < numberOfMessages; ++i) {
			payload = new StringBuilder();
			payload.append("Message [").append(i).append("] sent at: ").append(new Date());
			jmsTemplate.convertAndSend(payload.toString());
			log.info("Sending message number [" + i + "]");
		}
	}

	// 发送业务对象消息
	public void sendMessages(BusinessObject m) {
		jmsTemplate.convertAndSend(m);
	}

	// 发送邮件消息
	public void sendMessages(Mail mail) {
		jmsTemplate.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(JSONObject.toJSONString(mail));
			}
		});
	}
}
