package cn.smarthse.business.service.dubbo.impl;

import cn.smarthse.business.service.dubbo.DubboTestService;

/**
 * DubboTestService 远程调用测试
 */

public class DubboTestServiceImpl implements DubboTestService {

	public String sayHi() {
		System.out.println("来自DubboTestService的问候");
		return "DubboTestService调用成功!";
	};

}