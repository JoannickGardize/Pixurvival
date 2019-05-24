package com.pixurvival.gdxcore.drawer;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.entity.EffectEntity;
import com.pixurvival.core.util.Vector2;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.textures.TextureAnimation;
import com.pixurvival.gdxcore.textures.TextureAnimationSet;
import com.pixurvival.gdxcore.util.DrawUtils;

public class EffectDrawer extends EntityDrawer<EffectEntity> {

	@Override
	public void drawShadow(Batch batch, EffectEntity e) {
	}

	@Override
	public void draw(Batch batch, EffectEntity e) {
		TextureAnimationSet textureAnimationSet = PixurvivalGame.getContentPackTextures().getAnimationSet(e.getDefinition().getSpriteSheet());
		TextureAnimation textureAnimation = textureAnimationSet.get(ActionAnimation.NONE);
		int index = DrawUtils.getIndexAndUpdateTimer(e, textureAnimation);
		DrawData data = (DrawData) e.getCustomData();
		if (data.isFirstLoop() || e.getDefinition().isLoopAnimation()) {
			Vector2 drawPosition = data.getDrawPosition();
			DrawUtils.drawRotatedStandUpStyleTexture(batch, textureAnimationSet, ActionAnimation.NONE, index, drawPosition, e.getOrientation() * MathUtils.radDeg);
		}
	}

	@Override
	public void topDraw(Batch batch, EffectEntity e) {
	}

}
