package com.pixurvival.contentPackEditor.component.util;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.core.util.CaseUtils;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClassNameCellRenderer implements ListCellRenderer<Class<?>> {

	private DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

	private @NonNull String translationKeyPreffix;

	@Override
	public Component getListCellRendererComponent(JList<? extends Class<?>> list, Class<?> value, int index,
			boolean isSelected, boolean cellHasFocus) {
		Component component = defaultRenderer.getListCellRendererComponent(list, value, index, isSelected,
				cellHasFocus);
		String text = TranslationService.getInstance()
				.getString(translationKeyPreffix + "." + CaseUtils.pascalToCamelCase(value.getSimpleName()));
		((JLabel) component).setText(text);
		return component;
	}

}
