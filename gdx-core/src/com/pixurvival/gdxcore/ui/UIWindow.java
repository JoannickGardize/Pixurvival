package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.pixurvival.gdxcore.PixurvivalGame;

import java.util.ArrayList;
import java.util.List;

public class UIWindow extends Window {

    private List<HoverWindowListener> hoverWindowListeners = new ArrayList<>();

    public UIWindow(String name) {
        super(PixurvivalGame.getString("hud." + name + ".title"), PixurvivalGame.getSkin());
        setName(name);
        setResizable(true);
        addCaptureListener(new ClickListener() {
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

    public void addHoverWindowListener(HoverWindowListener listener) {
        hoverWindowListeners.add(listener);
    }

    public int getResizeBorder() {
        return 8;
    }
}
