package com.pixurvival.gdxcore.menu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.pixurvival.gdxcore.PixurvivalGame;

public class BackButton extends TextButton {

    public BackButton() {
        this(() -> {
        });
    }

    public BackButton(Runnable additionalAction) {
        super(PixurvivalGame.getString("generic.back"), PixurvivalGame.getSkin());
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                additionalAction.run();
                PixurvivalGame.setScreen(MainMenuScreen.class);
            }
        });
    }
}
