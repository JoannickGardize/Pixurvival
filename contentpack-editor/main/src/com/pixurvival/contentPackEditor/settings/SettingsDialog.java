package com.pixurvival.contentPackEditor.settings;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import com.pixurvival.contentPackEditor.ContentPackEditor;
import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.EditorDialog;
import com.pixurvival.contentPackEditor.component.util.CPEButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;

public class SettingsDialog extends EditorDialog {

	private static final long serialVersionUID = 1L;

	public SettingsDialog() {
		super("settings.title");

		SettingsEditor settingsEditor = new SettingsEditor();
		JButton applyButton = new CPEButton("settings.apply", () -> {
			Settings.getInstance().apply(ContentPackEditor.getInstance());
			Settings.getInstance().save();
			JOptionPane.showMessageDialog(this, TranslationService.getInstance().getString("settings.restartMessage"));
			setVisible(false);
		});

		JComponent contentPane = (JComponent) getContentPane();
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(5, 5));
		settingsEditor.setValue(Settings.getInstance());
		contentPane.add(LayoutUtils.single(settingsEditor), BorderLayout.CENTER);
		contentPane.add(applyButton, BorderLayout.SOUTH);
		pack();
	}
}
