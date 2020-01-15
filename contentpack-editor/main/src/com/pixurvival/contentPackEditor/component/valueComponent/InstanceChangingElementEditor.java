package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.BeanFactory;
import com.pixurvival.contentPackEditor.component.util.ClassNameCellRenderer;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.util.CachedSupplier;

import lombok.AllArgsConstructor;
import lombok.Getter;

public abstract class InstanceChangingElementEditor<E> extends ElementEditor<E> {

	@Getter
	@AllArgsConstructor
	public class ClassEntry {
		private Class<? extends E> type;
		private Supplier<JPanel> specificPanel;
	}

	private static final long serialVersionUID = 1L;

	private JComboBox<Class<? extends E>> typeChooser;
	private @Getter JPanel specificPartPanel;
	private Class<?> currentClass;
	private Map<Class<? extends E>, CachedSupplier<JPanel>> classEntries = new HashMap<>();

	@SuppressWarnings("unchecked")
	public InstanceChangingElementEditor(String translationPreffix, Object params) {
		for (ClassEntry classEntry : getClassEntries(params)) {
			classEntries.put(classEntry.getType(), new CachedSupplier<>(classEntry.getSpecificPanel()));
		}
		typeChooser = new JComboBox<>(classEntries.keySet().stream().toArray(Class[]::new));
		typeChooser.setRenderer(new ClassNameCellRenderer(translationPreffix));
		specificPartPanel = new JPanel(new BorderLayout());
		typeChooser.addItemListener(e -> {
			if (typeChooser.isPopupVisible() && e.getStateChange() == ItemEvent.SELECTED) {
				changeInstance(BeanFactory.newInstance(((Class<? extends E>) e.getItem())));
			}
		});
		// for (ClassEntry classEntry : classEntries) {
		// specificPartPanel.add(classEntry.getSpecificPanel(),
		// classEntry.getType().getSimpleName());
		// }
	}

	public Component getTypeChooser() {
		return LayoutUtils.single(typeChooser);
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
	protected void valueChanging() {
		Class<?> type = getValue().getClass();
		// Preload the panel to bind values
		classEntries.get(type).get();
	}

	@Override
	protected void valueChanged(ValueComponent<?> source) {
		if (source == this && getValue() != null) {
			Class<?> type = getValue().getClass();
			if (type != currentClass) {
				currentClass = type;
				specificPartPanel.removeAll();
				specificPartPanel.add(classEntries.get(type).get(), BorderLayout.CENTER);
				specificPartPanel.revalidate();
				specificPartPanel.repaint();
			}
			typeChooser.setSelectedItem(type);
		}
	}

	protected abstract List<ClassEntry> getClassEntries(Object params);

	protected abstract void initialize(E oldInstance, E newInstance);
}