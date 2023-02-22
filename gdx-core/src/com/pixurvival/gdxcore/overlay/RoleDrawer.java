package com.pixurvival.gdxcore.overlay;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.util.Caches;

public class RoleDrawer implements OverlayStackElementDrawer<PlayerEntity> {

    @Override
    public float draw(Batch batch, OverlayInfos infos, PlayerEntity e) {
        if (e.getRole() != null) {
            GlyphLayout layout = Caches.overlayYellowGlyphLayout.get(e.getRole().getName());
            PixurvivalGame.getOverlayFont().draw(batch, layout, infos.getReferencePosition().x - layout.width / 2, infos.getReferencePosition().y + layout.height);
            return layout.height;
        } else {
            return 0;
        }
    }
}