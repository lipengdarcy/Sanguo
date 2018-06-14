package org.darcy.gate.server;

import org.darcy.gate.net.ClientSession;

/**
 * 网关服务器
 */
public class GateServer {
	public static final int STAT_CLOSE = 0;
	public static final int STAT_NEW = 1;
	public static final int STAT_HOT = 2;
	private int id;
	private String name;
	private String ip;
	private int port;
	private boolean test;
	private int number;
	private boolean staticServer;
	private ClientSession session;
	private int status;

	public int getNumber() {
		return this.number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return this.port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isTest() {
		return this.test;
	}

	public void setTest(boolean test) {
		this.test = test;
	}

	public ClientSession getSession() {
		return this.session;
	}

	public void setSession(ClientSession session) {
		this.session = session;
	}

	public boolean isStaticServer() {
		return this.staticServer;
	}

	public void setStaticServer(boolean staticServer) {
		this.staticServer = staticServer;
	}

	public JsonServer toJosonServer() {
		int stat = this.status;
		if ((this.session == null) || (this.session.isDisconnect())) {
			stat = 0;
		}
		return new JsonServer(this.id, this.name, this.ip, this.port, stat, this.number);
	}
}
