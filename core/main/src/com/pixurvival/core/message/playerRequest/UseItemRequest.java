package com.pixurvival.core.message.playerRequest;

import com.pixurvival.core.contentPack.item.EdibleItem;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.PlayerEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UseItemRequest implements IPlayerActionRequest {

	private int slotIndex;

	@Override
	public void apply(PlayerEntity player) {
		ItemStack itemStack = player.getInventory().getSlot(slotIndex);
		player.useItem(itemStack, slotIndex);
	}

	@Override
	public boolean isClientPreapply() {
		return false;
	}

}
