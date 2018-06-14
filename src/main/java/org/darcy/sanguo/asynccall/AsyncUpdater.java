package org.darcy.sanguo.asynccall;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.persist.DbService;

public class AsyncUpdater implements Runnable {
	Object obj;

	public AsyncUpdater(Object obj) {
		this.obj = obj;
	}

	public void run() {
		((DbService) Platform.getServiceManager().get(DbService.class)).update(this.obj);
	}
}
