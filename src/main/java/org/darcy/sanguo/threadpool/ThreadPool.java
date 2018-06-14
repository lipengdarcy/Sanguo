package org.darcy.sanguo.threadpool;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
	public static final int CORE_POOL_SIZE = 10;
	public static final int MAX_POOL_SIZE = 300;
	protected ThreadPoolExecutor executor;
	private LinkedBlockingDeque<Runnable> queue = new LinkedBlockingDeque<Runnable>();

	public ThreadPool() {
		this.executor = new ThreadPoolExecutor(10, 300, 30L, TimeUnit.SECONDS, this.queue);
		this.executor.allowCoreThreadTimeOut(true);
	}

	public void execute(Runnable call) {
		this.executor.execute(call);
	}

	public int getSize() {
		return this.queue.size();
	}

	public void setCoreSize(int size) {
		this.executor.setCorePoolSize(size);
	}

	public int getCoreSize() {
		return this.executor.getCorePoolSize();
	}

	public int getMaxSize() {
		return this.executor.getMaximumPoolSize();
	}
}
