package com.pixurvival.gdxcore.input.processor;

import com.badlogic.gdx.Screen;
import com.pixurvival.gdxcore.HudStage;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.WorldScreen;

public abstract class HudStageActionProcessor implements InputActionProcessor {
    public void buttonDown() {
        Screen screen = PixurvivalGame.getInstance().getScreen();
        if (screen instanceof WorldScreen) {
            process(((WorldScreen) screen).getHudStage());
        }
    }

    protected abstract void process(HudStage hudStage);
}
