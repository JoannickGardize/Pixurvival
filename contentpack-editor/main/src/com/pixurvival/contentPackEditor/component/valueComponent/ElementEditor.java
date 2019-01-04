package com.pixurvival.contentPackEditor.component.valueComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.swing.JLabel;
import javax.swing.JPanel;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ElementEditor<E> extends JPanel implements ValueComponent<E> {

	private static final long serialVersionUID = 1L;

	@Getter
	@AllArgsConstructor
	private class SubValueEntry {
		@SuppressWarnings("rawtypes")
		private ValueComponent component;
		@SuppressWarnings("rawtypes")
		private Function getter;
		@SuppressWarnings("rawtypes")
		private BiConsumer setter;
	}

	private @Getter E value;
	private List<SubValueEntry> subValues = new ArrayList<>();
	private List<ValueChangeListener<E>> listeners = new ArrayList<>();

	@SuppressWarnings("unchecked")
	@Override
	public void setValue(E value) {
		this.value = value;
		subValues.forEach(entry -> entry.getComponent().setValue(entry.getGetter().apply(value)));
		valueChanged();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isValueValid(E value) {
		if (value == null) {
			return false;
		}
		boolean valid = true;
		boolean valueChanged = false;
		for (SubValueEntry entry : subValues) {
			if (!entry.getComponent().isValueValid(entry.getGetter().apply(value))) {
				// Remove references of elements when removed
				if (entry.getComponent() instanceof ElementChooserButton<?> && entry.getGetter().apply(value) != null) {
					entry.getSetter().accept(value, null);
					valueChanged = true;
					if (((ElementChooserButton<?>) entry.getComponent()).isRequired()) {
						valid = false;
					}
				} else {
					valid = false;
				}
			}
		}
		if (valueChanged) {
		}
		return valid;
	}

	@Override
	public void setAssociatedLabel(JLabel label) {
	}

	@Override
	public JLabel getAssociatedLabel() {
		return null;
	}

	@Override
	public void addValueChangeListener(ValueChangeListener<E> listener) {
		listeners.add(listener);
	}

	protected <T> void bind(ValueComponent<T> component, Function<E, T> getter, BiConsumer<E, T> setter) {
		subValues.add(new SubValueEntry(component, getter, setter));
		component.addValueChangeListener(v -> {
			if (value != null) {
				setter.accept(value, v);
				notifyValueChanged();
			}
			valueChanged();
		});
	}

	public void notifyValueChanged() {
		listeners.forEach(l -> l.valueChanged(value));
	}

	protected void valueChanged() {
	}

}
