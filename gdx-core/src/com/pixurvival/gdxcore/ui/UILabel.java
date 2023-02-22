package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.pixurvival.gdxcore.PixurvivalGame;

public class UILabel extends Label {

    public UILabel(String textKey) {
        super(PixurvivalGame.getString(textKey), PixurvivalGame.getSkin(), "default");
        setAlignment(Align.right);
    }

    public UILabel(String textKey, Color color) {
        super(PixurvivalGame.getString(textKey), PixurvivalGame.getSkin(), "default", color);
        setAlignment(Align.right);
    }

    public UILabel(String textKey, String suffix, Color color) {
        super(PixurvivalGame.getString(textKey) + suffix, PixurvivalGame.getSkin(), "default", color);
        setAlignment(Align.right);
    }

    public UILabel(Color color) {
        super("", PixurvivalGame.getSkin(), "default", color);
        setAlignment(Align.right);
    }

    public static UILabel rawText(String text, Color color) {
        UILabel label = new UILabel(color);
        label.setText(text);
        return label;
    }
}
