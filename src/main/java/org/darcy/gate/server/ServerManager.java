package org.darcy.gate.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.poi.ss.usermodel.Row;
import org.darcy.sanguo.utils.ExcelUtils;

import net.sf.json.JSONArray;

public class ServerManager {
	static ConcurrentHashMap<Integer, GateServer> servers = new ConcurrentHashMap<Integer, GateServer>();

	public static String jsonServers = "";
	private static ServerManager instance;
	private static String key = "key";

	ServerChecker serverCheck = new ServerChecker();

	private ServerComparator comparator = new ServerComparator();

	public static ServerManager getInstance() {
		if (instance == null) {
			synchronized (key) {
				instance = new ServerManager();
				reload();
			}
		}
		return instance;
	}

	private ServerManager() {
		Thread t = new Thread(this.serverCheck, "serverchecker");
		t.setDaemon(true);
		t.start();
	}

	public void shutdown() {
		this.serverCheck.close();
	}

	public List<GateServer> getServers() {
		List<GateServer> list = new ArrayList<GateServer>(servers.values());
		Collections.sort(list, this.comparator);
		return list;
	}

	public List<GateServer> getServers(boolean test) {
		List<GateServer> list = new ArrayList<GateServer>();
		for (GateServer server : servers.values()) {
			if (server.isTest() == test) {
				list.add(server);
			}
		}
		return list;
	}

	public void addServer(GateServer server) {
		servers.put(Integer.valueOf(server.getNumber()), server);
		geneJson();
	}

	public GateServer getServerByNumber(int number) {
		return ((GateServer) servers.get(Integer.valueOf(number)));
	}

	public static List<GateServer> getServerById(int id) {
		List<GateServer> ls = new ArrayList<GateServer>();
		for (GateServer s : servers.values()) {
			if (s.getId() == id) {
				ls.add(s);
			}
		}
		return ls;
	}

	public static void geneJson() {
		List<GateServer> list = getInstance().getServers();
		List<JsonServer> ls = new ArrayList<JsonServer>(list.size());
		for (GateServer server : list) {
			ls.add(server.toJosonServer());
		}
		JSONArray jsonArray = JSONArray.fromObject(ls);
		jsonServers = jsonArray.toString();
	}

	public static void reload() {
		List<Row> list = ExcelUtils.getGateInfo("servers.xls");
		for (Row r : list) {
			int pos = 0;
			if (r == null)
				break;
			if (r.getCell(pos) == null)
				break;
			int id = (int) r.getCell(pos++).getNumericCellValue();
			if (id == 0)
				continue;
			int number = (int) r.getCell(pos++).getNumericCellValue();
			String name = r.getCell(pos++).getStringCellValue();
			int state = (int) r.getCell(pos++).getNumericCellValue();

			GateServer server = (GateServer) servers.get(Integer.valueOf(number));
			if (server == null) {
				server = new GateServer();
				server.setId(id);
				server.setNumber(number);
				server.setName(name);
				server.setStatus(state);
				server.setStaticServer(true);
				servers.put(Integer.valueOf(number), server);
			} else {
				server.setNumber(number);
				server.setName(name);
				server.setStatus(state);
				server.setStaticServer(true);
			}
		}
		geneJson();
	}
}

class ServerChecker implements Runnable {
	boolean run;

	ServerChecker() {
		this.run = true;
	}

	public void close() {
		this.run = false;
	}

	public void run() {
		while (this.run) {
			try {
				synchronized (ServerManager.servers) {
					Iterator<GateServer> iter = ServerManager.servers.values().iterator();
					while (iter.hasNext()) {
						GateServer server = (GateServer) iter.next();
						if ((server.getSession() == null) || (server.isStaticServer())
								|| (!(server.getSession().isDisconnect())))
							continue;
						iter.remove();
					}

				}

				ServerManager.geneJson();
				Thread.sleep(10000L);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		System.out.println("servercheck over!!!!");
	}
}

class ServerComparator implements Comparator<GateServer> {
	public int compare(GateServer s1, GateServer s2) {
		return (s2.getNumber() - s1.getNumber());
	}
}
