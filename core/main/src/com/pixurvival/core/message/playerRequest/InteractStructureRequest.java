package com.pixurvival.core.message.playerRequest;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.ActionPreconditions;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.map.HarvestableMapStructure;
import com.pixurvival.core.map.MapStructure;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InteractStructureRequest implements IPlayerActionRequest {

	int x;
	int y;

	@Override
	public void apply(PlayerEntity player) {
		MapStructure structure = player.getWorld().getMap().tileAt(x, y).getStructure();
		if (ActionPreconditions.canInteract(player, structure)) {
			if (structure instanceof HarvestableMapStructure && !((HarvestableMapStructure) structure).isHarvested()) {
				player.harvest((HarvestableMapStructure) structure);
			} else {
				player.deconstruct(structure);
			}
		}
	}

	@Override
	public boolean isClientPreapply() {
		return false;
	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<InteractStructureRequest> {

		@Override
		public void write(Kryo kryo, Output output, InteractStructureRequest object) {
			output.writeInt(object.x);
			output.writeInt(object.y);
		}

		@Override
		public InteractStructureRequest read(Kryo kryo, Input input, Class<InteractStructureRequest> type) {
			return new InteractStructureRequest(input.readInt(), input.readInt());
		}

	}

}
