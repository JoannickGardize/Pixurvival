package com.pixurvival.contentPackEditor;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.contentPackEditor.component.tree.LayoutFolder;
import com.pixurvival.contentPackEditor.component.tree.LayoutManager;
import com.pixurvival.contentPackEditor.event.ContentPackConstantChangedEvent;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.ContentPackSavedEvent;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.contentPackEditor.util.AutoUpgradeTool;
import com.pixurvival.contentPackEditor.util.DialogUtils;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackContext;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.util.ReleaseVersion;

import lombok.Getter;

public class FileService {

	private static @Getter FileService instance = new FileService();

	private @Getter ContentPack currentContentPack;
	private @Getter File currentFile;
	private JFileChooser fileChooser = new JFileChooser();
	private @Getter ContentPackContext contentPackContext;

	private FileService() {
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	}

	public void initialize(File contentPackDirectory) {
		contentPackContext = new ContentPackContext(contentPackDirectory);
		contentPackContext.getSerialization().addPlugin(LayoutManager.getInstance());
	}

	public void newContentPack() {
		if (!savePrevious()) {
			return;
		}
		currentContentPack = new ContentPack();
		currentContentPack.setIdentifier(new ContentPackIdentifier());
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
		if (currentFile != null) {
			fileChooser.setSelectedFile(currentFile);
		}
		int option = fileChooser.showOpenDialog(null);
		if (option != JFileChooser.APPROVE_OPTION) {
			return;
		}
		open(fileChooser.getSelectedFile());
	}

	public void open(File file) {
		boolean forceUpgrade = false;
		try {
			currentContentPack = contentPackContext.getSerialization().load(file);
		} catch (Exception e) {
			Log.warn("Content pack cannot be read.", e);
			forceUpgrade = true;
		}
		if (manageReleaseVersion(forceUpgrade)) {
			currentFile = file;
			ResourcesService.getInstance().loadContentPack(currentContentPack);
			ContentPackEditionService.getInstance().updateNextStatFormulaId();
			EventManager.getInstance().fire(new ContentPackLoadedEvent(currentContentPack));
			EventManager.getInstance().fire(new ContentPackConstantChangedEvent(currentContentPack.getConstants()));
		}
	}

	public void save() {
		if (currentContentPack == null) {
			return;
		}
		if (currentFile == null) {
			currentFile = new File(contentPackContext.getWorkingDirectory(), currentContentPack.getIdentifier().fileName());
			if (currentFile.exists() && !chooseFile()) {
				return;
			}
		}
		currentContentPack.setReleaseVersion(ReleaseVersion.getActual().name());
		try {
			contentPackContext.getSerialization().save(currentFile, currentContentPack);
			EventManager.getInstance().fire(new ContentPackSavedEvent());
		} catch (IOException e) {
			DialogUtils.showErrorDialog(e);
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
			} else if (option == JOptionPane.YES_OPTION) {
				save();
			}
		}
		return true;
	}

	private boolean chooseFile() {
		if (currentContentPack == null) {
			return false;
		}
		if (currentFile == null) {
			fileChooser.setSelectedFile(new File(contentPackContext.getWorkingDirectory(), currentContentPack.getIdentifier().fileName()));
		}
		int option = fileChooser.showSaveDialog(null);
		if (option == JFileChooser.APPROVE_OPTION) {
			currentFile = fileChooser.getSelectedFile();
			return true;
		} else {
			return false;
		}
	}

	private boolean manageReleaseVersion(boolean forceUpgrade) {
		String messageKey;
		if (forceUpgrade) {
			messageKey = "autoUpgradeTool.loadErrorTry";
		} else if (ReleaseVersion.valueFor(currentContentPack.getReleaseVersion()) != ReleaseVersion.getActual()) {
			messageKey = "autoUpgradeTool.oldVersion";
		} else {
			return true;
		}
		int option = JOptionPane.showConfirmDialog(null, TranslationService.getInstance().getString(messageKey), "", JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.YES_OPTION) {
			ContentPack upgraded = AutoUpgradeTool.upgrade();
			if (upgraded != null) {
				currentContentPack = upgraded;
				currentContentPack.setReleaseVersion(ReleaseVersion.getActual().name());
			}
			return true;
		}
		return !forceUpgrade;
	}
}
