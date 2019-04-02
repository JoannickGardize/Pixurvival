package com.pixurvival.contentPackEditor.component.util;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.core.util.CaseUtils;

public class EnumConstantCellRenderer implements ListCellRenderer<Enum<?>> {

	private DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

	@Override
	public Component getListCellRendererComponent(JList<? extends Enum<?>> list, Enum<?> value, int index, boolean isSelected, boolean cellHasFocus) {
		Component component = defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value != null) {
			String text = TranslationService.getInstance().getString(CaseUtils.pascalToCamelCase(value.getClass().getSimpleName()) + "." + CaseUtils.upperToCamelCase(value.name()));
			((JLabel) component).setText(text);
		}
		return component;
	}

}
