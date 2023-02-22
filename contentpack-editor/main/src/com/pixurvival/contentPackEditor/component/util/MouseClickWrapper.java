package com.pixurvival.contentPackEditor.component.util;

import lombok.AllArgsConstructor;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

@AllArgsConstructor
public class MouseClickWrapper extends MouseAdapter {

    private Consumer<MouseEvent> action;

    @Override
    public void mouseClicked(MouseEvent e) {
        action.accept(e);
    }

    ;
}
