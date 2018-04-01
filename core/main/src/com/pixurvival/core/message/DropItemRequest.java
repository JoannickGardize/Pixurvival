package com.pixurvival.core.message;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DropItemRequest {

	private float direction;

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<DropItemRequest> {

		@Override
		public void write(Kryo kryo, Output output, DropItemRequest object) {
			output.writeFloat(object.direction);
		}

		@Override
		public DropItemRequest read(Kryo kryo, Input input, Class<DropItemRequest> type) {
			return new DropItemRequest(input.readFloat());
		}

	}
}
