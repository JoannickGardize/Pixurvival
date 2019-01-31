package com.pixurvival.contentPackEditor.component.valueComponent;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;

import com.pixurvival.contentPackEditor.component.util.ClassNameCellRenderer;

import lombok.Getter;

public class ChangeableTypeBuilder<T> {

	private @Getter ChangeableTypeEditor<T> editor;

	private @Getter JComboBox<Class<? extends T>> chooser;

	@SuppressWarnings("unchecked")
	public ChangeableTypeBuilder(Class<?> scanClass, String editorsPackageName, String translationPreffix) {
		editor = new ChangeableTypeEditor<>();
		List<Class<?>> items = new ArrayList<>();
		for (Class<?> subClass : scanClass.getClasses()) {
			if (!subClass.isInterface() && !Modifier.isAbstract(subClass.getModifiers())) {
				items.add(subClass);
				editor.addType(subClass, editorsPackageName);
			}
		}
		chooser = new JComboBox<>(items.toArray(new Class[items.size()]));

		chooser.addActionListener(e -> editor.changeType((Class<? extends T>) chooser.getSelectedItem()));
		chooser.setRenderer(new ClassNameCellRenderer(translationPreffix));
	}
}
