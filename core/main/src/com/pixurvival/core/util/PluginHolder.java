package com.pixurvival.core.util;

import java.util.IdentityHashMap;
import java.util.Map;

public class PluginHolder<T> {

	private Map<Class<?>, Plugin<T>> plugins = new IdentityHashMap<>();

	public void addPlugin(Plugin<T> plugin) {
		plugins.put(plugin.getClass(), plugin);
	}

	public void removePlugin(Class<? extends Plugin<T>> type) {
		plugins.remove(type);
	}

	public void removeAllPlugins() {
		plugins.clear();
	}

	public void updatePlugins(T holder) {
		for (Plugin<T> plugin : plugins.values()) {
			plugin.update(holder);
		}
	}

	@SuppressWarnings("unchecked")
	public <E> E getPlugin(Class<E> type) {
		return (E) plugins.get(type);
	}
}
