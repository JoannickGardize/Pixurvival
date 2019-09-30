package com.pixurvival.contentPackEditor.component.translation;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

import com.pixurvival.contentPackEditor.FileService;
import com.pixurvival.contentPackEditor.component.util.CPEButton;
import com.pixurvival.contentPackEditor.component.util.SpecialCellRenderer;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.ContentPack;

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
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
		buttonsPanel.add(addButton);
		buttonsPanel.add(modifyButton);
		buttonsPanel.add(removeButton);
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
		tmpList.sort((l1, l2) -> l1.getDisplayName().compareTo(l2.getDisplayName()));
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
			Properties removed = remove(locale);
			add(localeChooserDialog.getSelectedLocale(), removed);
		}

	}

	/**
	 * @return The Properties removed, or null if no Locale selected
	 */
	private Properties remove() {
		Locale locale = localeList.getSelectedValue();
		return remove(locale);
	}

	/**
	 * @param locale
	 * @return The Properties removed, or null if no Locale selected
	 */
	private Properties remove(Locale locale) {
		if (locale != null) {
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
}
