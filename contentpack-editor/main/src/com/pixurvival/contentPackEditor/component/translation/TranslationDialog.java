package com.pixurvival.contentPackEditor.component.translation;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Properties;

import javax.swing.JScrollPane;
import javax.swing.table.TableCellEditor;

import com.pixurvival.contentPackEditor.FileService;
import com.pixurvival.contentPackEditor.component.EditorDialog;
import com.pixurvival.core.contentPack.ContentPack;

import lombok.Getter;

public class TranslationDialog extends EditorDialog {

	public static final @Getter TranslationDialog instance = new TranslationDialog();

	private static final long serialVersionUID = 1L;

	private LocaleList localeList = new LocaleList();
	private PropertiesTable propertiesTable = new PropertiesTable();

	private TranslationDialog() {
		super("translationDialog.title");
		setModal(false);
		Container content = getContentPane();

		localeList.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				setSelectedProperties();
			}
		});

		content.setLayout(new BorderLayout());
		content.add(localeList, BorderLayout.WEST);
		content.add(new JScrollPane(propertiesTable), BorderLayout.CENTER);
		setMinimumSize(new Dimension(800, 600));
		pack();
	}

	private void setSelectedProperties() {
		ContentPack contentPack = FileService.getInstance().getCurrentContentPack();
		stopEditing();
		Properties prop = contentPack.getTranslations().get(localeList.getSelectedLocale());
		if (prop != null) {
			((PropertiesTableModel) propertiesTable.getModel()).setProperties(prop);
		}
	}

	public void notifyDataChanged() {
		setSelectedProperties();
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			setSelectedProperties();
		} else {
			stopEditing();
		}
		super.setVisible(visible);
	}

	private void stopEditing() {
		TableCellEditor editor = propertiesTable.getCellEditor();
		if (editor != null) {
			editor.stopCellEditing();
		}
	}
}
