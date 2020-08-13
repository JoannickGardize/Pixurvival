package com.pixurvival.core.mapLimits;

import com.pixurvival.core.Action;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.gameMode.MapLimitsAnchor;
import com.pixurvival.core.util.Rectangle;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class NextMapLimitAnchorAction implements Action {

	private MapLimitsAnchor anchor;

	@Override
	public void perform(World world) {
		MapLimitsRun mapLimits = world.getMapLimitsRun();
		mapLimits.setFrom(mapLimits.getTo());
		MapLimitsAnchorRun anchorRun = new MapLimitsAnchorRun();
		mapLimits.setTo(anchorRun);
		anchorRun.setTime(anchor.getTime());
		anchorRun.setDamagePerSecond(anchor.getDamagePerSecond());
		anchorRun.setRectangle(buildNextRectangle(world, anchor));
	}

	private Rectangle buildNextRectangle(World world, MapLimitsAnchor anchor) {
		MapLimitsRun mapLimits = world.getMapLimitsRun();
		if (world.getGameMode().getMapLimits().isShrinkRandomly()) {
			Rectangle rectangle = mapLimits.getFrom().getRectangle();
			float size = anchor.getSize();
			float widthDiff = rectangle.getWidth() - size;
			float heightDiff = rectangle.getHeight() - size;
			float startX = rectangle.getStartX() + world.getRandom().nextFloat() * widthDiff;
			float startY = rectangle.getStartY() + world.getRandom().nextFloat() * heightDiff;
			return new Rectangle(startX, startY, startX + size, startY + size);
		} else {
			return new Rectangle(mapLimits.getFrom().getRectangle().getCenter(), anchor.getSize());
		}
	}
}
