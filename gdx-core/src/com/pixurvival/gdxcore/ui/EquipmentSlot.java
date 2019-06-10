package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.Equipment;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.input.InputAction;

public class EquipmentSlot extends ImageButton {

	private int equipmentIndex;

	private ItemStackDrawer itemStackDrawer = new ItemStackDrawer(this, 2);
	private ShortcutDrawer bottomShortcutDrawer;
	private ShortcutDrawer topShortcutDrawer;

	public EquipmentSlot(int equipmentIndex) {
		super(getStyleFor(equipmentIndex));
		this.equipmentIndex = equipmentIndex;
		addListener(new EquipmentSlotInputListener(equipmentIndex));
		getImageCell().expand().fill();

		switch (equipmentIndex) {
		case Equipment.WEAPON_INDEX:
			bottomShortcutDrawer = new ShortcutDrawer(this, InputAction.WEAPON_SPECIAL, ShortcutDrawer.BOTTOM);
			topShortcutDrawer = new ShortcutDrawer(this, InputAction.WEAPON_BASE_OR_DROP_ITEM, ShortcutDrawer.TOP);
			break;
		case Equipment.ACCESSORY1_INDEX:
			bottomShortcutDrawer = new ShortcutDrawer(this, InputAction.ACCESSORY1_SPECIAL, ShortcutDrawer.BOTTOM);
			break;
		case Equipment.ACCESSORY2_INDEX:
			bottomShortcutDrawer = new ShortcutDrawer(this, InputAction.ACCESSORY2_SPECIAL, ShortcutDrawer.BOTTOM);
			break;
		default:
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		PlayerEntity player = PixurvivalGame.getClient().getMyPlayer();
		ItemStack equipmentItem = player.getEquipment().get(equipmentIndex);
		itemStackDrawer.setItemStack(equipmentItem);
		itemStackDrawer.draw(batch);
		if (bottomShortcutDrawer != null) {
			bottomShortcutDrawer.draw(batch);
		}
		if (topShortcutDrawer != null) {
			topShortcutDrawer.draw(batch);
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
