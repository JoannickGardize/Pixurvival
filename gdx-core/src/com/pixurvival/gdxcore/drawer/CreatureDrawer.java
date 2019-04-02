package com.pixurvival.gdxcore.drawer;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.textures.TextureAnimationSet;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreatureDrawer extends LivingEntityDrawer<CreatureEntity> {

	@Override
	protected TextureAnimationSet getBodyTextureAnimationSet(CreatureEntity e) {
		return PixurvivalGame.getContentPackTextures().getAnimationSet(e.getDefinition().getSpriteSheet());
	}

	@Override
	public void topDraw(Batch batch, CreatureEntity e) {
	}
}
