package org.darcy.sanguo.world;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.persist.DbService;
import org.darcy.sanguo.union.combat.CombatData;

public class World {
	public static final String CACHE_KEY_OPENSERVERTIME = "CACHE_KEY_OPENSERVERTIME";
	private int id;
	private int arenaCount;
	public static AtomicInteger arenaCountGen;
	private int worldCompetitionCount;
	public static AtomicInteger worldCompetitionCountGen;
	private int createRobot;
	private CombatData leagueCombatData = new CombatData();

	public void init() {
		arenaCountGen = new AtomicInteger(this.arenaCount);
		worldCompetitionCountGen = new AtomicInteger(this.worldCompetitionCount);
		new Thread(new UpdateServerRunnable(), "UpdateServerRunnable").start();
		Platform.getLeagueManager().getCombat().setData(this.leagueCombatData);
	}

	public boolean isAlreadyCreateRobot() {
		return (this.createRobot == 1);
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getArenaCount() {
		return this.arenaCount;
	}

	public void setArenaCount(int arenaCount) {
		this.arenaCount = arenaCount;
	}

	public int getWorldCompetitionCount() {
		return this.worldCompetitionCount;
	}

	public CombatData getLeagueCombatData() {
		return this.leagueCombatData;
	}

	public void setLeagueCombatData(CombatData leagueCombatData) {
		this.leagueCombatData = leagueCombatData;
	}

	public void setWorldCompetitionCount(int worldCompetitionCount) {
		this.worldCompetitionCount = worldCompetitionCount;
	}

	public int getCreateRobot() {
		return this.createRobot;
	}

	public void setCreateRobot(int createRobot) {
		this.createRobot = createRobot;
	}

	public int getCurArenaCount() {
		return arenaCountGen.get();
	}

	public int getNewArenaCount() {
		setArenaCount(arenaCountGen.incrementAndGet());
		return this.arenaCount;
	}

	public int getTotalWorldCompetitionCount() {
		return worldCompetitionCountGen.get();
	}

	public int getCurWorldCompetitionMaxRank() {
		return Math.min(worldCompetitionCountGen.get(), 5000);
	}

	public int getNewWorldComeptitionCount() {
		setWorldCompetitionCount(worldCompetitionCountGen.incrementAndGet());
		if (this.worldCompetitionCount > 5000) {
			return -1;
		}
		return this.worldCompetitionCount;
	}

	public void save() {
		((DbService) Platform.getServiceManager().get(DbService.class)).update(this);
	}

	public static long getOpenServerTime() {
		Object time = Platform.getEntityManager().getFromEhCache("persist", "CACHE_KEY_OPENSERVERTIME");
		if (time != null) {
			return ((Long) time).longValue();
		}
		return 0L;
	}

	public static long getFirstWeekTime() {
		long open = getOpenServerTime();
		if (open == 0L) {
			return open;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(open);
		cal.add(6, 7);
		cal.set(11, 0);
		cal.set(12, 0);
		cal.set(13, 0);
		cal.set(14, 0);
		return cal.getTimeInMillis();
	}

	class UpdateServerRunnable implements Runnable {
		long time;

		UpdateServerRunnable() {
			this.time = System.currentTimeMillis();
		}

		public void run() {
			try {
				if (System.currentTimeMillis() - this.time > 10000L) {
					Platform.getWorld().save();
					Platform.getTopManager().save();
					Platform.getBossManager().save();
					this.time = System.currentTimeMillis();
				}
				Thread.sleep(1000L);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
}
