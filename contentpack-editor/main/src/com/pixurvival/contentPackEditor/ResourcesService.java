package com.pixurvival.contentPackEditor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.contentPackEditor.event.ResourceListChangedEvent;
import com.pixurvival.contentPackEditor.event.ResourceRemovedEvent;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.util.FileUtils;

import lombok.Getter;

public class ResourcesService {

	private static @Getter ResourcesService instance = new ResourcesService();

	private Map<String, ResourceEntry> resources = new TreeMap<>();
	private JFileChooser fileChooser = new JFileChooser();

	private ResourcesService() {
	}

	public Collection<ResourceEntry> getResources() {
		return resources.values();
	}

	public ResourceEntry getResource(String name) {
		if (name == null) {
			return null;
		} else {
			return resources.get(name);
		}
	}

	public boolean containsResource(String name) {
		if (name == null) {
			return false;
		} else {
			return resources.containsKey(name);
		}
	}

	public void addResource() {
		ContentPack contentPack = FileService.getInstance().getCurrentContentPack();
		if (contentPack == null) {
			return;
		}
		String name = JOptionPane.showInputDialog(TranslationService.getInstance().getString("resources.name.chooseNameMessage"));
		if (name == null) {
			return;
		}
		name = name.trim();
		if (!Utils.isValidFilePath(name)) {
			Utils.showErrorDialog("resources.name.invalidMessage");
			return;
		}
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
			return;
		}
		try {
			byte[] data = FileUtils.readBytes(fileChooser.getSelectedFile());
			putResource(name, data);
			EventManager.getInstance().fire(new ResourceListChangedEvent());
		} catch (IOException e) {
			Utils.showErrorDialog(e);
		}
	}

	public void importFolder() {
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
			return;
		}
		if (!resources.isEmpty()) {
			String message = TranslationService.getInstance().getString("resources.importFolder.whatToDoWithPrevious");
			String merge = TranslationService.getInstance().getString("resources.importFolder.merge");
			String replace = TranslationService.getInstance().getString("resources.importFolder.replace");
			int option = JOptionPane.showOptionDialog(null, message, "", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] { merge, replace }, 0);
			if (option == 1) {
				resources.clear();
			}
		}
		try {
			int preffixLength = fileChooser.getSelectedFile().getAbsolutePath().length() + 1;
			Files.walk(fileChooser.getSelectedFile().toPath()).filter(Files::isRegularFile).forEach(p -> {
				File f = p.toFile();
				String name = f.getAbsolutePath().substring(preffixLength).replace('\\', '/');
				byte[] data = null;
				try {
					data = FileUtils.readBytes(f);
				} catch (IOException e) {
					e.printStackTrace();
				}
				putResource(name, data);
			});
		} catch (IOException e) {
			e.printStackTrace();
			Utils.showErrorDialog(e);
		}
		EventManager.getInstance().fire(new ResourceListChangedEvent());
	}

	public void loadContentPack(ContentPack contentPack) {
		resources.clear();
		for (Entry<String, byte[]> entry : contentPack.getResources().entrySet()) {
			String name = entry.getKey();
			byte[] data = entry.getValue();
			resources.put(name, new ResourceEntry(name, data));
		}
		EventManager.getInstance().fire(new ResourceListChangedEvent());
	}

	private void putResource(String name, byte[] data) {
		resources.put(name, new ResourceEntry(name, data));
		FileService.getInstance().getCurrentContentPack().addResource(name, data);
	}

	private void removeResource(String name) {
		resources.remove(name);
		FileService.getInstance().getCurrentContentPack().removeResource(name);
	}

	public void deleteResource(String name) {
		ContentPack contentPack = FileService.getInstance().getCurrentContentPack();
		if (contentPack == null) {
			return;
		}

		if (containsResource(name)) {
			removeResource(name);
			EventManager.getInstance().fire(new ResourceListChangedEvent());
			EventManager.getInstance().fire(new ResourceRemovedEvent());
		}
	}

	public void renameResource(String name) {
		ContentPack contentPack = FileService.getInstance().getCurrentContentPack();
		if (contentPack == null) {
			return;
		}

		String newName = JOptionPane.showInputDialog(TranslationService.getInstance().getString("resources.name.renameNameMessage"), name);

		if (newName == null || newName.equals(name)) {
			return;
		}

		newName = newName.trim();
		putResource(newName, getResource(name).getData());
		removeResource(name);
		EventManager.getInstance().fire(new ResourceListChangedEvent());
	}
}
