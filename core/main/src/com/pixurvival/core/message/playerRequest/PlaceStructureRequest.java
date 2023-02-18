package com.pixurvival.core.message.playerRequest;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.ActionPreconditions;
import com.pixurvival.core.contentPack.item.StructureItem;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.map.TiledMap;

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
		Structure structure = ((StructureItem) player.getInventory().getHeldItemStack().getItem()).getStructure();
		if (ActionPreconditions.canPlace(player, structure, x, y)) {
			if (heldItemStack.getQuantity() == 1) {
				player.getInventory().setHeldItemStack(null);
			} else {
				player.getInventory().setHeldItemStack(heldItemStack.sub(1));

			}
			removeExistingStructures(player, structure);
			player.getWorld().getMap().chunkAt(x, y).addNewStructure(structure, x, y);
		}
	}

	private void removeExistingStructures(PlayerEntity player, Structure futureStructure) {
		int x2 = x + futureStructure.getDimensions().getWidth();
		int y2 = y + futureStructure.getDimensions().getHeight();
		TiledMap map = player.getWorld().getMap();
		for (int xi = x; xi < x2; xi++) {
			for (int yi = y; yi < y2; yi++) {
				map.chunkAt(xi, yi).removeStructure(xi, yi);
			}
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
