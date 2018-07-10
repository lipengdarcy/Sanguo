package org.darcy.gate.version;

import lombok.Data;

public @Data class Version {
	private int id;
	private String verion;
	private String url;
	private String md5;
	private int size;
	private int type;

	public Version(int id, String verion, String url, String md5, int size, int type) {
		this.id = id;
		this.verion = verion;
		this.url = url;
		this.type = type;
		this.md5 = md5;
		this.size = size;
	}

	public Version() {
	}
}
