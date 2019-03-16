package com.pixurvival.gdxcore.drawer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.pixurvival.core.Direction;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.item.Item.Clothing;
import com.pixurvival.core.item.Item.Weapon;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.ability.WorkAbility;
import com.pixurvival.core.livingEntity.ability.WorkAbilityData;
import com.pixurvival.core.util.Vector2;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.textures.ColorTextures;
import com.pixurvival.gdxcore.textures.TextureAnimation;
import com.pixurvival.gdxcore.textures.TextureAnimationSet;
import com.pixurvival.gdxcore.util.GraphicsUtil;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerDrawer extends EntityDrawer<PlayerEntity> {

	private @NonNull TextureAnimationSet textureAnimationSet;

	@Override
	public void drawShadow(Batch batch, PlayerEntity e) {
		DrawData data = (DrawData) e.getCustomData();
		Vector2 drawPosition = data.getDrawPosition();
		float x = (float) (drawPosition.getX() - textureAnimationSet.getShadowWidth() / 2);
		float y = (float) drawPosition.getY();
		batch.draw(textureAnimationSet.getShadow(), x, y - textureAnimationSet.getShadowWidth() / 4, textureAnimationSet.getShadowWidth(), textureAnimationSet.getShadowWidth() / 2);
	}

	@Override
	public void draw(Batch batch, PlayerEntity e) {
		ActionAnimation actionAnimation = getActionAnimation(e);
		TextureAnimation textureAnimation = getTextureAnimationSet(e).get(actionAnimation);
		int index = GraphicsUtil.getIndexAndUpdateTimer(e, textureAnimation);
		Texture texture = textureAnimation.getTexture(index);
		Vector2 drawPosition = ((DrawData) e.getCustomData()).getDrawPosition();
		float x = (float) (drawPosition.getX() - textureAnimationSet.getWidth() / 2);
		float y = (float) drawPosition.getY();
		if (e.getWorld().getMap().tileAt(drawPosition).getTileDefinition().getVelocityFactor() < 1) {
			batch.draw(texture, x, y + textureAnimationSet.getYOffset(), textureAnimationSet.getWidth(), (float) (textureAnimationSet.getHeight() * 0.7), 0, 0.7f, 1, 0);
		} else {
			batch.draw(texture, x, y + textureAnimationSet.getYOffset(), textureAnimationSet.getWidth(), textureAnimationSet.getHeight());
			ItemStack weapon = e.getEquipment().getWeapon();
			if (weapon != null) {
				SpriteSheet spriteSheet = ((Weapon) weapon.getItem().getDetails()).getSpriteSheet();
				TextureAnimationSet weaponAnimationSet = PixurvivalGame.getContentPackTextures().getAnimationSet(spriteSheet);
				TextureAnimation weaponAnimation = weaponAnimationSet.get(actionAnimation);
				Texture weaponTexture = weaponAnimation.getTexture(index);
				batch.draw(weaponTexture, x + weaponAnimation.getOffsetX(index), y + weaponAnimationSet.getYOffset() + weaponAnimation.getOffsetY(index), weaponAnimationSet.getWidth(),
						weaponAnimationSet.getHeight());
			}
		}
	}

	@Override
	public void topDraw(Batch batch, PlayerEntity e) {
		if (e.getCurrentAbility() instanceof WorkAbility) {
			WorkAbility ability = (WorkAbility) e.getCurrentAbility();
			DrawData data = (DrawData) e.getCustomData();
			Vector2 drawPosition = data.getDrawPosition();
			float y = (float) (drawPosition.getY() + textureAnimationSet.getHeight() + 0.1);
			float x = (float) (drawPosition.getX() - 0.5);
			float lineWidth = (float) PixurvivalGame.getContentPackTextures().getTruePixelWidth();
			batch.draw(ColorTextures.get(Color.BLACK), x - lineWidth, y - lineWidth, 1 + lineWidth * 2, 0.2f + lineWidth * 2);
			double progress = ((WorkAbilityData) ability.getAbilityData(e)).getProgress(e.getWorld().getTime().getTimeMillis());
			batch.draw(ColorTextures.get(Color.YELLOW), x, y, (float) (1 - 1 * progress), 0.2f);
		}
	}

	private TextureAnimationSet getTextureAnimationSet(PlayerEntity e) {
		ItemStack clothing = e.getEquipment().getClothing();
		if (clothing != null) {
			return PixurvivalGame.getContentPackTextures().getAnimationSet(((Clothing) clothing.getItem().getDetails()).getSpriteSheet());
		}
		return textureAnimationSet;
	}

	private ActionAnimation getActionAnimation(PlayerEntity e) {
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
