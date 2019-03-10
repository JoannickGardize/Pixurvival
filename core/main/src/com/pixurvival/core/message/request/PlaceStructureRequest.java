package com.pixurvival.core.message.request;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.item.Item.StructureDetails;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.map.MapStructure;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlaceStructureRequest implements IPlayerActionRequest {

	private int x;
	private int y;

	@Override
	public void apply(PlayerEntity player) {
		ItemStack heldItemStack = player.getInventory().getHeldItemStack();
		if (heldItemStack == null) {
			return;
		}
		Structure structure = ((StructureDetails) player.getInventory().getHeldItemStack().getItem().getDetails())
				.getStructure();
		if (MapStructure.canPlace(player, player.getWorld().getMap(), structure, x, y)) {
			if (heldItemStack.getQuantity() == 1) {
				player.getInventory().setHeldItemStack(null);
			} else {
				heldItemStack.setQuantity(heldItemStack.getQuantity() - 1);

			}
			player.getWorld().getMap().chunkAt(x, y).addStructure(structure, x, y);
		}
	}

	@Override
	public boolean isClientPreapply() {
		return false;
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
