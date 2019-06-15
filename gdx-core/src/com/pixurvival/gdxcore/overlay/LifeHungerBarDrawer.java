package com.pixurvival.gdxcore.overlay;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.gdxcore.util.DrawUtils;

public class LifeHungerBarDrawer implements OverlayStackElementDrawer<PlayerEntity> {

	private static final Rectangle tmpRectangle = new Rectangle();

	@Override
	public float draw(Batch batch, OverlayInfos infos, PlayerEntity e) {
		tmpRectangle.width = OverlayConstants.LIFE_BAR_WIDTH;
		tmpRectangle.height = OverlayConstants.LIFE_BAR_HEIGHT;
		tmpRectangle.x = infos.getReferencePosition().x - tmpRectangle.width / 2;
		tmpRectangle.y = infos.getReferencePosition().y;
		DrawUtils.drawPercentBar(batch, tmpRectangle, e.getHunger() / PlayerEntity.MAX_HUNGER, Color.YELLOW, false);
		tmpRectangle.y += tmpRectangle.height - OverlayConstants.BAR_BORDER_SIZE;
		DrawUtils.drawPercentBar(batch, tmpRectangle, e.getHealth() / e.getMaxHealth(), Color.GREEN);
		return tmpRectangle.height * 2 - OverlayConstants.BAR_BORDER_SIZE;
	}
}
