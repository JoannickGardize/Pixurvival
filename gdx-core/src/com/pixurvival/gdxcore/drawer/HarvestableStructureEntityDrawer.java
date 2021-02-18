package com.pixurvival.gdxcore.drawer;

import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.map.HarvestableStructureEntity;
import com.pixurvival.core.map.StructureEntity;
import com.pixurvival.gdxcore.textures.TextureAnimation;
import com.pixurvival.gdxcore.textures.TextureAnimationSet;

public class HarvestableStructureEntityDrawer extends StructureEntityDrawer {

	@Override
	protected TextureAnimation getTextureAnimation(StructureEntity e, TextureAnimationSet animationSet) {
		if (((HarvestableStructureEntity) e).isHarvested()) {
			TextureAnimation harvestedAnimation = animationSet.get(ActionAnimation.HARVESTED);
			return harvestedAnimation == null ? animationSet.get(ActionAnimation.DEFAULT) : harvestedAnimation;
		} else {
			return animationSet.get(ActionAnimation.DEFAULT);
		}
	}
}
