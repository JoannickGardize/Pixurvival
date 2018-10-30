package com.pixurvival.contentPackEditor.component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.util.ValueChangeListener;
import com.pixurvival.contentPackEditor.component.util.ValueComponent;
import com.pixurvival.contentPackEditor.event.ElementChangedEvent;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.NamedElement;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ElementEditor<E> extends JPanel implements ValueComponent<E> {

	private static final long serialVersionUID = 1L;

	@Getter
	@AllArgsConstructor
	private class SubValueEntry<T> {
		private ValueComponent<?> component;
		private Consumer<E> parentUpdate;
		private BiConsumer<E, T> childUpdate;
	}

	private @Getter E value;
	private List<SubValueEntry<?>> subValues = new ArrayList<>();
	private List<ValueChangeListener<E>> listeners = new ArrayList<>();

	protected <T> void addSubValue(ValueComponent<T> component, Consumer<E> parentUpdate,
			BiConsumer<E, T> childUpdate) {
		subValues.add(new SubValueEntry<>(component, parentUpdate, childUpdate));
		component.addValueChangeListener(v -> {
			if (value != null) {
				childUpdate.accept(value, v);
				notifyValueChanged();
			}
			valueChanged();
		});
	}

	@Override
	public void setValue(E value) {
		this.value = value;
		subValues.forEach(entry -> entry.getParentUpdate().accept(value));
		valueChanged();
	}

	@Override
	public boolean isValueValid() {
		return isValueValid(value);
	}

	public boolean isValueValid(E value) {
		if (value == null) {
			return false;
		}
		for (SubValueEntry<?> entry : subValues) {
			if (!entry.getComponent().isValueValid()) {
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

	private void notifyValueChanged() {
		listeners.forEach(l -> l.valueChanged(value));
		if (value instanceof NamedElement) {
			EventManager.getInstance().fire(new ElementChangedEvent((NamedElement) value, isValueValid()));
		}
	}

	protected void valueChanged() {
	}

}
