package org.darcy.sanguo.persist;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.darcy.ServerStartup;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.account.BanAccount;
import org.darcy.sanguo.account.BanCharge;
import org.darcy.sanguo.account.BanIp;
import org.darcy.sanguo.loottreasure.DebrisOwn;
import org.darcy.sanguo.loottreasure.ShieldInfo;
import org.darcy.sanguo.mail.GlobalMail;
import org.darcy.sanguo.reward.TimeLimitReward;
import org.hibernate.EntityMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.persister.entity.EntityPersister;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

/**
 * 数据持久化和缓存
 */
public class EntityManager {
	public static final String CACHE_COMMON = "common";
	public static final String CACHE_PERSIST = "persist";
	private SessionFactory factory;
	private CacheManager cacheManager;
	protected HashMap<String, EntityPersister> entityPersisters = new HashMap<String, EntityPersister>();
	private Map<String, String> specialEhCaches = new HashMap<String, String>();

	public EntityManager() {
		initHibernate();
		initSpecialEhcache();
		initEhcache();
	}

	private void initHibernate() {
		Configuration conf = new Configuration();
		URL url = ServerStartup.class.getClassLoader().getResource("cfg/hibernate.cfg.xml");
		conf.configure(url);
		this.factory = conf.buildSessionFactory();

		Iterator<?> iter = this.factory.getAllClassMetadata().entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String entityName = (String) entry.getKey();
			EntityPersister entityPersister = (EntityPersister) entry.getValue();
			this.entityPersisters.put(entityName, entityPersister);
		}
	}

	private void initEhcache() {
		URL url = ServerStartup.class.getClassLoader().getResource("cfg/ehcache.xml");
		this.cacheManager = CacheManager.create(url);
		Set<String> classes = this.factory.getAllClassMetadata().keySet();
		for (String cm : classes)
			if (!(this.specialEhCaches.containsKey(cm)))
				this.cacheManager.addCache(cm);
	}

	private void initSpecialEhcache() {
		this.specialEhCaches.put(DebrisOwn.class.getName(), "debrisown");
		this.specialEhCaches.put(ShieldInfo.class.getName(), "shieldinfo");
		this.specialEhCaches.put(BanAccount.class.getName(), "banaccount");
		this.specialEhCaches.put(BanIp.class.getName(), "banip");
		this.specialEhCaches.put(BanCharge.class.getName(), "bancharge");
		this.specialEhCaches.put(GlobalMail.class.getName(), "globalmail");
		this.specialEhCaches.put(TimeLimitReward.class.getName(), "timelimitreward");
	}

	public void evictExpiredElements() {
		for (String cacheName : this.cacheManager.getCacheNames())
			getEhCache(cacheName).evictExpiredElements();
	}

	public Cache getEhCache(String className) {
		String cacheName = getEhCacheName(className);
		return this.cacheManager.getCache(cacheName);
	}

	public String getEhCacheName(String className) {
		String cacheName = className;
		if (this.specialEhCaches.containsKey(className)) {
			cacheName = (String) this.specialEhCaches.get(className);
		}
		return cacheName;
	}

	private Session getSession() {
		return this.factory.getCurrentSession();
	}

	private Serializable getEntityIdentifier(String entityName, Object entity, EntityMode entityMode) {
		EntityPersister eper = (EntityPersister) this.entityPersisters.get(entityName);
		return eper.getIdentifier(entity);
	}

	public void putInEhCache(String group, Object key, Object value, int second) {
		Ehcache cache = getEhCache(group);
		if (cache != null) {
			Element element = cache.get(key);
			if ((element != null) && (cache.getCacheConfiguration() == null))
				return;
			Element elem = new Element(key, value);
			if (second > 0) {
				elem.setTimeToIdle(second);
				elem.setTimeToLive(second);
			}
			cache.put(elem);
		}
	}

	public void putInEhCache(String group, Object key, Object value) {
		putInEhCache(group, key, value, -1);
	}

	public Object getFromEhCache(String group, Object key) {
		Ehcache cache = getEhCache(group);
		if (cache != null) {
			Element element = cache.get(key);
			if (element != null) {
				return element.getObjectValue();
			}
		}
		return null;
	}

	public List<Object> getAllFromEhCache(String group) {
		List<Object> result = new ArrayList<Object>();
		Ehcache cache = getEhCache(group);
		if (cache != null) {
			List<?> list = cache.getKeys();
			for (Iterator<?> localIterator = list.iterator(); localIterator.hasNext();) {
				Object obj = localIterator.next();
				Element element = cache.get(obj);
				if (element != null) {
					result.add(element.getObjectValue());
				}
			}
		}
		return result;
	}

	public void deleteFromEhCache(String group, Object key) {
		Ehcache cache = getEhCache(group);
		if (cache != null)
			cache.remove(key);
	}

	public <T> T find(Class<T> clazz, Serializable key) {
		Session session = getSession();
		if (session == null) {
			return null;
		}
		Transaction tx = session.beginTransaction();
		try {
			T obj = session.get(clazz, key);
			session.clear();
			tx.commit();
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
		}
		return null;
	}

	public <T> List<T> query(Class<T> clazz, String hql, Object[] values) {
		return queryPage(clazz, hql, -1, 0, values);
	}

	public <T> List<T> queryPage(Class<T> clazz, String hql, int pageSize, int pageIndex, Object[] values) {
		Session session = getSession();
		Transaction tx = session.beginTransaction();
		try {
			Query query = session.createQuery(hql);
			if (values != null) {
				for (int i = 0; i < values.length; ++i) {
					query.setParameter(i, values[i]);
				}
			}
			if (pageSize > 0) {
				query.setFirstResult(pageIndex * pageSize);
				query.setMaxResults(pageSize);
			}
			List list = query.list();
			session.clear();
			tx.commit();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
		}
		return null;
	}

	public <T> void excute(String hql, Object[] values) {
		Session session = getSession();
		Transaction tx = session.beginTransaction();
		try {
			Query query = session.createQuery(hql);
			if (values != null) {
				for (int i = 0; i < values.length; ++i) {
					query.setParameter(i, values[i]);
				}
			}
			query.executeUpdate();
			session.clear();
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
		}
	}

	public <T> void excuteSql(String sql, Object[] values) {
		Session session = getSession();
		Transaction tx = session.beginTransaction();
		try {
			SQLQuery query = session.createSQLQuery(sql);
			if (values != null) {
				for (int i = 0; i < values.length; ++i) {
					query.setParameter(i, values[i]);
				}
			}
			query.executeUpdate();
			session.clear();
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
		}
	}

	public Serializable save(Object obj) {
		Session session = getSession();
		Transaction tx = session.beginTransaction();
		Serializable key = null;
		try {
			key = session.save(obj);
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
			throw new DataAccessException(e);
		}
		return key;
	}

	public void addBatch(List<Object> list) {
		Session session = getSession();
		Transaction tx = session.beginTransaction();
		try {
			for (int i = 0; i < list.size(); ++i) {
				session.save(list.get(i));
				if (i % 100 == 0) {
					session.flush();
					session.clear();
					tx.commit();
					session = getSession();
					tx = session.beginTransaction();
				}
			}
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
			throw new DataAccessException(e);
		}
	}

	public void updateBatch(List<Object> list) {
		Session session = getSession();
		Transaction tx = session.beginTransaction();
		try {
			for (int i = 0; i < list.size(); ++i) {
				session.update(list.get(i));
				if (i % 100 == 0) {
					session.flush();
					session.clear();
					tx.commit();
					session = getSession();
					tx = session.beginTransaction();
				}
			}
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
			throw new DataAccessException(e);
		}
	}

	public void update(Object obj) {
		Session session = getSession();
		Transaction tx = session.beginTransaction();
		try {
			session.saveOrUpdate(obj);

			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
			throw new DataAccessException(e);
		}
	}

	public void delete(Object obj) {
		Session session = getSession();
		Transaction tx = session.beginTransaction();
		try {
			session.clear();
			session.delete(obj);
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
			throw new DataAccessException(e);
		}
	}

	public void shutDown() {
		String[] cacheNames = this.cacheManager.getCacheNames();
		for (String str : cacheNames) {
			Ehcache cache = Platform.getEntityManager().getEhCache(str);
			cache.flush();
		}
		this.cacheManager.shutdown();
	}

	public CacheManager getCacheManager() {
		return this.cacheManager;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}
}
