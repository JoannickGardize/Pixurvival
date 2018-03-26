package com.pixurvival.core.message;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CraftItemRequest {

	private short craftId;
	private short quantity;

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<CraftItemRequest> {

		@Override
		public void write(Kryo kryo, Output output, CraftItemRequest object) {
			output.writeShort(object.craftId);
			output.writeShort(object.quantity);
		}

		@Override
		public CraftItemRequest read(Kryo kryo, Input input, Class<CraftItemRequest> type) {
			return new CraftItemRequest(input.readShort(), input.readShort());
		}

	}
}
