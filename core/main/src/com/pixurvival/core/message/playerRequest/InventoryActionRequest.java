package com.pixurvival.core.message.playerRequest;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.item.PlayerInventoryInteractions;
import com.pixurvival.core.livingEntity.PlayerEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryActionRequest implements IPlayerActionRequest {

	private int slotIndex;
	private boolean splitMode;

	@Override
	public void apply(PlayerEntity player) {
		PlayerInventoryInteractions.interact(player.getInventory(), slotIndex, splitMode);
	}

	@Override
	public boolean isClientPreapply() {
		return true;
	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<InventoryActionRequest> {

		@Override
		public void write(Kryo kryo, Output output, InventoryActionRequest object) {
			output.writeVarInt(object.slotIndex, true);
			output.writeBoolean(object.splitMode);
		}

		@Override
		public InventoryActionRequest read(Kryo kryo, Input input, Class<InventoryActionRequest> type) {
			return new InventoryActionRequest(input.readVarInt(true), input.readBoolean());
		}
	}
}
