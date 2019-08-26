package com.pixurvival.contentPackEditor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.pixurvival.core.contentPack.effect.DelayedFollowingElement;
import com.pixurvival.core.contentPack.effect.FollowingEffect;
import com.pixurvival.core.contentPack.effect.FollowingElement;
import com.pixurvival.core.contentPack.effect.OffsetAngleEffect;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BeanFactory {

	private static Map<Class<?>, Supplier<?>> suppliers = new HashMap<>();

	static {
		suppliers.put(DelayedFollowingElement.class, () -> {
			DelayedFollowingElement delayedFollowingElement = new DelayedFollowingElement();
			delayedFollowingElement.setFollowingElement(BeanFactory.newInstance(FollowingElement.class));
			return delayedFollowingElement;
		});

		suppliers.put(FollowingElement.class, () -> {
			FollowingEffect result = new FollowingEffect();
			result.setOffsetAngleEffect(new OffsetAngleEffect());
			return result;
		});
	}

	@SneakyThrows
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> type) {
		Supplier<?> supplier = suppliers.get(type);
		if (supplier == null) {
			return type.newInstance();
		} else {
			return (T) suppliers.get(type).get();
		}
	}

	public static <T> Supplier<T> newInstanceSupplier(Class<T> type) {
		return () -> BeanFactory.newInstance(type);
	}
}
