package com.pixurvival.core.livingEntity.ability;

import java.nio.ByteBuffer;

import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.util.ByteBufferUtils;
import com.pixurvival.core.util.VarLenNumberIO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CraftAbilityData extends WorkAbilityData {

	private ItemCraft itemCraft;

	public void setItemCraft(ItemCraft itemCraft) {
		this.itemCraft = itemCraft;
		setDurationMillis(itemCraft.getDuration());
	}

	@Override
	public void write(ByteBuffer buffer, LivingEntity entity) {
		VarLenNumberIO.writePositiveVarInt(buffer, itemCraft.getId());
		ByteBufferUtils.writePastTime(buffer, entity.getWorld(), getStartTimeMillis());
	}

	@Override
	public void apply(ByteBuffer buffer, LivingEntity entity) {
		setItemCraft(entity.getWorld().getContentPack().getItemCrafts().get(VarLenNumberIO.readPositiveVarInt(buffer)));
		setStartTimeMillis(ByteBufferUtils.readPastTime(buffer, entity.getWorld()));
	}
}
