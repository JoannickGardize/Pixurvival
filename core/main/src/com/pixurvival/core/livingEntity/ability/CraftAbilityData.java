package com.pixurvival.core.livingEntity.ability;

import java.nio.ByteBuffer;

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
		setDuration(itemCraft.getDuration());
	}

	@Override
	public void write(ByteBuffer buffer) {
		buffer.putShort((short) itemCraft.getId());
		buffer.putDouble(getStartTime());
	}

	@Override
	public void apply(ByteBuffer buffer, LivingEntity entity) {
		itemCraft = entity.getWorld().getContentPack().getItemCrafts().get(buffer.getShort());
		setStartTime(buffer.getDouble());
	}

}
