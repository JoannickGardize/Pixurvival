package com.pixurvival.core;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EntityRegistry {

	private static Map<Byte, Supplier<Entity>> entityProducers = new HashMap<>();
	private static Map<Class<? extends Entity>, Byte> entityIds = new HashMap<>();

	static {
		entityProducers.put((byte) 0, () -> new PlayerEntity());
		entityIds.put(PlayerEntity.class, (byte) 0);
	}

	public static Entity newEntity(byte id) {
		Supplier<Entity> supplier = entityProducers.get(id);
		if (supplier == null) {
			return null;
		} else {
			return supplier.get();
		}
	}

	public static byte getIdOf(Class<? extends Entity> entityClass) {
		return entityIds.get(entityClass);
	}
}
