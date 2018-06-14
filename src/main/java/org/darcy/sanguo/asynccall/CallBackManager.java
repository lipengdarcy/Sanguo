package org.darcy.sanguo.asynccall;

import java.util.concurrent.ArrayBlockingQueue;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.updater.Updatable;

public class CallBackManager implements Updatable {
	private ArrayBlockingQueue<CallBackable> queue = new ArrayBlockingQueue<CallBackable>(2048);

	public void addCallBack(CallBackable call) {
		this.queue.add(call);
	}

	public boolean update() {
		CallBackable call = null;
		while ((call = (CallBackable) this.queue.poll()) != null) {
			try {
				long s = System.currentTimeMillis();
				call.callback();
				long dis = System.currentTimeMillis() - s;
				if (dis > 10L)
					Platform.getLog()
							.logWarn("callback too long time  " + call.getClass().getSimpleName() + " time:" + dis);
			} catch (Throwable t) {
				Platform.getLog().logError(t);
			}
		}
		return false;
	}
}
