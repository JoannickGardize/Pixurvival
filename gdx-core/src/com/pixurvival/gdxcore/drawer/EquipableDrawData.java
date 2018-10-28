package com.pixurvival.gdxcore.drawer;

import com.pixurvival.core.aliveEntity.EquipmentHolder;
import com.pixurvival.core.aliveEntity.Equipment;
import com.pixurvival.core.aliveEntity.EquipmentListener;
import com.pixurvival.core.item.ClothingItem;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.item.WeaponItem;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.textures.TextureAnimationSet;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EquipableDrawData extends DrawData implements EquipmentListener {

	private TextureAnimationSet clothingTexture;
	private TextureAnimationSet weaponTexture;

	public EquipableDrawData(EquipmentHolder e) {
		e.getEquipment().addListener(this);
		updateClothing(e.getEquipment().getClothing());
		updateWeapon(e.getEquipment().getWeapon());
	}

	@Override
	public void equipmentChanged(Equipment equipment, int equipmentIndex, ItemStack previousItemStack,
			ItemStack newItemStack) {
		if (equipmentIndex == Equipment.CLOTHING_INDEX) {
			updateClothing(newItemStack);
		} else if (equipmentIndex == Equipment.WEAPON_INDEX) {
			updateWeapon(newItemStack);
		}
	}

	private void updateClothing(ItemStack newItemStack) {
		if (newItemStack != null) {
			clothingTexture = PixurvivalGame.getContentPackTextures()
					.getAnimationSet(((ClothingItem) newItemStack.getItem()).getSpriteSheet());
		} else {
			clothingTexture = PixurvivalGame.getContentPackTextures()
					.getAnimationSet(PixurvivalGame.getWorld().getContentPack().getConstants().getDefaultCharacter());
		}
	}

	private void updateWeapon(ItemStack newItemStack) {
		if (newItemStack != null) {
			weaponTexture = PixurvivalGame.getContentPackTextures()
					.getAnimationSet(((WeaponItem) newItemStack.getItem()).getSpriteSheet());
		} else {
			weaponTexture = null;
		}
	}
}
