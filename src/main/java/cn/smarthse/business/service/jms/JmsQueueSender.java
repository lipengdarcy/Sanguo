package cn.smarthse.business.service.jms;

import java.util.HashMap;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.core.MessagePostProcessor;

/**
 * 
 * 发送消息:
 * 
 * JmsTemplate包含许多方便的方法来发送消息。有些发送方法可以使用 javax.jms.Destination对象指定目的地，
 * 
 * 也可以使用字符串在 JNDI 中查找目的地。没有目的地参数的发送方法使用默认的目的地。
 */
public class JmsQueueSender {

	private JmsTemplate jmsTemplate;
	private Queue queue;

	public void setConnectionFactory(ConnectionFactory cf) {
		this.jmsTemplate = new JmsTemplate(cf);
	}

	public void setQueue(Queue queue) {
		this.queue = queue;
	}

	public void simpleSend() {
		this.jmsTemplate.send(this.queue, new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage("hello queue world");
			}
		});
	}

	public void sendWithConversion() {
		Map map = new HashMap();
		map.put("Name", "Mark");
		map.put("Age", new Integer(47));
		jmsTemplate.convertAndSend("testQueue", map, new MessagePostProcessor() {
			public Message postProcessMessage(Message message) throws JMSException {
				message.setIntProperty("AccountID", 1234);
				message.setJMSCorrelationID("123-00001");
				return message;
			}
		});
	}
}