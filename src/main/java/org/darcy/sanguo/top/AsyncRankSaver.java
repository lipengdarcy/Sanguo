package org.darcy.sanguo.top;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.persist.EntityManager;

public class AsyncRankSaver implements Runnable {
	List<Top> btlRanks;
	List<Top> levelRanks;
	HashMap<Integer, Top> oldAllLevels;
	HashMap<Integer, Top> oldAllBtls;

	public AsyncRankSaver(List<Top> levelRanks, List<Top> btlRanks, HashMap<Integer, Top> oldAllLevels,
			HashMap<Integer, Top> oldAllBtls) {
		this.levelRanks = levelRanks;
		this.btlRanks = btlRanks;
		this.oldAllBtls = oldAllBtls;
		this.oldAllLevels = oldAllLevels;
	}

	public void run() {
		save();
	}

	public void save() {
		Top t1;
		Top t2;
		Platform.getLog().logSystem("rank saving......");
		EntityManager ds = Platform.getEntityManager();
		List news = new ArrayList();
		for (Iterator localIterator = this.btlRanks.iterator(); localIterator.hasNext();) {
			t1 = (Top) localIterator.next();
			t2 = (Top) this.oldAllBtls.get(Integer.valueOf(t1.getPid()));
			if (t2 == null) {
				news.add(t1);
			} else {
				t2.setRank(t1.getRank());
				t2.setValue(t1.getValue());
			}
		}
		for (Iterator localIterator = this.levelRanks.iterator(); localIterator.hasNext();) {
			t1 = (Top) localIterator.next();
			t2 = (Top) this.oldAllLevels.get(Integer.valueOf(t1.getPid()));
			if (t2 == null) {
				news.add(t1);
			} else {
				t2.setRank(t1.getRank());
				t2.setValue(t1.getValue());
			}

		}

		ds.addBatch(news);
		for (Iterator localIterator = news.iterator(); localIterator.hasNext();) {
			Object t = localIterator.next();
			Top top = (Top) t;
			try {
				if (top.getType() == 2) {
					this.oldAllBtls.put(Integer.valueOf(top.getPid()), top);
				}
				this.oldAllLevels.put(Integer.valueOf(top.getPid()), top);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		ds.updateBatch(new ArrayList(this.oldAllBtls.values()));

		ds.updateBatch(new ArrayList(this.oldAllLevels.values()));
		Platform.getLog().logSystem("rank saved!");
	}
}
