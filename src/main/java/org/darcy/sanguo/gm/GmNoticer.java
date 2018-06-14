package org.darcy.sanguo.gm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.CallBackable;

public class GmNoticer implements Runnable {
	public CopyOnWriteArrayList<GmNotice> list = new CopyOnWriteArrayList<GmNotice>();

	public void addNotice(String content, int num, long interval) {
		if ((num > 0) && (interval >= 0L)) {
			GmNotice gn = new GmNotice();
			gn.content = content;
			gn.num = num;
			gn.interval = interval;
			this.list.add(gn);
		}
	}

	public void run() {
		int errorCount = 0;
		while (true) {
			try {
				if (this.list.size() > 0) {
					long now = System.currentTimeMillis();
					List<String> msgs = new ArrayList<String>();

					for (GmNotice gn : this.list) {
						if (gn != null) {
							if (gn.num < 1) {
								this.list.remove(gn);
							} else if (gn.lastTime + gn.interval <= now) {
								msgs.add(gn.content);
								gn.lastTime = now;
								gn.num -= 1;
								Platform.getLog().logWorld("gm boardcast, surplus:" + gn.num + ",msg:" + gn.content);
							}
						}
					}

					if (msgs.size() > 0) {
						CallBackable call = new CallBackable() {
							public void callback() {
								for (String msg : msgs)
									Platform.getPlayerManager().boardCast(msg);
							}
						};
						Platform.getCallBackManager().addCallBack(call);
					}
				}
			} catch (Exception e) {
				Platform.getLog().logError(e);
				++errorCount;
				if (errorCount > 50) {
					this.list.clear();
					errorCount = 0;
				}
			}
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	class GmNotice {
		String content;
		int num;
		long interval;
		long lastTime;
	}
}
