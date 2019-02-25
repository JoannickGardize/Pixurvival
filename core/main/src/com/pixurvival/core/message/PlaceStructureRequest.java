package com.pixurvival.core.message;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.aliveEntity.PlayerEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlaceStructureRequest implements IPlayerActionRequest {

	private int x;
	private int y;

	@Override
	public void apply(PlayerEntity player) {

	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<PlaceStructureRequest> {

		@Override
		public void write(Kryo kryo, Output output, PlaceStructureRequest object) {
			output.writeInt(object.x);
			output.writeInt(object.y);
		}

		@Override
		public PlaceStructureRequest read(Kryo kryo, Input input, Class<PlaceStructureRequest> type) {
			return new PlaceStructureRequest(input.readInt(), input.readInt());
		}
	}
}
