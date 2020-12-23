package com.pixurvival.gdxcore.drawer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.map.HarvestableMapStructure;
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
		TextureAnimationSet animationSet = PixurvivalGame.getContentPackTextures().getAnimationSet(e.getDefinition().getSpriteSheet());
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
		TextureAnimationSet animationSet = PixurvivalGame.getContentPackTextures().getAnimationSet(e.getDefinition().getSpriteSheet());
		if (animationSet == null) {
			return;
		}
		float x = e.getPosition().getX() - animationSet.getWidth() / 2;
		float y = e.getPosition().getY();
		TextureAnimation animation = getTextureAnimation(e, animationSet);
		int index = DrawUtils.getIndexAndUpdateTimer(e, animation);
		batch.draw(animation.getTexture(index), x, y + animationSet.getYOffset(), animationSet.getWidth(), animationSet.getHeight());
	}

	private TextureAnimation getTextureAnimation(MapStructure e, TextureAnimationSet animationSet) {
		if (e instanceof HarvestableMapStructure && ((HarvestableMapStructure) e).isHarvested()) {
			TextureAnimation harvestedAnimation = animationSet.get(ActionAnimation.HARVESTED);
			return harvestedAnimation == null ? animationSet.get(ActionAnimation.DEFAULT) : harvestedAnimation;
		}
		return animationSet.get(ActionAnimation.DEFAULT);
	}

	@Override
	public void frontDraw(Batch batch, MapStructure e) {
	}

	@Override
	public void drawDebug(ShapeRenderer renderer, MapStructure e) {
		renderer.setColor(Color.WHITE);
		renderer.rect(e.getPosition().getX() - e.getHalfWidth(), e.getPosition().getY() - e.getHalfHeight(), e.getHalfWidth() * 2, e.getHalfHeight() * 2);
	}

	@Override
	public void backgroundDraw(Batch batch, MapStructure e) {
		// Empty
	}

}
