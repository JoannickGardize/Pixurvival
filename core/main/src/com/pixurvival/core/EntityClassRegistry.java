package com.pixurvival.core;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.pixurvival.core.aliveEntity.PlayerEntity;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EntityClassRegistry {

	@SuppressWarnings("unchecked")
	private static Supplier<Entity>[] entityProducers = new Supplier[2];
	private static Map<Class<? extends Entity>, Byte> entityIds = new HashMap<>();

	static {
		entityProducers[0] = () -> new PlayerEntity();
		entityIds.put(PlayerEntity.class, (byte) 0);

		entityProducers[1] = () -> new ItemStackEntity();
		entityIds.put(ItemStackEntity.class, (byte) 1);
	}

	public static Entity newEntity(byte id) {
		Supplier<Entity> supplier = entityProducers[id];
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
