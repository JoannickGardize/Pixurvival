package com.pixurvival.gdxcore.overlay;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.livingEntity.ability.WorkAbility;
import com.pixurvival.core.livingEntity.ability.WorkAbilityData;
import com.pixurvival.gdxcore.util.DrawUtils;

public class WorkingBarDrawer implements OverlayStackElementDrawer<LivingEntity> {
    private static final Rectangle tmpRectangle = new Rectangle();

    @Override
    public float draw(Batch batch, OverlayInfos infos, LivingEntity e) {
        if (!(e.getCurrentAbility() instanceof WorkAbility)) {
            return -OverlayConstants.ENTITY_OVERLAY_MARGIN;
        }
        tmpRectangle.width = OverlayConstants.WORKING_BAR_WIDTH;
        tmpRectangle.height = OverlayConstants.WORKING_BAR_HEIGH;
        tmpRectangle.x = infos.getReferencePosition().x - tmpRectangle.width / 2;
        tmpRectangle.y = infos.getReferencePosition().y;
        DrawUtils.drawPercentBar(batch, tmpRectangle, (float) ((WorkAbilityData) e.getCurrentAbility().getAbilityData(e)).getProgress(e.getWorld().getTime().getTimeMillis()),
                OverlayConstants.WORKING_BAR_COLOR);
        return tmpRectangle.height;
    }
}
