package org.darcy.sanguo.asynccall;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.persist.DbService;

public class AsyncDelete implements Runnable {
	Object obj;
	Object key;

	public AsyncDelete(Object obj, Object key) {
		this.obj = obj;
		this.key = key;
	}

	public void run() {
		((DbService) Platform.getServiceManager().get(DbService.class)).delete(this.obj, this.key);
	}
}
