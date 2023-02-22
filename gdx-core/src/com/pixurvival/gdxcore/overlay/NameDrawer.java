package com.pixurvival.gdxcore.overlay;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.util.Caches;

public class NameDrawer implements OverlayStackElementDrawer<PlayerEntity> {

    @Override
    public float draw(Batch batch, OverlayInfos infos, PlayerEntity e) {
        GlyphLayout layout = Caches.overlayGlyphLayout.get(e.getName());
        PixurvivalGame.getOverlayFont().draw(batch, layout, infos.getReferencePosition().x - layout.width / 2, infos.getReferencePosition().y + layout.height);
        return layout.height;
    }

}
