package com.pixurvival.gdxcore.input.processor;

import com.pixurvival.gdxcore.HudStage;

public class SwitchDebugInfosProcessor extends HudStageActionProcessor {

    @Override
    protected void process(HudStage hudStage) {
        hudStage.switchShowDebugInfos();
    }
}
