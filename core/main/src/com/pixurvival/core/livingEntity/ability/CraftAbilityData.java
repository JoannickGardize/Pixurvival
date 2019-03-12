package com.pixurvival.core.livingEntity.ability;

import java.nio.ByteBuffer;

import com.pixurvival.core.Time;
import com.pixurvival.core.item.ItemCraft;
import com.pixurvival.core.livingEntity.LivingEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CraftAbilityData extends WorkAbilityData {

	private ItemCraft itemCraft;

	public void setItemCraft(ItemCraft itemCraft) {
		this.itemCraft = itemCraft;
		setDurationMillis(Time.secToMillis(itemCraft.getDuration()));
	}

	@Override
	public void write(ByteBuffer buffer) {
		buffer.putShort((short) itemCraft.getId());
		buffer.putLong(getStartTimeMillis());
	}

	@Override
	public void apply(ByteBuffer buffer, LivingEntity entity) {
		setItemCraft(entity.getWorld().getContentPack().getItemCrafts().get(buffer.getShort()));
		setStartTimeMillis(buffer.getLong());
	}
}
