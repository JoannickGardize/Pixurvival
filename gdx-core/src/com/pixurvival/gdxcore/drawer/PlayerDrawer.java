package com.pixurvival.gdxcore.drawer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.pixurvival.core.Direction;
import com.pixurvival.core.aliveEntity.PlayerEntity;
import com.pixurvival.core.aliveEntity.WorkActivity;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.item.WeaponItem;
import com.pixurvival.core.util.Vector2;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.textures.ColorTextures;
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
		float x = (float) (drawPosition.x - textureAnimationSet.getShadowWidth() / 2);
		float y = (float) (drawPosition.y /*- e.getBoundingRadius()*/);
		batch.draw(textureAnimationSet.getShadow(), x, y - textureAnimationSet.getShadowWidth() / 4,
				textureAnimationSet.getShadowWidth(), textureAnimationSet.getShadowWidth() / 2);
	}

	@Override
	public void draw(Batch batch, PlayerEntity e) {
		ActionAnimation actionAnimation = getActionAnimation(e);
		TextureAnimation textureAnimation = textureAnimationSet.get(actionAnimation);
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
			ItemStack weapon = e.getEquipment().getWeapon();
			if (weapon != null) {
				SpriteSheet spriteSheet = ((WeaponItem) weapon.getItem()).getSpriteSheet();
				TextureAnimationSet weaponAnimationSet = PixurvivalGame.getContentPackTextures()
						.getAnimationSet(spriteSheet);
				TextureAnimation weaponAnimation = weaponAnimationSet.get(actionAnimation);
				Texture weaponTexture = weaponAnimation.getTexture(index);
				batch.draw(weaponTexture, x + weaponAnimation.getOffsetX(index),
						y + weaponAnimationSet.getYOffset() + weaponAnimation.getOffsetY(index),
						weaponAnimationSet.getWidth(), weaponAnimationSet.getHeight());
			}
		}
	}

	@Override
	public void topDraw(Batch batch, PlayerEntity e) {
		if (e.getActivity() instanceof WorkActivity) {
			WorkActivity activity = (WorkActivity) e.getActivity();
			DrawData data = (DrawData) e.getCustomData();
			Vector2 drawPosition = data.getDrawPosition();
			float y = (float) (drawPosition.y + textureAnimationSet.getHeight() + 0.1);
			float x = (float) (drawPosition.x - 0.5);
			float lineWidth = (float) PixurvivalGame.getContentPackTextures().getTruePixelWidth();
			batch.draw(ColorTextures.get(Color.BLACK), x - lineWidth, y - lineWidth, 1 + lineWidth * 2,
					0.2f + lineWidth * 2);
			batch.draw(ColorTextures.get(Color.YELLOW), x, y, (float) (1 - (1 * activity.getProgress())), 0.2f);
		}
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

	private ActionAnimation getActionAnimation(PlayerEntity e) {
		if (e.getActivity().getActionAnimation() != null) {
			return e.getActivity().getActionAnimation();
		}
		Direction aimingDirection = Direction.closestCardinal(e.getMovingAngle());
		if (e.isForward()) {
			return ActionAnimation.getMoveFromDirection(aimingDirection);
		} else {
			return ActionAnimation.getStandFromDirection(aimingDirection);
		}
	}
}
