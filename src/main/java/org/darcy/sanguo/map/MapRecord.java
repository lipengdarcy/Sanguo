package org.darcy.sanguo.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.darcy.sanguo.activity.ActivityInfo;
import org.darcy.sanguo.activity.item.ActivityItem;
import org.darcy.sanguo.activity.item.SpecialMapAI;
import org.darcy.sanguo.combat.Stage;
import org.darcy.sanguo.persist.PlayerBlobEntity;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.MapService;

import sango.packet.PbStageMap;

public class MapRecord implements PlayerBlobEntity {
	private static final long serialVersionUID = -8236619834774034660L;
	public static final int version = 2;
	private Stage tmpStage;
	private int proMapChallengeTimes;
	private long multiChallengeTime;
	public static final int PROMAP_MAX_CHALLENGE_TIMES = 3;
	public static final int WARRIOR_MAP_CHALLENGE_TIMES = 2;
	public static final int TREASURE_MAP_CHALLENGE_TIMES = 2;
	public static final int MONEY_MAP_CHALLENGE_TIMES = 2;
	public static final long MULTICHALLENGE_CD_MILISECONDS = 3600000L;
	private HashMap<Integer, ClearMap> clearMaps = new HashMap();

	private ArrayList<MapTemplate> openedProMaps = new ArrayList();

	private int warriorMapLeftTimes = 2;

	private int treasureMapLeftTimes = 2;

	private int moneyMapLeftTimes = 2;

	private Set<Integer> clearProMaps = new HashSet();

	private Set<Integer> firstMaps = new HashSet();
	private Set<Integer> firstStages = new HashSet();

	private Set<Integer> helpStages = new HashSet();

	ClearMapComparator comparator = new ClearMapComparator();

	public static int[] activityMapStartTime = { 10 };

	public static int[] activityMapEndTime = { 23, 59, 59 };

	public static int[] warriorMapRunDays = { 2, 4, 6, 1 };

	public static int[] treasureMapRunDays = { 3, 5, 7, 1 };

	public void openAll() {
		for (MapTemplate mt : MapService.mapTemplates.values()) {
			if (mt.type == 0) {
				ClearMap cm = (ClearMap) this.clearMaps.get(Integer.valueOf(mt.id));
				if (cm == null) {
					cm = new ClearMap();
					cm.id = mt.id;
					this.clearMaps.put(Integer.valueOf(mt.id), cm);
				}
				for (StageTemplate st : mt.stageTemplates) {
					ClearStage cs = cm.getClearStage(st.id);
					if (cs == null) {
						cm.addClearStage(st.id);
						cs = cm.getClearStage(st.id);
					}
					for (int i = 0; i < st.channels.length; ++i) {
						if (st.channels[i] != null)
							cs.finishRecords[i] = true;
					}
				}
			} else if (mt.type == 1) {
				this.clearProMaps.add(Integer.valueOf(mt.id));
				this.openedProMaps.add(mt);
			}
		}
		Collections.sort(this.openedProMaps, new Comparator<MapTemplate>() {
			@Override
			public int compare(MapTemplate o1, MapTemplate o2) {
				return (o1.id - o2.id);
			}
		});
	}

	public Set<Integer> getHelpStages() {
		return this.helpStages;
	}

	public void setHelpStages(Set<Integer> helpStages) {
		this.helpStages = helpStages;
	}

	public void addClearProMap(int mapId) {
		this.clearProMaps.add(Integer.valueOf(mapId));
	}

	public boolean isOpenedProMap(int mapId) {
		for (MapTemplate mt : this.openedProMaps) {
			if (mt.id == mapId) {
				return true;
			}
		}

		return false;
	}

	public int getLeftMultiChalengeMiliSeconds() {
		if (this.multiChallengeTime == 0L) {
			return 0;
		}

		long last = System.currentTimeMillis() - this.multiChallengeTime;
		long left = 3600000L - last;
		if (left < 0L) {
			left = 0L;
			this.multiChallengeTime = 0L;
		}
		return (int) left;
	}

	public void addClearMap(int mapId) {
		if (this.clearMaps.get(Integer.valueOf(mapId)) == null) {
			ClearMap cm = new ClearMap();
			cm.id = mapId;
			this.clearMaps.put(Integer.valueOf(cm.id), cm);

			refreshProMaps();
		}
	}

	public void addClearStage(int mapId, int stageId) {
		ClearMap clearMap = (ClearMap) this.clearMaps.get(Integer.valueOf(mapId));
		if (clearMap != null)
			clearMap.addClearStage(stageId);
	}

	public void refreshProMaps() {
		this.openedProMaps.clear();
		for (MapTemplate mt : MapService.mapTemplates.values()) {
			if (mt.type == 1) {
				ClearMap t = (ClearMap) this.clearMaps.get(Integer.valueOf(mt.preId));
				if ((t != null) && (t.isFinished())) {
					if ((mt.id != 1001) && (!(this.clearProMaps.contains(Integer.valueOf(mt.id - 1))))) {
						continue;
					}
					this.openedProMaps.add(mt);
				}
			}
		}
		Collections.sort(this.openedProMaps, new Comparator<MapTemplate>() {
			public int compare(MapTemplate m1, MapTemplate m2) {
				return (m1.id - m2.id);
			}
		});
	}

	public int getLeftProMapChallengeTimes() {
		int rst = 3 - this.proMapChallengeTimes;
		if (rst < 0) {
			this.proMapChallengeTimes = 3;
			rst = 0;
		}

		return rst;
	}

	public void refresh(Player p) {
		int i = 0;
		this.proMapChallengeTimes = 0;
		for (ClearMap cm : this.clearMaps.values()) {
			for (ClearStage cs : cm.getStages()) {
				cs.chanllengeTimes = 0;
				cs.resetTimes = 0;
			}
		}
		int w = 0;
		ActivityItem ai = ActivityInfo.getItem(p, 15);
		if (ai != null) {
			SpecialMapAI mai = (SpecialMapAI) ai;
			w = mai.count;
		}
		int t = 0;
		ai = ActivityInfo.getItem(p, 16);
		if (ai != null) {
			SpecialMapAI mai = (SpecialMapAI) ai;
			t = mai.count;
		}
		ai = ActivityInfo.getItem(p, 14);
		if (ai != null) {
			SpecialMapAI mai = (SpecialMapAI) ai;
			i = mai.count;
		}
		this.warriorMapLeftTimes = (2 + w);
		this.treasureMapLeftTimes = (2 + t);
		this.moneyMapLeftTimes = (2 + i);
	}

	public void init() {
		refreshProMaps();

		List clearMaps = getClearMaps();
		if (clearMaps.size() > 0) {
			ClearMap cm = (ClearMap) clearMaps.get(clearMaps.size() - 1);
			if ((cm.isFinished()) && (cm.getTemplate().nextId != -1))
				addClearMap(cm.getTemplate().nextId);
		}
	}

	public MapRecord() {
		for (MapTemplate mt : MapService.mapTemplates.values())
			if ((mt.preId < 0) && (mt.type == 0)) {
				ClearMap cm = new ClearMap();
				cm.id = mt.id;
				this.clearMaps.put(Integer.valueOf(cm.id), cm);
			}
	}

	public List<ClearMap> getClearMaps() {
		List list = new ArrayList(this.clearMaps.values());
		Collections.sort(list, this.comparator);
		return list;
	}

	public ClearMap getClearMap(int mapId) {
		return ((ClearMap) this.clearMaps.get(Integer.valueOf(mapId)));
	}

	public boolean hasClearProMap(int mapId) {
		return this.clearProMaps.contains(Integer.valueOf(mapId));
	}

	public int getProMapChallengeTimes() {
		return this.proMapChallengeTimes;
	}

	public void setProMapChallengeTimes(int proMapChallengeTimes) {
		this.proMapChallengeTimes = proMapChallengeTimes;
	}

	public int getWarriorMapLeftTimes() {
		return this.warriorMapLeftTimes;
	}

	public void setWarriorMapLeftTimes(int warriorMapLeftTimes) {
		this.warriorMapLeftTimes = warriorMapLeftTimes;
	}

	public int getTreasureMapLeftTimes() {
		return this.treasureMapLeftTimes;
	}

	public void setTreasureMapLeftTimes(int treasureMapLeftTimes) {
		this.treasureMapLeftTimes = treasureMapLeftTimes;
	}

	public int getMoneyMapLeftTimes() {
		return this.moneyMapLeftTimes;
	}

	public void setMoneyMapLeftTimes(int moneyMapLeftTimes) {
		this.moneyMapLeftTimes = moneyMapLeftTimes;
	}

	public Stage getTmpStage() {
		return this.tmpStage;
	}

	public ArrayList<MapTemplate> getOpenedProMaps() {
		return this.openedProMaps;
	}

	public void setOpenedProMaps(ArrayList<MapTemplate> openedProMaps) {
		this.openedProMaps = openedProMaps;
	}

	public void setTmpStage(Stage tmpStage) {
		this.tmpStage = tmpStage;
	}

	public void setClearMaps(HashMap<Integer, ClearMap> clearMaps) {
		this.clearMaps = clearMaps;
	}

	private void readObject(ObjectInputStream in) {
		int i;
		try {
			int version = in.readInt();
			int size = in.readInt();
			this.clearMaps = new HashMap();
			this.comparator = new ClearMapComparator();
			this.openedProMaps = new ArrayList();
			this.clearProMaps = new HashSet();
			this.firstMaps = new HashSet();
			this.firstStages = new HashSet();
			this.helpStages = new HashSet();

			for (i = 0; i < size; ++i) {
				ClearMap cm = ClearMap.readObject(in, version);
				this.clearMaps.put(Integer.valueOf(cm.id), cm);
			}

			this.proMapChallengeTimes = in.readInt();
			size = in.readInt();
			for (i = 0; i < size; ++i) {
				this.clearProMaps.add(Integer.valueOf(in.readInt()));
			}
			this.warriorMapLeftTimes = in.readInt();
			this.treasureMapLeftTimes = in.readInt();
			this.moneyMapLeftTimes = in.readInt();
			this.multiChallengeTime = in.readLong();

			size = in.readInt();
			for (i = 0; i < size; ++i) {
				this.firstMaps.add(Integer.valueOf(in.readInt()));
			}
			size = in.readInt();
			for (i = 0; i < size; ++i) {
				this.firstStages.add(Integer.valueOf(in.readInt()));
			}
			size = in.readInt();
			for (i = 0; i < size; ++i)
				this.helpStages.add(Integer.valueOf(in.readInt()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		Integer i;
		out.writeInt(2);
		out.writeInt(this.clearMaps.size());
		for (ClearMap cm : this.clearMaps.values()) {
			cm.writeObject(out);
		}
		out.writeInt(this.proMapChallengeTimes);
		out.writeInt(this.clearProMaps.size());
		for (Iterator<Integer> it = this.clearProMaps.iterator(); it.hasNext();) {
			i = it.next();
			out.writeInt(i);
		}
		out.writeInt(this.warriorMapLeftTimes);
		out.writeInt(this.treasureMapLeftTimes);
		out.writeInt(this.moneyMapLeftTimes);
		out.writeLong(this.multiChallengeTime);

		out.writeInt(this.firstMaps.size());
		for (Iterator<Integer> it = this.firstMaps.iterator(); it.hasNext();) {
			i = it.next();
			out.writeInt(i.intValue());
		}
		out.writeInt(this.firstStages.size());
		for (Iterator<Integer> it = this.firstStages.iterator(); it.hasNext();) {
			i = it.next();
			out.writeInt(i.intValue());
		}
		out.writeInt(this.helpStages.size());
		for (Iterator<Integer> it = this.helpStages.iterator(); it.hasNext();) {
			i = it.next();
			out.writeInt(i.intValue());
		}
	}

	public int getTotalEarnedStars() {
		int num = 0;
		Iterator itx = this.clearMaps.keySet().iterator();
		while (itx.hasNext()) {
			int id = ((Integer) itx.next()).intValue();
			ClearMap cm = (ClearMap) this.clearMaps.get(Integer.valueOf(id));
			if (cm != null) {
				num += cm.getEarnedStars();
			}
		}
		return num;
	}

	public int getActivityMapLeftTimes(int mapType) {
		if (mapType == 3)
			return this.warriorMapLeftTimes;
		if (mapType == 4)
			return this.treasureMapLeftTimes;
		if (mapType == 5) {
			return this.moneyMapLeftTimes;
		}
		return 0;
	}

	public void decActivityMapLeftTimes(int mapType) {
		if (mapType == 3) {
			if (this.warriorMapLeftTimes > 0)
				this.warriorMapLeftTimes -= 1;
		} else if (mapType == 4) {
			if (this.treasureMapLeftTimes > 0)
				this.treasureMapLeftTimes -= 1;
		} else {
			if ((mapType != 5) || (this.moneyMapLeftTimes <= 0))
				return;
			this.moneyMapLeftTimes -= 1;
		}
	}

	public PbStageMap.ActivityMapData genActivityMapData(int mapType) {
		PbStageMap.ActivityMapData.Builder builder = PbStageMap.ActivityMapData.newBuilder();
		MapTemplate template = MapService.getSpecialMapTemplate(mapType);
		PbStageMap.MapInfo mapInfo = PbStageMap.MapInfo.newBuilder().setId(template.id).setIconId(template.iconId)
				.setName(template.name).setOpenLevel(template.openLevel).setIsOpen(true).build();
		builder.setMap(mapInfo);
		Calendar now = Calendar.getInstance();
		if (mapType == 3) {
			builder.setType(PbStageMap.ActivityMapData.Type.Warrior);
			builder.setIsOpen(isOpenDay(mapType, now));
			if (builder.getIsOpen())
				builder.setLeftTimes(this.warriorMapLeftTimes);
			else {
				builder.setLeftTimes(0);
			}
		} else if (mapType == 4) {
			builder.setType(PbStageMap.ActivityMapData.Type.Treasure);
			builder.setIsOpen(isOpenDay(mapType, now));
			if (builder.getIsOpen())
				builder.setLeftTimes(this.treasureMapLeftTimes);
			else
				builder.setLeftTimes(0);
		} else if (mapType == 5) {
			builder.setType(PbStageMap.ActivityMapData.Type.Money);
			builder.setLeftTimes(this.moneyMapLeftTimes);
			builder.setIsOpen(true);
		}
		return builder.build();
	}

	public long getMultiChallengeTime() {
		return this.multiChallengeTime;
	}

	public void setMultiChallengeTime(long multiChallengeTime) {
		this.multiChallengeTime = multiChallengeTime;
	}

	public static boolean isOpenActivity(int mapType) {
		Calendar now = Calendar.getInstance();

		return ((!(isOpenDay(mapType, now))) || (!(beforeEnd(now))) || (!(afterStart(now))));
	}

	private static boolean isOpenDay(int mapType, Calendar now) {
		int[] days;
		if (mapType == 3)
			days = warriorMapRunDays;
		else if (mapType == 4)
			days = treasureMapRunDays;
		else {
			return false;
		}
		int weekDay = now.get(7);
		for (int tmp : days) {
			if (weekDay == tmp) {
				return true;
			}
		}
		return false;
	}

	private static boolean afterStart(Calendar cal) {
		int hour = cal.get(11);
		if (hour > activityMapStartTime[0])
			return true;
		if (hour < activityMapStartTime[0]) {
			return false;
		}
		int min = cal.get(12);
		if (min > activityMapStartTime[1])
			return true;
		if (min < activityMapStartTime[1]) {
			return false;
		}
		int sec = cal.get(13);

		return (sec <= activityMapStartTime[2]);
	}

	private static boolean beforeEnd(Calendar cal) {
		int hour = cal.get(11);
		if (hour < activityMapEndTime[0])
			return true;
		if (hour > activityMapEndTime[0]) {
			return false;
		}
		int min = cal.get(12);
		if (min < activityMapEndTime[1])
			return true;
		if (min > activityMapEndTime[1]) {
			return false;
		}
		int sec = cal.get(13);

		return (sec > activityMapEndTime[2]);
	}

	public Set<Integer> getFirstMaps() {
		return this.firstMaps;
	}

	public Set<Integer> getFirstStages() {
		return this.firstStages;
	}

	public void setFirstMaps(Set<Integer> firstMaps) {
		this.firstMaps = firstMaps;
	}

	public void setFirstStages(Set<Integer> firstStages) {
		this.firstStages = firstStages;
	}

	public int getBlobId() {
		return 5;
	}

	class ClearMapComparator implements Comparator<ClearMap> {
		public int compare(ClearMap m1, ClearMap m2) {
			return (m1.id - m2.id);
		}
	}
}
