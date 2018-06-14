package org.darcy.sanguo.gate;

import org.darcy.sanguo.Configuration;
import org.darcy.sanguo.service.Service;

public class GateService implements Service {
	public void startup() throws Exception {
		for (int i = 0; i < Configuration.gateIps.length; ++i) {
			String ip = Configuration.gateIps[i];
			int port = Configuration.gatePorts[i];
			new Thread(new GateConnector(ip, port), "GateConnector:" + ip).start();
		}
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
	}
}
