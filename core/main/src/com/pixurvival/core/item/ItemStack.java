package com.pixurvival.core.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ItemStack {

	private Item item;
	private int quantity;

	public ItemStack(Item item) {
		this.item = item;
		quantity = 1;
	}
}
