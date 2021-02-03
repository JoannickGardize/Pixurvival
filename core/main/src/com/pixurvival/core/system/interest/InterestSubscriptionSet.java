package com.pixurvival.core.system.interest;

import java.util.HashMap;
import java.util.Map;

import com.pixurvival.core.util.ReflectionUtils;

/**
 * Holds {@link InterestSubscription}s, reachable by {@link Interest} type.
 * 
 * @author SharkHendrix
 *
 */
public class InterestSubscriptionSet {

	private Map<Class<? extends Interest>, InterestSubscription<? extends Interest>> interestSubscriptions = new HashMap<>();

	/**
	 * Get or or create an {@link InterestSubscription} for the given
	 * {@link Interest} type.
	 * 
	 * @param <T>
	 *            the interest type
	 * @param type
	 *            the type associated to the {@link InterestSubscription}
	 * @return the existing or newly created {@link InterestSubscription} for the
	 *         given type.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Interest> InterestSubscription<T> get(Class<T> type) {
		return (InterestSubscription<T>) interestSubscriptions.computeIfAbsent(type, InterestSubscription::new);
	}

	/**
	 * Subscribe the given object to all {@link InterestSubscription}s according all
	 * its implementing interfaces.
	 * 
	 * @param subscriber
	 *            the object to subscribe to all applicable
	 *            {@link InterestSubscription}s.
	 */
	@SuppressWarnings("unchecked")
	public void subscribeAll(Object subscriber) {
		for (Class<?> type : ReflectionUtils.getAllInterfaces(subscriber.getClass())) {
			if (Interest.class.isAssignableFrom(type)) {
				get((Class<Interest>) type).subscribe((Interest) subscriber);
			}
		}
	}
}