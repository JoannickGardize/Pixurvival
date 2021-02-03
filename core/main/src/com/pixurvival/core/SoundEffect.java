package com.pixurvival.core;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.util.Vector2;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SoundEffect {
	private int id;
	private Vector2 position;

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<SoundEffect> {

		@Override
		public void write(Kryo kryo, Output output, SoundEffect object) {
			output.writeVarInt(object.id, false);
			output.writeFloat(object.position.getX());
			output.writeFloat(object.position.getY());
		}

		@Override
		public SoundEffect read(Kryo kryo, Input input, Class<SoundEffect> type) {
			return new SoundEffect(input.readVarInt(false), new Vector2(input.readFloat(), input.readFloat()));
		}
	}
}
