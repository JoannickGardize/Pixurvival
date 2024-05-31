package com.pixurvival.gdxcore.input.processor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixurvival.gdxcore.HudStage;

public class SwitchEquimentAndInventoryUIProcessor extends HudStageActionProcessor {

    @Override
    protected void process(HudStage hudStage) {
        Actor ui = hudStage.getEquipmentAndInventoryUI();
        ui.setVisible(!ui.isVisible());
    }
}
