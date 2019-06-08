package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.gdxcore.PixurvivalGame;

import lombok.Getter;
import lombok.Setter;

public class InventorySlot extends Button {

	private Inventory inventory;
	private @Getter int slotIndex;
	private ItemStackDrawer itemStackDrawer;
	private @Setter ShortcutDrawer shortcutDrawer;

	public InventorySlot(Inventory inventory, int slotIndex) {
		super(PixurvivalGame.getSkin());
		this.inventory = inventory;
		this.slotIndex = slotIndex;

		itemStackDrawer = new ItemStackDrawer(this, 2);

		this.addListener(new InventorySlotInputListener(slotIndex));
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		itemStackDrawer.setItemStack(inventory.getSlot(slotIndex));
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
