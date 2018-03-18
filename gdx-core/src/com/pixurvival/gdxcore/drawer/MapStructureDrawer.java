package com.pixurvival.gdxcore.drawer;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.gdxcore.graphics.ContentPackTextures;
import com.pixurvival.gdxcore.graphics.TextureAnimationSet;

public class MapStructureDrawer implements EntityDrawer<MapStructure> {

	TextureAnimationSet[] animationSets;

	public MapStructureDrawer(ContentPack contentpack, ContentPackTextures contentPackTextures) {
		List<Structure> structures = contentpack.getStructuresById();
		animationSets = new TextureAnimationSet[structures.size()];
		for (int i = 0; i < structures.size(); i++) {
			animationSets[i] = contentPackTextures.getAnimationSet(structures.get(i).getSpriteSheet().getName());
		}
	}

	@Override
	public void update(MapStructure e) {
	}

	@Override
	public void draw(Batch batch, MapStructure e) {
		TextureAnimationSet animationSet = animationSets[e.getDefinition().getId()];
		float x = (float) (e.getX() - animationSet.getWidth() / 2);
		float y = (float) (e.getY() /*- e.getHalfHeight()*/) + animationSet.getYOffset();
		batch.draw(animationSet.getShadow(), x, y - animationSet.getWidth() / 6, animationSet.getWidth(),
				animationSet.getWidth() / 2);
		batch.draw(animationSet.get(ActionAnimation.NONE).getTexture(0), x, y, animationSet.getWidth(),
				animationSet.getHeight());
	}

}
