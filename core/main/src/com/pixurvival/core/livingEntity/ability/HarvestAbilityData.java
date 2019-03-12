package com.pixurvival.core.livingEntity.ability;

import java.nio.ByteBuffer;

import com.pixurvival.core.Time;
import com.pixurvival.core.contentPack.map.Structure.Harvestable;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.map.HarvestableStructure;

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
	public void write(ByteBuffer buffer) {
		buffer.putLong(getDurationMillis());
		buffer.putLong(getStartTimeMillis());
	}

	@Override
	public void apply(ByteBuffer buffer, LivingEntity entity) {
		setDurationMillis(buffer.getLong());
		setStartTimeMillis(buffer.getLong());
	}

}
