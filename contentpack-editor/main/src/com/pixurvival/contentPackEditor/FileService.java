package com.pixurvival.contentPackEditor;

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
import com.pixurvival.core.contentPack.serialization.ContentPackSerialization;
import com.pixurvival.core.contentPack.serialization.io.DirectoryStoreOutput;
import com.pixurvival.core.contentPack.serialization.io.StoreFactory;
import com.pixurvival.core.contentPack.serialization.io.ZipStoreOutput;
import com.pixurvival.core.util.FileUtils;
import com.pixurvival.core.util.ReleaseVersion;
import lombok.Getter;

import javax.swing.*;
import java.io.File;

public class FileService {

    private static @Getter FileService instance = new FileService();

    private @Getter ContentPack currentContentPack;
    private @Getter File currentFile;
    private ContentPackIdentifier previousIdentifier;
    private JFileChooser fileChooser = new JFileChooser();
    private @Getter ContentPackContext contentPackContext;

    private FileService() {
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    }

    public void initialize(File contentPackDirectory) {
        contentPackContext = new ContentPackContext(contentPackDirectory);
        contentPackContext.getSerialization().addPlugin(LayoutManager.getInstance());
        fileChooser.setCurrentDirectory(contentPackContext.getWorkingDirectory());
    }

    public void newContentPack() {
        if (!savePrevious()) {
            return;
        }
        currentContentPack = new ContentPack();
        currentContentPack.setIdentifier(new ContentPackIdentifier());
        currentContentPack.setReleaseVersion(ReleaseVersion.actual().toString());
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
            fileChooser.setCurrentDirectory(currentFile.getParentFile());
        }
        int option = fileChooser.showOpenDialog(null);
        if (option != JFileChooser.APPROVE_OPTION) {
            return;
        }
        open(fileChooser.getSelectedFile());
    }

    public void open(File file) {
        boolean forceUpgrade = false;
        ContentPack previousContentPack = currentContentPack;
        try {
            currentContentPack = contentPackContext.getSerialization().load(() -> StoreFactory.input(file));
        } catch (Exception e) {
            Log.warn("Content pack cannot be read.", e);
            forceUpgrade = true;
            currentContentPack = null;
        }
        File previousFile = currentFile;
        currentFile = file;
        if (manageReleaseVersion(forceUpgrade)) {
            fileChooser.setSelectedFile(currentFile);
            fileChooser.setCurrentDirectory(currentFile.getParentFile());
            previousIdentifier = new ContentPackIdentifier(currentContentPack.getIdentifier());
            ResourcesService.getInstance().loadContentPack(currentContentPack);
            ContentPackEditionService.getInstance().updateNextStatFormulaId();
            EventManager.getInstance().fire(new ContentPackLoadedEvent(currentContentPack));
            EventManager.getInstance().fire(new ContentPackConstantChangedEvent(currentContentPack.getConstants()));
        } else {
            currentFile = previousFile;
            currentContentPack = previousContentPack;
        }
    }

    public void save() {
        save(false);
    }

    public void save(boolean isNew) {
        if (currentContentPack == null) {
            return;
        }
        if (currentFile == null) {
            if (!getNewDefaultContentPack()) {
                return;
            }
        } else if (previousIdentifier != null && !previousIdentifier.equals(currentContentPack.getIdentifier())) {
            int option = JOptionPane.showConfirmDialog(null, TranslationService.getInstance().getString("dialog.saveAsNewQuestion"), "", JOptionPane.YES_NO_CANCEL_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                if (!getNewDefaultContentPack()) {
                    return;
                }
            } else if (option != JOptionPane.NO_OPTION) {
                return;
            }
        }
        try {
            if (!isNew && isDirectoryMode()) {

            }
            if (!ValidationService.getInstance().getErrorList().isEmpty()
                    && JOptionPane.showConfirmDialog(null, TranslationService.getInstance().getString("dialog.saveErroredContentPackQuestion"),
                    "", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
            if (isNew && isDirectoryMode() && anyManagedDirectoryExists()) {
                if (JOptionPane.showConfirmDialog(null, TranslationService.getInstance().getString("dialog.eraseResourceFolders"),
                        "", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                    return;
                } else {
                    clearDirectoryForSave();
                }
            }
            currentContentPack.setReleaseVersion(ReleaseVersion.actual().name());
            contentPackContext.getSerialization().save(
                    () -> currentFile.isDirectory() ? new DirectoryStoreOutput(currentFile) : new ZipStoreOutput(currentFile),
                    currentContentPack, isNew || !isDirectoryMode());
            previousIdentifier = new ContentPackIdentifier(currentContentPack.getIdentifier());
            EventManager.getInstance().fire(new ContentPackSavedEvent());
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.showErrorDialog(e);
        }
    }

    private boolean getNewDefaultContentPack() {
        if (currentFile == null) {
            currentFile = new File(contentPackContext.getWorkingDirectory(), currentContentPack.getIdentifier().fileName());
        } else {
            currentFile = new File(currentFile.getParentFile(), currentContentPack.getIdentifier().fileName());
        }
        return !currentFile.exists() || chooseFile();
    }

    public void saveAs() {
        if (chooseFile()) {
            previousIdentifier = null;
            save(true);
        }
    }

    public boolean savePrevious() {
        if (!ContentPackEditionService.getInstance().isEmpty()) {
            int option = JOptionPane.showConfirmDialog(null, TranslationService.getInstance().getString("dialog.unsavedContentPack"), "", JOptionPane.YES_NO_CANCEL_OPTION);
            if (option == JOptionPane.CANCEL_OPTION || option == JOptionPane.CLOSED_OPTION) {
                return false;
            } else if (option == JOptionPane.YES_OPTION) {
                save();
            }
        }
        return true;
    }

    public boolean isDirectoryMode() {
        return currentFile != null && currentFile.isDirectory();
    }

    public boolean anyManagedDirectoryExists() {
        File res = getCurrentResourcesDirectory();
        File tra = getCurrentTranslationsDirectory();
        return res != null && res.isDirectory() || tra != null && tra.isDirectory();
    }

    public File getCurrentResourcesDirectory() {
        if (currentFile == null || !currentFile.isDirectory()) {
            throw new IllegalStateException();
        }
        return new File(currentFile, ContentPackSerialization.RESOURCES_ROOT);
    }

    public File getCurrentTranslationsDirectory() {
        if (currentFile == null || !currentFile.isDirectory()) {
            throw new IllegalStateException();
        }
        return new File(currentFile, ContentPackSerialization.TRANSLATIONS_ROOT);
    }

    private boolean chooseFile() {
        if (currentContentPack == null) {
            return false;
        }
        if (currentFile == null) {
            fileChooser.setSelectedFile(new File(contentPackContext.getWorkingDirectory(), currentContentPack.getIdentifier().fileName()));
        } else {
            fileChooser.setSelectedFile(currentFile);
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
        } else if (ReleaseVersion.valueFor(currentContentPack.getReleaseVersion()) != ReleaseVersion.actual()) {
            messageKey = "autoUpgradeTool.oldVersion";
        } else {
            return true;
        }
        int option = JOptionPane.showConfirmDialog(null, TranslationService.getInstance().getString(messageKey), "", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            ContentPack upgraded = AutoUpgradeTool.upgrade();
            if (upgraded == null) {
                return false;
            } else {
                currentContentPack = upgraded;
                currentContentPack.setReleaseVersion(ReleaseVersion.actual().name());
                return true;
            }
        }
        return !forceUpgrade;
    }

    private void clearDirectoryForSave() {
        File f = getCurrentResourcesDirectory();
        if (f != null) {
            FileUtils.deleteDirectoryRecursively(f);
        }
        f = getCurrentTranslationsDirectory();
        if (f != null) {
            FileUtils.deleteDirectoryRecursively(f);
        }
    }
}
