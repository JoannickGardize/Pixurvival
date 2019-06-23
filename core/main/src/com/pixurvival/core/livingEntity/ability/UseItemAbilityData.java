package com.pixurvival.core.livingEntity.ability;

import java.nio.ByteBuffer;

import com.pixurvival.core.Time;
import com.pixurvival.core.contentPack.item.EdibleItem;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.InventoryHolder;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.livingEntity.PlayerEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UseItemAbilityData extends WorkAbilityData {

	private EdibleItem edibleItem;
	private short slotIndex;

	public void setEdibleItem(EdibleItem edibleItem) {
		this.edibleItem = edibleItem;
		setDurationMillis(Time.secToMillis(edibleItem.getDuration()));
	}

	public void setIndex(LivingEntity entity) {
		// TODO
		Inventory inventory = ((InventoryHolder) entity).getInventory();
		if (entity instanceof PlayerEntity) {
			ItemStack heldItemStack = ((PlayerEntity) entity).getInventory().getHeldItemStack();
		}

	}

	@Override
	public void write(ByteBuffer buffer, LivingEntity entity) {
		buffer.putShort((short) edibleItem.getId());
		buffer.putLong(getStartTimeMillis());
		buffer.putShort(slotIndex);
	}

	@Override
	public void apply(ByteBuffer buffer, LivingEntity entity) {
		setEdibleItem((EdibleItem) entity.getWorld().getContentPack().getItems().get(buffer.getShort()));
		setStartTimeMillis(buffer.getLong());
		setIndex(entity);
	}

}
