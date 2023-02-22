package com.pixurvival.gdxcore.util;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

public class AutoScrollFocusListener implements EventListener {

    @Override
    public boolean handle(Event event) {
        if (event instanceof InputEvent) {
            InputEvent inputEvent = (InputEvent) event;
            if (inputEvent.getType() == InputEvent.Type.enter) {
                event.getStage().setScrollFocus(event.getTarget());
            } else if (inputEvent.getType() == InputEvent.Type.exit && event.getStage().getScrollFocus() == event.getTarget()) {
                event.getStage().setScrollFocus(null);
            }
        }
        return false;
    }

}
