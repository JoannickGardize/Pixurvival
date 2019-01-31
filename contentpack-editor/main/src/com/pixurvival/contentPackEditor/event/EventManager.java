package com.pixurvival.contentPackEditor.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.SwingUtilities;

import com.pixurvival.core.util.ReflectionUtil;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class EventManager {

	@AllArgsConstructor
	private static class MethodRegistration {
		private Object instance;
		private Method method;

		public void invoke(Event event) {
			try {
				method.invoke(instance, event);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	private static @Getter EventManager instance = new EventManager();

	private BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<>();
	private Set<Object> registeredObjects = new HashSet<>();
	private Map<Class<? extends Event>, List<MethodRegistration>> registrations = new HashMap<>();

	private EventManager() {
	}

	@SuppressWarnings("unchecked")
	public synchronized void register(Object object) {
		if (registeredObjects.contains(object)) {
			return;
		}
		registeredObjects.add(object);
		for (Method method : ReflectionUtil.getAllMethods(object.getClass())) {
			if (method.isAnnotationPresent(EventListener.class) && method.getParameterTypes().length == 1 && Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
				List<MethodRegistration> list = registrations.get(method.getParameterTypes()[0]);
				if (list == null) {
					list = new ArrayList<>();
					registrations.put((Class<Event>) (method.getParameterTypes()[0]), list);
				}
				list.add(new MethodRegistration(object, method));
			}
		}
	}

	public void fire(Event event) {
		SwingUtilities.invokeLater(() -> {
			synchronized (EventManager.this) {
				List<MethodRegistration> list = registrations.get(event.getClass());
				if (list != null) {
					for (MethodRegistration methodRegistration : list) {
						methodRegistration.invoke(event);
					}
				}
			}
		});
		try {
			eventQueue.put(event);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void run() {

	}

}
