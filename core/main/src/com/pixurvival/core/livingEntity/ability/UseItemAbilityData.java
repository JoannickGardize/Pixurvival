package com.pixurvival.core.livingEntity.ability;

import java.nio.ByteBuffer;

import com.pixurvival.core.contentPack.item.EdibleItem;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.util.ByteBufferUtils;
import com.pixurvival.core.util.VarLenNumberIO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UseItemAbilityData extends WorkAbilityData {

	private EdibleItem edibleItem;
	private int slotIndex;

	public void setEdibleItem(EdibleItem edibleItem) {
		this.edibleItem = edibleItem;
		setDurationMillis(edibleItem.getDuration());
	}

	@Override
	public void write(ByteBuffer buffer, LivingEntity entity) {
		VarLenNumberIO.writePositiveVarInt(buffer, edibleItem.getId());
		VarLenNumberIO.writeVarInt(buffer, slotIndex);
		ByteBufferUtils.writePastTime(buffer, entity.getWorld(), getStartTimeMillis());
	}

	@Override
	public void apply(ByteBuffer buffer, LivingEntity entity) {
		setEdibleItem((EdibleItem) entity.getWorld().getContentPack().getItems().get(VarLenNumberIO.readPositiveVarInt(buffer)));
		slotIndex = VarLenNumberIO.readVarInt(buffer);
		setStartTimeMillis(ByteBufferUtils.readPastTime(buffer, entity.getWorld()));
	}

}
