package cn.smarthse.business.service.jms.business;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.smarthse.business.dao.system.ASyslogMapper;
import cn.smarthse.business.model.system.ASyslog;

/**
 * 日志处理器（更适合放在data层） 因为：
 * 1、data依赖于rpc，而rpc不依赖于data，所以如果该类放在rpc层，并且该类需要用到数据库操作（eg.将日志写入数据库），那么就不好办了
 * 2、rpc层说白了，就是一些rpc工具类，实际上与业务无关，与业务有关的，我们可以抽取到该部分来
 */
@Component
public class LogMessageHandler {
	
	private final Log log = LogFactory.getLog(getClass());

	@Autowired
	private ASyslogMapper logDao;

	public void handle(Message message) {
		log.debug("接收业务对象消息：" + message.toString());
		ObjectMessage objMsg = (ObjectMessage) message;
		try {
			ASyslog log = (ASyslog) objMsg.getObject();
			logDao.insertSelective(log);// 将日志写入数据库
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}

}
