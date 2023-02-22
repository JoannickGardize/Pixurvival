package com.pixurvival.gdxcore.input.processor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;

public class SwitchFullScreenProcessor implements InputActionProcessor {

    @Override
    public void buttonDown() {
        Graphics.DisplayMode currentMode = Gdx.graphics.getDisplayMode();
        if (Gdx.graphics.isFullscreen()) {
            Gdx.graphics.setWindowedMode(currentMode.width, currentMode.height);
        } else {
            Gdx.graphics.setFullscreenMode(currentMode);
        }
    }
}
