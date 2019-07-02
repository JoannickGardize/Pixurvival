package com.pixurvival.core.message.playerRequest;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.contentPack.item.EdibleItem;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.PlayerInventory;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UseItemRequest implements IPlayerActionRequest {

	private int slotIndex;

	@Override
	public void apply(PlayerEntity player) {
		ItemStack itemStack;
		if (PlayerInventory.HELD_ITEM_STACK_INDEX == slotIndex) {
			itemStack = player.getInventory().getHeldItemStack();
		} else {
			itemStack = player.getInventory().getSlot(slotIndex);
		}
		player.useItem((EdibleItem) itemStack.getItem(), slotIndex);
	}

	@Override
	public boolean isClientPreapply() {
		return false;
	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<UseItemRequest> {

		@Override
		public void write(Kryo kryo, Output output, UseItemRequest object) {
			output.writeInt(object.slotIndex);
		}

		@Override
		public UseItemRequest read(Kryo kryo, Input input, Class<UseItemRequest> type) {
			return new UseItemRequest(input.readInt());
		}
	}

}
