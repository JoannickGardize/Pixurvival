package com.pixurvival.core.livingEntity.ability;

import java.nio.ByteBuffer;

import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.core.util.ByteBufferUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeconstructAbilityData extends WorkAbilityData {

	private MapStructure structure;

	public void setStructure(MapStructure structure) {
		this.structure = structure;
		setDurationMillis(structure.getDefinition().getDeconstructionDuration());
	}

	@Override
	public void write(ByteBuffer buffer, LivingEntity entity) {
		ByteBufferUtils.writePastTime(buffer, entity.getWorld(), getStartTimeMillis());
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
		setStartTimeMillis(ByteBufferUtils.readPastTime(buffer, entity.getWorld()));
		if (buffer.get() == -1) {
			structure = null;
		} else {
			MapStructure mapStructure = entity.getWorld().getMap().tileAt(buffer.getInt(), buffer.getInt()).getStructure();
			setStructure(mapStructure);
		}
	}

}
