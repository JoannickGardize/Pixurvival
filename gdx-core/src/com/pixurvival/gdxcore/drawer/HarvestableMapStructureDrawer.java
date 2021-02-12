package com.pixurvival.gdxcore.drawer;

import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.map.HarvestableMapStructure;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.gdxcore.textures.TextureAnimation;
import com.pixurvival.gdxcore.textures.TextureAnimationSet;

public class HarvestableMapStructureDrawer extends MapStructureDrawer {

	@Override
	protected TextureAnimation getTextureAnimation(MapStructure e, TextureAnimationSet animationSet) {
		if (((HarvestableMapStructure) e).isHarvested()) {
			TextureAnimation harvestedAnimation = animationSet.get(ActionAnimation.HARVESTED);
			return harvestedAnimation == null ? animationSet.get(ActionAnimation.DEFAULT) : harvestedAnimation;
		} else {
			return animationSet.get(ActionAnimation.DEFAULT);
		}
	}
}
