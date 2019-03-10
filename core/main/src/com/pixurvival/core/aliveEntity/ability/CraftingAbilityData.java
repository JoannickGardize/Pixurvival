package com.pixurvival.core.aliveEntity.ability;

import java.nio.ByteBuffer;

import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.item.ItemCraft;

import lombok.Getter;

public class CraftingAbilityData extends WorkAbilityData {

	private @Getter ItemCraft itemCraft;

	public CraftingAbilityData(ItemCraft itemCraft) {
		super(itemCraft.getDuration());
	}

	@Override
	public void write(ByteBuffer buffer) {
		buffer.putShort((short) itemCraft.getId());
		buffer.putDouble(getStartTime());
	}

	@Override
	public void apply(ByteBuffer buffer, ContentPack contentPack) {
		itemCraft = contentPack.getItemCrafts().get(buffer.getShort());
		setStartTime(buffer.getDouble());
	}

}
