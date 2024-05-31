package com.pixurvival.gdxcore.drawer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.pixurvival.core.contentPack.item.ClothingItem;
import com.pixurvival.core.contentPack.item.WeaponItem;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.textures.TextureAnimation;
import com.pixurvival.gdxcore.textures.TextureAnimationSet;

public class PlayerDrawer extends LivingEntityDrawer<PlayerEntity> {

    private TextureAnimationSet defaultBody = PixurvivalGame.getContentPackTextures().getAnimationSet(PixurvivalGame.getWorld().getContentPack().getConstants().getDefaultCharacter());
    private boolean tmpSkipDraw;

    @Override
    public void frontDraw(Batch batch, PlayerEntity e) {
    }

    @Override
    protected TextureAnimationSet getBodyTextureAnimationSet(PlayerEntity e) {
        ItemStack clothing = e.getEquipment().getClothing();
        if (clothing != null) {
            return PixurvivalGame.getContentPackTextures().getAnimationSet(((ClothingItem) clothing.getItem()).getSpriteSheet());
        }
        return defaultBody;
    }

    @Override
    protected void drawBeforeBody(Batch batch, PlayerEntity e, TextureAnimation textureAnimation, ActionAnimation actionAnimation, int index, float x, float y) {
        if (textureAnimation.hasOffset()) {
            drawWeapon(true, batch, e, textureAnimation, actionAnimation, index, x, y);
        }
    }

    @Override
    protected void drawAfterBody(Batch batch, PlayerEntity e, TextureAnimation textureAnimation, ActionAnimation actionAnimation, int index, float x, float y) {
        if (textureAnimation.hasOffset()) {
            drawWeapon(false, batch, e, textureAnimation, actionAnimation, index, x, y);
        }
    }

    protected void drawWeapon(boolean back, Batch batch, PlayerEntity e, TextureAnimation textureAnimation, ActionAnimation actionAnimation, int index, float x, float y) {
        ItemStack weapon = e.getEquipment().getWeapon();
        if (weapon != null && (e.getCurrentAbility() == null || e.getCurrentAbility().getAnimationItem(e) == null)) {
            SpriteSheet spriteSheet = ((WeaponItem) weapon.getItem()).getSpriteSheet();
            TextureAnimationSet weaponAnimationSet = PixurvivalGame.getContentPackTextures().getAnimationSet(spriteSheet);
            TextureAnimation weaponAnimation = weaponAnimationSet.get(actionAnimation);
            int weaponIndex = index % weaponAnimation.size();
            if (textureAnimation.isBack(index) == back) {
                Texture weaponTexture = weaponAnimation.getTexture(weaponIndex);
                batch.draw(weaponTexture, x + textureAnimation.getOffsetX(index) - weaponAnimation.getOffsetX(weaponIndex),
                        y + weaponAnimationSet.getYOffset() + textureAnimation.getOffsetY(index) - weaponAnimation.getOffsetY(weaponIndex), weaponAnimationSet.getWidth(),
                        weaponAnimationSet.getHeight());
            }
        }
    }

    @Override
    protected boolean skipDraw(PlayerEntity e) {
        return e.isHiddenForEnemies() && (e.getWorld().getTime().getTimeMillis() % 500) < 250;
    }
}
