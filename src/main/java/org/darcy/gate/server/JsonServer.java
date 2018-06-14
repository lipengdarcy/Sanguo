package org.darcy.gate.server;

public class JsonServer {
	private int id;
	private String name;
	private String ip;
	private int port;
	private int nubmer;
	private int status;

	public JsonServer(int id, String name, String ip, int port, int status, int number) {
		this.id = id;
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.status = status;
		this.nubmer = number;
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

	public int getNubmer() {
		return this.nubmer;
	}

	public void setNubmer(int nubmer) {
		this.nubmer = nubmer;
	}
}
