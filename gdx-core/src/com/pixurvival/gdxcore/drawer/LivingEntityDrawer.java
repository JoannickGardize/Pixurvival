package com.pixurvival.gdxcore.drawer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.pixurvival.core.Direction;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.util.Vector2;
import com.pixurvival.gdxcore.textures.TextureAnimation;
import com.pixurvival.gdxcore.textures.TextureAnimationSet;
import com.pixurvival.gdxcore.util.GraphicsUtil;

public abstract class LivingEntityDrawer<E extends LivingEntity> extends EntityDrawer<E> {

	@Override
	public void drawShadow(Batch batch, E e) {
		TextureAnimationSet textureAnimationSet = getBodyTextureAnimationSet(e);
		DrawData data = (DrawData) e.getCustomData();
		Vector2 drawPosition = data.getDrawPosition();
		float x = (float) (drawPosition.getX() - textureAnimationSet.getShadowWidth() / 2);
		float y = (float) drawPosition.getY();
		batch.draw(textureAnimationSet.getShadow(), x, y - textureAnimationSet.getShadowWidth() / 4, textureAnimationSet.getShadowWidth(), textureAnimationSet.getShadowWidth() / 2);
	}

	@Override
	public void draw(Batch batch, E e) {
		ActionAnimation actionAnimation = getActionAnimation(e);
		TextureAnimationSet textureAnimationSet = getBodyTextureAnimationSet(e);
		TextureAnimation textureAnimation = textureAnimationSet.get(actionAnimation);
		int index = GraphicsUtil.getIndexAndUpdateTimer(e, textureAnimation);
		Texture texture = textureAnimation.getTexture(index);
		Vector2 drawPosition = ((DrawData) e.getCustomData()).getDrawPosition();
		float x = (float) (drawPosition.getX() - textureAnimationSet.getWidth() / 2);
		float y = (float) drawPosition.getY();
		float yOffset = e.getWorld().getMap().tileAt(drawPosition).getTileDefinition().getVelocityFactor() < 1 ? -textureAnimationSet.getHeight() * 0.3f : 0;
		float equipmentY = y + yOffset;
		drawBeforeBody(batch, e, textureAnimation, actionAnimation, index, x, equipmentY);
		if (yOffset != 0) {
			batch.draw(texture, x, y + textureAnimationSet.getYOffset(), textureAnimationSet.getWidth(), textureAnimationSet.getHeight() + yOffset, 0, 1 + yOffset, 1, 0);
		} else {
			batch.draw(texture, x, y + textureAnimationSet.getYOffset(), textureAnimationSet.getWidth(), textureAnimationSet.getHeight());
		}
		drawAfterBody(batch, e, textureAnimation, actionAnimation, index, x, equipmentY);
	}

	protected void drawAfterBody(Batch batch, E e, TextureAnimation textureAnimation, ActionAnimation actionAnimation, int index, float x, float y) {
	}

	protected void drawBeforeBody(Batch batch, E e, TextureAnimation textureAnimation, ActionAnimation actionAnimation, int index, float x, float y) {
	}

	protected abstract TextureAnimationSet getBodyTextureAnimationSet(E e);

	private ActionAnimation getActionAnimation(E e) {
		if (e.getCurrentAbility() != null) {
			ActionAnimation animation = e.getCurrentAbility().getActionAnimation(e);
			if (animation != null) {
				return animation;
			}
		}
		Direction aimingDirection = Direction.closestCardinal(e.getMovingAngle());
		if (e.isForward()) {
			return ActionAnimation.getMoveFromDirection(aimingDirection);
		} else {
			return ActionAnimation.getStandFromDirection(aimingDirection);
		}
	}
}
