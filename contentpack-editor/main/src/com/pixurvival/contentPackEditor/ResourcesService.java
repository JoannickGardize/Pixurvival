package com.pixurvival.contentPackEditor;

import com.pixurvival.contentPackEditor.event.*;
import com.pixurvival.contentPackEditor.util.DialogUtils;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.util.FileUtils;
import lombok.Getter;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;

public class ResourcesService {

    private static @Getter ResourcesService instance = new ResourcesService();

    private Map<String, ResourceEntry> resources = new TreeMap<>();
    private JFileChooser fileChooser = new JFileChooser();

    private ResourcesService() {
    }

    public Collection<ResourceEntry> getResources() {
        return resources.values();
    }

    public Supplier<Collection<ResourceEntry>> getResourcesSupplier() {
        return this::getResources;
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
        if (!com.pixurvival.contentPackEditor.util.FileUtils.isValidFilePath(name)) {
            DialogUtils.showErrorDialog("resources.name.invalidMessage");
            return;
        }
        addResource(name);
    }

    public void addResource(String name) {
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            byte[] data = FileUtils.readBytes(fileChooser.getSelectedFile());
            addResource(name, data);
        } catch (IOException e) {
            DialogUtils.showErrorDialog(e);
        }
    }

    public void addResource(String name, byte[] data) {
        if (putResource(name, data)) {
            EventManager.getInstance().fire(new ResourceChangedEvent(name));
        } else {
            EventManager.getInstance().fire(new ResourceAddedEvent(name));
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
            int option = JOptionPane.showOptionDialog(null, message, "", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{merge, replace}, 0);
            if (option == 1) {
                resources.clear();
            }
        }
        File folderFile = fileChooser.getSelectedFile();
        importFolder(folderFile);
    }

    public void importFolder(File folderFile) {
        try {
            int prefixLength = folderFile.getAbsolutePath().length() + 1;
            Files.walk(folderFile.toPath()).filter(Files::isRegularFile).forEach(p -> {
                File f = p.toFile();
                String name = f.getAbsolutePath().substring(prefixLength).replace('\\', '/');
                byte[] data = null;
                try {
                    data = FileUtils.readBytes(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                boolean contains = containsResource(name);
                putResource(name, data);
                if (contains) {
                    EventManager.getInstance().fire(new ResourceChangedEvent(name));
                } else {
                    EventManager.getInstance().fire(new ResourceAddedEvent(name));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtils.showErrorDialog(e);
        }
    }

    public void loadContentPack(ContentPack contentPack) {
        resources.clear();
        for (Entry<String, byte[]> entry : contentPack.getResources().entrySet()) {
            String name = entry.getKey();
            byte[] data = entry.getValue();
            resources.put(name, new ResourceEntry(name, data));
        }
    }

    public void clear() {
        resources.clear();
    }

    public void clearAndNotify() {
        List<ResourceEntry> entries = new ArrayList<>(resources.values());
        entries.forEach(e -> deleteResource(e.getName()));
    }

    private boolean putResource(String name, byte[] data) {
        ResourceEntry prevEntry = resources.put(name, new ResourceEntry(name, data));
        FileService.getInstance().getCurrentContentPack().addResource(name, data);
        return prevEntry != null;
    }

    private ResourceEntry removeResource(String name) {
        ResourceEntry removed = resources.remove(name);
        FileService.getInstance().getCurrentContentPack().removeResource(name);
        return removed;
    }

    public ResourceEntry deleteResource(String name) {
        ContentPack contentPack = FileService.getInstance().getCurrentContentPack();
        if (contentPack == null) {
            return null;
        }
        ResourceEntry removed = null;
        if (containsResource(name)) {
            removed = removeResource(name);
            EventManager.getInstance().fire(new ResourceRemovedEvent(name));
        }
        return removed;
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
        EventManager.getInstance().fire(new ResourceRenamedEvent(name, newName));
    }
}
