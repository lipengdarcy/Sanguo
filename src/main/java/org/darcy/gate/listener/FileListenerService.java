package org.darcy.gate.listener;

import java.net.URL;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.darcy.ServerStartup;

public class FileListenerService {
	private final FileAlterationMonitor monitor;
	public final long INTERVAL = 2000L;

	public FileListenerService() {
		URL url = ServerStartup.class.getClassLoader().getResource("cfg/gate");
		FileAlterationObserver observer = new FileAlterationObserver(url.getFile());
		observer.addListener(new FileListener());
		this.monitor = new FileAlterationMonitor(2000L, new FileAlterationObserver[] { observer });
	}

	public void start() {
		try {
			this.monitor.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void shutdown() {
		try {
			this.monitor.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
