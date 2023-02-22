package com.pixurvival.gdxcore.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.ui.UILabel;

public class QuestionWindow extends Window {

    private Label contentLabel;

    public QuestionWindow(String titleKey, String messageKey, Runnable yesAction, Runnable noAction) {
        this(titleKey, yesAction, noAction);
        contentLabel.setText(PixurvivalGame.getString(messageKey));
    }

    public QuestionWindow(String titleKey, Runnable yesAction, Runnable noAction) {
        super(PixurvivalGame.getString(titleKey), PixurvivalGame.getSkin());
        setResizable(false);
        setModal(true);

        TextButton yesButton = new TextButton(PixurvivalGame.getString("generic.yes"), PixurvivalGame.getSkin());
        TextButton noButton = new TextButton(PixurvivalGame.getString("generic.no"), PixurvivalGame.getSkin());

        yesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                yesAction.run();
                setVisible(false);
            }

        });

        noButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                noAction.run();
                setVisible(false);
            }
        });

        defaults().pad(2);
        contentLabel = UILabel.rawText("", Color.WHITE);
        contentLabel.setAlignment(Align.left);
        contentLabel.setWrap(true);
        add(contentLabel).expand().fill();
        row();
        HorizontalGroup buttonGroup = new HorizontalGroup();
        buttonGroup.space(2);
        buttonGroup.addActor(yesButton);
        buttonGroup.addActor(noButton);
        add(buttonGroup);
    }

    public void setVisible(String message) {
        contentLabel.setText(message);
        super.setVisible(true);
    }

    public void update(Viewport viewport) {
        setWidth(viewport.getWorldWidth() / 3);
        setHeight(viewport.getWorldHeight() / 3);
        setPosition(viewport.getWorldWidth() / 2 - getWidth() / 2, viewport.getWorldHeight() / 2 - getHeight() / 2);
    }
}
