package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.pixurvival.core.aliveEntity.Equipment;
import com.pixurvival.core.aliveEntity.PlayerEntity;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.gdxcore.PixurvivalGame;

public class EquipmentSlot extends ImageButton {

	private int equipmentIndex;

	private ItemStackDrawer itemStackDrawer = new ItemStackDrawer(this, 2);

	public EquipmentSlot(int equipmentIndex) {
		super(getStyleFor(equipmentIndex));
		this.equipmentIndex = equipmentIndex;
		addListener(new EquipmentSlotInputListener(equipmentIndex));
		getImageCell().expand().fill();
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		PlayerEntity player = PixurvivalGame.getClient().getMyPlayer();
		if (player != null) {
			ItemStack equipmentItem = player.getEquipment().get(equipmentIndex);
			itemStackDrawer.setItemStack(equipmentItem);
			itemStackDrawer.draw(batch);
		}
	}

	private static ImageButtonStyle getStyleFor(int equipmentIndex) {
		switch (equipmentIndex) {
		case Equipment.WEAPON_INDEX:
			return PixurvivalGame.getSkin().get("weapon", ImageButtonStyle.class);
		case Equipment.CLOTHING_INDEX:
			return PixurvivalGame.getSkin().get("clothing", ImageButtonStyle.class);
		case Equipment.ACCESSORY1_INDEX:
		case Equipment.ACCESSORY2_INDEX:
			return PixurvivalGame.getSkin().get("accessory", ImageButtonStyle.class);
		default:
			throw new IllegalArgumentException();
		}
	}
}
