package cn.smarthse.business.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.smarthse.business.service.jms.BusinessObject;
import cn.smarthse.business.service.jms.JmsMessageConsumer;
import cn.smarthse.business.service.jms.JmsMessageProducer;

@Controller
@RequestMapping("/")
public class HomeController {

	@Autowired
	private JmsMessageProducer JmsMessageProducer;

	@Autowired
	private JmsMessageConsumer JmsMessageConsumer;


	/**
	 * 首页
	 */
	@RequestMapping()
	public String init(ModelMap m) {
		return "index";
	}

	/**
	 * 空白页，便于快速构建页面
	 */
	@RequestMapping("blank")
	public String blank(ModelMap m) {
		return "common/blank";
	}

	

	@ResponseBody
	@RequestMapping("jms")
	public String jms(ModelMap m) {
		BusinessObject obj = new BusinessObject();
		obj.setIndex(100);
		obj.setDetail("测试对象消息");
		JmsMessageProducer.sendMessages(obj);
		return JmsMessageConsumer.receiveMessages();
	}

}
