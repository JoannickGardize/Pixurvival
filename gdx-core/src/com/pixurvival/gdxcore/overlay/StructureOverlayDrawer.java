package com.pixurvival.gdxcore.overlay;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.textures.TextureAnimationSet;
import com.pixurvival.gdxcore.util.DrawUtils;

public class StructureOverlayDrawer implements OverlayDrawer<MapStructure> {

	private static final Rectangle tmpRectangle = new Rectangle(0, 0, OverlayConstants.WORKING_BAR_WIDTH, OverlayConstants.WORKING_BAR_HEIGH);
	private static final Vector2 tmpVector2 = new Vector2();

	@Override
	public void draw(Batch batch, Viewport worldViewport, MapStructure mapStructure) {
		if (mapStructure.getDefinition().getSpriteSheet() == null) {
			return;
		}

		boolean hasDuration = mapStructure.getDefinition().getDuration() != 0;
		boolean hasDamage = mapStructure.getMaxHealth() > 0 && mapStructure.getPercentHealth() < 1;
		if (hasDuration || hasDamage) {
			TextureAnimationSet animationSet = PixurvivalGame.getContentPackTextures().getAnimationSet(mapStructure.getDefinition().getSpriteSheet());
			animationSet.getHeight();
			tmpVector2.set(mapStructure.getPosition().getX(), mapStructure.getPosition().getY() + animationSet.getHeight());
			worldViewport.project(tmpVector2);
			if (hasDuration) {
				tmpRectangle.width = OverlayConstants.WORKING_BAR_WIDTH;
				tmpRectangle.height = OverlayConstants.WORKING_BAR_HEIGH;
				tmpRectangle.x = tmpVector2.x - tmpRectangle.width / 2;
				tmpRectangle.y = tmpVector2.y + OverlayConstants.ENTITY_OVERLAY_MARGIN;
				DrawUtils.drawPercentBar(batch, tmpRectangle,
						1f - (float) (mapStructure.getChunk().getMap().getWorld().getTime().getTimeMillis() - mapStructure.getCreationTime()) / (float) mapStructure.getDefinition().getDuration(),
						OverlayConstants.DURATION_BAR_COLOR);
			}
			if (hasDamage) {
				float percentHealth = mapStructure.getPercentHealth();
				tmpRectangle.width = OverlayConstants.WORKING_BAR_WIDTH;
				tmpRectangle.height = OverlayConstants.WORKING_BAR_HEIGH;
				tmpRectangle.x = tmpVector2.x - tmpRectangle.width / 2;
				tmpRectangle.y = tmpVector2.y + OverlayConstants.ENTITY_OVERLAY_MARGIN + (hasDuration ? (tmpRectangle.height + OverlayConstants.ENTITY_OVERLAY_MARGIN) : 0);
				DrawUtils.drawPercentBar(batch, tmpRectangle, percentHealth, OverlayConstants.ENNEMY_LIFE_BAR_COLOR);
			}
		}

	}

}
