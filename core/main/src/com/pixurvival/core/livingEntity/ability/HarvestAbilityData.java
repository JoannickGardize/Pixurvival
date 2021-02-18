package com.pixurvival.core.livingEntity.ability;

import java.nio.ByteBuffer;

import com.pixurvival.core.contentPack.structure.HarvestableStructure;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.map.HarvestableStructureEntity;
import com.pixurvival.core.map.StructureEntity;
import com.pixurvival.core.util.ByteBufferUtils;
import com.pixurvival.core.util.VarLenNumberIO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HarvestAbilityData extends WorkAbilityData {

	private HarvestableStructureEntity structure;

	public void setStructure(HarvestableStructureEntity structure) {
		this.structure = structure;
		setDurationMillis(((HarvestableStructure) structure.getDefinition()).getHarvestingTime());
	}

	@Override
	public void write(ByteBuffer buffer, LivingEntity entity) {
		ByteBufferUtils.writePastTime(buffer, entity.getWorld(), getStartTimeMillis());
		if (structure == null) {
			buffer.put((byte) -1);
		} else {
			buffer.put((byte) 0);
			VarLenNumberIO.writeVarInt(buffer, structure.getTileX());
			VarLenNumberIO.writeVarInt(buffer, structure.getTileY());
		}
	}

	@Override
	public void apply(ByteBuffer buffer, LivingEntity entity) {
		setStartTimeMillis(ByteBufferUtils.readPastTime(buffer, entity.getWorld()));
		if (buffer.get() == -1) {
			structure = null;
		} else {
			StructureEntity mapStructure = entity.getWorld().getMap().tileAt(VarLenNumberIO.readVarInt(buffer), VarLenNumberIO.readVarInt(buffer)).getStructure();
			if (mapStructure instanceof HarvestableStructureEntity) {
				setStructure((HarvestableStructureEntity) mapStructure);
			}
		}
	}

}
