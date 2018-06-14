package cn.smarthse.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
/**
 * Ehcache 缓存配置，shiro配置需要用到
 * */
@Configuration
public class EhcacheConfig{
	
	private final Log log = LogFactory.getLog(getClass());
	
	@Bean(name="HSECacheManager")
	public CacheManager cacheManager() {
		log.debug("CacheManager 创建");
		//默认cache
		CacheConfiguration defaultCacheConfiguration = new CacheConfiguration();
		defaultCacheConfiguration.setMemoryStoreEvictionPolicy("LRU");
		defaultCacheConfiguration.setMaxEntriesLocalHeap(1000);
		
		//shiro cache
		CacheConfiguration shiroCacheConfig = new CacheConfiguration();
		shiroCacheConfig.setName("shiro-activeSessionCache");
		shiroCacheConfig.setMemoryStoreEvictionPolicy("LRU");
		shiroCacheConfig.setMaxEntriesLocalHeap(1000);
		//密码错误缓存
		CacheConfiguration passwordRetryCache = new CacheConfiguration();
		passwordRetryCache.setName("passwordRetryCache");
		passwordRetryCache.setMemoryStoreEvictionPolicy("LRU");
		passwordRetryCache.setMaxEntriesLocalHeap(1000);
		passwordRetryCache.setTimeToIdleSeconds(600);

		net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
		config.setDefaultCacheConfiguration(defaultCacheConfiguration);
		config.addCache(shiroCacheConfig);
		config.addCache(passwordRetryCache);
		//不要自动更新
		//config.setUpdateCheck(false);
		return net.sf.ehcache.CacheManager.newInstance(config);
	}

}