package com.pixurvival.contentPackEditor.component.util;

import java.awt.Component;
import java.util.function.Function;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SpecialCellRenderer<T> implements ListCellRenderer<T> {

	private DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

	private @NonNull Function<T, String> stringFunction;

	@Override
	public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected, boolean cellHasFocus) {
		Component component = defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		((JLabel) component).setText(stringFunction.apply(value));
		return component;
	}

}
