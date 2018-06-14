package org.darcy.sanguo.updater;

import java.util.ArrayList;

import org.darcy.sanguo.Platform;

public class UpdaterManager implements Updatable {
	protected ArrayList<Updatable> syncUpdatables = new ArrayList<Updatable>();

	public void addSyncUpdatable(Updatable updatable) {
		this.syncUpdatables.add(updatable);
	}

	public boolean update() {
		for (Updatable up : this.syncUpdatables) {
			try {
				long old = System.currentTimeMillis();
				up.update();
				long dis = System.currentTimeMillis() - old;
				if (dis > 40L)
					Platform.getLog().logWarn("update too long:" + up.getClass().getName() + ", time:" + dis);
			} catch (Throwable e) {
				Platform.getLog().logError(e);
			}
		}
		return false;
	}
}
