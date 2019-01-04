package com.pixurvival.contentPackEditor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.ContentPackLoader;

import lombok.Getter;

public class FileService {

	private static @Getter FileService instance = new FileService();

	private FileService() {
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	}

	private @Getter ContentPack currentContentPack;
	private @Getter File currentFile;
	private JFileChooser fileChooser = new JFileChooser();

	public void newContentPack() {
		if (!savePrevious()) {
			return;
		}
		currentContentPack = new ContentPack();
		currentFile = null;
		EventManager.getInstance().fire(new ContentPackLoadedEvent(currentContentPack));
	}

	public void open() {
		if (!savePrevious()) {
			return;
		}
		int option = fileChooser.showOpenDialog(null);
		if (option != JFileChooser.APPROVE_OPTION) {
			return;
		}
		ContentPackLoader loader = new ContentPackLoader();
		try {
			currentContentPack = loader.load(fileChooser.getSelectedFile());
			currentFile = fileChooser.getSelectedFile();
			ResourcesService.getInstance().loadContentPack(currentContentPack);
			EventManager.getInstance().fire(new ContentPackLoadedEvent(currentContentPack));
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
			try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(currentFile))) {
				zipOutputStream.putNextEntry(new ZipEntry(ContentPack.SERIALIZATION_ENTRY_NAME));
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(zipOutputStream);
				objectOutputStream.writeObject(currentContentPack);
				for (Entry<String, byte[]> resource : currentContentPack.getResources().entrySet()) {
					zipOutputStream.putNextEntry(new ZipEntry(resource.getKey()));
					zipOutputStream.write(resource.getValue());
				}
				zipOutputStream.closeEntry();
			}
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
