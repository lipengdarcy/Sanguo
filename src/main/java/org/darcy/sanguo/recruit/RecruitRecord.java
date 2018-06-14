package org.darcy.sanguo.recruit;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.activity.ActivityInfo;
import org.darcy.sanguo.activity.item.RecruitDropAI;
import org.darcy.sanguo.drop.DropGroup;
import org.darcy.sanguo.persist.PlayerBlobEntity;
import org.darcy.sanguo.player.Player;

import sango.packet.PbRecruit;

public class RecruitRecord implements PlayerBlobEntity {
	public static final int LEAD_FIRST_DROP = 10407;
	private static final long serialVersionUID = -3822805786710125746L;
	private static int VERSION = 1;

	private Map<Integer, RecruitInfo> recruitInfos = new HashMap();

	public RecruitRecord() {
		this.recruitInfos.put(Integer.valueOf(1), new RecruitInfo(1));
		this.recruitInfos.put(Integer.valueOf(2), new RecruitInfo(2));
		this.recruitInfos.put(Integer.valueOf(3), new RecruitInfo(3));
	}

	public RecruitInfo getInfo(int id) {
		return ((RecruitInfo) this.recruitInfos.get(Integer.valueOf(id)));
	}

	public Map<Integer, RecruitInfo> getRecruitInfos() {
		return this.recruitInfos;
	}

	public long getLastTime(int id) {
		RecruitInfo info = getInfo(id);
		return (System.currentTimeMillis() - info.lastTime);
	}

	public long getLastFreeTime(int id) {
		RecruitInfo info = getInfo(id);
		return (System.currentTimeMillis() - info.lastFreeTime);
	}

	public long getRestTimeToNextFree(int id) {
		if (id == 1) {
			return -1L;
		}
		long gap = ((RecruitService) Platform.getServiceManager().get(RecruitService.class))
				.getTemplate(id).freeInterval * 1000;
		long lastTime = getLastFreeTime(id);
		int rest = 0;
		if (lastTime < gap) {
			rest = (int) (gap - lastTime);
		}
		return rest;
	}

	public boolean isFree(int type) {
		return (getRestTimeToNextFree(type) > 0L);
	}

	public boolean isFirstRecruit(int type) {
		return (getInfo(type).count > 0);
	}

	public boolean isJewelFirstRecruit(int type) {
		if (type == 1) {
			return false;
		}

		return (getInfo(type).jewelCount > 0);
	}

	public void execute(int id, boolean isInsure, boolean isFree) {
		RecruitInfo info = getInfo(id);
		info.count += 1;
		if (isInsure)
			info.insureCount = 0;
		else {
			info.insureCount += 1;
		}
		info.lastTime = System.currentTimeMillis();
		if (isFree)
			info.lastFreeTime = info.lastTime;
		else
			info.jewelCount += 1;
	}

	public int getCount(int type) {
		return getInfo(type).count;
	}

	public DropGroup getDrop(int type) {
		RecruitTemplate rt = ((RecruitService) Platform.getServiceManager().get(RecruitService.class))
				.getTemplate(type);
		int thisCount = getCount(type) + 1;
		int mod = thisCount % rt.drops.length;
		if (mod == 0) {
			mod = rt.drops.length;
		}
		return rt.drops[(mod - 1)];
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		try {
			int version = in.readInt();
			this.recruitInfos = new HashMap();
			RecruitInfo info = new RecruitInfo(1);
			info.readObject(in);
			this.recruitInfos.put(Integer.valueOf(info.id), info);
			info = new RecruitInfo(2);
			info.readObject(in);
			this.recruitInfos.put(Integer.valueOf(info.id), info);
			info = new RecruitInfo(3);
			info.readObject(in);
			this.recruitInfos.put(Integer.valueOf(info.id), info);
		} catch (Exception e) {
			this.recruitInfos = new HashMap();
			this.recruitInfos.put(Integer.valueOf(1), new RecruitInfo(1));
			this.recruitInfos.put(Integer.valueOf(2), new RecruitInfo(2));
			this.recruitInfos.put(Integer.valueOf(3), new RecruitInfo(3));
			e.printStackTrace();
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(VERSION);
		Iterator itx = this.recruitInfos.keySet().iterator();
		while (itx.hasNext()) {
			int key = ((Integer) itx.next()).intValue();
			RecruitInfo info = (RecruitInfo) this.recruitInfos.get(Integer.valueOf(key));
			if (info != null)
				info.writeObject(out);
		}
	}

	public Object clone() {
		RecruitRecord record = new RecruitRecord();
		Set<Entry<Integer, RecruitInfo>> set = record.recruitInfos.entrySet();
		for (Map.Entry entry : set) {
			RecruitInfo info = (RecruitInfo) entry.getValue();
			RecruitInfo thisInfo = (RecruitInfo) this.recruitInfos.get(entry.getKey());
			info.count = thisInfo.count;
			info.insureCount = thisInfo.insureCount;
			info.lastTime = thisInfo.lastTime;
			info.lastFreeTime = thisInfo.lastFreeTime;
		}
		return record;
	}

	public int getBlobId() {
		return 4;
	}

	public class RecruitInfo {
		public int id;
		public int count;
		public int insureCount;
		public long lastTime;
		public long lastFreeTime;
		public int jewelCount;

		public RecruitInfo(int id) {
			this.id = id;
			int initInterval = ((RecruitService) Platform.getServiceManager().get(RecruitService.class))
					.getTemplate(id).initInterval;
			int freeInterval = ((RecruitService) Platform.getServiceManager().get(RecruitService.class))
					.getTemplate(id).freeInterval;
			if (initInterval > 0) {
				this.lastTime = (System.currentTimeMillis() + initInterval * 1000 - (freeInterval * 1000));
				this.lastFreeTime = this.lastTime;
			}
		}

		public void readObject(ObjectInputStream in) {
			try {
				this.count = in.readInt();
				this.insureCount = in.readInt();
				this.lastTime = in.readLong();
				this.lastFreeTime = in.readLong();
				this.jewelCount = in.readInt();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void writeObject(ObjectOutputStream out) throws IOException {
			out.writeInt(this.count);
			out.writeInt(this.insureCount);
			out.writeLong(this.lastTime);
			out.writeLong(this.lastFreeTime);
			out.writeInt(this.jewelCount);
		}

		public int getInsureNum() {
			RecruitTemplate template = ((RecruitService) Platform.getServiceManager().get(RecruitService.class))
					.getTemplate(this.id);
			int insureNum = 0;
			if (this.count >= template.firstInsureCount)
				insureNum = template.normalInsureCount;
			else {
				insureNum = template.firstInsureCount;
			}
			return insureNum;
		}

		public PbRecruit.RecruitInfo genRecruitInfo(Player p) {
			RecruitTemplate template = ((RecruitService) Platform.getServiceManager().get(RecruitService.class))
					.getTemplate(this.id);
			PbRecruit.RecruitInfo.Builder builder = PbRecruit.RecruitInfo.newBuilder();
			builder.setRecruitType(this.id);
			builder.setNextFreeTime(RecruitRecord.this.getRestTimeToNextFree(this.id));
			int type = 0;
			if (this.id == 1) {
				builder.setItemId(template.singleCost.template.id);
				builder.setItemNum(template.singleCost.count);
				type = 1;
			} else {
				builder.setSingleJewel(template.singleCost.count);
				if (this.id == 3) {
					int insureCount = getInsureNum();
					int restNum = insureCount - this.insureCount - 1;
					builder.setInsureRecruitRestNum(restNum);

					if (template.tenCost != null) {
						builder.setTenJewel(template.tenCost.count);
					}
					type = 3;
				} else {
					type = 2;
				}
			}
			if (ActivityInfo.isOpenActivity(11, p)) {
				RecruitDropAI ai = (RecruitDropAI) ActivityInfo.getItem(p, 11);
				if (ai != null) {
					builder.setHasExtraDrop(ai.isEffect(type));
					if (template.tenCost != null) {
						builder.setHasTenExtraDrop(ai.isEffect(4));
					}
				}
			}
			builder.setRecruitCount(this.jewelCount);
			return builder.build();
		}
	}
}
