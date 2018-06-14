package test.jms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.smarthse.AppStart;
import cn.smarthse.business.service.jms.JmsMessageConsumer;
import cn.smarthse.business.service.jms.JmsMessageProducer;

/**
 * Author: 遇见小星 Email: tengxing7452@163.com Date: 17-6-16 Time: 下午12:18
 * Describe: member应用测试类
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppStart.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class JmsMessageProducerTest {

	@Autowired
	JmsMessageProducer jmsMessageProducer;

	@Autowired
	JmsMessageConsumer jmsMessageConsumer;

	// 1.测试发送异步消息
	@Test
	public void testSend() {
		jmsMessageProducer.sendMessages();
	}

	// 2.测试同步接收消息
	@Test
	public void testReceive() {
		jmsMessageConsumer.receiveMessages();
	}
}