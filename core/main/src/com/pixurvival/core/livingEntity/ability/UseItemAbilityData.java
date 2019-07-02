package com.pixurvival.core.livingEntity.ability;

import java.nio.ByteBuffer;

import com.pixurvival.core.contentPack.item.EdibleItem;
import com.pixurvival.core.livingEntity.LivingEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UseItemAbilityData extends WorkAbilityData {

	private EdibleItem edibleItem;
	private short slotIndex;

	public void setEdibleItem(EdibleItem edibleItem) {
		this.edibleItem = edibleItem;
		setDurationMillis(edibleItem.getDuration());
	}

	public void setIndex(int slotIndex) {
		this.slotIndex = (short) slotIndex;
	}

	@Override
	public void write(ByteBuffer buffer, LivingEntity entity) {
		buffer.putShort((short) edibleItem.getId());
		buffer.putShort(slotIndex);
		buffer.putLong(getStartTimeMillis());
	}

	@Override
	public void apply(ByteBuffer buffer, LivingEntity entity) {
		setEdibleItem((EdibleItem) entity.getWorld().getContentPack().getItems().get(buffer.getShort()));
		setIndex(buffer.getShort());
		setStartTimeMillis(buffer.getLong());
	}

}
