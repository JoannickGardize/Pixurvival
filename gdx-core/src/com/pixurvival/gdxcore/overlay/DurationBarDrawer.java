package com.pixurvival.gdxcore.overlay;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.textures.TextureAnimationSet;
import com.pixurvival.gdxcore.util.DrawUtils;

public class DurationBarDrawer {
	private static final Rectangle tmpRectangle = new Rectangle();
	private static final Vector2 tmpVector2 = new Vector2();

	public void draw(Batch batch, Viewport worldViewport, MapStructure mapStructure) {
		if (mapStructure.getDefinition().getDuration() == 0 || mapStructure.getDefinition().getSpriteSheet() == null) {
			return;
		}
		TextureAnimationSet animationSet = PixurvivalGame.getContentPackTextures().getAnimationSet(mapStructure.getDefinition().getSpriteSheet());
		animationSet.getHeight();
		tmpRectangle.width = OverlayConstants.WORKING_BAR_WIDTH;
		tmpRectangle.height = OverlayConstants.WORKING_BAR_HEIGH;
		tmpVector2.set((float) mapStructure.getPosition().getX(), (float) (mapStructure.getPosition().getY() + animationSet.getHeight()));
		worldViewport.project(tmpVector2);
		tmpRectangle.x = tmpVector2.x - tmpRectangle.width / 2;
		tmpRectangle.y = tmpVector2.y + OverlayConstants.ENTITY_OVERLAY_MARGIN;

		DrawUtils.drawPercentBar(batch, tmpRectangle,
				1f - (float) (mapStructure.getChunk().getMap().getWorld().getTime().getTimeMillis() - mapStructure.getCreationTime()) / (float) mapStructure.getDefinition().getDuration(),
				OverlayConstants.DURATION_BAR_COLOR);
	}
}
