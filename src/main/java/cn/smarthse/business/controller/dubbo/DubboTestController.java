package cn.smarthse.business.controller.dubbo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.smarthse.business.service.dubbo.DubboTestService;

@RestController
public class DubboTestController {

	// 本地服务
	@Autowired
	private DubboTestService DubboTestService;


	@RequestMapping("/dubbo")
	public String saveUser() {
		try {
			
		}catch(Exception e) {
			//没有提供者时候就异常啦
			e.printStackTrace();
		}		
		return DubboTestService.sayHi();
	}
}