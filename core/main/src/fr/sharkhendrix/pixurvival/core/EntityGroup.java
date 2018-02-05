package fr.sharkhendrix.pixurvival.core;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import lombok.Getter;

public enum EntityGroup {
	PLAYER;

	static {
		for (byte i = 0; i < EntityGroup.values().length; i++) {
			EntityGroup.values()[i].id = i;
		}
	}

	private @Getter byte id;

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<EntityGroup> {

		@Override
		public void write(Kryo kryo, Output output, EntityGroup object) {
			output.writeByte(object.id);
		}

		@Override
		public EntityGroup read(Kryo kryo, Input input, Class<EntityGroup> type) {
			return EntityGroup.values()[input.readByte()];
		}

	}
}
