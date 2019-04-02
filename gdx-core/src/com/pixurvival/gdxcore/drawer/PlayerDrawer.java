package com.pixurvival.gdxcore.drawer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
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

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerDrawer extends LivingEntityDrawer<PlayerEntity> {

	private TextureAnimationSet defaultBody = PixurvivalGame.getContentPackTextures().getAnimationSet(PixurvivalGame.getWorld().getContentPack().getConstants().getDefaultCharacter());

	@Override
	public void topDraw(Batch batch, PlayerEntity e) {
		if (e.getCurrentAbility() instanceof WorkAbility) {
			WorkAbility ability = (WorkAbility) e.getCurrentAbility();
			DrawData data = (DrawData) e.getCustomData();
			Vector2 drawPosition = data.getDrawPosition();
			float y = (float) (drawPosition.getY() + getBodyTextureAnimationSet(e).getHeight() + 0.1);
			float x = (float) (drawPosition.getX() - 0.5);
			float lineWidth = (float) PixurvivalGame.getContentPackTextures().getTruePixelWidth();
			batch.draw(ColorTextures.get(Color.BLACK), x - lineWidth, y - lineWidth, 1 + lineWidth * 2, 0.2f + lineWidth * 2);
			double progress = ((WorkAbilityData) ability.getAbilityData(e)).getProgress(e.getWorld().getTime().getTimeMillis());
			batch.draw(ColorTextures.get(Color.YELLOW), x, y, (float) (1 - 1 * progress), 0.2f);
		}
	}

	@Override
	protected TextureAnimationSet getBodyTextureAnimationSet(PlayerEntity e) {
		ItemStack clothing = e.getEquipment().getClothing();
		if (clothing != null) {
			return PixurvivalGame.getContentPackTextures().getAnimationSet(((Clothing) clothing.getItem().getDetails()).getSpriteSheet());
		}
		return defaultBody;
	}

	@Override
	protected void drawAfterBody(Batch batch, PlayerEntity e, TextureAnimation textureAnimation, ActionAnimation actionAnimation, int index, float x, float y) {
		ItemStack weapon = e.getEquipment().getWeapon();
		if (weapon != null) {
			SpriteSheet spriteSheet = ((Weapon) weapon.getItem().getDetails()).getSpriteSheet();
			TextureAnimationSet weaponAnimationSet = PixurvivalGame.getContentPackTextures().getAnimationSet(spriteSheet);
			TextureAnimation weaponAnimation = weaponAnimationSet.get(actionAnimation);
			Texture weaponTexture = weaponAnimation.getTexture(index);
			batch.draw(weaponTexture, x + textureAnimation.getOffsetX(index) - weaponAnimation.getOffsetX(index),
					y + weaponAnimationSet.getYOffset() + textureAnimation.getOffsetY(index) - weaponAnimation.getOffsetY(index), weaponAnimationSet.getWidth(), weaponAnimationSet.getHeight());
		}

	}
}
