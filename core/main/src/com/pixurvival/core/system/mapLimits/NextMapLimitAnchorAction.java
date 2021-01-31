package com.pixurvival.core.system.mapLimits;

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
		MapLimitsSystem system = world.getSystem(MapLimitsSystem.class);
		if (system == null) {
			return;
		}
		system.getData().setFrom(system.getData().getTo());
		MapLimitsAnchorRun anchorRun = new MapLimitsAnchorRun();
		system.getData().setTo(anchorRun);
		anchorRun.setTime(anchor.getTime());
		anchorRun.setDamagePerSecond(anchor.getDamagePerSecond());
		anchorRun.setRectangle(buildNextRectangle(system.getData().getFrom(), world, anchor));
		system.notifyAnchorChanged();
	}

	private Rectangle buildNextRectangle(MapLimitsAnchorRun from, World world, MapLimitsAnchor anchor) {
		if (world.getGameMode().getMapLimits().isShrinkRandomly()) {
			Rectangle rectangle = from.getRectangle();
			float size = anchor.getSize();
			float widthDiff = rectangle.getWidth() - size;
			float heightDiff = rectangle.getHeight() - size;
			float startX = rectangle.getStartX() + world.getRandom().nextFloat() * widthDiff;
			float startY = rectangle.getStartY() + world.getRandom().nextFloat() * heightDiff;
			return new Rectangle(startX, startY, startX + size, startY + size);
		} else {
			return new Rectangle(from.getRectangle().getCenter(), anchor.getSize());
		}
	}
}
