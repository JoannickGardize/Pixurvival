package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.pixurvival.contentPackEditor.component.util.EnumConstantCellRenderer;
import com.pixurvival.core.util.CaseUtils;

import lombok.Getter;

public class EnumChooser<E extends Enum<E>> extends JComboBox<E> implements ValueComponent<E> {

	private static final long serialVersionUID = 1L;

	private @Getter JLabel associatedLabel;

	private List<ValueChangeListener<E>> listeners = new ArrayList<>();

	public EnumChooser(Class<E> enumType) {
		this(enumType, CaseUtils.pascalToCamelCase(enumType.getSimpleName()));
	}

	public EnumChooser(Class<E> enumType, String translationPreffix) {
		this(translationPreffix, enumType.getEnumConstants());
	}

	@SafeVarargs
	public EnumChooser(Class<E> enumType, E... elements) {
		this(CaseUtils.pascalToCamelCase(enumType.getSimpleName()), elements);
	}

	@SafeVarargs
	public EnumChooser(String translationPreffix, E... elements) {
		super(elements);
		setRenderer(new EnumConstantCellRenderer(translationPreffix));
		addItemListener(e -> {
			if (isPopupVisible() && e.getStateChange() == ItemEvent.SELECTED) {
				listeners.forEach(l -> l.valueChanged(getValue()));
				updateColor();
			}
		});
	}

	@Override
	@SuppressWarnings("unchecked")
	public E getValue() {
		return (E) getSelectedItem();
	}

	@Override
	public void setValue(E value) {
		setSelectedItem(value);
		updateColor();
	}

	@Override
	public boolean isValueValid(E value) {
		return value != null;
	}

	@Override
	public void setAssociatedLabel(JLabel label) {
		associatedLabel = label;
		associatedLabel.setForeground(getForeground());
	}

	@Override
	public void addValueChangeListener(ValueChangeListener<E> listener) {
		listeners.add(listener);
	}

	@Override
	public void setForeground(Color fg) {
		if (associatedLabel != null) {
			associatedLabel.setForeground(fg);
		}
		super.setForeground(fg);
	}

	private void updateColor() {
		if (getValue() == null) {
			setForeground(Color.RED);
		} else {
			setForeground(Color.BLACK);
		}
	}
}
