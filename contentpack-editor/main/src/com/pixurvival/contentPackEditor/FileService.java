package com.pixurvival.contentPackEditor;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.pixurvival.contentPackEditor.component.tree.LayoutFolder;
import com.pixurvival.contentPackEditor.component.tree.LayoutManager;
import com.pixurvival.contentPackEditor.event.ContentPackConstantChangedEvent;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.ContentPackSavedEvent;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.serialization.ContentPackSerialization;

import lombok.Getter;

public class FileService {

	private static @Getter FileService instance = new FileService();

	private @Getter ContentPack currentContentPack;
	private @Getter File currentFile;
	private JFileChooser fileChooser = new JFileChooser();
	private ContentPackSerialization contentPackSerializer = new ContentPackSerialization();

	private FileService() {
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		contentPackSerializer.addPlugin(LayoutManager.getInstance());
	}

	public void newContentPack() {
		if (!savePrevious()) {
			return;
		}
		currentContentPack = new ContentPack();
		ResourcesService.getInstance().clear();
		currentFile = null;
		LayoutManager.getInstance().setRoot(new LayoutFolder("root"));
		ContentPackEditionService.getInstance().updateNextStatFormulaId();
		EventManager.getInstance().fire(new ContentPackLoadedEvent(currentContentPack));
		EventManager.getInstance().fire(new ContentPackConstantChangedEvent(currentContentPack.getConstants()));
	}

	public void open() {
		if (!savePrevious()) {
			return;
		}
		int option = fileChooser.showOpenDialog(null);
		if (option != JFileChooser.APPROVE_OPTION) {
			return;
		}
		open(fileChooser.getSelectedFile());
	}

	public void open(File file) {
		try {
			currentContentPack = contentPackSerializer.load(file);
			currentFile = file;
			ResourcesService.getInstance().loadContentPack(currentContentPack);
			ContentPackEditionService.getInstance().updateNextStatFormulaId();
			EventManager.getInstance().fire(new ContentPackLoadedEvent(currentContentPack));
			EventManager.getInstance().fire(new ContentPackConstantChangedEvent(currentContentPack.getConstants()));
		} catch (ContentPackException e) {
			Utils.showErrorDialog(e);
		}
	}

	public void save() {
		if (currentContentPack == null) {
			return;
		}
		if (currentFile == null && !chooseFile()) {
			return;
		}
		try {
			if (!currentFile.exists()) {
				currentFile.createNewFile();
			}
			contentPackSerializer.save(currentFile, currentContentPack);
			EventManager.getInstance().fire(new ContentPackSavedEvent());
		} catch (IOException e) {
			Utils.showErrorDialog(e);
		}
	}

	public void saveAs() {
		if (chooseFile()) {
			save();
		}
	}

	private boolean savePrevious() {
		if (currentContentPack != null) {
			int option = JOptionPane.showConfirmDialog(null, TranslationService.getInstance().getString("dialog.unsavedContentPack"), "", JOptionPane.YES_NO_CANCEL_OPTION);
			if (option == JOptionPane.CANCEL_OPTION) {
				return false;
			} else if (option == JOptionPane.OK_OPTION) {
				save();
			}
		}
		return true;
	}

	private boolean chooseFile() {
		if (currentContentPack == null) {
			return false;
		}
		if (currentFile != null) {
			fileChooser.setSelectedFile(new File(currentFile.getParent(), currentContentPack.getIdentifier().fileName()));
		} else {
			fileChooser.setSelectedFile(new File(System.getProperty("user.home"), currentContentPack.getIdentifier().fileName()));
		}
		int option = fileChooser.showSaveDialog(null);
		if (option == JFileChooser.APPROVE_OPTION) {
			currentFile = fileChooser.getSelectedFile();
			return true;
		} else {
			return false;
		}
	}
}
