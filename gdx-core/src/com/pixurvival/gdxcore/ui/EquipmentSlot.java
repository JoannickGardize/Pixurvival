package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.pixurvival.core.aliveEntity.PlayerEntity;
import com.pixurvival.gdxcore.PixurvivalGame;

public class EquipmentSlot extends Button {

	private int equipmentIndex;

	private ItemStackDrawer itemStackDrawer = new ItemStackDrawer(this, 2);

	public EquipmentSlot(int equipmentIndex) {
		super(PixurvivalGame.getSkin());
		this.equipmentIndex = equipmentIndex;
		addListener(new EquipmentSlotInputListener(equipmentIndex));
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		PlayerEntity player = PixurvivalGame.getClient().getMyPlayer();
		if (player != null) {
			itemStackDrawer.setItemStack(player.getEquipment().get(equipmentIndex));
			itemStackDrawer.draw(batch);
		}
	}
}
