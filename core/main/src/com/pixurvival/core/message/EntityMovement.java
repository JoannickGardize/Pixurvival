package com.pixurvival.core.message;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.util.Vector2;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * Lite version of an entity, containing only movement data. Used to share far
 * ally positions between client and server.
 * 
 * @author SharkHendrix
 *
 */
@Value
@AllArgsConstructor
public class EntityMovement {

	private long id;
	private Vector2 position;
	private Vector2 velocity;

	public EntityMovement(PlayerEntity e) {
		id = e.getId();
		position = e.getPosition();
		velocity = e.getVelocity();
	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<EntityMovement> {

		@Override
		public void write(Kryo kryo, Output output, EntityMovement object) {
			output.writeLong(object.id);
			kryo.writeObject(output, object.position);
			kryo.writeObject(output, object.velocity);
		}

		@Override
		public EntityMovement read(Kryo kryo, Input input, Class<EntityMovement> type) {
			return new EntityMovement(input.readLong(), kryo.readObject(input, Vector2.class), kryo.readObject(input, Vector2.class));
		}

	}

}
