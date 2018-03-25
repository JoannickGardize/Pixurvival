package com.pixurvival.gdxcore.drawer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.pixurvival.core.Direction;
import com.pixurvival.core.aliveEntity.PlayerEntity;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.util.Vector2;
import com.pixurvival.gdxcore.textures.TextureAnimation;
import com.pixurvival.gdxcore.textures.TextureAnimationSet;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerDrawer extends EntityDrawer<PlayerEntity> {

	private @NonNull TextureAnimationSet textureAnimationSet;

	@Override
	public void drawShadow(Batch batch, PlayerEntity e) {
		DrawData data = (DrawData) e.getCustomData();
		Vector2 drawPosition = data.getDrawPosition();
		float x = (float) (drawPosition.x - textureAnimationSet.getWidth() / 2);
		float y = (float) (drawPosition.y /*- e.getBoundingRadius()*/);
		batch.draw(textureAnimationSet.getShadow(), x, y - textureAnimationSet.getWidth() / 4,
				textureAnimationSet.getWidth(), textureAnimationSet.getWidth() / 2);
	}

	@Override
	public void draw(Batch batch, PlayerEntity e) {
		TextureAnimation textureAnimation = getAnimation(e);
		int index = getIndexAndUpdateTimer(e, textureAnimation);
		Texture texture = textureAnimation.getTexture(index);
		DrawData data = (DrawData) e.getCustomData();
		Vector2 drawPosition = data.getDrawPosition();
		float x = (float) (drawPosition.x - textureAnimationSet.getWidth() / 2);
		float y = (float) (drawPosition.y /*- e.getBoundingRadius()*/);
		if (e.getWorld().getMap().tileAt(drawPosition).getTileDefinition().getVelocityFactor() < 1) {
			batch.draw(texture, x, y + textureAnimationSet.getYOffset(), textureAnimationSet.getWidth(),
					(float) (textureAnimationSet.getHeight() * 0.7), 0, 0.7f, 1, 0);
		} else {
			batch.draw(texture, x, y + textureAnimationSet.getYOffset(), textureAnimationSet.getWidth(),
					textureAnimationSet.getHeight());
		}
	}

	@Override
	public void topDraw(Batch batch, PlayerEntity e) {
		// TODO Auto-generated method stub

	}

	private int getIndexAndUpdateTimer(PlayerEntity e, TextureAnimation textureAnimation) {
		Object o = e.getCustomData();
		if (o == null) {
			o = new DrawData();
			((DrawData) o).getDrawPosition().set(e.getPosition());
			e.setCustomData(o);
		}

		DrawData data = (DrawData) o;
		float timer = data.getTimer();
		timer += Gdx.graphics.getRawDeltaTime();
		while (timer >= textureAnimation.getFrameDuration() * textureAnimation.size()) {
			timer -= textureAnimation.getFrameDuration() * textureAnimation.size();
		}
		data.setTimer(timer);
		return (int) (timer / textureAnimation.getFrameDuration());
	}

	private TextureAnimation getAnimation(PlayerEntity e) {
		if (e.getActivity().getActionAnimation() != null) {
			return textureAnimationSet.get(e.getActivity().getActionAnimation());
		}
		Direction aimingDirection = Direction.closestCardinal(e.getMovingAngle());
		if (e.isForward()) {
			return textureAnimationSet.get(ActionAnimation.getMoveFromDirection(aimingDirection));
		} else {
			return textureAnimationSet.get(ActionAnimation.getStandFromDirection(aimingDirection));
		}
	}

}
