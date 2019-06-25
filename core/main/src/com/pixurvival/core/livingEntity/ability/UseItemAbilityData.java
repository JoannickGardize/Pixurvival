package com.pixurvival.core.livingEntity.ability;

import java.nio.ByteBuffer;

import com.pixurvival.core.Time;
import com.pixurvival.core.contentPack.item.EdibleItem;
import com.pixurvival.core.item.InventoryHolder;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.LivingEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UseItemAbilityData extends WorkAbilityData {

	private ItemStack itemStack;
	private short slotIndex;
	
	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
		if(itemStack.getItem() instanceof EdibleItem) {
			setDurationMillis(Time.secToMillis(((EdibleItem) itemStack.getItem()).getDuration()));
		}
	}

	public void setIndex(int slotIndex) {
		this.slotIndex = (short) slotIndex;
	}

	@Override
	public void write(ByteBuffer buffer, LivingEntity entity) {
		buffer.putShort(slotIndex);
		buffer.putLong(getStartTimeMillis());
	}

	@Override
	public void apply(ByteBuffer buffer, LivingEntity entity) {
		setItemStack(((InventoryHolder) entity).getInventory().getSlot(buffer.getShort()));
		setStartTimeMillis(buffer.getLong());
	}

}
