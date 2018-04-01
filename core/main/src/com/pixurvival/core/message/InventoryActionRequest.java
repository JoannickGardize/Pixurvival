package com.pixurvival.core.message;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryActionRequest {

	@Getter
	public static enum Type {
		SWAP_CLICK_MY_INVENTORY,
		SPLIT_CLICK_MY_INVENTORY;
	}

	private Type type;
	private short slotIndex;

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<InventoryActionRequest> {

		@Override
		public void write(Kryo kryo, Output output, InventoryActionRequest object) {
			output.writeByte(object.type.ordinal());
			output.writeShort(object.slotIndex);
		}

		@Override
		public InventoryActionRequest read(Kryo kryo, Input input, Class<InventoryActionRequest> type) {
			return new InventoryActionRequest(Type.values()[input.readByte()], input.readShort());
		}

	}
}
