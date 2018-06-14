package org.darcy.sanguo.arena;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ArenaInfo implements Serializable {
	private static final long serialVersionUID = -4867375730072156136L;
	private static int version = 1;

	private ConcurrentHashMap<Integer, RankInfo> rankInfos = new ConcurrentHashMap();

	private ConcurrentHashMap<Integer, BeChallengedInfo> beChallengedInfos = new ConcurrentHashMap();

	public ConcurrentHashMap<Integer, RankInfo> getRankInfos() {
		return this.rankInfos;
	}

	public void setRankInfos(ConcurrentHashMap<Integer, RankInfo> rankInfos) {
		this.rankInfos = rankInfos;
	}

	public ConcurrentHashMap<Integer, BeChallengedInfo> getChallengeInfos() {
		return this.beChallengedInfos;
	}

	public void setChallengeInfos(ConcurrentHashMap<Integer, BeChallengedInfo> challengeInfos) {
		this.beChallengedInfos = challengeInfos;
	}

	private void readObject(ObjectInputStream in) {
		try {
			in.readInt();
			this.rankInfos = new ConcurrentHashMap();
			int size = in.readInt();
			for (int i = 0; i < size; ++i) {
				RankInfo info = new RankInfo();
				info.readObject(in);
				this.rankInfos.put(Integer.valueOf(info.id), info);
			}
			this.beChallengedInfos = new ConcurrentHashMap();
			size = in.readInt();
			for (int i = 0; i < size; ++i) {
				BeChallengedInfo info = new BeChallengedInfo();
				info.readObject(in);
				this.beChallengedInfos.put(Integer.valueOf(info.id), info);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		RankInfo info;
		BeChallengedInfo cinfo;
		out.writeInt(version);
		List<RankInfo> rankInfoList = new ArrayList();
		Iterator itx = this.rankInfos.keySet().iterator();
		while (itx.hasNext()) {
			int id = ((Integer) itx.next()).intValue();
			info = (RankInfo) this.rankInfos.get(Integer.valueOf(id));
			if (info != null) {
				rankInfoList.add(info);
			}
		}
		out.writeInt(rankInfoList.size());
		for (RankInfo a : rankInfoList) {
			a.writeObject(out);
		}
		List<BeChallengedInfo> beChallengedInfoList = new ArrayList();
		itx = this.beChallengedInfos.keySet().iterator();
		while (itx.hasNext()) {
			int id = ((Integer) itx.next()).intValue();
			cinfo = (BeChallengedInfo) this.beChallengedInfos.get(Integer.valueOf(id));
			if (cinfo != null) {
				beChallengedInfoList.add(cinfo);
			}
		}
		out.writeInt(beChallengedInfoList.size());
		for (BeChallengedInfo a : beChallengedInfoList)
			a.writeObject(out);
	}
}
