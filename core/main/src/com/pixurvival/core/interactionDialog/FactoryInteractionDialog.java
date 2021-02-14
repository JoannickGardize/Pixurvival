package com.pixurvival.core.interactionDialog;

import com.pixurvival.core.contentPack.structure.FactoryStructure;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.InventoryListener;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.PlayerEntity;

import lombok.Getter;

@Getter
public class FactoryInteractionDialog extends InteractionDialog implements InventoryListener {

	private FactoryStructure factoryStructure;
	private Inventory recipesInventory;
	private Inventory fuelsInventory;
	private Inventory resultsInventory;

	public FactoryInteractionDialog(InteractionDialogHolder owner, FactoryStructure factoryStructure) {
		super(owner);
		this.factoryStructure = factoryStructure;
		recipesInventory = new Inventory(factoryStructure.getRecipeSize());
		fuelsInventory = new Inventory(factoryStructure.getFuelSize());
		resultsInventory = new Inventory(factoryStructure.getResultSize());
	}

	@Override
	public void interact(PlayerEntity player, int index, boolean splitMode) {
		// TODO

	}

	@Override
	public void slotChanged(Inventory inventory, int slotIndex, ItemStack previousItemStack, ItemStack newItemStack) {
		notifyChanged();
	}
}
