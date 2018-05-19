package com.pixurvival.core.message;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EquipmentActionRequest {

	private short index;

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<EquipmentActionRequest> {

		@Override
		public void write(Kryo kryo, Output output, EquipmentActionRequest object) {
			output.writeShort(object.index);
		}

		@Override
		public EquipmentActionRequest read(Kryo kryo, Input input, Class<EquipmentActionRequest> type) {
			return new EquipmentActionRequest(input.readShort());
		}

	}
}
