package com.pixurvival.core.livingEntity.ability;

import java.nio.ByteBuffer;

import com.pixurvival.core.Time;
import com.pixurvival.core.contentPack.map.Structure.Harvestable;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.map.HarvestableStructure;
import com.pixurvival.core.map.MapStructure;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HarvestAbilityData extends WorkAbilityData {

	private HarvestableStructure structure;

	public void setStructure(HarvestableStructure structure) {
		this.structure = structure;
		setDurationMillis(Time.secToMillis(((Harvestable) structure.getDefinition().getDetails()).getHarvestingTime()));
	}

	@Override
	public void write(ByteBuffer buffer, LivingEntity entity) {
		buffer.putLong(getStartTimeMillis());
		if (structure == null) {
			buffer.put((byte) -1);
		} else {
			buffer.put((byte) 0);
			buffer.putInt(structure.getTileX());
			buffer.putInt(structure.getTileY());
		}
	}

	@Override
	public void apply(ByteBuffer buffer, LivingEntity entity) {
		setStartTimeMillis(buffer.getLong());
		if (buffer.get() == -1) {
			structure = null;
		} else {
			MapStructure mapStructure = entity.getWorld().getMap().tileAt(buffer.getInt(), buffer.getInt()).getStructure();
			if (mapStructure instanceof HarvestableStructure) {
				setStructure((HarvestableStructure) mapStructure);
			}
		}
	}

}
