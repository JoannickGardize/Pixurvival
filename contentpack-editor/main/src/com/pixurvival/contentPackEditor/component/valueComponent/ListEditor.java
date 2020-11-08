package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.util.CPEButton;
import com.pixurvival.core.contentPack.IdentifiedElement;

import lombok.Getter;
import lombok.Setter;

public abstract class ListEditor<E> extends ElementEditor<List<E>> {

	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;

	private static final long serialVersionUID = 1L;

	private @Getter @Setter boolean oneRequired = false;
	private @Getter ValueComponent<E> editorForValidation;
	@SuppressWarnings("rawtypes")
	private Supplier elementEditorSupplier;
	protected JPanel listPanel = new JPanel();
	protected JButton addButton;
	protected JButton removeButton;
	protected Supplier<? extends E> valueSupplier;

	@SuppressWarnings("unchecked")
	public <F extends E> ListEditor(Supplier<ValueComponent<F>> elementEditorSupplier, Supplier<F> valueSupplier) {
		this.valueSupplier = valueSupplier;
		this.elementEditorSupplier = elementEditorSupplier;
		editorForValidation = (ValueComponent<E>) elementEditorSupplier.get();
		addButton = new CPEButton("generic.add", () -> add(valueSupplier.get()));
		removeButton = new CPEButton("generic.remove", () -> {
			if (!getValue().isEmpty()) {
				removeLast();
				getValue().remove(getValue().size() - 1);
				notifyValueChanged();
				listPanel.validate();
				listPanel.repaint();
			}
		});
	}

	protected abstract void addEditor(ValueComponent<E> editor);

	protected abstract void removeLast();

	@Override
	public void setValue(List<E> value, boolean sneaky) {
		List<E> nonNullValue = value;
		if (nonNullValue == null) {
			nonNullValue = new ArrayList<>();
		}
		List<E> oldValue = getValue();
		super.setValue(nonNullValue, sneaky);

		// Rebuild the components only if the structure of the list has changed
		if (!Objects.equals(value, oldValue)) {
			listPanel.removeAll();
			for (int i = 0; i < nonNullValue.size(); i++) {
				E element = nonNullValue.get(i);
				addComponent(i, element);
			}
			endModifications();
		}
		listPanel.revalidate();
		listPanel.repaint();
	}

	protected void endModifications() {

	}

	@Override
	public boolean isValueValid(List<E> value) {
		if (value == null) {
			return false;
		}
		if (oneRequired && value.isEmpty()) {
			return false;
		}
		for (E item : value) {
			if (!editorForValidation.isValueValid(item)) {
				return false;
			}
		}
		return true;
	}

	public void add(E value) {
		if (value instanceof IdentifiedElement) {
			((IdentifiedElement) value).setId(getValue().size());
		}
		if (getValue() == null) {
			setValue(new ArrayList<>());
		}
		getValue().add(addComponent(getValue().size(), value).getValue());
		endModifications();
		notifyValueChanged();
		listPanel.revalidate();
		listPanel.repaint();
	}

	@SuppressWarnings("unchecked")
	public void forEachEditors(Consumer<ValueComponent<E>> action) {
		for (Component component : listPanel.getComponents()) {
			action.accept((ElementEditor<E>) component);
		}
		action.accept(editorForValidation);
	}

	private ValueComponent<E> addComponent(int index, E value) {
		@SuppressWarnings("unchecked")
		ValueComponent<E> elementEditor = (ValueComponent<E>) elementEditorSupplier.get();
		if (value != null) {
			elementEditor.setValue(value);
		}
		elementEditor.addValueChangeListener(v -> {
			getValue().set(index, v);
			notifyValueChanged();
		});
		addEditor(elementEditor);
		return elementEditor;
	}

}
