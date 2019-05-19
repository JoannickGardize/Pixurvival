package com.pixurvival.gdxcore.overlay;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.gdxcore.util.DrawUtils;

public class LifeBarDrawer implements OverlayStackElementDrawer<LivingEntity> {

	private static final Rectangle tmpRectangle = new Rectangle();

	@Override
	public float draw(Batch batch, OverlayInfos infos, LivingEntity e) {
		tmpRectangle.width = OverlaySettings.LIFE_BAR_WIDTH * infos.getScaleX();
		tmpRectangle.height = OverlaySettings.LIFE_BAR_HEIGHT * infos.getScaleY();
		tmpRectangle.x = infos.getReferencePosition().x - tmpRectangle.width / 2;
		tmpRectangle.y = infos.getReferencePosition().y;
		PlayerEntity myPlayer = e.getWorld().getMyPlayer();
		Color color = Color.RED;
		if (myPlayer != null && myPlayer.getTeam().equals(e.getTeam())) {
			color = Color.GREEN;
		}
		DrawUtils.drawPercentBar(batch, tmpRectangle, e.getHealth() / e.getMaxHealth(), color);
		return tmpRectangle.height;
	}
}
