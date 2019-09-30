package com.pixurvival.contentPackEditor.component.translation;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.util.BackingArrayComboBoxModel;
import com.pixurvival.contentPackEditor.component.util.CPEButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.util.SpecialCellRenderer;

public class LocaleChooserDialog extends JDialog {

	public static final int OK_CODE = 0;
	public static final int CANCEL_CODE = 1;

	private static final long serialVersionUID = 1L;

	private JComboBox<LocaleLanguage> languageList;
	private JComboBox<LocaleCountry> countryList;
	private JButton okButton = new CPEButton("generic.add", () -> {
		returnCode = OK_CODE;
		setVisible(false);
	});
	private int returnCode;

	public LocaleChooserDialog(Component parent) {
		setModal(true);
		setLocationRelativeTo(parent);
		languageList = new JComboBox<>(new BackingArrayComboBoxModel<LocaleLanguage>(LocaleLanguage.getLanguages()));
		countryList = new JComboBox<>(new BackingArrayComboBoxModel<LocaleCountry>(LocaleCountry.getCountries()));
		languageList.setRenderer(new SpecialCellRenderer<>(e -> e == null ? TranslationService.getInstance().getString("generic.undefined") : e.toString()));
		countryList.setRenderer(new SpecialCellRenderer<>(e -> e == null ? TranslationService.getInstance().getString("generic.undefined") : e.toString()));
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		Container content = getContentPane();
		((JPanel) content).setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		LayoutUtils.addHorizontalLabelledItem(content, "localeChooserDialog.language", languageList, gbc);
		LayoutUtils.addHorizontalLabelledItem(content, "localeChooserDialog.country", countryList, gbc);
		gbc.gridwidth = 2;
		okButton.setEnabled(false);
		JButton cancelButton = new CPEButton("generic.cancel", () -> setVisible(false));
		content.add(LayoutUtils.createHorizontalBox(okButton, cancelButton), gbc);

		languageList.addActionListener(e -> okButton.setEnabled(languageList.getSelectedItem() != null));
		pack();
	}

	public int show(Locale currentLocale) {
		returnCode = CANCEL_CODE;
		if (currentLocale == null) {
			setTitle(TranslationService.getInstance().getString("localChooserDialog.title.add"));
			languageList.setSelectedIndex(0);
			countryList.setSelectedIndex(0);
			okButton.setEnabled(false);
		} else {
			setTitle(TranslationService.getInstance().getString("localChooserDialog.title.modify"));
			languageList.setSelectedItem(new LocaleLanguage(currentLocale.getLanguage()));
			countryList.setSelectedItem(currentLocale.getCountry().isEmpty() ? null : new LocaleCountry(currentLocale.getCountry()));
			okButton.setEnabled(true);
		}
		setVisible(true);
		return returnCode;
	}

	public Locale getSelectedLocale() {
		return new Locale(((LocaleLanguage) languageList.getSelectedItem()).getCode(), countryList.getSelectedIndex() > 0 ? ((LocaleCountry) countryList.getSelectedItem()).getCode() : "");
	}
}
