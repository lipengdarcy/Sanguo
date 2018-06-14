package cn.smarthse.framework.cache;

import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisOperations;

/**
 * 自定义缓存管理器，
 * 基于Redis
 * */
public class HSERedisCacheManager extends RedisCacheManager {

	public HSERedisCacheManager(RedisOperations redisOperations) {
		super(redisOperations);
	}


}
