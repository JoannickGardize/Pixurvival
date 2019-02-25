package com.pixurvival.core.message;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.aliveEntity.Equipment;
import com.pixurvival.core.aliveEntity.PlayerEntity;
import com.pixurvival.core.item.ItemStack;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EquipmentActionRequest implements IPlayerActionRequest {

	private short index;

	@Override
	public void apply(PlayerEntity player) {
		if (player.getInventory().getHeldItemStack() == null
				|| Equipment.canEquip(index, player.getInventory().getHeldItemStack())) {
			ItemStack previousEquipment = player.getEquipment().get(index);
			player.getEquipment().set(index, player.getInventory().getHeldItemStack());
			player.getInventory().setHeldItemStack(previousEquipment);
		}
	}

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
