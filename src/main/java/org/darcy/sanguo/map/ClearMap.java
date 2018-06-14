package org.darcy.sanguo.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.darcy.sanguo.service.MapService;

public class ClearMap {
	public static final int REWARDBOX_SIZE = 3;
	public int id;
	private HashMap<Integer, ClearStage> stages = new HashMap();

	boolean[] starReward = new boolean[3];

	private StageComparator comparator = new StageComparator();

	public boolean isFetchedRewardBox(int index) {
		return this.starReward[index];
	}

	public void fetchRewardBox(int index) {
		this.starReward[index] = true;
	}

	public void addClearStage(int stageId) {
		if (this.stages.get(Integer.valueOf(stageId)) == null) {
			ClearStage cs = new ClearStage();
			cs.id = stageId;
			this.stages.put(Integer.valueOf(stageId), cs);
		}
	}

	public MapTemplate getTemplate() {
		return ((MapTemplate) MapService.mapTemplates.get(Integer.valueOf(this.id)));
	}

	public boolean isFinished() {
		MapTemplate mt = (MapTemplate) MapService.mapTemplates.get(Integer.valueOf(this.id));
		if ((mt != null) && (mt.stageTemplates.length <= this.stages.size())) {
			Iterator localIterator = this.stages.values().iterator();
			while (true) {
				ClearStage cs = (ClearStage) localIterator.next();
				if (!(cs.isFinished()))
					return false;
				if (!(localIterator.hasNext())) {
					return true;
				}
			}
		}
		return false;
	}

	public int getEarnedStars() {
		int rst = 0;
		for (ClearStage stage : this.stages.values()) {
			rst += stage.getStars();
		}
		return rst;
	}

	public List<ClearStage> getStages() {
		if (this.stages.size() == 0) {
			MapTemplate mt = (MapTemplate) MapService.mapTemplates.get(Integer.valueOf(this.id));
			if (mt != null) {
				int id = mt.stageTemplates[0].id;
				ClearStage cs = new ClearStage();
				cs.id = id;
				this.stages.put(Integer.valueOf(id), cs);
			}
		}

		List list = new ArrayList(this.stages.values());
		Collections.sort(list, this.comparator);
		return list;
	}

	public ClearStage getClearStage(int stageId) {
		return ((ClearStage) this.stages.get(Integer.valueOf(stageId)));
	}

	public void setStages(HashMap<Integer, ClearStage> stages) {
		this.stages = stages;
	}

	public boolean hasGotStarReward() {
		MapTemplate mt = getTemplate();
		for (int i = 0; i < mt.startNeeds.length; ++i) {
			if ((getEarnedStars() >= mt.startNeeds[i]) && (!(isFetchedRewardBox(i)))) {
				return true;
			}
		}
		return false;
	}

	public void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(this.id);
		for (boolean b : this.starReward) {
			out.writeBoolean(b);
		}
		out.writeInt(this.stages.size());
		for (ClearStage cs : this.stages.values())
			cs.writeObject(out);
	}

	public static ClearMap readObject(ObjectInputStream in, int version) throws IOException {
		ClearMap cm = new ClearMap();

		cm.id = in.readInt();
		for (int i = 0; i < 3; ++i) {
			cm.starReward[i] = in.readBoolean();
		}
		cm.stages = new HashMap();
		int size = in.readInt();
		for (int i = 0; i < size; ++i) {
			ClearStage cs = ClearStage.readObject(in, version);
			cm.stages.put(Integer.valueOf(cs.id), cs);
		}
		return cm;
	}

	class StageComparator implements Comparator<ClearStage> {
		public int compare(ClearStage c1, ClearStage c2) {
			return (c1.id - c2.id);
		}
	}
}
