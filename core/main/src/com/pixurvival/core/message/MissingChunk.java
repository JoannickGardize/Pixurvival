package com.pixurvival.core.message;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.map.Position;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MissingChunk extends Position {

	public MissingChunk(int x, int y) {
		super(x, y);
	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<MissingChunk> {

		@Override
		public void write(Kryo kryo, Output output, MissingChunk object) {
			output.writeInt(object.getX());
			output.writeInt(object.getY());
		}

		@Override
		public MissingChunk read(Kryo kryo, Input input, Class<MissingChunk> type) {
			return new MissingChunk(input.readInt(), input.readInt());
		}

	}
}
