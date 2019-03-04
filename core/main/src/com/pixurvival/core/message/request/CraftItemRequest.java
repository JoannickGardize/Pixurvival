package com.pixurvival.core.message.request;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.aliveEntity.Activity;
import com.pixurvival.core.aliveEntity.CraftingActivity;
import com.pixurvival.core.aliveEntity.PlayerEntity;
import com.pixurvival.core.item.ItemCraft;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CraftItemRequest implements IPlayerActionRequest {

	private int craftId;
	private short quantity;

	@Override
	public void apply(PlayerEntity player) {
		ItemCraft craft = player.getWorld().getContentPack().getItemCrafts().get(craftId);
		if (player.getInventory().contains(craft.getRecipes())
				&& player.getActivity().in(Activity.NONE_ID, Activity.CRAFTING_ACTIVITY_ID)) {
			player.setActivity(new CraftingActivity(player, craft));
		}
	}

	@Override
	public boolean isClientPreapply() {
		return false;
	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<CraftItemRequest> {

		@Override
		public void write(Kryo kryo, Output output, CraftItemRequest object) {
			output.writeInt(object.craftId);
			output.writeShort(object.quantity);
		}

		@Override
		public CraftItemRequest read(Kryo kryo, Input input, Class<CraftItemRequest> type) {
			return new CraftItemRequest(input.readInt(), input.readShort());
		}

	}
}
