package com.pixurvival.gdxcore.drawer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.gdxcore.GhostStructure;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.textures.TextureAnimation;
import com.pixurvival.gdxcore.textures.TextureAnimationSet;

public class GhostStructureDrawer implements ElementDrawer<GhostStructure> {
	@Override
	public void update(GhostStructure e) {
	}

	@Override
	public void drawShadow(Batch batch, GhostStructure e) {
	}

	@Override
	public void draw(Batch batch, GhostStructure e) {
		TextureAnimationSet animationSet = PixurvivalGame.getContentPackTextures()
				.getAnimationSet(e.getDefinition().getSpriteSheet());
		float x = (float) (e.getX() - animationSet.getWidth() / 2);
		float y = (float) e.getY();
		ActionAnimation action = ActionAnimation.NONE;
		TextureAnimation animation = animationSet.get(action);
		batch.setColor(e.isValid() ? Color.GREEN : Color.RED);
		batch.draw(animation.getTexture(0), x, y + animationSet.getYOffset(), animationSet.getWidth(),
				animationSet.getHeight());
		batch.setColor(Color.WHITE);
	}

	@Override
	public void topDraw(Batch batch, GhostStructure e) {

	}
}