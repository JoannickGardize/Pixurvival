package com.pixurvival.contentPackEditor.component;

import javax.swing.JButton;

import com.pixurvival.contentPackEditor.TranslationService;

public class CPEButton extends JButton {

	private static final long serialVersionUID = 1L;

	public CPEButton(String textKey, Runnable action) {
		super(TranslationService.getInstance().getString(textKey));
		addActionListener(l -> action.run());
	}

}
