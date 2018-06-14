package org.darcy.sanguo.service;

public abstract interface Service {
	public abstract void startup() throws Exception;

	public abstract void shutdown();

	public abstract void reload() throws Exception;
}
