package com.pixurvival.contentPackEditor.component.translation;

import com.pixurvival.contentPackEditor.FileService;
import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.util.CPEButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.util.SpecialCellRenderer;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.contentPackEditor.util.DialogUtils;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.serialization.ContentPackSerialization;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.*;

public class LocaleList extends JPanel {

    private static final long serialVersionUID = 1L;

    private JList<Locale> localeList;

    private LocaleChooserDialog localeChooserDialog;

    public LocaleList() {
        localeList = new JList<>(new DefaultListModel<>());
        localeChooserDialog = new LocaleChooserDialog(getRootPane());
        localeList.setCellRenderer(new SpecialCellRenderer<>(Locale::getDisplayName));
        localeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        CPEButton addButton = new CPEButton("generic.add", this::add);
        CPEButton modifyButton = new CPEButton("generic.modify", this::modify);
        CPEButton removeButton = new CPEButton("generic.remove", this::remove);

        setLayout(new BorderLayout());
        add(new JScrollPane(localeList), BorderLayout.CENTER);
        JPanel buttonsPanel = LayoutUtils.createVerticalBox(addButton, modifyButton, removeButton);
        add(buttonsPanel, BorderLayout.SOUTH);
        EventManager.getInstance().register(this);
    }

    public Locale getSelectedLocale() {
        return localeList.getSelectedValue();
    }

    public void addListSelectionListener(ListSelectionListener listener) {
        localeList.addListSelectionListener(listener);
    }

    @EventListener
    public void contentPackLoaded(ContentPackLoadedEvent event) {
        DefaultListModel<Locale> model = (DefaultListModel<Locale>) localeList.getModel();
        model.clear();
        Map<Locale, Properties> translations = event.getContentPack().getTranslations();
        List<Locale> tmpList = new ArrayList<>(translations.size());
        tmpList.addAll(translations.keySet());
        tmpList.sort(Comparator.comparing(Locale::getDisplayName));
        for (Locale locale : tmpList) {
            model.addElement(locale);
        }
        localeList.setSelectedIndex(-1);
    }

    private void add() {
        if (localeChooserDialog.show(null) == LocaleChooserDialog.OK_CODE) {
            ContentPack contentPack = FileService.getInstance().getCurrentContentPack();
            Locale newLocale = localeChooserDialog.getSelectedLocale();
            Properties properties = new Properties();
            for (String key : contentPack.getAllTranslationKeys()) {
                properties.put(key, "");
            }
            add(newLocale, properties);
        }
    }

    private void add(Locale newLocale, Properties properties) {
        ContentPack contentPack = FileService.getInstance().getCurrentContentPack();
        contentPack.addTranslation(newLocale, properties);
        localeList.setSelectedIndex(insertOrdered(newLocale));
    }

    private void modify() {
        Locale locale = localeList.getSelectedValue();
        if (locale != null && localeChooserDialog.show(locale) == LocaleChooserDialog.OK_CODE) {
            if (FileService.getInstance().getCurrentContentPack().getTranslations().containsKey(localeChooserDialog.getSelectedLocale())) {
                DialogUtils.showMessageDialog("localeList.exists");
                return;
            }
            if (FileService.getInstance().isDirectoryMode()) {
                File file = getTranslationFile(locale);
                if (file.exists()) {
                    if (!file.renameTo(getTranslationFile(localeChooserDialog.getSelectedLocale()))) {
                        DialogUtils.showMessageDialog("localeList.unableRenameFile");
                        return;
                    }
                }
            }
            Properties removed = remove(locale);
            add(localeChooserDialog.getSelectedLocale(), removed);
        }

    }

    /**
     * @return The Properties removed, or null if no Locale selected
     */
    private Properties remove() {
        Locale locale = localeList.getSelectedValue();
        if (locale == null || JOptionPane.showConfirmDialog(this.getRootPane(),
                TranslationService.getInstance().getString("localeList.confirmRemove")) != JOptionPane.YES_OPTION) {
            return null;
        }
        return remove(locale);
    }

    /**
     * @param locale
     * @return The Properties removed, or null if no Locale selected
     */
    private Properties remove(Locale locale) {
        if (locale != null) {
            File file = getTranslationFile(locale);
            if (FileService.getInstance().isDirectoryMode() && file.exists()) {
                if (!file.delete()) {
                    DialogUtils.showMessageDialog("localeList.unableDeleteFile");
                    return null;
                }
            }
            ContentPack contentPack = FileService.getInstance().getCurrentContentPack();
            DefaultListModel<Locale> model = (DefaultListModel<Locale>) localeList.getModel();
            model.removeElement(locale);
            return contentPack.getTranslations().remove(locale);
        }
        return null;
    }

    /**
     * @param locale
     * @return The index where the Locale is inserted
     */
    private int insertOrdered(Locale locale) {
        DefaultListModel<Locale> model = (DefaultListModel<Locale>) localeList.getModel();
        int insertIndex = 0;
        while (insertIndex < model.size() && locale.getDisplayName().compareTo(model.elementAt(insertIndex).getDisplayName()) > 0) {
            insertIndex++;
        }
        model.add(insertIndex, locale);
        return insertIndex;
    }

    private File getTranslationFile(Locale locale) {
        return new File(FileService.getInstance().getCurrentFile(), ContentPackSerialization.buildTranslationFileName(locale));
    }
}
