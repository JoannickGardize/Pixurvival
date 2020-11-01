package com.pixurvival.core.contentPack.gameMode.role;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.PlayerEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StarterKit implements Serializable {

	private static final long serialVersionUID = 1L;

	private Item weaponItem;
	private Item clothingItem;
	private Item accessory1Item;
	private Item accessory2Item;
	private List<ItemStack> inventory = new ArrayList<>();

	public void apply(PlayerEntity player) {
		if (weaponItem != null) {
			player.getEquipment().setWeapon(new ItemStack(weaponItem));
		}
		if (clothingItem != null) {
			player.getEquipment().setClothing(new ItemStack(clothingItem));
		}
		if (accessory1Item != null) {
			player.getEquipment().setAccessory1(new ItemStack(accessory1Item));
		}
		if (accessory2Item != null) {
			player.getEquipment().setAccessory2(new ItemStack(accessory2Item));
		}
		for (ItemStack item : inventory) {
			player.getInventory().add(item);
		}
	}
}
