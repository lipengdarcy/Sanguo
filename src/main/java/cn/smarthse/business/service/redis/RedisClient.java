package cn.smarthse.business.service.redis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

/**
 * 自定义redis客户端，封装一些常用操作
 */
@Service
public class RedisClient {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private final Log log = LogFactory.getLog(getClass());

	/**
	 * opsForXXX和boundXXXOps的区别？
	 * XXX为Value的类型，前者获取一个operator，但是没有指定操作的对象（key），可以在一个连接（事务）内操作多个key以及对应的value；后者获取了一个指定的对象（key）的operator，在一个连接（事务）内只能操作这个对应的key。
	 * 关于计数API（increment）中有个小问题需要注意，通过increment计数后，通过get方式获取计数值的时候可能会抛出EOF异常（和本地的jdk以及redis的编译版本有关），可以考虑使用boundValueOps(key).get(0,-1)获得计数值。
	 */
	public void set(String key, Object value) {
		ValueOperations<String, Object> operations = redisTemplate.opsForValue();
		// 1.存入key-value
		// operations.set(key,value);
		// 2.追加
		// operations.append("name","is man");
		// 3.获得并修改
		Object oldValue = operations.getAndSet(key, value);
		log.debug("redis 缓存更新数据，key: " + key + "更新前数据： " + oldValue.toString() + "更新后数据：" + value.toString());
	}

	public Object get(String key) {
		ValueOperations<String, Object> operations = redisTemplate.opsForValue();
		// 根据key取出Value
		Object value = operations.get(key);
		log.debug("redis 缓存获取数据，key: " + key + "value: " + value.toString());
		return value;
	}

}
