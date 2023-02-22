package com.pixurvival.contentPackEditor.component.util;

import lombok.AllArgsConstructor;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.function.Consumer;

@AllArgsConstructor
public class DocumentAdapter implements DocumentListener {

    private Consumer<DocumentEvent> action;

    @Override
    public void insertUpdate(DocumentEvent e) {
        action.accept(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        action.accept(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        action.accept(e);
    }

}
