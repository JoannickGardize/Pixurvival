package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.gdxcore.PixurvivalGame;

import lombok.Getter;

public class InventorySlot extends Button {

	private Inventory inventory;
	private @Getter int slotIndex;
	private ItemStackDrawer itemStackDrawer;

	public InventorySlot(Inventory inventory, int slotIndex) {
		super(PixurvivalGame.getSkin());
		this.inventory = inventory;
		this.slotIndex = slotIndex;

		itemStackDrawer = new ItemStackDrawer(this, 2);

		// this.addListener(new ClickListener() {
		// @Override
		// public void clicked(InputEvent event, float x, float y) {
		// PixurvivalGame.getClient()
		// .sendAction(new InventoryActionRequest(Type.CURSOR_MY_INVENTORY, (short)
		// slotIndex));
		// }
		// });

		this.addListener(new InventorySlotInputListener(slotIndex));
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		itemStackDrawer.setItemStack(inventory.getSlot(slotIndex));
		itemStackDrawer.draw(batch);
	}

	@Override
	public String toString() {
		return "InventorySlot " + slotIndex;
	}
}
