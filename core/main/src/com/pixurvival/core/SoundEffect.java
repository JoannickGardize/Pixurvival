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
	private SoundPreset preset;
	private Vector2 position;

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<SoundEffect> {

		@Override
		public void write(Kryo kryo, Output output, SoundEffect object) {
			output.writeByte(object.preset.ordinal());
			output.writeFloat(object.position.getX());
			output.writeFloat(object.position.getY());
		}

		@Override
		public SoundEffect read(Kryo kryo, Input input, Class<SoundEffect> type) {
			return new SoundEffect(SoundPreset.values()[input.readByte()], new Vector2(input.readFloat(), input.readFloat()));
		}
	}
}
