package com.pixurvival.core.message;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.map.Position;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MissingChunk {

	private Position[] positions;

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<MissingChunk> {

		@Override
		public void write(Kryo kryo, Output output, MissingChunk object) {
			output.writeShort(object.positions.length);
			for (Position position : object.positions) {
				output.writeInt(position.getX());
				output.writeInt(position.getY());
			}
		}

		@Override
		public MissingChunk read(Kryo kryo, Input input, Class<MissingChunk> type) {
			int length = input.readShort();
			Position[] positions = new Position[length];
			for (int i = 0; i < length; i++) {
				positions[i] = new Position(input.readInt(), input.readInt());
			}
			return new MissingChunk(positions);
		}

	}
}
