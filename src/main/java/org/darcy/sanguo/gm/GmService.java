package org.darcy.sanguo.gm;

import org.darcy.sanguo.service.Service;

public class GmService implements Service {
	public static GmNoticer noticer = new GmNoticer();

	public void startup() throws Exception {
		//new Thread(new GmConnector(Configuration.gmIp, Configuration.gmPort), "GmConnector").start();
		//new Thread(noticer, "GmNoticer").start();
	}

	public static void addGmNotice(String content, int num, long interval) {
		noticer.addNotice(content, num, interval);
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
	}
}
