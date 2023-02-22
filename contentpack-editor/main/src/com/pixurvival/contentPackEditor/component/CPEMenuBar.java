package com.pixurvival.contentPackEditor.component;

import com.pixurvival.contentPackEditor.FileService;
import com.pixurvival.contentPackEditor.component.translation.TranslationDialog;
import com.pixurvival.contentPackEditor.settings.SettingsDialog;
import com.pixurvival.contentPackEditor.util.MenuBuilder;

import javax.swing.*;

public class CPEMenuBar extends JMenuBar {

    private static final long serialVersionUID = 1L;

    public CPEMenuBar() {
        FileService fs = FileService.getInstance();
        ResourcesDialog resourcesDialog = new ResourcesDialog();
        IdentifierDialog identifierDialog = new IdentifierDialog();
        ConstantsDialog constantsDialog = new ConstantsDialog();
        TranslationDialog translationDialog = TranslationDialog.getInstance();
        ContentPackChooserDialog contentPackChooserDialog = new ContentPackChooserDialog();
        SettingsDialog settingsDialog = new SettingsDialog();
        ErrorListDialog errorListDialog = new ErrorListDialog();

        MenuBuilder builder = new MenuBuilder(this, "menuBar");
        builder.addItem("file.new", fs::newContentPack);
        builder.addItem("file.open", () -> contentPackChooserDialog.setVisible(true));
        builder.addItem("file.openFile", fs::open);
        builder.addItem("file.save", fs::save);
        builder.addItem("file.saveAs", fs::saveAs);
        builder.addItem("contentPack.identifier", () -> identifierDialog.setVisible(true));
        builder.addItem("contentPack.resources", () -> resourcesDialog.setVisible(true));
        builder.addItem("contentPack.constants", () -> constantsDialog.setVisible(true));
        builder.addItem("contentPack.translations", () -> translationDialog.setVisible(true));
        builder.addItem("contentPack.errorList", () -> errorListDialog.setVisible(true));
        builder.addItem("editor.settings", () -> settingsDialog.setVisible(true));
    }
}
