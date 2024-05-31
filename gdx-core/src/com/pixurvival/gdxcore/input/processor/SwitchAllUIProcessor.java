package com.pixurvival.gdxcore.input.processor;

import com.pixurvival.gdxcore.HudStage;

public class SwitchAllUIProcessor extends HudStageActionProcessor {

    @Override
    protected void process(HudStage hudStage) {
        long count = hudStage.uiWindowStream().count();
        long visibleCount = hudStage.uiWindowStream().filter(w -> w.isVisible()).count();
        if (visibleCount > count - visibleCount) {
            hudStage.forEachUIWindow(w -> w.setVisible(false));
        } else {
            hudStage.forEachUIWindow(w -> w.setVisible(true));
        }
    }
}
