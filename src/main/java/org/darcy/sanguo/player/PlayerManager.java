package org.darcy.sanguo.player;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.event.EventHandler;
import org.darcy.sanguo.persist.DbService;
import org.darcy.sanguo.updater.Updatable;
import org.darcy.sanguo.util.DBUtil;
import org.darcy.sanguo.util.PlayerLockService;

import sango.packet.PbDown;

public class PlayerManager implements Updatable, EventHandler {
	public ConcurrentHashMap<Integer, Player> players = new ConcurrentHashMap<Integer, Player>();
	public ConcurrentHashMap<Integer, MiniPlayer> miniPlayers = new ConcurrentHashMap<Integer, MiniPlayer>();

	public PlayerManager() {
		Platform.getEventManager().registerListener(this);
	}

	public void loadMiniPlayer() {
		Platform.getLog().logSystem("loading miniplayer ...... ");
		long start = System.currentTimeMillis();
		List<MiniPlayer> minis = DBUtil.loadMiniPlayers();
		Platform.getLog().logSystem("load count:" + minis.size());
		for (MiniPlayer m : minis) {
			this.miniPlayers.put(Integer.valueOf(m.getId()), m);
		}
		Platform.getLog().logSystem("load cost : " + (System.currentTimeMillis() - start));
		Platform.getLog().logSystem("load miniplayer completed! ");
	}

	public void updateMiniPlayer(Player player) {
		if (player.getId() > 0)
			this.miniPlayers.put(Integer.valueOf(player.getId()), player.getMiniPlayer());
	}

	public void addPlayer(Player player) {
		this.players.put(Integer.valueOf(player.getId()), player);
		this.miniPlayers.put(Integer.valueOf(player.getId()), player.getMiniPlayer());
	}

	public Player getPlayerById(int id) {
		return ((Player) this.players.get(Integer.valueOf(id)));
	}

	public Player getPlayer(int id, boolean isGetFromDB, boolean needInit) throws Exception {
		Player player = (Player) this.players.get(Integer.valueOf(id));
		if ((player == null) && (isGetFromDB)) {
			synchronized (PlayerLockService.getLock(id)) {
				player = (Player) ((DbService) Platform.getServiceManager().get(DbService.class)).get(Player.class,
						Integer.valueOf(id));
				if ((player != null) && (needInit)) {
					player.asyncInit();
					player.init();
				}
			}
		}
		return player;
	}

	public MiniPlayer getMiniPlayer(int id) {
		return ((MiniPlayer) this.miniPlayers.get(Integer.valueOf(id)));
	}

	public Player getPlayer(String name) {
		for (Player p : this.players.values()) {
			if ((p != null) && (p.getName().equals(name))) {
				return p;
			}
		}
		return null;
	}

	public boolean update() {
		Iterator<Player> iter = this.players.values().iterator();
		while (iter.hasNext()) {
			try {
				Player player = (Player) iter.next();
				if ((player.getSession() == null) || (player.getSession().isDisconnect())) {
					try {
						Platform.getLog().logLogout(player);
						player.setLastLogout(new Date());
						player.clear();
						PlayerSaveCall call = new PlayerSaveCall(player);
						player.offLineClear();
						Platform.getThreadPool().execute(call);
					} catch (Exception e) {
						Platform.getLog().logError(e);
					}
					iter.remove();
				}
				player.update();
			} catch (Throwable e) {
				Platform.getLog().logError(e);
			}
		}
		return false;
	}

	public void kickOutAll() {
		for (Player p : this.players.values())
			if (p != null)
				p.getSession().disconnect();
	}

	public void boardCast(String msg) {
		msg = msg.trim();
		if ((msg != null) && (!(msg.isEmpty()))) {
			PbDown.BoardCastRst rst = PbDown.BoardCastRst.newBuilder().setMsg(msg).build();
			for (Player player : this.players.values())
				if (player != null)
					player.send(1250, rst);
		}
	}

	public int[] getEventCodes() {
		return new int[] { 1007 };
	}

	public void handleEvent(Event event) {
		if (event.type == 1007)
			Platform.getLog().logWorld("30 Sec.");
	}
}
