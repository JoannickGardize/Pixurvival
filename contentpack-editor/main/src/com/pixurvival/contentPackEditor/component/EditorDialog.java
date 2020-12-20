package com.pixurvival.contentPackEditor.component;

import javax.swing.JDialog;

import com.pixurvival.contentPackEditor.ContentPackEditor;
import com.pixurvival.contentPackEditor.TranslationService;

public class EditorDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public EditorDialog(String titleKey) {
		setTitle(TranslationService.getInstance().getString(titleKey));
		setModal(true);
	}

	@Override
	public void setVisible(boolean b) {
		setLocationRelativeTo(ContentPackEditor.getInstance());
		super.setVisible(b);
	}
}
