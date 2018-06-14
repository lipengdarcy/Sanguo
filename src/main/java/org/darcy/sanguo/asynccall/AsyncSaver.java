package org.darcy.sanguo.asynccall;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.persist.DbService;

public class AsyncSaver implements Runnable {
	Object obj;

	public AsyncSaver(Object obj) {
		this.obj = obj;
	}

	public void run() {
		((DbService) Platform.getServiceManager().get(DbService.class)).add(this.obj);
	}
}
