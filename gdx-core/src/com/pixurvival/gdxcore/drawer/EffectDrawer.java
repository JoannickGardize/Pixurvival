package com.pixurvival.gdxcore.drawer;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.pixurvival.core.contentPack.effect.DrawDepth;
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
		if (e.getDefinition().getEffect().getSpriteSheet() == null) {
			return;
		}
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
	public void backgroundDraw(Batch batch, EffectEntity e) {
		if (e.getDefinition().getEffect().getDrawDepth() == DrawDepth.BACKGROUND) {
			drawEffect(batch, e);
		}
	}

	@Override
	public void draw(Batch batch, EffectEntity e) {
		if (e.getDefinition().getEffect().getDrawDepth() == DrawDepth.NORMAL) {
			drawEffect(batch, e);
		}
	}

	@Override
	public void frontDraw(Batch batch, EffectEntity e) {
		if (e.getDefinition().getEffect().getDrawDepth() == DrawDepth.FOREGROUND) {
			drawEffect(batch, e);
		}
	}

	private void drawEffect(Batch batch, EffectEntity e) {
		Effect effect = e.getDefinition().getEffect();
		if (effect.getSpriteSheet() == null) {
			return;
		}
		TextureAnimationSet textureAnimationSet = PixurvivalGame.getContentPackTextures().getAnimationSet(effect.getSpriteSheet());
		DrawData data = (DrawData) e.getCustomData();
		TextureAnimation textureAnimation;
		ActionAnimation actionAnimation = ActionAnimation.BEFORE_DEFAULT;
		if ((textureAnimation = textureAnimationSet.get(ActionAnimation.BEFORE_DEFAULT)) == null || !data.isFirstLoop()) {
			textureAnimation = textureAnimationSet.get(ActionAnimation.DEFAULT);
			actionAnimation = ActionAnimation.DEFAULT;
		}
		int index = DrawUtils.getIndexAndUpdateTimer(e, textureAnimation);
		if (effect.isLoopAnimation() || data.isFirstLoop()) {
			Vector2 drawPosition = data.getDrawPosition();
			float angle = effect.getOrientation() == OrientationType.MOVING_ANGLE ? (float) e.getMovingAngle() : 0;
			DrawUtils.drawRotatedStandUpStyleTexture(batch, textureAnimationSet, actionAnimation, index, drawPosition, angle * MathUtils.radDeg + data.getAngle());
		}
	}

}
