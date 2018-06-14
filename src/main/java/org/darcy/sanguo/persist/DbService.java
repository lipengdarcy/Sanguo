package org.darcy.sanguo.persist;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.arena.Arena;
import org.darcy.sanguo.awardcenter.Awards;
import org.darcy.sanguo.boss.BossData;
import org.darcy.sanguo.mail.Mail;
import org.darcy.sanguo.pay.Receipt;
import org.darcy.sanguo.player.MiniPlayer;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.relation.Relations;
import org.darcy.sanguo.service.PayService;
import org.darcy.sanguo.service.Service;
import org.darcy.sanguo.top.Top;
import org.darcy.sanguo.union.League;
import org.darcy.sanguo.union.Union;
import org.darcy.sanguo.world.World;
import org.darcy.sanguo.worldcompetition.WorldCompetition;

public class DbService implements Service {
	public static final int SERVER_INFO_FLAG = 1;
	public static final int PLAYER_INFO_FLAG = 2;
	private Map<Class<?>, Boolean> canInCache = new HashMap<Class<?>, Boolean>();

	public void startup() throws Exception {
		loadCanInCache();
	}

	private void loadCanInCache() {
		this.canInCache.put(MiniPlayer.class, Boolean.valueOf(false));
		this.canInCache.put(Player.class, Boolean.valueOf(true));
		this.canInCache.put(World.class, Boolean.valueOf(true));
		this.canInCache.put(Arena.class, Boolean.valueOf(true));
		this.canInCache.put(Top.class, Boolean.valueOf(false));
		this.canInCache.put(Relations.class, Boolean.valueOf(true));
		this.canInCache.put(Awards.class, Boolean.valueOf(true));
		this.canInCache.put(BossData.class, Boolean.valueOf(false));
		this.canInCache.put(Mail.class, Boolean.valueOf(false));
		this.canInCache.put(WorldCompetition.class, Boolean.valueOf(true));
		this.canInCache.put(Receipt.class, Boolean.valueOf(true));
		this.canInCache.put(League.class, Boolean.valueOf(false));
		this.canInCache.put(Union.class, Boolean.valueOf(true));
	}

	public <T> T get(Class<T> clazz, Serializable key) throws Exception {
		if (!(this.canInCache.containsKey(clazz))) {
			throw new Exception("DbService init canInCache error, lost class:" + clazz.getName());
		}
		if (((Boolean) this.canInCache.get(clazz)).booleanValue()) {
			String name = clazz.getName();
			T obj = (T) Platform.getEntityManager().getFromEhCache(name, key);
			if (obj != null) {
				return obj;
			}
		}
		T obj = null;
		obj = Platform.getEntityManager().find(clazz, key);
		if ((obj != null) && (((Boolean) this.canInCache.get(clazz)).booleanValue())) {
			Platform.getEntityManager().putInEhCache(clazz.getName(), key, obj);
		}

		return obj;
	}

	public void add(Object entity) {
		EntityManager entityManager = Platform.getEntityManager();
		Serializable key = entityManager.save(entity);
		if (key == null) {
			return;
		}
		if (((Boolean) this.canInCache.get(entity.getClass())).booleanValue())
			entityManager.putInEhCache(entity.getClass().getName(), key, entity);
	}

	public void update(Object entity) {
		Platform.getEntityManager().update(entity);
	}

	public void delete(Object entity, Object key) {
		Platform.getEntityManager().delete(entity);
		if ((this.canInCache.containsKey(entity.getClass()))
				&& (((Boolean) this.canInCache.get(entity.getClass())).booleanValue()))
			deleteFromCache(entity, key);
	}

	public void deleteFromCache(Object entity, Object key) {
		Platform.getEntityManager().deleteFromEhCache(entity.getClass().getName(), key);
	}

	public void shutdown() {
		Platform.getEntityManager().putInEhCache("common", "globalChargeRewardKey",
				Integer.valueOf(PayService.getChargePlayerCount()));
		Platform.getEntityManager().shutDown();
	}

	public void reload() throws Exception {
	}
}
