package com.pixurvival.core;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.pixurvival.core.item.ItemStackEntity;
import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.core.livingEntity.PlayerEntity;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EntityClassRegistry {

	@SuppressWarnings("unchecked")
	private static Supplier<Entity>[] entityProducers = new Supplier[3];
	private static Map<Class<? extends Entity>, Byte> entityIds = new HashMap<>();

	static {
		entityProducers[0] = PlayerEntity::new;
		entityIds.put(PlayerEntity.class, (byte) 0);

		entityProducers[1] = ItemStackEntity::new;
		entityIds.put(ItemStackEntity.class, (byte) 1);

		entityProducers[2] = CreatureEntity::new;
		entityIds.put(CreatureEntity.class, (byte) 2);
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
