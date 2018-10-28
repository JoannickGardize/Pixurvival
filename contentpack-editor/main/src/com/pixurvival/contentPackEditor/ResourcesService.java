package com.pixurvival.contentPackEditor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.EventHandler;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.contentPackEditor.event.ResourceListChangedEvent;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.util.FileUtils;

import lombok.Getter;

public class ResourcesService {

	private static @Getter ResourcesService instance = new ResourcesService();

	private Map<String, ResourceEntry> resources = new TreeMap<>();
	private JFileChooser fileChooser = new JFileChooser();

	private ResourcesService() {
		EventManager.getInstance().register(this);
	}

	public Collection<ResourceEntry> getResources() {
		return resources.values();
	}

	public void addResource() {
		ContentPack contentPack = FileService.getInstance().getCurrentContentPack();
		if (contentPack == null) {
			return;
		}
		String name = JOptionPane
				.showInputDialog(TranslationService.getInstance().getString("resources.name.chooseNameMessage"));
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
			resources.put(name, new ResourceEntry(name, data, loadPreview(name, data)));
			contentPack.addResource(name, data);
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
			int option = JOptionPane.showOptionDialog(null, message, "", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, new String[] { merge, replace }, 0);
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
				resources.put(name, new ResourceEntry(name, data, loadPreview(name, data)));
			});
		} catch (IOException e) {
			e.printStackTrace();
			Utils.showErrorDialog(e);
		}
		EventManager.getInstance().fire(new ResourceListChangedEvent());
	}

	@EventHandler
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		resources.clear();
		for (Entry<String, byte[]> entry : event.getContentPack().getResources().entrySet()) {
			String name = entry.getKey();
			byte[] data = entry.getValue();
			resources.put(name, new ResourceEntry(name, data, loadPreview(name, data)));
		}
		EventManager.getInstance().fire(new ResourceListChangedEvent());
	}

	private Object loadPreview(String name, byte[] data) {
		Object preview = null;
		int dotIndex = name.lastIndexOf('.');
		if (dotIndex != -1) {
			String extension = name.substring(dotIndex + 1);
			if ("png".equalsIgnoreCase(extension)) {
				try {
					preview = ImageIO.read(new ByteArrayInputStream(data));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return preview;
	}
}
