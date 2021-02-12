package com.pixurvival.gdxcore.drawer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.textures.TextureAnimation;
import com.pixurvival.gdxcore.textures.TextureAnimationSet;
import com.pixurvival.gdxcore.util.DrawUtils;

public class MapStructureDrawer implements ElementDrawer<MapStructure> {

	@Override
	public void update(MapStructure e) {
	}

	@Override
	public void drawShadow(Batch batch, MapStructure e) {
		TextureAnimationSet animationSet = getTextureAnimationSet(e);
		// TODO gérer les entités non dessinés directement dans l'entityDrawer
		if (animationSet == null || animationSet.getShadow() == null) {
			return;
		}
		TextureAnimation animation = getTextureAnimation(e, animationSet);
		float y = e.getPosition().getY();
		batch.draw(animation.getShadow(), e.getPosition().getX() - animation.getWorldShadowWidth() / 2, y - animation.getWorldShadowWidth() / 6, animation.getWorldShadowWidth(),
				animation.getWorldShadowWidth() / 2);
	}

	@Override
	public void draw(Batch batch, MapStructure e) {
		TextureAnimationSet animationSet = getTextureAnimationSet(e);
		if (animationSet == null) {
			return;
		}
		float x = e.getPosition().getX() - animationSet.getWidth() / 2;
		float y = e.getPosition().getY();
		TextureAnimation animation = getTextureAnimation(e, animationSet);
		int index = DrawUtils.getIndexAndUpdateTimer(e, animation);
		Texture texture = animation.getTexture(index);
		batch.draw(texture, x, y + animationSet.getYOffset(), animationSet.getWidth(), animationSet.getHeight(), 0, 0, texture.getWidth(), texture.getHeight(), ((DrawData) e.getCustomData()).isFlip(),
				false);
	}

	protected TextureAnimationSet getTextureAnimationSet(MapStructure e) {
		return PixurvivalGame.getContentPackTextures().getAnimationSet(e.getDefinition().getSpriteSheet());
	}

	protected TextureAnimation getTextureAnimation(MapStructure e, TextureAnimationSet animationSet) {
		return animationSet.get(ActionAnimation.DEFAULT);
	}

	@Override
	public void frontDraw(Batch batch, MapStructure e) {
	}

	@Override
	public void drawDebug(ShapeRenderer renderer, MapStructure e) {
		renderer.setColor(Color.WHITE);
		renderer.rect(e.getTileX(), e.getTileY(), e.getWidth(), e.getHeight());
	}

	@Override
	public void backgroundDraw(Batch batch, MapStructure e) {
		// Empty
	}

}
