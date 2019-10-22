package com.pixurvival.contentPackEditor.component;

import javax.swing.JMenuBar;

import com.pixurvival.contentPackEditor.FileService;
import com.pixurvival.contentPackEditor.component.translation.TranslationDialog;
import com.pixurvival.contentPackEditor.util.MenuBuilder;

public class CPEMenuBar extends JMenuBar {

	private static final long serialVersionUID = 1L;

	public CPEMenuBar() {
		FileService fs = FileService.getInstance();
		final ResourcesDialog resourcesDialog = new ResourcesDialog();
		final IdentifierDialog identifierDialog = new IdentifierDialog();
		final ConstantsDialog constantsDialog = new ConstantsDialog();
		final TranslationDialog translationDialog = TranslationDialog.getInstance();

		MenuBuilder builder = new MenuBuilder(this, "menuBar");
		builder.addItem("file.new", fs::newContentPack);
		builder.addItem("file.open", fs::open);
		builder.addItem("file.save", fs::save);
		builder.addItem("file.saveAs", fs::saveAs);
		builder.addItem("contentPack.identifier", () -> identifierDialog.setVisible(true));
		builder.addItem("contentPack.resources", () -> resourcesDialog.setVisible(true));
		builder.addItem("contentPack.constants", () -> constantsDialog.setVisible(true));
		builder.addItem("contentPack.translations", () -> translationDialog.setVisible(true));
	}
}
