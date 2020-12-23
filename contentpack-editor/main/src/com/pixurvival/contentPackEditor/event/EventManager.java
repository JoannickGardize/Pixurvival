package com.pixurvival.contentPackEditor.event;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;

import com.pixurvival.core.util.ReflectionUtils;

import lombok.Getter;

public class EventManager {

	private class WeakReferenceList<T> extends WeakReference<T> {

		@Getter
		private List<MethodRegistration> containingList;

		public WeakReferenceList(List<MethodRegistration> containingList, T referent, ReferenceQueue<? super T> q) {
			super(referent, q);
			this.containingList = containingList;
		}

	}

	private class MethodRegistration {
		private Reference<Object> instanceRef;
		private Method method;

		public MethodRegistration(List<MethodRegistration> containingList, Object object, Method method) {
			this.instanceRef = new WeakReferenceList<>(containingList, object, referenceQueue);
			this.method = method;
			method.setAccessible(true);
		}

		public void invoke(Event event) {
			Object o = instanceRef.get();
			if (o == null) {
				return;
			}
			try {
				method.invoke(o, event);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	private static @Getter EventManager instance = new EventManager();

	private Set<Object> registeredObjects = new HashSet<>();
	private Map<Class<? extends Event>, List<MethodRegistration>> registrations = new HashMap<>();
	private ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();

	private EventManager() {
	}

	@SuppressWarnings("unchecked")
	public synchronized void register(Object object) {
		clearQueue();
		if (registeredObjects.contains(object)) {
			return;
		}
		registeredObjects.add(object);
		for (Method method : ReflectionUtils.getAllMethods(object.getClass())) {
			if (method.isAnnotationPresent(EventListener.class) && method.getParameterTypes().length == 1 && Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
				List<MethodRegistration> list = registrations.get(method.getParameterTypes()[0]);
				if (list == null) {
					list = new ArrayList<>();
					registrations.put((Class<Event>) (method.getParameterTypes()[0]), list);
				}
				list.add(new MethodRegistration(list, object, method));
			}
		}
	}

	public void fire(Event event) {
		clearQueue();
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
	}

	private void clearQueue() {
		Reference<? extends Object> ref;
		while ((ref = referenceQueue.poll()) != null) {
			for (Iterator<MethodRegistration> iterator = ((WeakReferenceList<? extends Object>) ref).getContainingList().iterator(); iterator.hasNext();) {
				MethodRegistration methodRegistration = iterator.next();
				if (methodRegistration.instanceRef == ref) {
					iterator.remove();
					break;
				}
			}
		}
	}

}
