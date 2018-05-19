package com.pixurvival.gdxcore.drawer;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.map.HarvestableStructure;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.gdxcore.textures.ContentPackTextures;
import com.pixurvival.gdxcore.textures.TextureAnimation;
import com.pixurvival.gdxcore.textures.TextureAnimationSet;

public class MapStructureDrawer implements ElementDrawer<MapStructure> {

	TextureAnimationSet[] animationSets;

	public MapStructureDrawer(ContentPack contentpack, ContentPackTextures contentPackTextures) {
		List<Structure> structures = contentpack.getStructuresById();
		animationSets = new TextureAnimationSet[structures.size()];
		for (int i = 0; i < structures.size(); i++) {
			animationSets[i] = contentPackTextures.getAnimationSet(structures.get(i).getSpriteSheet());
		}
	}

	@Override
	public void update(MapStructure e) {
	}

	@Override
	public void drawShadow(Batch batch, MapStructure e) {
		TextureAnimationSet animationSet = animationSets[e.getDefinition().getId()];
		ActionAnimation action = ActionAnimation.NONE;
		if (e instanceof HarvestableStructure && ((HarvestableStructure) e).isHarvested()) {
			action = ActionAnimation.HARVESTED;
		}
		TextureAnimation animation = animationSet.get(action);
		float x = (float) (e.getX() - animationSet.getWidth() / 2);
		float y = (float) (e.getY() /*- e.getHalfHeight()*/);
		batch.draw(animation.getShadow(), (float) e.getX() - animation.getWorldShadowWidth() / 2,
				y - animation.getWorldShadowWidth() / 6, animation.getWorldShadowWidth(),
				animation.getWorldShadowWidth() / 2);
	}

	@Override
	public void draw(Batch batch, MapStructure e) {
		TextureAnimationSet animationSet = animationSets[e.getDefinition().getId()];
		float x = (float) (e.getX() - animationSet.getWidth() / 2);
		float y = (float) (e.getY() /*- e.getHalfHeight()*/);
		ActionAnimation action = ActionAnimation.NONE;
		if (e instanceof HarvestableStructure && ((HarvestableStructure) e).isHarvested()) {
			action = ActionAnimation.HARVESTED;
		}
		TextureAnimation animation = animationSet.get(action);
		batch.draw(animation.getTexture(0), x, y + animationSet.getYOffset(), animationSet.getWidth(),
				animationSet.getHeight());
	}

	@Override
	public void topDraw(Batch batch, MapStructure e) {
		// TODO Auto-generated method stub

	}

}
