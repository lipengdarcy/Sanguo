package cn.smarthse.config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import cn.smarthse.framework.Constant;
import cn.smarthse.framework.cache.FastJson2JsonRedisSerializer;
import cn.smarthse.framework.cache.HSERedisCacheManager;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {

	private final Log log = LogFactory.getLog(getClass());

	// 是否启用redis缓存服务
	@Value("${spring.redis.cacheable}")
	private Boolean cacheable;

	// redis 单机地址
	@Value("${spring.redis.host}")
	private String host;

	// redis 单机端口
	@Value("${spring.redis.port}")
	private Integer port;

	@Value("${spring.redis.pool.max-active}")
	private Integer max_active;

	@Value("${spring.redis.pool.max-wait}")
	private Long max_wait;

	@Value("${spring.redis.pool.max-idle}")
	private Integer max_idle;

	@Value("${spring.redis.pool.min-idle}")
	private Integer min_idle;

	// 是否启用redis集群
	@Value("${spring.redis.cluster.clusterable}")
	private Boolean clusterable;

	@Value("${spring.redis.cluster.nodes}")
	private String nodes;
	@Value("${spring.redis.cluster.timeout}")
	private String timeout;
	@Value("${spring.redis.cluster.max-redirects}")
	private String max_redirects;

	// <!-- step5：配置redis连接池 JedisPoolConfig-->
	@Bean("JedisPoolConfig")
	public JedisPoolConfig redisConfig() {
		log.info("step5.1：配置redis连接池 JedisPoolConfig");
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(max_active);
		config.setMaxWaitMillis(max_wait);
		config.setMaxIdle(max_idle);
		config.setMinIdle(min_idle);
		return config;
	}

	// <!-- step5： 配置ConnectionFactory JedisConnectionFactory-->
	@Bean("JedisConnectionFactory")
	public JedisConnectionFactory redisFactory() {
		log.info("step5.2： 配置Redis ConnectionFactory JedisConnectionFactory");
		JedisPoolConfig config = redisConfig();
		JedisConnectionFactory factory = null;
		// 不启用redis集群
		if (!clusterable) {
			factory = new JedisConnectionFactory();
			factory.setHostName(host);
			factory.setPort(port);
		} else {
			RedisClusterConfiguration clusterConfig = redisClusterConfiguration();
			factory = new JedisConnectionFactory(clusterConfig);
			// 用了集群的地址，单机的地址就不需要了
			// factory.setHostName(env.getProperty("redis.ip"));
			// factory.setPort(Integer.valueOf(env.getProperty("redis.port")));
		}
		factory.setPoolConfig(config);
		return factory;
	}

	// <!-- step5：配置Redis 序列化-->
	@Bean
	@SuppressWarnings("rawtypes")
	public RedisSerializer fastJson2JsonRedisSerializer() {
		return new FastJson2JsonRedisSerializer<Object>(Object.class);
	}

	// <!-- step5：配置redisTemplate StringRedisTemplate-->
	@Bean
	public RedisTemplate<?,?> redisTemplate() {
		log.info("step5.3：配置redisTemplate StringRedisTemplate");
		RedisSerializer<?> redisSerializer = fastJson2JsonRedisSerializer();
		JedisConnectionFactory connectionFactory = redisFactory();
		RedisTemplate t = new RedisTemplate();
		t.setConnectionFactory(connectionFactory);
		// 对象序列化
		t.setValueSerializer(redisSerializer);
		return t;
	}

	// <!-- step5：配置RedisClusterConfiguration 集群-->
	@Bean
	public RedisClusterConfiguration redisClusterConfiguration() {
		log.info("step5.4：配置集群： RedisClusterConfiguration");
		Map<String, Object> source = new HashMap<String, Object>();
		source.put("spring.redis.cluster.nodes", nodes);
		source.put("spring.redis.cluster.timeout", timeout);
		source.put("spring.redis.cluster.max-redirects", max_redirects);
		RedisClusterConfiguration config = new RedisClusterConfiguration(
				new MapPropertySource("RedisClusterConfiguration", source));
		// 如果不设置clusterTimeOut值，源码中默认为2S。当集群服务器与客户端不在同一服务器上时，容易报：Could not get a
		// resource from the Cluster;
		// 如果不设置maxRedirects值，源码中默认为5。一般当此值设置过大时，容易报：Too many Cluster
		// redirections
		return config;
	}

	/**
	 * 自定义缓存策略，对于同一业务(同一业务逻辑处理的方法，哪怕是集群/分布式系统)，生成的 key 始终一致，对于不同业务则不一致：
	 */
	@Bean
	public KeyGenerator customKeyGenerator() {
		return new KeyGenerator() {
			@Override
			public Object generate(Object o, Method method, Object... objects) {
				StringBuilder sb = new StringBuilder();
				sb.append(o.getClass().getName());
				sb.append(method.getName());
				for (Object obj : objects) {
					sb.append(obj.toString());
				}
				return sb.toString();
			}
		};
	}

	// spring缓存管理器
	@Bean
	public CacheManager cacheManager() {
		// 1.启用redis缓存 Constant.redisCacheName
		if (cacheable) {
			RedisTemplate redisTemplate = redisTemplate();
			HSERedisCacheManager manager = new HSERedisCacheManager(redisTemplate);
			List<String> cacheNames = new ArrayList<String>();
			cacheNames.add(Constant.redisCacheName);
			manager.setCacheNames(cacheNames);
			return manager;
		}
		// 3.不启用任何缓存
		else {
			NoOpCacheManager manager = new NoOpCacheManager();
			return manager;
		}
	}

}
