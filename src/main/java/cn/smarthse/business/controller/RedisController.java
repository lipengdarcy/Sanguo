package cn.smarthse.business.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.smarthse.business.model.system.SysAreaStandard;
import cn.smarthse.business.service.redis.RedisClient;

@Controller
@RequestMapping("/redis")
public class RedisController {

	@Autowired
	private RedisClient redisClient;

	@RequestMapping("/get")
	@ResponseBody
	public Object get() throws Exception {
		SysAreaStandard a = new SysAreaStandard();
		a.setName("name1");
		redisClient.set("name1", a);
		SysAreaStandard cacheValue = (SysAreaStandard) redisClient.get("name1");
		return cacheValue;
	}

}
