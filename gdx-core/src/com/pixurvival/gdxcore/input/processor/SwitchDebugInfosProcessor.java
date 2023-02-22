package com.pixurvival.gdxcore.input.processor;

import com.badlogic.gdx.Screen;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.WorldScreen;

public class SwitchDebugInfosProcessor implements InputActionProcessor {

    @Override
    public void buttonDown() {
        Screen screen = PixurvivalGame.getInstance().getScreen();
        if (screen instanceof WorldScreen) {
            ((WorldScreen) screen).switchShowDebugInfos();
        }
    }
}
