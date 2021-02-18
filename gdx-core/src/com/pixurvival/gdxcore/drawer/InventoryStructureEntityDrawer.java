package com.pixurvival.gdxcore.drawer;

import com.pixurvival.core.contentPack.structure.InventoryStructure;
import com.pixurvival.core.map.InventoryStructureEntity;
import com.pixurvival.core.map.StructureEntity;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.textures.TextureAnimationSet;

public class InventoryStructureEntityDrawer extends StructureEntityDrawer {

	@Override
	protected TextureAnimationSet getTextureAnimationSet(StructureEntity e) {
		InventoryStructureEntity invStructure = (InventoryStructureEntity) e;
		InventoryStructure definition = (InventoryStructure) invStructure.getDefinition();
		if (definition.getOpenSpriteSheet() != null && PixurvivalGame.getClient().getMyPlayer().getInteractionDialog() == invStructure.getInteractionDialog()) {
			return PixurvivalGame.getContentPackTextures().getAnimationSet(definition.getOpenSpriteSheet());
		} else {
			return PixurvivalGame.getContentPackTextures().getAnimationSet(definition.getSpriteSheet());
		}
	}
}
