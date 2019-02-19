package com.pixurvival.gdxcore.drawer;

import com.pixurvival.core.aliveEntity.Equipment;
import com.pixurvival.core.aliveEntity.EquipmentHolder;
import com.pixurvival.core.aliveEntity.EquipmentListener;
import com.pixurvival.core.item.Item.Equipable;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.textures.TextureAnimationSet;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EquipableDrawData extends DrawData implements EquipmentListener {

	private TextureAnimationSet[] textureAnimationSets = new TextureAnimationSet[Equipment.EQUIPMENT_SIZE];
	private TextureAnimationSet[] defaultTextureAnimationSets = new TextureAnimationSet[Equipment.EQUIPMENT_SIZE];

	public EquipableDrawData(EquipmentHolder e) {
		defaultTextureAnimationSets[Equipment.CLOTHING_INDEX] = PixurvivalGame.getContentPackTextures()
				.getAnimationSet(PixurvivalGame.getWorld().getContentPack().getConstants().getDefaultCharacter());
		for (int i = 0; i < Equipment.EQUIPMENT_SIZE; i++) {
			updateAnimationSet(i, e.getEquipment().get(i));
		}
		e.getEquipment().addListener(this);
	}

	@Override
	public void equipmentChanged(Equipment equipment, int equipmentIndex, ItemStack previousItemStack,
			ItemStack newItemStack) {
		updateAnimationSet(equipmentIndex, newItemStack);
	}

	private void updateAnimationSet(int equipmentIndex, ItemStack newItemStack) {
		if (newItemStack == null) {
			textureAnimationSets[equipmentIndex] = defaultTextureAnimationSets[equipmentIndex];
		} else {
			textureAnimationSets[equipmentIndex] = PixurvivalGame.getContentPackTextures()
					.getAnimationSet(((Equipable) newItemStack.getItem().getDetails()).getSpriteSheet());
		}
	}
}
