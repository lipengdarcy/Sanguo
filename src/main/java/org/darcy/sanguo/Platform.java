package org.darcy.sanguo;

import org.darcy.sanguo.asynccall.CallBackManager;
import org.darcy.sanguo.boss.BossManager;
import org.darcy.sanguo.event.EventManager;
import org.darcy.sanguo.keyword.KeyWordManager;
import org.darcy.sanguo.log.LogManager;
import org.darcy.sanguo.net.ClientSessionManager;
import org.darcy.sanguo.packethandler.PacketHandlerManager;
import org.darcy.sanguo.persist.EntityManager;
import org.darcy.sanguo.player.PlayerManager;
import org.darcy.sanguo.service.ServiceManager;
import org.darcy.sanguo.threadpool.ThreadPool;
import org.darcy.sanguo.top.TopManager;
import org.darcy.sanguo.union.LeagueManager;
import org.darcy.sanguo.updater.UpdaterManager;
import org.darcy.sanguo.world.World;

public class Platform {

	static World world;
	static ServiceManager serviceManager;
	static PacketHandlerManager packetHanderManager;
	static ClientSessionManager clientSessionManager;
	static EventManager eventManager;
	static UpdaterManager updaterManager;
	static ThreadPool threadPool;
	static CallBackManager callBackManager;
	static EntityManager entityManager;
	static PlayerManager playerManager;
	static BossManager bossManager;
	static TopManager topManager;
	static LogManager logManager;
	static LeagueManager leagueManager;
	static KeyWordManager keyWordManager;

	public static ThreadPool getThreadPool() {
		return threadPool;
	}

	public static void setThreadPool(ThreadPool threadPool1) {
		threadPool = threadPool1;
	}

	public static UpdaterManager getUpdaterManager() {
		return updaterManager;
	}

	public static void setUpdaterManager(UpdaterManager updaterManager1) {
		updaterManager = updaterManager1;
	}

	public static EventManager getEventManager() {
		return eventManager;
	}

	public static void setEventManager(EventManager eventManager1) {
		eventManager = eventManager1;
	}

	public static PacketHandlerManager getPacketHanderManager() {
		return packetHanderManager;
	}

	public static void setPacketHanderManager(PacketHandlerManager packetHanderManager1) {
		packetHanderManager = packetHanderManager1;
	}

	public static ServiceManager getServiceManager() {
		return serviceManager;
	}

	public static void setServiceManager(ServiceManager serviceManager1) {
		serviceManager = serviceManager1;
	}

	public static ClientSessionManager getClientSessionManager() {
		return clientSessionManager;
	}

	public static void setClientSessionManager(ClientSessionManager clientSessionManager1) {
		clientSessionManager = clientSessionManager1;
	}

	public static LogManager getLog() {
		return logManager;
	}

	public static void setLogManager(LogManager logManager1) {
		logManager = logManager1;
	}

	public static CallBackManager getCallBackManager() {
		return callBackManager;
	}

	public static void setCallBackManager(CallBackManager callBackManager1) {
		callBackManager = callBackManager1;
	}

	public static PlayerManager getPlayerManager() {
		return playerManager;
	}

	public static void setPlayerManager(PlayerManager playerManager1) {
		playerManager = playerManager1;
	}

	public static EntityManager getEntityManager() {
		return entityManager;
	}

	public static void setEntityManager(EntityManager entityManager1) {
		entityManager = entityManager1;
	}

	public static World getWorld() {
		return world;
	}

	public static void setWorld(World world1) {
		world = world1;
	}

	public static TopManager getTopManager() {
		return topManager;
	}

	public static void setTopManager(TopManager topManager1) {
		topManager = topManager1;
	}

	public static BossManager getBossManager() {
		return bossManager;
	}

	public static void setBossManager(BossManager bossManager1) {
		bossManager = bossManager1;
	}

	public static LeagueManager getLeagueManager() {
		return leagueManager;
	}

	public static void setLeagueManager(LeagueManager leagueManager1) {
		leagueManager = leagueManager1;
	}

	public static KeyWordManager getKeyWordManager() {
		return keyWordManager;
	}

	public static void setKeyWordManager(KeyWordManager keyWordManager1) {
		keyWordManager = keyWordManager1;
	}
}
