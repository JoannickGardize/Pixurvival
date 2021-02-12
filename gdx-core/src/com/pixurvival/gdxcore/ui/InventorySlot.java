package com.pixurvival.gdxcore.ui;

import java.util.Objects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.util.Scene2dUtils;

import lombok.Getter;
import lombok.Setter;

public class InventorySlot extends Button {

	private @Getter Inventory inventory;
	private @Getter int slotIndex;
	private ItemStackDrawer itemStackDrawer;
	private @Setter ShortcutDrawer shortcutDrawer;

	public InventorySlot(Inventory inventory, int slotIndex) {
		super(PixurvivalGame.getSkin());
		this.inventory = inventory;
		this.slotIndex = slotIndex;

		itemStackDrawer = new ItemStackDrawer(this, 2);
		this.addListener(new InventorySlotInputListener(inventory, slotIndex));
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		ItemStack newItem = inventory.getSlot(slotIndex);
		// TODO inventory listener for this instead
		if (newItem != null && !Objects.equals(itemStackDrawer.getItemStack(), newItem)) {
			addAction(Scene2dUtils.yellowLightning());
		}
		itemStackDrawer.setItemStack(newItem);
		itemStackDrawer.draw(batch);
		if (shortcutDrawer != null) {
			shortcutDrawer.draw(batch);
		}
	}

	@Override
	public String toString() {
		return "InventorySlot " + slotIndex;
	}

}
