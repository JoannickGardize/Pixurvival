package com.pixurvival.core.message;

import com.pixurvival.core.item.ItemStack;

import lombok.Data;

@Data
public class EntityInventoryUpdate {

	private long entityId;
	private int slot;
	private ItemStack itemStack;
}
