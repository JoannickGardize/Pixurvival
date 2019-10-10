package com.pixurvival.gdxcore.drawer;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.pixurvival.core.contentPack.effect.Effect;
import com.pixurvival.core.contentPack.effect.OrientationType;
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
		TextureAnimationSet textureAnimationSet = PixurvivalGame.getContentPackTextures().getAnimationSet(e.getDefinition().getEffect().getSpriteSheet());
		if (textureAnimationSet.getShadow() != null) {
			DrawData data = (DrawData) e.getCustomData();
			Vector2 drawPosition = data.getDrawPosition();
			float x = (float) (drawPosition.getX() - textureAnimationSet.getShadowWidth() / 2);
			float y = (float) drawPosition.getY();
			batch.draw(textureAnimationSet.getShadow(), x, y - textureAnimationSet.getShadowWidth() / 4, textureAnimationSet.getShadowWidth(), textureAnimationSet.getShadowWidth() / 2);
		}
	}

	@Override
	public void draw(Batch batch, EffectEntity e) {
		Effect effect = e.getDefinition().getEffect();
		if (effect.getSpriteSheet() == null) {
			return;
		}
		TextureAnimationSet textureAnimationSet = PixurvivalGame.getContentPackTextures().getAnimationSet(effect.getSpriteSheet());
		TextureAnimation textureAnimation = textureAnimationSet.get(ActionAnimation.DEFAULT);
		int index = DrawUtils.getIndexAndUpdateTimer(e, textureAnimation);
		DrawData data = (DrawData) e.getCustomData();
		if (data.isFirstLoop() || effect.isLoopAnimation()) {
			Vector2 drawPosition = data.getDrawPosition();
			float angle = effect.getOrientation() == OrientationType.MOVING_ANGLE ? (float) e.getMovingAngle() : 0;
			DrawUtils.drawRotatedStandUpStyleTexture(batch, textureAnimationSet, ActionAnimation.DEFAULT, index, drawPosition, angle * MathUtils.radDeg + data.getAngle());
		}
	}

	@Override
	public void topDraw(Batch batch, EffectEntity e) {
	}
}
