package cn.smarthse.business.service.jms;

import java.util.Map;

import org.springframework.messaging.handler.annotation.Header;

//@Component
public class BusinessWithJmsService {

   	//@JmsListener(destination = "darcy.queue")
   	public void processOrder(BusinessObject order, @Header("order_type") String orderType) {
       	System.out.println("处理订单结束，准备发送消息~~~");
   	}
   	
   //	@JmsListener(destination = "darcy.queue")
   	//@SendTo("status")
   	public Map processOrder(BusinessObject order) {
   	   	// order processing
   	   	return null;
   	}

}
