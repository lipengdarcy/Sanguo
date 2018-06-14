package org.darcy.sanguo.service;

import java.util.LinkedHashMap;
import java.util.Map;

public class ServiceManager {
	public static Map<Class<? extends Service>, Service> services = new LinkedHashMap<Class<? extends Service>, Service>();

	public void add(Service service) throws Exception {
		service.startup();
		services.put(service.getClass(), service);
	}

	public Service get(Class<?> clazz) {
		return services.get(clazz);
	}
}
