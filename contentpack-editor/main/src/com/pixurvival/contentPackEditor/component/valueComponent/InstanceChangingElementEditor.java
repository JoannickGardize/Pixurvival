package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.CardLayout;
import java.awt.event.ItemEvent;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.BeanFactory;
import com.pixurvival.contentPackEditor.component.util.ClassNameCellRenderer;

import lombok.AllArgsConstructor;
import lombok.Getter;

public abstract class InstanceChangingElementEditor<E> extends ElementEditor<E> {

	@Getter
	@AllArgsConstructor
	public class ClassEntry {
		private Class<? extends E> type;
		private JPanel specificPanel;
	}

	private static final long serialVersionUID = 1L;

	private @Getter JComboBox<Class<? extends E>> typeChooser;
	private @Getter JPanel specificPartPanel = new JPanel(new CardLayout());

	@SuppressWarnings("unchecked")
	public InstanceChangingElementEditor(String translationPreffix, Object params) {
		List<ClassEntry> classEntries = getClassEntries(params);
		typeChooser = new JComboBox<>(classEntries.stream().map(ClassEntry::getType).toArray(Class[]::new));
		typeChooser.setRenderer(new ClassNameCellRenderer(translationPreffix));
		specificPartPanel = new JPanel(new CardLayout());
		typeChooser.addItemListener(e -> {
			if (typeChooser.isPopupVisible() && e.getStateChange() == ItemEvent.SELECTED) {
				changeInstance(BeanFactory.newInstance(((Class<? extends E>) e.getItem())));
			}
		});
		for (ClassEntry classEntry : classEntries) {
			specificPartPanel.add(classEntry.getSpecificPanel(), classEntry.getType().getSimpleName());
		}
	}

	private void changeInstance(E newInstance) {
		if (newInstance == null) {
			return;
		}
		E oldInstance = getValue();
		if (oldInstance != null) {
			initialize(oldInstance, newInstance);
		}
		setValue(newInstance);
		notifyValueChanged();
	}

	@Override
	protected void valueChanged(ValueComponent<?> source) {
		if (source == this && getValue() != null) {
			Class<?> type = getValue().getClass();
			((CardLayout) specificPartPanel.getLayout()).show(specificPartPanel, type.getSimpleName());
			typeChooser.setSelectedItem(type);
		}
	}

	protected abstract List<ClassEntry> getClassEntries(Object params);

	protected abstract void initialize(E oldInstance, E newInstance);
}