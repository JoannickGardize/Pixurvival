package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.pixurvival.gdxcore.PixurvivalGame;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class UIWindow extends Window {

    private List<HoverWindowListener> hoverWindowListeners = new ArrayList<>();

    private @Getter boolean hoverTitleActor;

    public UIWindow(String name) {
        super(PixurvivalGame.getString("hud." + name + ".title"), PixurvivalGame.getSkin());
        setName(name);
        setResizable(true);
        getTitleTable().add(createCloseButton()).align(Align.right).padTop(4);
        addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                hoverWindowListeners.forEach(l -> l.enter(UIWindow.this, x, y));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                hoverWindowListeners.forEach(l -> l.exit(UIWindow.this, x, y));
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                hoverWindowListeners.forEach(l -> l.moved(UIWindow.this, x, y));
                return false;
            }
        });
    }

    private ImageButton createCloseButton() {
        ImageButton closeButton = new ImageButton(PixurvivalGame.getSkin()) {
            @Override
            public float getPrefWidth() {
                return 22;
            }

            @Override
            public float getPrefHeight() {
                return 22;
            }
        };
        closeButton.addCaptureListener(new ClickListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                UIWindow.this.setMovable(false);
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                UIWindow.this.setVisible(false);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                hoverTitleActor = true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                UIWindow.this.setMovable(true);
                hoverTitleActor = false;
            }
        });
        return closeButton;
    }

    public void addHoverWindowListener(HoverWindowListener listener) {
        hoverWindowListeners.add(listener);
    }

    public int getResizeBorder() {
        return 8;
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            setMovable(true);
        }
        super.setVisible(visible);
    }
}
