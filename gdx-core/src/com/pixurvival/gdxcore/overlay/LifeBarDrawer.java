package com.pixurvival.gdxcore.overlay;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.util.DrawUtils;

public class LifeBarDrawer implements OverlayStackElementDrawer<LivingEntity> {

	private static final Rectangle tmpRectangle = new Rectangle();

	@Override
	public float draw(Batch batch, OverlayInfos infos, LivingEntity e) {
		if (e.getHealth() == e.getMaxHealth() && e instanceof CreatureEntity && ((CreatureEntity) e).getDefinition().isHideFullLifeBar()) {
			return 0;
		}
		tmpRectangle.width = OverlayConstants.LIFE_BAR_WIDTH;
		tmpRectangle.height = OverlayConstants.LIFE_BAR_HEIGHT;
		tmpRectangle.x = infos.getReferencePosition().x - tmpRectangle.width / 2;
		tmpRectangle.y = infos.getReferencePosition().y;
		DrawUtils.drawPercentBar(batch, tmpRectangle, e.getHealth() / e.getMaxHealth(),
				e.getTeam().getId() == PixurvivalGame.getClient().getMyTeamId() ? OverlayConstants.ALLY_LIFE_BAR_COLOR : OverlayConstants.ENNEMY_LIFE_BAR_COLOR);
		return tmpRectangle.height;
	}

}
