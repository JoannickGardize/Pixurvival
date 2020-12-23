package com.pixurvival.contentPackEditor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.effect.DelayedFollowingElement;
import com.pixurvival.core.contentPack.effect.Effect;
import com.pixurvival.core.contentPack.effect.FollowingEffect;
import com.pixurvival.core.contentPack.effect.FollowingElement;
import com.pixurvival.core.contentPack.effect.LinearEffectMovement;
import com.pixurvival.core.contentPack.effect.OffsetAngleEffect;
import com.pixurvival.core.contentPack.gameMode.event.EffectEvent;
import com.pixurvival.core.contentPack.gameMode.event.PlayerProximityEventPosition;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.item.ResourceItem;
import com.pixurvival.core.contentPack.map.MapProvider;
import com.pixurvival.core.contentPack.map.ProcedurallyGeneratedMapProvider;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.livingEntity.alteration.Alteration;
import com.pixurvival.core.livingEntity.alteration.FollowingElementAlteration;
import com.pixurvival.core.util.Vector2;

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

		suppliers.put(Alteration.class, () -> {
			FollowingElementAlteration followingElement = new FollowingElementAlteration();
			followingElement.setFollowingElement(newInstance(FollowingElement.class));
			return followingElement;
		});

		suppliers.put(Item.class, () -> {
			Item item = new ResourceItem();
			item.setFrame(new Frame());
			item.setMaxStackSize(1);
			return item;
		});

		suppliers.put(Effect.class, () -> {
			Effect effect = new Effect();
			effect.setMovement(new LinearEffectMovement());
			return effect;
		});
		suppliers.put(EffectEvent.class, () -> {
			EffectEvent effectEvent = new EffectEvent();
			effectEvent.setPosition(new PlayerProximityEventPosition());
			return effectEvent;
		});

		suppliers.put(MapProvider.class, ProcedurallyGeneratedMapProvider::new);

		suppliers.put(Vector2.class, Vector2::new);
	}

	@SneakyThrows
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> type) {
		Supplier<?> supplier = suppliers.get(type);
		if (supplier == null) {
			return newFilledInstance(type);
		} else {
			return (T) suppliers.get(type).get();
		}
	}

	public static <T> Supplier<T> of(Class<T> type) {
		return () -> BeanFactory.newInstance(type);
	}

	private static <T> T newFilledInstance(Class<T> clazz) {
		try {
			T instance = clazz.newInstance();
			fill(instance);
			return instance;
		} catch (InstantiationException | IllegalAccessException e) {
			return null;
		}
	}

	private static void fill(Object instance) {
		Class<?> clazz = instance.getClass();
		forEachPropertyMethods(clazz, (getter, setter) -> {
			try {
				Object object = getter.invoke(instance);
				if (object == null) {
					Object attributeValue = newInstanceIfPossible(getter.getReturnType());
					if (attributeValue != null) {
						fill(attributeValue);
						setter.invoke(instance, attributeValue);
					}
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		});
	}

	private static void forEachPropertyMethods(Class<?> clazz, BiConsumer<Method, Method> action) {
		for (Method getter : clazz.getMethods()) {
			if (getter.getName().startsWith("get") && getter.getName().length() > 3 && getter.getParameterCount() == 0 && !NamedIdentifiedElement.class.isAssignableFrom(getter.getReturnType())) {
				try {
					Method setter = clazz.getMethod("set" + getter.getName().substring(3), getter.getReturnType());
					if (setter.getParameterCount() == 1 && setter.getParameters()[0].getType() == getter.getReturnType()) {
						action.accept(getter, setter);
					}
				} catch (NoSuchMethodException e) {
					// Nothing
				}
			}
		}
	}

	private static Object newInstanceIfPossible(Class<?> type) {
		if (type == String.class) {
			return null;
		} else if (Collection.class.isAssignableFrom(type)) {
			return new ArrayList<>();
		}
		Supplier<?> supplier = suppliers.get(type);
		if (supplier == null) {
			return null;
		} else {
			return supplier.get();
		}
	}
}
