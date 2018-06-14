package org.darcy.sanguo.top;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.event.EventHandler;
import org.darcy.sanguo.persist.DbService;
import org.darcy.sanguo.player.MiniPlayer;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.TowerService;
import org.darcy.sanguo.util.DBUtil;

public class TopManager implements EventHandler {
	public static final int TOWER_RANK_MAX_SIZE = 50;
	public static final int MAP_RANK_MAX_SIZE = 50;
	public static final int LEVEL_RANK_MAX_SIZE = 50;
	public static final int BTL_RANK_MAX_SIZE = 50;
	private List<Top> towerRanks = new ArrayList<Top>();
	private Map<Integer, Top> towerRanksMap = new HashMap<Integer, Top>();
	private List<Top> mapRanks = new ArrayList<Top>();
	private List<Top> btlRanks = new ArrayList<Top>();
	private List<Top> levelRanks = new ArrayList<Top>();

	private HashMap<Integer, Top> allLevelRanks = new HashMap<Integer, Top>();
	private HashMap<Integer, Top> oldAllLevelRanks = new HashMap<Integer, Top>();
	private HashMap<Integer, Top> allBtlRanks = new HashMap<Integer, Top>();
	private HashMap<Integer, Top> oldAllBtlRanks = new HashMap<Integer, Top>();

	public Comparator<MiniPlayer> compaBTL = new Comparator<MiniPlayer>() {
		public int compare(MiniPlayer t1, MiniPlayer t2) {
			int rst = t2.getBtlCapability() - t1.getBtlCapability();
			if (rst != 0) {
				return rst;
			}
			return (t1.getId() - t2.getId());
		}
	};

	public Comparator<MiniPlayer> compaLevel = new Comparator<MiniPlayer>() {
		public int compare(MiniPlayer t1, MiniPlayer t2) {
			int rst = t2.getLevel() - t1.getLevel();
			if (rst != 0) {
				return rst;
			}
			return (t1.getId() - t2.getId());
		}
	};

	public Comparator<Top> compa = new Comparator<Top>() {
		public int compare(Top t1, Top t2) {
			return (t2.getValue() - t1.getValue());
		}
	};

	public int getTitleId(int pid) {
		Top t = (Top) this.towerRanksMap.get(Integer.valueOf(pid));
		if (t != null) {
			return TowerService.getTitleID(t.getValue());
		}
		return 0;
	}

	public void refreshBTLRank() {
		List<MiniPlayer> list = new ArrayList<MiniPlayer>(Platform.getPlayerManager().miniPlayers.values());

		Collections.sort(list, this.compaBTL);

		List<Top> tmp = new ArrayList<Top>();
		for (int i = 0; i < list.size(); ++i) {
			MiniPlayer mini = (MiniPlayer) list.get(i);
			Top top = new Top();
			top.setPid(mini.getId());
			top.setRank(i + 1);
			top.setType(2);
			top.setValue(mini.getBtlCapability());
			if (i < 50) {
				tmp.add(top);
			}
			this.allBtlRanks.put(Integer.valueOf(mini.getId()), top);
		}
		this.btlRanks = tmp;
	}

	public void refreshLevelRank() {
		List<MiniPlayer> list = new ArrayList<MiniPlayer>(Platform.getPlayerManager().miniPlayers.values());

		Collections.sort(list, this.compaLevel);

		List<Top> tmp = new ArrayList<Top>();
		for (int i = 0; i < list.size(); ++i) {
			MiniPlayer mini = (MiniPlayer) list.get(i);
			Top top = new Top();
			top.setPid(mini.getId());
			top.setRank(i + 1);
			top.setType(3);
			top.setValue(mini.getLevel());
			if (i < 50) {
				tmp.add(top);
			}
			this.allLevelRanks.put(Integer.valueOf(mini.getId()), top);
		}
		this.levelRanks = tmp;
	}

	public void loadOldRanks() {
		Top top;
		List<Top> tops = DBUtil.getTopsNoRank(3);
		for (Iterator<Top> localIterator = tops.iterator(); localIterator.hasNext();) {
			top = (Top) localIterator.next();
			this.oldAllLevelRanks.put(Integer.valueOf(top.getPid()), top);
		}

		tops = DBUtil.getTopsNoRank(2);
		for (Iterator<?> localIterator = tops.iterator(); localIterator.hasNext();) {
			top = (Top) localIterator.next();
			this.oldAllBtlRanks.put(Integer.valueOf(top.getPid()), top);
		}
	}

	public void init() {
		Platform.getEventManager().registerListener(this);
		List<Top> tops = DBUtil.getTops(0);
		for (Top t : tops) {
			this.towerRanksMap.put(Integer.valueOf(t.getPid()), t);
		}
		this.towerRanks.addAll(tops);
		this.mapRanks.addAll(DBUtil.getTops(1));
	}

	public void save() {
		List<Top> list = new ArrayList<Top>();
		list.addAll(this.towerRanks);
		list.addAll(this.mapRanks);
		for (Top top : list) {
			top.setRank(getRank(top.getType(), top.getPid()));
			if (top.getId() > 0)
				((DbService) Platform.getServiceManager().get(DbService.class)).update(top);
			else
				((DbService) Platform.getServiceManager().get(DbService.class)).add(top);
		}
	}

	public int[] getEventCodes() {
		return new int[] { 1010, 2006, 1002 };
	}

	public List<Top> getRanks(int type) {
		switch (type) {
		case 2:
			return this.btlRanks;
		case 3:
			return this.levelRanks;
		case 1:
			return this.mapRanks;
		case 0:
			return this.towerRanks;
		}
		return null;
	}

	public void setTowerRanks(List<Top> towerRanks) {
		this.towerRanks = towerRanks;
	}

	public void setMapRanks(List<Top> mapRanks) {
		this.mapRanks = mapRanks;
	}

	public int getChange(int pid, int type) {
		Top o = null;
		Top n = null;
		if (type == 2) {
			o = (Top) this.oldAllBtlRanks.get(Integer.valueOf(pid));
			n = (Top) this.allBtlRanks.get(Integer.valueOf(pid));
			if (n == null) {
				return 0;
			}
			if (o != null)
				return (o.getRank() - n.getRank());
			return (this.allBtlRanks.size() - n.getRank());
		}
		if (type == 3) {
			o = (Top) this.oldAllLevelRanks.get(Integer.valueOf(pid));
			n = (Top) this.allLevelRanks.get(Integer.valueOf(pid));
			if (n == null) {
				return 0;
			}
			if (o != null)
				return -1;
			return (this.allLevelRanks.size() - n.getRank());
		}
		return (o.getRank() - n.getRank());
	}

	public int getRank(int type, int pid) {
		Top top;
		List<Top> list = null;
		switch (type) {
		case 0:
			list = this.towerRanks;
			break;
		case 1:
			list = this.mapRanks;
			break;
		case 3:
			top = (Top) this.allLevelRanks.get(Integer.valueOf(pid));
			if (top != null) {
				return top.getRank();
			}
			return -1;
		case 2:
			top = (Top) this.allBtlRanks.get(Integer.valueOf(pid));
			if (top != null) {
				return top.getRank();
			}
			return -1;
		}

		if (list == null) {
			throw new RuntimeException("getRank, list is null, type:" + type);
		}
		for (int i = 0; i < list.size(); ++i) {
			top = (Top) list.get(i);
			if (top.getPid() == pid) {
				return (i + 1);
			}
		}
		return -1;
	}

	public void handleEvent(Event event) {
		if (event.type == 1002) {
			Thread rankSaver = new Thread(
					new AsyncRankSaver(new ArrayList(this.allLevelRanks.values()),
							new ArrayList(this.allBtlRanks.values()), this.oldAllLevelRanks, this.oldAllBtlRanks),
					"AsyncRankSaver");
			rankSaver.start();
			return;
		}

		Player player = (Player) event.params[0];
		int value = ((Integer) event.params[1]).intValue();
		int type = 0;
		List<Top> list = null;
		int maxSize = 0;
		String note = null;
		switch (event.type) {
		case 1010:
			type = 0;
			list = this.towerRanks;
			maxSize = 50;
			break;
		case 2006:
			type = 1;
			list = this.mapRanks;
			maxSize = 50;
			note = (String) event.params[2];
		}

		Top old = getTop(type, player.getId());
		if (old != null) {
			old.setValue(value);
			if ((type == 1) && (old.compareNote(note) < 0)) {
				old.setNote(note);
			}
			Collections.sort(list, this.compa);
		} else {
			Top top;
			if (list.size() < maxSize) {
				top = new Top();
				top.setPid(player.getId());
				top.setValue(value);
				top.setType(type);
				if (type == 1) {
					top.setNote(note);
				}
				list.add(top);
				Collections.sort(list, this.compa);
			} else if (value > ((Top) list.get(list.size() - 1)).getValue()) {
				top = (Top) list.get(list.size() - 1);
				top.setPid(player.getId());
				top.setValue(value);
				if (type == 1) {
					top.setNote(note);
				}
				Collections.sort(list, this.compa);
			}
		}
	}

	private Top getTop(int type, int pid) {
		List list = null;
		switch (type) {
		case 0:
			list = this.towerRanks;
			break;
		case 1:
			list = this.mapRanks;
		}

		if (list == null) {
			throw new RuntimeException("getTop, list is null, type:" + type);
		}
		for (int i = 0; i < list.size(); ++i) {
			Top top = (Top) list.get(i);
			if (top.getPid() == pid) {
				return top;
			}
		}
		return null;
	}
}
