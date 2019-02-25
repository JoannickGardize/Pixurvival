package com.pixurvival.core.message;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.aliveEntity.PlayerEntity;
import com.pixurvival.core.item.ItemStackEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DropItemRequest implements IPlayerActionRequest {

	private float direction;

	public void apply(DropItemRequest request) {
	}

	@Override
	public void apply(PlayerEntity player) {
		if (player.getInventory().getHeldItemStack() != null) {
			ItemStackEntity entity = new ItemStackEntity(player.getInventory().getHeldItemStack());
			entity.getPosition().set(player.getPosition());
			player.getWorld().getEntityPool().add(entity);
			entity.spawn(direction);
			player.getInventory().setHeldItemStack(null);
		}
	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<DropItemRequest> {

		@Override
		public void write(Kryo kryo, Output output, DropItemRequest object) {
			output.writeFloat(object.direction);
		}

		@Override
		public DropItemRequest read(Kryo kryo, Input input, Class<DropItemRequest> type) {
			return new DropItemRequest(input.readFloat());
		}

	}

}
