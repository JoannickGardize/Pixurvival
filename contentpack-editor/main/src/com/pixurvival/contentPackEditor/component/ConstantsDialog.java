package com.pixurvival.contentPackEditor.component;

import javax.swing.BorderFactory;

public class ConstantsDialog extends EditorDialog {

	private static final long serialVersionUID = 1L;

	public ConstantsDialog() {
		super("constantsDialog.title");
		ConstantsEditor constantsEditor = new ConstantsEditor();
		constantsEditor.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setContentPane(constantsEditor);
		pack();
	}
}
