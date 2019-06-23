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
		ActionAnimation action = ActionAnimation.DEFAULT;
		if (e instanceof HarvestableMapStructure && ((HarvestableMapStructure) e).isHarvested()) {
			action = ActionAnimation.HARVESTED;
		}
		TextureAnimation animation = animationSet.get(action);
		float y = (float) e.getY();
		batch.draw(animation.getShadow(), (float) e.getX() - animation.getWorldShadowWidth() / 2, y - animation.getWorldShadowWidth() / 6, animation.getWorldShadowWidth(),
				animation.getWorldShadowWidth() / 2);
	}

	@Override
	public void draw(Batch batch, MapStructure e) {
		TextureAnimationSet animationSet = PixurvivalGame.getContentPackTextures().getAnimationSet(e.getDefinition().getSpriteSheet());
		float x = (float) (e.getX() - animationSet.getWidth() / 2);
		float y = (float) e.getY();
		ActionAnimation action = ActionAnimation.DEFAULT;
		if (e instanceof HarvestableMapStructure && ((HarvestableMapStructure) e).isHarvested()) {
			action = ActionAnimation.HARVESTED;
		}
		TextureAnimation animation = animationSet.get(action);
		int index = DrawUtils.getIndexAndUpdateTimer(e, animation);
		batch.draw(animation.getTexture(index), x, y + animationSet.getYOffset(), animationSet.getWidth(), animationSet.getHeight());
	}

	@Override
	public void topDraw(Batch batch, MapStructure e) {
	}

	@Override
	public void drawDebug(ShapeRenderer renderer, MapStructure e) {
		renderer.setColor(Color.WHITE);
		renderer.rect((float) (e.getX() - e.getHalfWidth()), (float) (e.getY() - e.getHalfHeight()), (float) (e.getHalfWidth() * 2), (float) (e.getHalfHeight() * 2));
	}

}
