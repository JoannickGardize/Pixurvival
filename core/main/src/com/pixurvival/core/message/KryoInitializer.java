package com.pixurvival.core.message;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.pixurvival.core.util.Vector2;

import lombok.experimental.UtilityClass;

@UtilityClass
public class KryoInitializer {

	public static void apply(Kryo kryo) {
		register(kryo, Direction.class);
		register(kryo, Vector2.class);
		register(kryo, PlayerActionRequest.class);
		register(kryo, TimeRequest.class);
		register(kryo, TimeResponse.class);
		register(kryo, LoginRequest.class);
		register(kryo, LoginResponse.class);
		register(kryo, EntitiesUpdate.class);
		register(kryo, CreateWorld.class);
		register(kryo, StartGame.class);
	}

	@SuppressWarnings("unchecked")
	private static void register(Kryo kryo, Class<?> clazz) {
		try {
			Class<?>[] internalClasses = clazz.getClasses();
			for (Class<?> internalClass : internalClasses) {
				if (internalClass.getSimpleName().equals("Serializer")
						&& internalClass.getSuperclass() == Serializer.class) {
					kryo.register(clazz, ((Class<? extends Serializer<?>>) internalClass).newInstance());
					return;
				}
			}
			kryo.register(clazz);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
