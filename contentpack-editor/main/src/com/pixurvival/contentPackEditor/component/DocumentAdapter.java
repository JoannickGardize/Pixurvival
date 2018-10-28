package com.pixurvival.contentPackEditor.component;

import java.util.function.Consumer;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import lombok.AllArgsConstructor;

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
