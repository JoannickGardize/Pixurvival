package com.pixurvival.core.message;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.aliveEntity.HarvestingActivity;
import com.pixurvival.core.aliveEntity.PlayerEntity;
import com.pixurvival.core.map.HarvestableStructure;
import com.pixurvival.core.map.MapTile;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InteractStructureRequest implements IPlayerActionRequest {

	int x;
	int y;

	@Override
	public void apply(PlayerEntity player) {
		MapTile mapTile = player.getWorld().getMap().tileAt(x, y);
		if (mapTile.getStructure() instanceof HarvestableStructure && mapTile.getStructure().canInteract(player)) {
			HarvestableStructure structure = (HarvestableStructure) mapTile.getStructure();
			player.setActivity(new HarvestingActivity(player, structure));
			player.setForward(false);
		}
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
