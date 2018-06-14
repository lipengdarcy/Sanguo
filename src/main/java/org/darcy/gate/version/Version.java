package org.darcy.gate.version;

public class Version {
	private int id;
	private String verion;
	private String url;
	private String md5;
	private int size;
	private int type;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getVerion() {
		return this.verion;
	}

	public void setVerion(String verion) {
		this.verion = verion;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMd5() {
		return this.md5;
	}

	public int getSize() {
		return this.size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

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
