package com.pixurvival.gdxcore.drawer;

import com.pixurvival.core.contentPack.structure.InventoryStructure;
import com.pixurvival.core.map.InventoryMapStructure;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.textures.TextureAnimationSet;

public class InventoryMapStructureDrawer extends MapStructureDrawer {

	@Override
	protected TextureAnimationSet getTextureAnimationSet(MapStructure e) {
		InventoryMapStructure invStructure = (InventoryMapStructure) e;
		InventoryStructure definition = (InventoryStructure) invStructure.getDefinition();
		if (definition.getOpenSpriteSheet() != null && PixurvivalGame.getClient().getMyPlayer().getInteractionDialog() == invStructure.getInteractionDialog()) {
			return PixurvivalGame.getContentPackTextures().getAnimationSet(definition.getOpenSpriteSheet());
		} else {
			return PixurvivalGame.getContentPackTextures().getAnimationSet(definition.getSpriteSheet());
		}
	}
}
