package com.pixurvival.contentPackEditor.component.valueComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

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
		@SuppressWarnings("rawtypes")
		private Predicate condition;
	}

	private @Getter E value;
	private List<SubValueEntry> subValues = new ArrayList<>();
	private List<ValueChangeListener<E>> listeners = new ArrayList<>();

	@SuppressWarnings("unchecked")
	@Override
	public void setValue(E value) {
		this.value = value;
		if (value != null) {
			for (SubValueEntry entry : subValues) {
				if (entry.getCondition().test(value)) {
					entry.getComponent().setValue(entry.getGetter().apply(value));
				}
			}
		}
		valueChanged(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isValueValid(E value) {
		if (value == null) {
			return isNullable();
		}
		for (SubValueEntry entry : subValues) {
			if (entry.getCondition().test(value) && !entry.getComponent().isValueValid(entry.getGetter().apply(value))) {
				return false;
			}
		}
		return true;
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

	public <T> void bind(ValueComponent<? extends T> component, Function<E, T> getter, BiConsumer<E, T> setter, Predicate<E> condition) {
		subValues.add(new SubValueEntry(component, getter, setter, condition));
		component.addValueChangeListener(v -> {
			if (value != null && condition.test(value)) {
				setter.accept(value, v);
				notifyValueChanged();
			}
			valueChanged(component);
		});
	}

	@SuppressWarnings("unchecked")
	public <T, F extends E> void bind(ValueComponent<? extends T> component, Function<F, T> getter, BiConsumer<F, T> setter, Class<F> type) {
		bind(component, (Function<E, T>) getter, (BiConsumer<E, T>) setter, type::isInstance);
	}

	public <T> void bind(ValueComponent<? extends T> component, Function<E, T> getter, BiConsumer<E, T> setter) {
		bind(component, getter, setter, v -> true);
	}

	public void notifyValueChanged() {
		listeners.forEach(l -> l.valueChanged(value));
	}

	/**
	 * Called after the value of this editor has been changed and all sub fields has
	 * been updated.
	 * 
	 * @param value
	 */
	protected void valueChanged(ValueComponent<?> source) {
		// optional overwrite
	}

	protected boolean isNullable() {
		return false;
	}
}
